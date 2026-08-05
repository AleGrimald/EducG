package modelo;

/** Resultado de persistir un intento de test ({@code sp_alta_resultado_test}): el id del intento
 * guardado y si esta aprobación generó un certificado nuevo (primera vez que se aprueba ese
 * curso — {@code certificados} tiene {@code UNIQUE(usuario_id, curso_id)}, así que reintentos
 * posteriores no cuentan como nuevos aunque vuelvan a aprobar). */
public final class ResultadoTestGuardado {

    private final int resultadoId;
    private final int puntaje;
    private final boolean certificadoNuevo;

    public ResultadoTestGuardado(int resultadoId, int puntaje, boolean certificadoNuevo) {
        this.resultadoId = resultadoId;
        this.puntaje = puntaje;
        this.certificadoNuevo = certificadoNuevo;
    }

    public int getResultadoId()         { return resultadoId; }
    public int getPuntaje()             { return puntaje; }
    public boolean isCertificadoNuevo() { return certificadoNuevo; }
}
