import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Pag_Mascota extends JDialog {
    private JPanel contentPane;
    public JPanel clientePanel;
    private JTextField especieField;
    private JTextField razaField;
    private JTextField fechaField;
    private JTable table1;
    private JButton SELECCIONARMASCOTAButton;
    private JTextField nombreField;
    private JButton regresarButton;
    private JButton CREARMASCOTAButton;
    private int idCliente;
    ConexionDB conexionDB=new ConexionDB();
    static JFrame mascotaFrame = new JFrame("Seleccionar Mascota");

    public Pag_Mascota(int idCliente) {
        this.idCliente = idCliente;
        actualizarTabla();
        SELECCIONARMASCOTAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = table1.getSelectedRow();
                if (filaSeleccionada != -1){
                    int idMascota = Integer.parseInt(table1.getValueAt(filaSeleccionada,0).toString());
                    mascotaFrame.dispose();

                    Pag_Servicio pagServicio = new Pag_Servicio(idMascota);

                    if (!pagServicio.servicioFrame.isUndecorated()){
                        pagServicio.servicioFrame.setUndecorated(true);
                    }
                    pagServicio.servicioFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    pagServicio.servicioFrame.setContentPane(pagServicio.servicioPanel);
                    pagServicio.servicioFrame.setSize(600,560);
                    pagServicio.servicioFrame.setVisible(true);
                    pagServicio.servicioFrame.setLocationRelativeTo(null);
                }else {
                    JOptionPane.showMessageDialog(null,"Selecciona una mascota","Fila no seleccionada",JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        CREARMASCOTAButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (nombreField.getText().isEmpty() || razaField.getText().isEmpty() || especieField.getText().isEmpty() || fechaField.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Todos los campos deben estar completados");
                }else{
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO mascotas (nombre_mascota, especie, raza, fecha_nacimiento, cliente_id) VALUES (?,?,?,?,?)");
                        ps.setString(1,nombreField.getText());
                        ps.setString(2,especieField.getText());
                        ps.setString(3,razaField.getText());
                        ps.setString(4, fechaField.getText());
                        ps.setString(5, String.valueOf(idCliente));
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null,"Se han ingresado los datos correctamente");
                        actualizarTabla();
                        nombreField.setText("");
                        especieField.setText("");
                        razaField.setText("");
                        fechaField.setText("");
                    }catch (SQLException exception){
                        JOptionPane.showMessageDialog(null,exception.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mascotaFrame.setVisible(false);
                Pag5i_Cliente.frameCliente.setVisible(true);
            }
        });
    }
    public void actualizarTabla(){
        try {
            Connection connection = conexionDB.ConexionLocal();

            DefaultTableModel modelo = new DefaultTableModel();
            table1.setModel(modelo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM mascotas where cliente_id = ?");
            preparedStatement.setInt(1,this.idCliente);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            int cantidadColumnas = resultSetMetaData.getColumnCount();

            modelo.addColumn("ID Mascota");
            modelo.addColumn("Nombre");
            modelo.addColumn("Especie");
            modelo.addColumn("Raza");
            modelo.addColumn("Fecha de Nacimiento");
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
