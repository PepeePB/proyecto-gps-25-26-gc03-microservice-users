<#-- cambiar_contrasena.ftl -->
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cambiar Contraseña</title>
    <script>
        <#-- Si la variable 'success' está presente, redireccionamos en 3 segundos -->
        <#if success??>
        setTimeout(function () {
            window.location.href = "${redirect}"; // o cualquier URL que necesites
        }, 3000);
        </#if>
    </script>
</head>
<body>
<h2>Cambiar Contraseña</h2>

<#if error??>
    <div style="color: red;">${error}</div>
</#if>

<#if success??>
    <div style="color: green;">${success}</div>
</#if>

<form action="/core/views/password-reset-form" method="post">
    <input type="hidden" name="passwordResetToken" value="${passwordResetToken!''}">
    <div>
        <label for="usuario">Usuario:</label>
        <input type="text" id="usuario" name="username" value="${username!''}" readonly>
    </div>
    <div>
        <label for="actual">Contraseña Actual:</label>
        <input type="password" id="actual" name="actual" <#if success??>disabled</#if>  required>
    </div>

    <div>
        <label for="nueva">Nueva Contraseña:</label>
        <input type="password" id="nueva" name="nueva" <#if success??>disabled</#if>  required>
    </div>

    <div>
        <label for="confirmar">Confirmar Nueva Contraseña:</label>
        <input type="password" id="confirmar" name="confirmar" <#if success??>disabled</#if>  required>
    </div>

    <div>
        <button type="submit" <#if success??>disabled</#if> >Cambiar Contraseña</button>
    </div>
</form>
</body>
</html>
