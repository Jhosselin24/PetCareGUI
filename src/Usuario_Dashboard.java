import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Usuario_Dashboard {
    private JPanel barraTop;
    private JButton cerrarButton;
    private JButton citasButton;
    private JTable table1;
    public JPanel usuario_panel;
    private JButton refrescarButton;
    static JFrame frameUsuarioDashboard = new JFrame("Usuarios");
    int xMouse,yMouse;
    ConexionDB conexionDB = new ConexionDB();

    public Usuario_Dashboard() {
        actualizarTabla();
        cerrarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                frameUsuarioDashboard.dispose();
                Pag1_Inicio.frame.setVisible(true);
            }
        });
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
                frameUsuarioDashboard.setLocation(x - xMouse,y - yMouse);
            }
        });
        citasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameUsuarioDashboard.dispose();
                if (!Pag5i_Cliente.frameCliente.isUndecorated()){
                    Pag5i_Cliente.frameCliente.setUndecorated(true);
                }
                Pag5i_Cliente.frameCliente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Pag5i_Cliente.frameCliente.setContentPane(new Pag5i_Cliente().clientePanel);
                Pag5i_Cliente.frameCliente.setSize(600,560);
                Pag5i_Cliente.frameCliente.setVisible(true);
                Pag5i_Cliente.frameCliente.setLocationRelativeTo(null);
            }
        });
        refrescarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTabla();
            }
        });
    }
    public void actualizarTabla(){
        try {
            Connection connection = conexionDB.ConexionLocal();

            DefaultTableModel modelo = new DefaultTableModel();
            table1.setModel(modelo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT \n" +
                    "    c.cita_id,\n" +
                    "    c.fecha,\n" +
                    "    c.hora,\n" +
                    "    c.estado,\n" +
                    "    m.nombre_mascota,\n" +
                    "    s.nombre_serv AS servicio,\n" +
                    "    CONCAT(v.nombre_vet, ' ', v.apellido_vet) AS veterinario\n" +
                    "FROM citas c\n" +
                    "INNER JOIN mascotas m ON c.mascota_id = m.mascota_id\n" +
                    "INNER JOIN servicios s ON c.servicio_id = s.servicio_id\n" +
                    "INNER JOIN veterinarios v ON c.vet_id = v.vet_id\n" +
                    "ORDER BY c.fecha DESC, c.hora DESC;");
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            int cantidadColumnas = resultSetMetaData.getColumnCount();

            modelo.addColumn("Cita ID");
            modelo.addColumn("Fecha");
            modelo.addColumn("Hora");
            modelo.addColumn("Estado");
            modelo.addColumn("Mascota");
            modelo.addColumn("Servicio");
            modelo.addColumn("Veterinario");
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
