#!/bin/bash

# Lista de ventanas que necesitan actualización
VENTANAS=(
  "src/vista/VentanaMisCursos.java"
  "src/vista/VentanaMisDatos.java"
  "src/vista/VentanaMisEstadisticas.java"
  "src/vista/VentanaContenidoCurso.java"
  "src/vista/VentanaTest.java"
  "src/vista/admin/VentanaAdminAlumnos.java"
  "src/vista/admin/VentanaAdminCursos.java"
  "src/vista/admin/VentanaAdminEstadisticas.java"
  "src/vista/admin/VentanaCrearCurso.java"
)

for archivo in "${VENTANAS[@]}"; do
  if [ -f "$archivo" ]; then
    # Cambiar encabezado.setOpaque(false) a true y agregar color
    sed -i '/encabezado\.setOpaque(false);/a\        encabezado.setBackground(new Color(240, 245, 250));' "$archivo"
    sed -i 's/encabezado\.setOpaque(false);/encabezado.setOpaque(true);/' "$archivo"
    
    # Cambiar logo
    sed -i 's/JLabel appLbl = new JLabel("Educ G");/JLabel appLbl = FabricaUI.crearLogoEducG(100);/' "$archivo"
    sed -i '/JLabel appLbl = FabricaUI.crearLogoEducG(100);/{n;/appLbl\.setFont/d;}' "$archivo"
    sed -i '/JLabel appLbl = FabricaUI.crearLogoEducG(100);/{n;/appLbl\.setForeground/d;}' "$archivo"
    
    # Cambiar color de subLbl
    sed -i 's/subLbl\.setForeground(new Color(180, 210, 255));/subLbl.setForeground(new Color(80, 100, 130));/' "$archivo"
    
    # Cambiar color de paginaLbl si existe
    sed -i 's/paginaLbl\.setForeground(new Color(180, 210, 255));/paginaLbl.setForeground(new Color(80, 100, 130));/' "$archivo"
    
    echo "✅ $archivo actualizado"
  fi
done
