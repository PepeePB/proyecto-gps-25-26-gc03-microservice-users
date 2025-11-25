<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Recuperación de contraseña</title>
</head>
<body style="margin: 0; padding: 0; background-color: #f5f5f5;">
<table role="presentation" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td align="center" style="padding: 40px 0;">
            <!-- Contenedor principal -->
            <table role="presentation" cellpadding="0" cellspacing="0" width="600" style="background-color: #ffffff; padding: 40px; border-radius: 8px; font-family: sans-serif;">

                <!-- Imagen superior -->
                <tr>
                    <td align="center" style="padding-bottom: 30px;">
                        <img src="https://i.ibb.co/GYBgPnm/Blanco-fondo-negro.jpg" alt="MusicFly Logo" width="600"
                             style="display: block; width: 100%; max-width: 600px; height: auto;">
                    </td>
                </tr>

                <!-- Título -->
                <tr>
                    <td align="center" style="font-size: 24px; font-weight: bold; color: #333333;">
                        Recupera tu contraseña
                    </td>
                </tr>

                <!-- Subtítulo -->
                <tr>
                    <td align="center" style="padding: 20px 0; font-size: 16px; color: #555555;">
                        Ingresa el siguiente código para continuar con el proceso de recuperación:
                    </td>
                </tr>

                <!-- CÓDIGO DE 6 DÍGITOS -->
                <tr>
                    <td align="center">
                        <table role="presentation" border="0" cellpadding="0" cellspacing="10" align="center">
                            <tr>
                                <!-- Reemplazá los números aquí -->
                                <#list code as digit>
                                    <td style="width: 40px; height: 40px; background-color: #eeeeee;
                                                text-align: center; vertical-align: middle;
                                                 font-size: 24px; font-family: monospace;
                                                 border-radius: 4px;">${digit}</td>
                                </#list>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- Botón para cambiar contraseña -->
                <tr>
                    <td align="center" style="padding: 30px 0;">
                        <table role="presentation" border="0" cellpadding="0" cellspacing="0" align="center">
                            <tr>
                                <td align="center">
                                    <a href="${resetPasswordLink}"
                                       style="display: inline-block;
                              padding: 12px 24px;
                              background-color: #000000;
                              color: #ffffff;
                              text-decoration: none;
                              border-radius: 5px;
                              font-size: 18px;
                              font-family: sans-serif;">
                                        Cambiar contraseña
                                    </a>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- Nota de seguridad -->
                <tr>
                    <td align="center" style="font-size: 14px; color: #999999;">
                        Si no solicitaste este cambio, puedes ignorar este correo.
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
