#!/bin/bash

# Función para agregar pestañas a una ventana
agregar_pestanas_a_ventana() {
    local archivo=$1
    local nombre_ventana=$2
    local indice_pestaña=$3
    
    # Agregar import MouseAdapter si no existe
    if ! grep -q "import java.awt.event.MouseAdapter" "$archivo"; then
        sed -i '/import java.awt/a\import java.awt.event.MouseAdapter;\nimport java.awt.event.MouseEvent;' "$archivo"
    fi
    
    # Agregar las líneas de pestañas al encabezado (antes del cierre del encabezado.add)
    if ! grep -q "crearPestanas()" "$archivo"; then
        # Encontrar la última línea de "encabezado.add" y agregar las pestañas después
        sed -i '/encabezado.add.*BorderLayout.EAST/a\
\
        \/\/ ── Pestañas de navegación ─────────────────────────────────────────\
        JPanel pestanas = crearPestanas();\
        encabezado.add(pestanas, BorderLayout.SOUTH);' "$archivo"
    fi
}

# Agregar pestañas a las tres ventanas
for archivo in src/vista/VentanaMisDatos.java src/vista/VentanaMisCursos.java src/vista/VentanaMisEstadisticas.java; do
    agregar_pestanas_a_ventana "$archivo"
done

echo "✅ Pestañas agregadas"
