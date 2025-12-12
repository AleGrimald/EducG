package controlador;
import java.util.List;
import modelo.mAdmin;

public class cAdmin {
    private final String ape;
    private final String nom;
    private final String user;
    private final String mail;
    private final String pass;
    private final String tel;
    private final String cursos;
    
    public cAdmin(String ape, String nom, String user, String pass, String tel, String mail, List<String> cursos) {
        this.ape = ape;
        this.nom = nom;
        this.user = user;
        this.pass = pass;
        this.tel = tel;
        this.mail = mail;
        this.cursos = String.join(",", cursos);
    }
    
    public String AgregarAlumno(){
        mAdmin mod = new mAdmin();
        
        if(mod.InsertAlumno(ape, nom, user, pass, tel, mail, cursos)){
            return "Alumno registrado.";
        }else{
            return "No se pudo agregar al Alumno";
        }
    }
    
    
}
