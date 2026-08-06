package dao;

import java.sql.SQLException;

public interface VerificacionDAO {

    void generarCodigo(long dni, String codigo, int minutosExpiracion) throws SQLException;

    /** @return 1 = válido (activa cuenta), 0 = no existe/no coincide, -1 = expirado */
    int verificarCodigo(long dni, String codigo) throws SQLException;
}
