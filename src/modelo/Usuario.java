package modelo;

/** Datos personales de un usuario registrado. */
public class Usuario {

    private final int id;
    private final String nombre;
    private final String apellido;
    private final String email;
    private final long dni;
    private final String telefono;

    public Usuario(int id, String nombre, String apellido, String email, long dni, String telefono) {
        this.id       = id;
        this.nombre   = nombre;
        this.apellido = apellido;
        this.email    = email;
        this.dni      = dni;
        this.telefono = telefono;
    }

    public int getId()          { return id; }
    public String getNombre()   { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail()    { return email; }
    public long getDni()        { return dni; }
    public String getTelefono() { return telefono; }
}
