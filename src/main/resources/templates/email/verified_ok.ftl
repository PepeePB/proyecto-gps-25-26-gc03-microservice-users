<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Verificación Exitosa</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f9fb;
            color: #333;
            margin: 0;
            padding: 0;
        }

        .container {
            text-align: center;
            padding: 60px 20px;
            max-width: 600px;
            margin: 100px auto;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #2c3e50;
            margin-bottom: 20px;
        }

        p {
            font-size: 16px;
            line-height: 1.5;
        }

        .btn {
            display: inline-block;
            margin-top: 30px;
            padding: 12px 25px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }

        .btn:hover {
            background-color: #2980b9;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>¡Correo verificado!</h1>
    <p>Gracias por verificar tu cuenta en <strong>MusicFly</strong>.</p>
    <p>Ya puedes iniciar sesión con tus credenciales o cuenta de Google.</p>

    <a href="${frontendURL}" class="btn">Ir al inicio de sesión</a>
</div>
</body>
</html>
