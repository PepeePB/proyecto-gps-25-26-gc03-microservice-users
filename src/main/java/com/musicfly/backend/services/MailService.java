package com.musicfly.backend.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailService {

    private final GmailOAuth2Service oAuth2Service;

    @Value("${gmail.email}")
    private String userEmail;

    public void sendEmail(String to, String subject, String body) throws MessagingException, IOException {
        String accessToken = oAuth2Service.getAccessToken();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // username = email, password = access token (as per XOAUTH2)
                return new PasswordAuthentication(userEmail, accessToken);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");

        Transport.send(message);
    }

    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> model) {
        try {
            Template template = freemarkerConfiguration().getTemplate("email/" + templateName);
            StringWriter writer = new StringWriter();
            if (template!=null) template.process(model, writer);
            String htmlBody = writer.toString();

            sendEmail(to, subject, htmlBody);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Configuration freemarkerConfiguration() throws IOException {
        Configuration config = new Configuration(Configuration.VERSION_2_3_32);
        config.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates"); // Ruta base
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
        return config;
    }
}
