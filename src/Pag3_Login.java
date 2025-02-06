import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static java.sql.DriverManager.println;

public class Pag3_Login {
    public JPanel baselogin;
    private JTextField nombreTextField;
    private JPasswordField passwordField;
    private JButton INGRESARButton;
    private JButton REGRESARButton;
    private JComboBox comboBox1;
    static JFrame frameLogin = new JFrame("Login CICLOSHOP");
    ConexionDB conexionDB = new ConexionDB();
    static int idCajeroActual = -1;

    public Pag3_Login() {
        INGRESARButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        REGRESARButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        REGRESARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameLogin.dispose();
                Pag1_Inicio.frame.setVisible(true);
            }
        });
        INGRESARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean usuarioEncontrado=false;
                if (comboBox1.getSelectedIndex() == 0){
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        //SQL para la tabla de Usuarios
                        PreparedStatement ps = connection.prepareStatement("SELECT * FROM usuarios");
                        ResultSet resultSetUsuarios = ps.executeQuery();
                        while (resultSetUsuarios.next()&&!usuarioEncontrado){
                            if (resultSetUsuarios.getString(2).equals(nombreTextField.getText())
                                    && resultSetUsuarios.getString(4).equals(String.valueOf(passwordField.getPassword())))
                            {
                                usuarioEncontrado=true;
                                frameLogin.dispose();
                                idCajeroActual = resultSetUsuarios.getInt(1);
                                if (!Usuario_Dashboard.frameUsuarioDashboard.isUndecorated()){
                                    Usuario_Dashboard.frameUsuarioDashboard.setUndecorated(true);
                                }
                                Usuario_Dashboard.frameUsuarioDashboard.setContentPane(new Usuario_Dashboard().usuario_panel);
                                Usuario_Dashboard.frameUsuarioDashboard.setSize(800,500);
                                Usuario_Dashboard.frameUsuarioDashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                Usuario_Dashboard.frameUsuarioDashboard.setVisible(true);
                                Usuario_Dashboard.frameUsuarioDashboard.setLocationRelativeTo(null);
                            }
                        }
                    } catch (Exception exception){
                        JOptionPane.showMessageDialog(null,"Error: "+exception.getMessage(),"ERROR SQL",JOptionPane.ERROR_MESSAGE);
                    }
                } else if (comboBox1.getSelectedIndex() == 1){
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        //SQL para la tabla de Usuarios
                        PreparedStatement ps = connection.prepareStatement("SELECT * FROM administradores");
                        ResultSet resultSetUsuarios = ps.executeQuery();
                        while (resultSetUsuarios.next()&&!usuarioEncontrado){
                            if (resultSetUsuarios.getString(2).equals(nombreTextField.getText())
                                    && resultSetUsuarios.getString(4).equals(String.valueOf(passwordField.getPassword())))
                            {
                                usuarioEncontrado=true;
                                frameLogin.dispose();
                                if (!Pag4_Admin.frameAdminP.isUndecorated()){
                                    Pag4_Admin.frameAdminP.setUndecorated(true);
                                }
                                Pag4_Admin.frameAdminP.setContentPane(new Pag4_Admin().admin_pag);
                                Pag4_Admin.frameAdminP.setSize(800,600);
                                Pag4_Admin.frameAdminP.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                Pag4_Admin.frameAdminP.setVisible(true);
                                Pag4_Admin.frameAdminP.setLocationRelativeTo(null);
                            }
                        }
                    } catch (Exception exception){
                        JOptionPane.showMessageDialog(null,"Error: "+exception.getMessage(),"ERROR SQL",JOptionPane.ERROR_MESSAGE);
                    }


                } else if (comboBox1.getSelectedIndex() == 2) {
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        //SQL para la tabla de Usuarios
                        PreparedStatement ps = connection.prepareStatement("SELECT * FROM clientes");
                        ResultSet resultSetUsuarios = ps.executeQuery();
                        while (resultSetUsuarios.next()&&!usuarioEncontrado){
                            if (resultSetUsuarios.getString(2).equals(nombreTextField.getText())
                                    && resultSetUsuarios.getString(7).equals(String.valueOf(passwordField.getPassword())))
                            {
                                usuarioEncontrado=true;
                                frameLogin.dispose();
                                Cliente_Dashboard clienteDashboard = new Cliente_Dashboard(resultSetUsuarios.getInt(1),resultSetUsuarios.getString(2));
                                if (!clienteDashboard.frameClienteDashboard.isUndecorated()){
                                    clienteDashboard.frameClienteDashboard.setUndecorated(true);
                                }
                                clienteDashboard.frameClienteDashboard.setContentPane(clienteDashboard.cliente_dashboar_panel);
                                clienteDashboard.frameClienteDashboard.setSize(800,600);
                                clienteDashboard.frameClienteDashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                clienteDashboard.frameClienteDashboard.setVisible(true);
                                clienteDashboard.frameClienteDashboard.setLocationRelativeTo(null);
                            }
                        }
                    } catch (Exception exception){
                        JOptionPane.showMessageDialog(null,"Error: "+exception.getMessage(),"ERROR SQL",JOptionPane.ERROR_MESSAGE);
                    }
                } else if (comboBox1.getSelectedIndex() == 3) {
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        //SQL para la tabla de Usuarios
                        PreparedStatement ps = connection.prepareStatement("SELECT * FROM veterinarios");
                        ResultSet resultSetUsuarios = ps.executeQuery();
                        while (resultSetUsuarios.next()&&!usuarioEncontrado){
                            if (resultSetUsuarios.getString(2).equals(nombreTextField.getText())
                                    && resultSetUsuarios.getString(5).equals(String.valueOf(passwordField.getPassword())))
                            {
                                usuarioEncontrado=true;
                                frameLogin.dispose();
                                Veterinario_Dashboard veterinarioDashboard = new Veterinario_Dashboard(resultSetUsuarios.getInt(1));
                                if (!veterinarioDashboard.veterinarioFrame.isUndecorated()){
                                    veterinarioDashboard.veterinarioFrame.setUndecorated(true);
                                }

                                veterinarioDashboard.veterinarioFrame.setContentPane(veterinarioDashboard.veterinarioDashboardPanel);
                                veterinarioDashboard.veterinarioFrame.setSize(800,600);
                                veterinarioDashboard.veterinarioFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                veterinarioDashboard.veterinarioFrame.setVisible(true);
                                veterinarioDashboard.veterinarioFrame.setLocationRelativeTo(null);
                            }
                        }
                    } catch (Exception exception){
                        JOptionPane.showMessageDialog(null,"Error: "+exception.getMessage(),"ERROR SQL",JOptionPane.ERROR_MESSAGE);
                    }
                }
                if (!usuarioEncontrado){
                    JOptionPane.showMessageDialog(null,"Ingrese datos correctos");
                    nombreTextField.setText("");
                    passwordField.setText("");
                }
            }
        });
    }
}
