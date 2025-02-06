import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;

public class Cliente_Dashboard {
    private JPanel barraTop;
    private JButton cerrarButton;
    private JTable table1;
    public JPanel cliente_dashboar_panel;
    private JButton historialCitasButton;
    private JLabel bienvenidaLabel;
    ConexionDB conexionDB = new ConexionDB();
    static JFrame frameClienteDashboard = new JFrame("Cliente Dashboard");
    int xMouse,yMouse;
    private int idCliente;
    private String nombreCliente;

    public Cliente_Dashboard(int idCliente, String nombreCliente) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        bienvenidaLabel.setText("Bienvenido/a, "+nombreCliente);

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
                frameClienteDashboard.setLocation(x - xMouse,y - yMouse);
            }
        });
        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameClienteDashboard.dispose();
                Pag1_Inicio.frame.setVisible(true);
            }
        });

        historialCitasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Document document = new Document();
                try {
                    Connection connection = conexionDB.ConexionLocal();
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "SELECT c.cita_id, c.fecha, c.hora, c.estado, m.nombre_mascota, s.nombre_serv AS servicio, " +
                                    "CONCAT(v.nombre_vet, ' ', v.apellido_vet) AS veterinario " +
                                    "FROM citas c " +
                                    "INNER JOIN mascotas m ON c.mascota_id = m.mascota_id " +
                                    "INNER JOIN servicios s ON c.servicio_id = s.servicio_id " +
                                    "INNER JOIN veterinarios v ON c.vet_id = v.vet_id " +
                                    "WHERE m.cliente_id = ? " +
                                    "ORDER BY c.fecha DESC, c.hora DESC"
                    );
                    preparedStatement.setString(1, String.valueOf(idCliente));
                    ResultSet rs = preparedStatement.executeQuery();

                    // Crear PDF
                    String filePath = "Historial_Citas_Cliente_" + idCliente + ".pdf";
                    PdfWriter.getInstance(document, new FileOutputStream(filePath));
                    document.open();

                    Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                    Font tableFont = new Font(Font.FontFamily.HELVETICA, 12);

                    document.add(new Paragraph("Historial de Citas - " + nombreCliente, titleFont));
                    document.add(new Paragraph("\n"));

                    PdfPTable table = new PdfPTable(6);
                    table.addCell(new PdfPCell(new Phrase("Cita ID", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Fecha", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Hora", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Estado", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Mascota", tableFont)));
                    table.addCell(new PdfPCell(new Phrase("Servicio", tableFont)));

                    while (rs.next()) {
                        table.addCell(String.valueOf(rs.getInt("cita_id")));
                        table.addCell(rs.getString("fecha"));
                        table.addCell(rs.getString("hora"));
                        table.addCell(rs.getString("estado"));
                        table.addCell(rs.getString("nombre_mascota"));
                        table.addCell(rs.getString("servicio"));
                    }

                    document.add(table);
                    document.close();

                    JOptionPane.showMessageDialog(null, "Historial generado con éxito en: " + filePath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al generar historial: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    public void actualizarTabla(){
        try {
            Connection connection = conexionDB.ConexionLocal();

            DefaultTableModel modelo = new DefaultTableModel();
            table1.setModel(modelo);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT c.cita_id, c.fecha, c.hora, m.nombre_mascota, s.nombre_serv AS servicio, " +
                            "CONCAT(v.nombre_vet, ' ', v.apellido_vet) AS veterinario " +
                            "FROM citas c " +
                            "INNER JOIN mascotas m ON c.mascota_id = m.mascota_id " +
                            "INNER JOIN servicios s ON c.servicio_id = s.servicio_id " +
                            "INNER JOIN veterinarios v ON c.vet_id = v.vet_id " +
                            "WHERE m.cliente_id = ? AND c.estado = 'programada' " +
                            "ORDER BY c.fecha DESC, c.hora DESC"
            );
            preparedStatement.setString(1, String.valueOf(idCliente));
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            int columnCount = metaData.getColumnCount();
            modelo.addColumn("Cita ID");
            modelo.addColumn("Fecha");
            modelo.addColumn("Hora");
            modelo.addColumn("Mascota");
            modelo.addColumn("Servicio");
            modelo.addColumn("Veterinario");

            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                modelo.addRow(rowData);
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar las citas pendientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
