-- Crear el usuario alumno y otorgar todos los privilegios
CREATE USER IF NOT EXISTS 'alumno'@'%' IDENTIFIED BY 'alumno';
GRANT ALL PRIVILEGES ON UnderSounds.* TO 'alumno'@'%';
FLUSH PRIVILEGES;

