import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.*;

public class Gestionar_Usuarios {
    private JPanel background;
    private JTable table1;
    private JTextField nombreField;
    private JTextField apellidoField;
    private JButton eliminarButton;
    private JButton mostrarContraseñaButton;
    private JButton ingresarButtonU;
    private JButton modificarButton;
    private JButton limpiarButton;
    private JPanel barraTop;
    private JButton regresarButton;
    private JPasswordField passwordField;
    public JPanel gestionarUsuariosPanel;
    static JFrame gestionarUsuariosFrame = new JFrame("Gestionar Usuarios");
    int xMouse, yMouse;
    ConexionDB conexionDB = new ConexionDB();

    public Gestionar_Usuarios() {
        regresarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        barraTop.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xMouse = e.getX();
                yMouse = e.getY();
            }
        });
        barraTop.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                gestionarUsuariosFrame.setLocation(x - xMouse,y - yMouse);
            }
        });
        table1.setDefaultEditor(Object.class, null);
        actualizarTabla();
        table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int filaSeleccionada = table1.getSelectedRow();
                    if (filaSeleccionada != -1) {
                        obtenerDatos(filaSeleccionada);
                    }
                }
            }
        });

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestionarUsuariosFrame.setVisible(false);
                Pag4_Admin.frameAdminP.setVisible(true);
            }
        });
        ingresarButtonU.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = conexionDB.ConexionLocal();
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO usuarios(nombre_usr, apellido_usr, password_usr) VALUES (?,?,?)");
                    ps.setString(1, nombreField.getText());
                    ps.setString(2, apellidoField.getText());
                    ps.setString(3, String.valueOf(passwordField.getPassword()));

                    ps.executeUpdate();
                    actualizarTabla();
                    JOptionPane.showMessageDialog(null,"Se han ingresado los datos correctamente");
                    limpiarCampos();
                }catch (SQLException exception){
                    JOptionPane.showMessageDialog(null,exception.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = conexionDB.ConexionLocal();
                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE usuarios\n" +
                            "SET nombre_usr = ?, apellido_usr = ?, password_usr = ?\n" +
                            "WHERE user_id = ?");
                    preparedStatement.setString(1, nombreField.getText());
                    preparedStatement.setString(2, apellidoField.getText());
                    preparedStatement.setString(3, String.valueOf(passwordField.getPassword()));
                    preparedStatement.setInt(4, Integer.parseInt(table1.getValueAt(table1.getSelectedRow(),0).toString()));

                    preparedStatement.executeUpdate();
                    actualizarTabla();
                    JOptionPane.showMessageDialog(null,"Se han modificado los datos correctamente");
                    limpiarCampos();
                }catch (SQLException ex){
                    JOptionPane.showMessageDialog(null,"Error: "+ex.getMessage(),"SQL ERROR",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int filaSeleccionada = table1.getSelectedRow();
                    if(filaSeleccionada==-1){
                        JOptionPane.showMessageDialog(null,"Seleccione el usuario que desee eliminar!","Usuario no seleccionado",JOptionPane.ERROR_MESSAGE);
                    }else {
                        Connection connection = conexionDB.ConexionLocal();
                        int value = JOptionPane.showConfirmDialog(null,"Estas seguro que deseas eliminar el usuario?","PRECAUCION",JOptionPane.OK_CANCEL_OPTION);
                        if (value!=2){
                            PreparedStatement ps = connection.prepareStatement("DELETE FROM usuarios WHERE user_id = ?");
                            ps.setInt(1, Integer.parseInt(table1.getValueAt(filaSeleccionada,0).toString()));
                            ps.executeUpdate();
                            actualizarTabla();
                            JOptionPane.showMessageDialog(null,"Se ha eliminado el usuario correctamente");
                            limpiarCampos();
                        }
                    }
                }catch (SQLException sqlException){
                    JOptionPane.showMessageDialog(null,"Error: "+sqlException.getMessage(),"SQL ERROR",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        mostrarContraseñaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(passwordField.getEchoChar());
                if (passwordField.getEchoChar() == '•'){
                    passwordField.setEchoChar('\0');
                    mostrarContraseñaButton.setText("Ocultar Contraseña");
                } else {
                    passwordField.setEchoChar('•');
                    mostrarContraseñaButton.setText("Mostrar Contraseña");
                }
            }
        });
    }

    private void obtenerDatos(int filaSeleccionada){
        try {
            Connection connection = conexionDB.ConexionLocal();
            String idFilaSeleccionada=table1.getValueAt(filaSeleccionada,0).toString();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM usuarios WHERE user_id=?");
            ps.setInt(1,Integer.parseInt(idFilaSeleccionada));
            ResultSet result = ps.executeQuery();
            if (result.next()){
                nombreField.setText(result.getString("nombre_usr"));
                apellidoField.setText(result.getString("apellido_usr"));
                passwordField.setText(result.getString("password_usr"));
            }
        }catch (SQLException ex){
            JOptionPane.showMessageDialog(null,"Error: "+ex.getMessage(),"SQL ERROR",JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarTabla(){
        try {
            DefaultTableModel modelo = new DefaultTableModel();
            table1.setModel(modelo);
            table1.setDefaultEditor(Object.class, null);
            table1.getTableHeader().setReorderingAllowed(false) ;

            Connection connection = conexionDB.ConexionLocal();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM usuarios");
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            int cantidadColumnas = resultSetMetaData.getColumnCount();

            modelo.addColumn("ID");
            modelo.addColumn("Nombre");
            modelo.addColumn("Apellido");
            while (rs.next()){
                Object[] filas = new Object[cantidadColumnas];
                for (int i=0;i<cantidadColumnas;i++){
                    filas[i] = rs.getObject(i + 1);
                }
                modelo.addRow(filas);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }catch (NullPointerException ignored){}
    }
    private void limpiarCampos(){
        nombreField.setText("");
        apellidoField.setText("");
        passwordField.setText("");
    }
}
