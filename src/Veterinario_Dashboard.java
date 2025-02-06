import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Veterinario_Dashboard {
    private JPanel barraTop;
    private JButton cerrarButton;
    private JTable table1;
    public JPanel veterinarioDashboardPanel;
    private JButton cambiarEstadoButton;
    static JFrame veterinarioFrame = new JFrame("Veterinario Dashboard");
    int xMouse, yMouse;
    ConexionDB conexionDB = new ConexionDB();
    int idVet;

    public Veterinario_Dashboard(int idVet) {
        this.idVet = idVet;
        actualizarTabla();
        cerrarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
                veterinarioFrame.setLocation(x - xMouse,y - yMouse);
            }
        });
        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                veterinarioFrame.dispose();
                Pag1_Inicio.frame.setVisible(true);
            }
        });
        cambiarEstadoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Por favor, seleccione una cita para cambiar el estado.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int citaId = (int) table1.getValueAt(selectedRow, 0);
                String estadoActual = (String) table1.getValueAt(selectedRow, 3);

                // Verificar si la cita ya está completada o cancelada
                if (estadoActual.equals("completada") || estadoActual.equals("cancelada")) {
                    JOptionPane.showMessageDialog(null, "La cita ya ha sido " + estadoActual + ".", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Mostrar opciones de estado
                String[] opciones = {"completada", "cancelada"};
                String nuevoEstado = (String) JOptionPane.showInputDialog(
                        null,
                        "Seleccione el nuevo estado de la cita:",
                        "Cambiar Estado",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (nuevoEstado != null) {
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        PreparedStatement preparedStatement = connection.prepareStatement(
                                "UPDATE citas SET estado = ? WHERE cita_id = ?"
                        );
                        preparedStatement.setString(1, nuevoEstado);
                        preparedStatement.setInt(2, citaId);
                        int filasActualizadas = preparedStatement.executeUpdate();

                        if (filasActualizadas > 0) {
                            JOptionPane.showMessageDialog(null, "El estado de la cita ha sido actualizado a: " + nuevoEstado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            actualizarTabla();
                        } else {
                            JOptionPane.showMessageDialog(null, "Error al actualizar el estado de la cita.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        preparedStatement.close();
                        connection.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

    }
    public void actualizarTabla(){
        try {
            Connection connection = conexionDB.ConexionLocal();

            DefaultTableModel modelo = new DefaultTableModel();
            table1.setModel(modelo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT\n" +
                    "                        c.cita_id,\n" +
                    "                        c.fecha,\n" +
                    "                        c.hora,\n" +
                    "                        c.estado,\n" +
                    "                        m.nombre_mascota,\n" +
                    "                        s.nombre_serv AS servicio\n" +
                    "                    FROM citas c\n" +
                    "                    INNER JOIN mascotas m ON c.mascota_id = m.mascota_id\n" +
                    "                    INNER JOIN servicios s ON c.servicio_id = s.servicio_id\n" +
                    "                    INNER JOIN veterinarios v ON c.vet_id = v.vet_id\n" +
                    "                    WHERE v.vet_id = ?\n" +
                    "                    ORDER BY c.fecha DESC, c.hora DESC;");
            preparedStatement.setInt(1,this.idVet);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            int cantidadColumnas = resultSetMetaData.getColumnCount();

            modelo.addColumn("Cita ID");
            modelo.addColumn("Fecha");
            modelo.addColumn("Hora");
            modelo.addColumn("Estado");
            modelo.addColumn("Mascota");
            modelo.addColumn("Servicio");
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
