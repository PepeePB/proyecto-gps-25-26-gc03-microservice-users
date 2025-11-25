<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Verificación de código</title>
</head>
<body>
<h2>Verificación de código</h2>

<#if error??>
    <div style="color: red;">${error}</div>
</#if>

<form action="/core/views/code-verified" method="post">

    <label for="codigo">Introduce el código de 6 dígitos que recibiste por correo:</label><br>
    <input type="text" id="codigo" name="codigo" maxlength="6" required><br><br>

    <button type="submit">Verificar código</button>
</form>
</body>
</html>
