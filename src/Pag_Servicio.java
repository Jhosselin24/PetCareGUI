import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Pag_Servicio {
    public JPanel servicioPanel;
    private JTable table1;
    private JButton SELECCIONARSERVICIOButton;
    private JButton regresarButton;
    ConexionDB conexionDB=new ConexionDB();
    static JFrame servicioFrame = new JFrame("Seleccionar Servicio");
    private int idMascota;

    public Pag_Servicio(int idMascota) {
        actualizarTabla();
        this.idMascota = idMascota;
        SELECCIONARSERVICIOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = table1.getSelectedRow();
                if (filaSeleccionada != -1){
                    int idServicio = Integer.parseInt(table1.getValueAt(filaSeleccionada,0).toString());
                    servicioFrame.dispose();

                    Pag_Veterinario pagVeterinario = new Pag_Veterinario(idMascota, idServicio);

                    if (!pagVeterinario.veterinarioFrame.isUndecorated()){
                        pagVeterinario.veterinarioFrame.setUndecorated(true);
                    }
                    pagVeterinario.veterinarioFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    pagVeterinario.veterinarioFrame.setContentPane(pagVeterinario.veterinarioPanel);
                    pagVeterinario.veterinarioFrame.setSize(600,560);
                    pagVeterinario.veterinarioFrame.setVisible(true);
                    pagVeterinario.veterinarioFrame.setLocationRelativeTo(null);
                }else {
                    JOptionPane.showMessageDialog(null,"Seleccione un servicio","Fila no seleccionada",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                servicioFrame.dispose();
                Pag_Mascota.mascotaFrame.setVisible(true);
            }
        });
    }
    public void actualizarTabla(){
        try {
            Connection connection = conexionDB.ConexionLocal();

            DefaultTableModel modelo = new DefaultTableModel();
            table1.setModel(modelo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM servicios");
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            int cantidadColumnas = resultSetMetaData.getColumnCount();

            modelo.addColumn("Servicio ID");
            modelo.addColumn("Nombre");
            modelo.addColumn("Descripción");
            modelo.addColumn("Duración");
            modelo.addColumn("Costo");
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
