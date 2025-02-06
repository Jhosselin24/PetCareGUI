import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Pag2_Registro {
    private JButton REGRESARButton;
    private JTextField nombreField;
    private JPasswordField passwordField;
    private JButton REGISTRARSEButton;
    public JPanel registro;
    private JPasswordField confirmPass;
    static JFrame frameRegistro = new JFrame("Registro");
    ConexionDB conexionDB = new ConexionDB();

    public Pag2_Registro() {
        REGRESARButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        REGISTRARSEButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        REGRESARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameRegistro.dispose();
                Pag1_Inicio.frame.setVisible(true);
            }
        });
        REGISTRARSEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreIn = nombreField.getText();
                String passIn = String.valueOf(passwordField.getPassword());
                String confPassIn = String.valueOf(confirmPass.getPassword());
                if (nombreIn.isEmpty() || passIn.isEmpty() || confPassIn.isEmpty()){
                    JOptionPane.showMessageDialog(null,"Ingrese valores correctos","Valores Incorrectos",JOptionPane.ERROR_MESSAGE);
                } else if (!passIn.equals(confPassIn)) {
                    JOptionPane.showMessageDialog(null,"Su contraseña no coincide, revise nuevamente","Valores Incorrectos",JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO CAJEROS(nombre, pass_cajero) VALUES (?,?)");
                        ps.setString(1,nombreIn);
                        ps.setString(2,passIn);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null,"Se ha ingresado un nuevo cajero correctamente");
                        nombreField.setText("");
                        passwordField.setText("");
                        confirmPass.setText("");
                    }catch (SQLException ex){
                        JOptionPane.showMessageDialog(null,"Error: "+ex.getMessage(),"ERROR SQL",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}
