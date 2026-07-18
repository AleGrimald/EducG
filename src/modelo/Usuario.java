package modelo;

/** Datos personales de un usuario registrado. */
public class Usuario {

    private final String nombre;
    private final String apellido;
    private final String email;

    public Usuario(String nombre, String apellido, String email) {
        this.nombre   = nombre;
        this.apellido = apellido;
        this.email    = email;
    }

    public String getNombre()   { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail()    { return email; }
}
