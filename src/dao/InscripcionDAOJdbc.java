package dao;

import bd.ConexionBD;
import modelo.Inscripcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación de {@link InscripcionDAO} sobre stored procedures (sp_alta_inscripcion, sp_baja_inscripcion, etc.). */
public class InscripcionDAOJdbc implements InscripcionDAO {

    @Override
    public int altaInscripcion(String email, String cursoTitulo) throws SQLException {
        final String sql = "{call sp_alta_inscripcion(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, cursoTitulo);
            cs.registerOutParameter(3, Types.TINYINT);
            cs.execute();
            return cs.getInt(3);
        }
    }

    @Override
    public void bajaInscripcion(String email, String cursoTitulo) throws SQLException {
        final String sql = "{call sp_baja_inscripcion(?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, cursoTitulo);
            cs.execute();
        }
    }

    @Override
    public boolean estaInscripto(String email, String cursoTitulo) throws SQLException {
        final String sql = "{call sp_obtener_inscripcion(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, cursoTitulo);
            cs.registerOutParameter(3, Types.TINYINT);
            cs.execute();
            return cs.getInt(3) == 1;
        }
    }

    @Override
    public List<Inscripcion> listarPorUsuario(String email) throws SQLException {
        List<Inscripcion> inscripciones = new ArrayList<>();
        final String sql = "{call sp_listar_inscripciones_usuario(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next())
                    inscripciones.add(new Inscripcion(rs.getString("curso_titulo"), rs.getString("fecha_inscripcion")));
            }
        }
        return inscripciones;
    }

    @Override
    public int obtenerProgreso(String email, String cursoTitulo) throws SQLException {
        final String sql = "{call sp_obtener_progreso_inscripcion(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, cursoTitulo);
            cs.registerOutParameter(3, Types.TINYINT);
            cs.execute();
            return cs.getInt(3);
        }
    }

    @Override
    public void actualizarProgreso(String email, String cursoTitulo, int leccionActual) throws SQLException {
        final String sql = "{call sp_modificar_progreso_inscripcion(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, cursoTitulo);
            cs.setInt(3, leccionActual);
            cs.execute();
        }
    }
}
