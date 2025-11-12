package com.musicfly.backend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicfly.backend.models.UserOptionsUUID;
import com.musicfly.backend.properties.ApplicationProperties;
import com.musicfly.backend.repositories.UserRepository;
import com.musicfly.backend.services.JwtService;
import com.musicfly.backend.services.RedisTokenService;
import com.musicfly.backend.views.DTO.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;
    private final UserDetailsService userDetailsService;
    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;
    private final ApplicationProperties applicationProperties;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        final String username;
        final String ip = request.getRemoteAddr();

        // Si la opción "Open Doors" está habilitada, no procesamos la autenticación JWT
        if (applicationProperties.isOpenDoors()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Comprueba si el token está vacio
        if(token == null){
            String authHeader = request.getHeader("Authorization");
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                String tokenCheck = authHeader.substring(7);
                if (tokenCheck.equals("")){
                    logger.warn("CONTROLLER REQUEST WITH TOKEN FAILED - TOKEN IS NULL - IP: {}- URI: {}", ip, request.getRequestURI());
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(new ObjectMapper().writeValueAsString(ErrorResponseDTO.builder()
                            .error("token_is_null")
                            .message("The request don't contain required token.")
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .timestamp(LocalDateTime.now().toString())
                            .build()));
                    return;
                }
            }else{
                token = extractTokenFromCookies(request);
                if(token == null) {
                    logger.info("CONTROLLER REQUEST WITHOUT TOKEN - IP: {} - URI: {}", ip, request.getRequestURI());
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        // Comprueba si el token es un token JWT valido
        try {
            System.err.println(token);
            jwtDecoder.decode(token);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("CONTROLLER REQUEST WITHOUT INVALID TOKEN JWT - IP: {} - URI: {}", ip, request.getRequestURI());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new ObjectMapper().writeValueAsString(ErrorResponseDTO.builder()
                    .error("token_is_invalid")
                    .message("This token is invalid.")
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build()));
            return;
        }

        username = jwtService.getUsernameFromToken(token);

        // Comprueba si el token está en la blacklist
        if (redisTokenService.hasTokenType(UserOptionsUUID.BLACK_LIST,token)) {
            logger.warn("CONTROLLER REQUEST WITH TOKEN FAILED - IP: {} - URI: {} - TOKEN: {}", ip, request.getRequestURI(), token);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new ObjectMapper().writeValueAsString(ErrorResponseDTO.builder()
                    .error("token_is_blacklist")
                    .message("This token is blacklisted.")
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build()));
            SecurityContextHolder.getContext().setAuthentication(null); // Borra cualquier token alamcenado
            return; // Detener el filtro aquí, no continuar con el flujo
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            System.err.println("DETALLES \n"
                    +userDetails.getUsername()+"\n"
                    +userDetails.getPassword()+"\n"
                    +userDetails.getAuthorities()+"\n"
                    +request.getHeader("User-Agent")+"\n");
            if(jwtService.isTokenValid(token, userDetails, request)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("CONTROLLER REQUEST WITH TOKEN SUCCESS - IP: {} - URI: {} - TOKEN: {}", ip, request.getRequestURI(), token);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            }else{
                logger.warn("CONTROLLER REQUEST WITH TOKEN FAILED - IP: {} - URI: {} - TOKEN: {}", ip, request.getRequestURI(), token);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(new ObjectMapper().writeValueAsString(ErrorResponseDTO.builder()
                        .error("token_invalid")
                        .message("This token is not valid.")
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .timestamp(LocalDateTime.now().toString())
                        .build()));
                SecurityContextHolder.getContext().setAuthentication(null); // Borra cualquier token alamcenado
            }
        }
    }

    public String getTokenFromRequest(HttpServletRequest request){
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Debemos de comprobar si la cabecera contiene algun token
        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")){
            // Si es cierto, el token comienza a partir del octavo caracter
            return authHeader.substring(7);
        }else if(request.getCookies() != null){
            return extractTokenFromCookies(request);
        }

        return null;
    }

    public String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
