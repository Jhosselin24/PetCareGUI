import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Pag_Veterinario {
    public JPanel veterinarioPanel;
    private JTable table1;
    private JButton AGENDARCITAButton;
    private JButton regresarButton;
    private JFormattedTextField fechaField;
    private JButton comprobarButton;
    private JFormattedTextField horaField;
    ConexionDB conexionDB = new ConexionDB();
    private int idMascota;
    private int idServicio;
    static JFrame veterinarioFrame = new JFrame("Seleccionar Veterinario");

    public Pag_Veterinario(int idMascota, int idServicio) {
        this.idMascota = idMascota;
        this.idServicio = idServicio;

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Nombre", "Apellido"});
        table1.setModel(model);

        comprobarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Formato de fecha y hora sin segundos
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date fechaHora = sdf.parse(fechaField.getText() + " " + horaField.getText());

                    List<Veterinario> disponibles = obtenerVeterinariosDisponibles(
                            fechaHora,
                            new Time(fechaHora.getTime()),
                            idServicio
                    );

                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    model.setRowCount(0);
                    for (Veterinario vet : disponibles) {
                        model.addRow(new Object[]{
                                vet.getId(),
                                vet.getNombre(),
                                vet.getApellido()
                        });
                    }

                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(veterinarioFrame,
                            "Formato de fecha/hora inválido. Use YYYY-MM-DD y HH:mm",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(veterinarioFrame,
                            "Error al consultar veterinarios: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        AGENDARCITAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = table1.getSelectedRow();
                if (filaSeleccionada != -1){
                    int idVeterinario = Integer.parseInt(table1.getValueAt(filaSeleccionada,0).toString());
                    try {
                        Connection connection = conexionDB.ConexionLocal();
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO citas (fecha, hora, estado, mascota_id, servicio_id, vet_id) VALUES (?,?,?,?,?,?)");
                        ps.setString(1,fechaField.getText());
                        ps.setString(2,horaField.getText());
                        ps.setString(3,"programada");
                        ps.setString(4, String.valueOf(idMascota));
                        ps.setString(5, String.valueOf(idServicio));
                        ps.setString(6, String.valueOf(idVeterinario));
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null,"Se han ingresado los datos correctamente");
                        veterinarioFrame.dispose();
                        Usuario_Dashboard.frameUsuarioDashboard.setVisible(true);
                    }catch (SQLException exception){
                        JOptionPane.showMessageDialog(null,exception.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });
        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                veterinarioFrame.dispose();
                Pag_Servicio.servicioFrame.setVisible(true);
            }
        });
    }

    private static JFormattedTextField createDateField() {
        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(dateFormatter);
            field.setValue(new Date());
            field.setColumns(10);

            field.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() {
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return new JFormattedTextField.AbstractFormatter() {
                        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        @Override
                        public Object stringToValue(String text) throws ParseException {
                            return dateFormat.parse(text);
                        }

                        @Override
                        public String valueToString(Object value) {
                            if (value != null) {
                                return dateFormat.format((Date) value);
                            }
                            return "";
                        }
                    };
                }
            });

            return field;
        } catch (ParseException e) {
            throw new RuntimeException("Error en formato de fecha", e);
        }
    }

    private static JFormattedTextField createTimeField() {
        try {
            MaskFormatter timeFormatter = new MaskFormatter("##:##"); // Formato HH:mm
            timeFormatter.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(timeFormatter);
            field.setValue(new Date());
            field.setColumns(8);

            field.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() {
                @Override
                public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return new JFormattedTextField.AbstractFormatter() {
                        private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // Formato sin segundos

                        @Override
                        public Object stringToValue(String text) throws ParseException {
                            return timeFormat.parse(text);
                        }

                        @Override
                        public String valueToString(Object value) {
                            if (value != null) {
                                return timeFormat.format((Date) value);
                            }
                            return "";
                        }
                    };
                }
            });

            return field;
        } catch (ParseException e) {
            throw new RuntimeException("Error en formato de hora", e);
        }
    }

    public List<Veterinario> obtenerVeterinariosDisponibles(Date fecha, Time hora, int servicioId) throws SQLException {
        List<Veterinario> veterinarios = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);

        // Selecciona la columna de horarios según el día
        String columnaHorarioInicio = switch (diaSemana) {
            case Calendar.MONDAY -> "v.lunes_inicio";
            case Calendar.TUESDAY -> "v.martes_inicio";
            case Calendar.WEDNESDAY -> "v.miercoles_inicio";
            case Calendar.THURSDAY -> "v.jueves_inicio";
            case Calendar.FRIDAY -> "v.viernes_inicio";
            default -> throw new IllegalStateException("Día de la semana inválido");
        };

        String columnaHorarioFin = columnaHorarioInicio.replace("_inicio", "_fin"); // Genera la columna de fin

        // Reemplaza dinámicamente las columnas en la consulta
        String sql = "SELECT v.vet_id, v.nombre_vet, v.apellido_vet " +
                "FROM veterinarios v " +
                "WHERE ? BETWEEN " + columnaHorarioInicio + " AND " + columnaHorarioFin + " " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM citas c " +
                "    WHERE c.vet_id = v.vet_id " +
                "    AND c.fecha = ? " +
                "    AND ( " +
                "        (? BETWEEN c.hora AND ADDTIME(c.hora, (SELECT SEC_TO_TIME(s.duracion * 60) FROM servicios s WHERE s.servicio_id = ?))) " +
                "        OR " +
                "        (ADDTIME(?, SEC_TO_TIME((SELECT s.duracion * 60 FROM servicios s WHERE s.servicio_id = ?))) BETWEEN c.hora  " +
                "        AND ADDTIME(c.hora, SEC_TO_TIME((SELECT s.duracion * 60 FROM servicios s WHERE s.servicio_id = c.servicio_id)))) " +
                "    ) " +
                ");";

        try (Connection connection = conexionDB.ConexionLocal();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Parámetro 1: Hora de la cita (aquí usamos 'hora' como Time)
            stmt.setTime(1, new java.sql.Time(hora.getTime())); // Asegúrate de que 'hora' es de tipo java.util.Date o java.sql.Time
            System.out.println("Hora de la cita: " + new java.sql.Time(hora.getTime()));

            // Parámetro 2: Fecha de la cita
            stmt.setDate(2, new java.sql.Date(fecha.getTime()));
            System.out.println("Fecha de la cita: " + new java.sql.Date(fecha.getTime()));

            // Parámetros 3: Hora (misma que el parámetro 1)
            stmt.setTime(3, new java.sql.Time(hora.getTime()));

            // Parámetro 4: ID del servicio
            stmt.setInt(4, servicioId);

            // Parámetro 5: Hora (misma que los anteriores)
            stmt.setTime(5, new java.sql.Time(hora.getTime()));

            // Parámetro 6: ID del servicio (misma lógica que en el parámetro 4)
            stmt.setInt(6, servicioId);

            // Ejecutar consulta y procesar resultados...
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID Veterinario: " + rs.getInt("vet_id"));
                    Veterinario vet = new Veterinario();
                    vet.setId(rs.getInt("vet_id"));
                    vet.setNombre(rs.getString("nombre_vet"));
                    vet.setApellido(rs.getString("apellido_vet"));
                    veterinarios.add(vet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(veterinarioFrame,
                    "Error al consultar veterinarios: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return veterinarios;
    }

    private void createUIComponents() {
        fechaField = createDateField();
        horaField = createTimeField();
    }
}