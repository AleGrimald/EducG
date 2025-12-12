package controlador;
import java.util.List;
import modelo.mLogin;

public class cLogin {
    private String user;
    private String passw;

    public cLogin() {}
    
    public cLogin(String _user, String _passw) {
        this.user = _user;
        this.passw = _passw;
    }
    
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassw() {
        return passw;
    }
    public void setPassw(String passw) {
        this.passw = passw;
    }
    
    private boolean ValidarNoSqlInyection(String input) {
        String regex = "['\";#=\\-\\(\\)<>%*+/\\\\]";
        return !input.matches(".*" + regex + ".*");
    }
    
    public boolean IngresarLogin(){
        if(ValidarNoSqlInyection(getUser()) && ValidarNoSqlInyection(getPassw())){
            mLogin mLoginData = new mLogin();
            List<String[]> listaDB = mLoginData.SelectData();

            boolean encontrado = false;

            for (String[] fila : listaDB) {
                String userDB = fila[0];
                String passwDB = fila[1];

                if (userDB.equals(getUser()) && passwDB.equals(getPassw())) {
                    encontrado = true;
                    break;
                }
            }
            
            return encontrado;
            
        }else return false;
       
    }

    
}
