package modelo;

/** Datos de un alumno para el panel de administrador (incluye id/estado, a diferencia de {@link Usuario}). */
public class AlumnoAdmin {

    private final int id;
    private final String nombre;
    private final String apellido;
    private final String email;
    private final long dni;
    private final String telefono;
    private final String fechaRegistro;
    private final boolean activo;

    public AlumnoAdmin(int id, String nombre, String apellido, String email, long dni, String telefono,
                        String fechaRegistro, boolean activo) {
        this.id            = id;
        this.nombre        = nombre;
        this.apellido      = apellido;
        this.email         = email;
        this.dni           = dni;
        this.telefono      = telefono;
        this.fechaRegistro = fechaRegistro;
        this.activo        = activo;
    }

    public int getId()               { return id; }
    public String getNombre()        { return nombre; }
    public String getApellido()      { return apellido; }
    public String getEmail()         { return email; }
    public long getDni()             { return dni; }
    public String getTelefono()      { return telefono; }
    public String getFechaRegistro() { return fechaRegistro; }
    public boolean isActivo()        { return activo; }
}
