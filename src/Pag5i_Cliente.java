import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;

public class Pag5i_Cliente {
    private JTextField correoField;
    private JTable table1;
    private JButton SELECCIONARUSUARIOButton;
    private JButton CREARUSUARIOButton;
    private JTextField telefonoField;
    private JTextField nombreField;
    private JTextField direccionField;
    public JPanel clientePanel;
    private JButton regresarButton;
    private JPasswordField passwordField;
    private JTextField clienteidField;
    private JTextField apellidoField;
    ConexionDB conexionDB=new ConexionDB();
    static JFrame frameCliente = new JFrame("Clientes");

    public Pag5i_Cliente() {
        actualizarTabla();
        SELECCIONARUSUARIOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = table1.getSelectedRow();
                if (filaSeleccionada != -1){
                    int idCliente = Integer.parseInt(table1.getValueAt(filaSeleccionada,0).toString());
                    frameCliente.dispose();

                    // Modificación aquí: Pasar el idCliente al constructor
                    Pag_Mascota pagMascota = new Pag_Mascota(idCliente); // 1. Crear instancia con parámetro

                    if (!pagMascota.mascotaFrame.isUndecorated()){ // 2. Usar la instancia creada
                        pagMascota.mascotaFrame.setUndecorated(true);
                    }
                    pagMascota.mascotaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    pagMascota.mascotaFrame.setContentPane(pagMascota.clientePanel);
                    pagMascota.mascotaFrame.setSize(600,560);
                    pagMascota.mascotaFrame.setVisible(true);
                    pagMascota.mascotaFrame.setLocationRelativeTo(null);
                }else {
                    JOptionPane.showMessageDialog(null,"Seleccione un cliente","Fila no seleccionada",JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        CREARUSUARIOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nombreField.getText().isEmpty() || telefonoField.getText().isEmpty() || correoField.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Todos los campos deben estar completados");
                }else{
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO clientes(cliente_id, nombre_cln, apellido_cln, direccion_cln, celular_cln, email_cln, password_cln) VALUES (?,?,?,?,?,?,?)");
                        ps.setString(1,clienteidField.getText());
                        ps.setString(2,nombreField.getText());
                        ps.setString(3,apellidoField.getText());
                        ps.setString(4, direccionField.getText());
                        ps.setString(5, telefonoField.getText());
                        ps.setString(6, correoField.getText());
                        ps.setString(7, String.valueOf(passwordField.getPassword()));
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null,"Se han ingresado los datos correctamente");
                        actualizarTabla();
                        clienteidField.setText("");
                        nombreField.setText("");
                        apellidoField.setText("");
                        direccionField.setText("");
                        telefonoField.setText("");
                        correoField.setText("");
                        passwordField.setText("");
                    }catch (SQLException exception){
                        JOptionPane.showMessageDialog(null,exception.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameCliente.setVisible(false);
                Usuario_Dashboard.frameUsuarioDashboard.setVisible(true);
            }
        });
    }
    public void actualizarTabla(){
        try {
            Connection connection = conexionDB.ConexionLocal();

            DefaultTableModel modelo = new DefaultTableModel();
            table1.setModel(modelo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT cliente_id, nombre_cln, apellido_cln, direccion_cln, celular_cln, email_cln FROM clientes");
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            int cantidadColumnas = resultSetMetaData.getColumnCount();

            modelo.addColumn("Cliente ID");
            modelo.addColumn("Nombre");
            modelo.addColumn("Apellido");
            modelo.addColumn("Direccion");
            modelo.addColumn("Telefono");
            modelo.addColumn("Email");
            while (rs.next()){
                Object[] filas = new Object[cantidadColumnas];
                for (int i=0;i<cantidadColumnas;i++){
                    filas[i] = rs.getObject(i + 1);
                }
                modelo.addRow(filas);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
