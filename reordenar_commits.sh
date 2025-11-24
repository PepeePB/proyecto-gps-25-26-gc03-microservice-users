#!/bin/bash
# Script para limpiar commits intermedios y mover el contenido de 89619e5 al commit 447021d

# Cambia TU_RAMA por el nombre de tu rama (ej: main o develop)
RAMA="develop"

# Hash del commit final cuyo contenido queremos mover
COMMIT_FINAL="89619e5"

# Hash del commit que queremos mantener como nueva cima
COMMIT_BASE="447021d"

echo "Cambiando a la rama $RAMA..."
git checkout $RAMA || { echo "Error: no se pudo cambiar a la rama $RAMA"; exit 1; }

echo "Extrayendo contenido del commit $COMMIT_FINAL al área de trabajo..."
git checkout $COMMIT_FINAL -- . || { echo "Error al extraer contenido"; exit 1; }

echo "Reseteando la rama al commit base $COMMIT_BASE..."
git reset --hard $COMMIT_BASE || { echo "Error al hacer reset"; exit 1; }

echo "Creando nuevo commit con el contenido del commit $COMMIT_FINAL..."
git add . || { echo "Error al agregar archivos"; exit 1; }
git commit -m "Merge pull request #6 from UniExtremadura/SCRUM-37-HU12.-Soporte-de-pagos (contenido de $COMMIT_FINAL)" || { echo "Error al crear commit"; exit 1; }

echo "¡Historial reescrito correctamente!"
echo "Si la rama está en remoto, ejecuta:"
echo "git push origin $RAMA --force"

