import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private String host="jdbc:mysql://sql10.freesqldatabase.com:3306/sql10761405?useSSL=false&serverTimezone=UTC";
    private String user="sql10761405";
    private String password="k369mwEHgf";
    public String mensaje;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /*Constructor vacio*/
    public ConexionDB(){}

    public Connection ConexionLocal(){
        Connection connection=null;
        try {
             connection = DriverManager.getConnection(this.host, this.user, this.password);
        }
        catch (SQLException e){
            System.out.println(e);
            mensaje = "Algo salió mal :(";
            System.out.println(mensaje);
        }
        return connection;
    }
}
