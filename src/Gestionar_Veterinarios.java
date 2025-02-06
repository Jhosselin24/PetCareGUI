import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Gestionar_Veterinarios {
    private JPanel background;
    private JTable table1;
    private JTextField nombreField;
    private JTextField apellidoField;
    private JButton eliminarButton;
    private JButton ingresarButtonU;
    private JPanel barraTop;
    private JButton regresarButton;
    private JButton limpiarButton;
    private JButton mostrarContraseñaButton;
    private JPasswordField passwordField;
    public JPanel gestionarVetPanel;
    private JTextField especialidadField;
    private JCheckBox lunesCheckBox;
    private JButton modificarButton;
    private JTextField horaInicioField;
    private JTextField horaFinField;
    private JCheckBox martesCheckBox;
    private JCheckBox miercolesCheckBox;
    private JCheckBox juevesCheckBox;
    private JCheckBox viernesCheckBox;
    static JFrame gestionarVetFrame = new JFrame("Gestionar Veterinaria");
    int xMouse, yMouse;
    ConexionDB conexionDB = new ConexionDB();

    public Gestionar_Veterinarios() {
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
                gestionarVetFrame.setLocation(x - xMouse,y - yMouse);
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
                gestionarVetFrame.setVisible(false);
                Pag4_Admin.frameAdminP.setVisible(true);
            }
        });
        ingresarButtonU.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = conexionDB.ConexionLocal();
                    String sql = "INSERT INTO veterinarios(nombre_vet, apellido_vet, especialidad, password_vet, " +
                            "lunes_inicio, lunes_fin, martes_inicio, martes_fin, miercoles_inicio, miercoles_fin, " +
                            "jueves_inicio, jueves_fin, viernes_inicio, viernes_fin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement ps = connection.prepareStatement(sql);

                    // Insertar datos comunes
                    ps.setString(1, nombreField.getText());
                    ps.setString(2, apellidoField.getText());
                    ps.setString(3, especialidadField.getText());
                    ps.setString(4, String.valueOf(passwordField.getPassword()));

                    // Insertar horarios por día
                    ps.setString(5, lunesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(6, lunesCheckBox.isSelected() ? horaFinField.getText() : null);
                    ps.setString(7, martesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(8, martesCheckBox.isSelected() ? horaFinField.getText() : null);
                    ps.setString(9, miercolesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(10, miercolesCheckBox.isSelected() ? horaFinField.getText() : null);
                    ps.setString(11, juevesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(12, juevesCheckBox.isSelected() ? horaFinField.getText() : null);
                    ps.setString(13, viernesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(14, viernesCheckBox.isSelected() ? horaFinField.getText() : null);

                    ps.executeUpdate();
                    actualizarTabla();
                    JOptionPane.showMessageDialog(null,"Se han ingresado los datos correctamente");
                    limpiarCampos();
                } catch (SQLException exception) {
                    JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = conexionDB.ConexionLocal();
                    String sql = "UPDATE veterinarios SET nombre_vet = ?, apellido_vet = ?, especialidad = ?, password_vet = ?, " +
                            "lunes_inicio = ?, lunes_fin = ?, martes_inicio = ?, martes_fin = ?, miercoles_inicio = ?, miercoles_fin = ?, " +
                            "jueves_inicio = ?, jueves_fin = ?, viernes_inicio = ?, viernes_fin = ? WHERE vet_id = ?";

                    PreparedStatement ps = connection.prepareStatement(sql);

                    // Insertar datos comunes
                    ps.setString(1, nombreField.getText());
                    ps.setString(2, apellidoField.getText());
                    ps.setString(3, especialidadField.getText());
                    ps.setString(4, String.valueOf(passwordField.getPassword()));

                    // Insertar horarios por día
                    ps.setString(5, lunesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(6, lunesCheckBox.isSelected() ? horaFinField.getText() : null);
                    ps.setString(7, martesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(8, martesCheckBox.isSelected() ? horaFinField.getText() : null);
                    ps.setString(9, miercolesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(10, miercolesCheckBox.isSelected() ? horaFinField.getText() : null);
                    ps.setString(11, juevesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(12, juevesCheckBox.isSelected() ? horaFinField.getText() : null);
                    ps.setString(13, viernesCheckBox.isSelected() ? horaInicioField.getText() : null);
                    ps.setString(14, viernesCheckBox.isSelected() ? horaFinField.getText() : null);

                    ps.setInt(15, Integer.parseInt(table1.getValueAt(table1.getSelectedRow(), 0).toString()));

                    ps.executeUpdate();
                    actualizarTabla();
                    JOptionPane.showMessageDialog(null, "Se han modificado los datos correctamente");
                    limpiarCampos();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "SQL ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int filaSeleccionada = table1.getSelectedRow();
                    if(filaSeleccionada==-1){
                        JOptionPane.showMessageDialog(null,"Seleccione el veterinario que desee eliminar!","Veterinario no seleccionado",JOptionPane.ERROR_MESSAGE);
                    }else {
                        Connection connection = conexionDB.ConexionLocal();
                        int value = JOptionPane.showConfirmDialog(null,"Estas seguro que deseas eliminar este veterinario?","PRECAUCION",JOptionPane.OK_CANCEL_OPTION);
                        if (value!=2){
                            PreparedStatement ps = connection.prepareStatement("DELETE FROM veterinarios WHERE vet_id = ?");
                            ps.setInt(1, Integer.parseInt(table1.getValueAt(filaSeleccionada,0).toString()));
                            ps.executeUpdate();
                            actualizarTabla();
                            JOptionPane.showMessageDialog(null,"Se ha eliminado el veterinario correctamente");
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
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM veterinarios WHERE vet_id=?");
            ps.setInt(1,Integer.parseInt(idFilaSeleccionada));
            ResultSet result = ps.executeQuery();
            if (result.next()){
                nombreField.setText(result.getString("nombre_vet"));
                apellidoField.setText(result.getString("apellido_vet"));
                especialidadField.setText(result.getString("especialidad"));
                passwordField.setText(result.getString("password_vet"));

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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT vet_id,nombre_vet,apellido_vet,especialidad FROM veterinarios");
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            int cantidadColumnas = resultSetMetaData.getColumnCount();

            modelo.addColumn("ID");
            modelo.addColumn("Nombre");
            modelo.addColumn("Apellido");
            modelo.addColumn("Especialidad");
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
        especialidadField.setText("");
        horaInicioField.setText("");
        horaFinField.setText("");
        passwordField.setText("");
        lunesCheckBox.setSelected(false);
        martesCheckBox.setSelected(false);
        miercolesCheckBox.setSelected(false);
        juevesCheckBox.setSelected(false);
        viernesCheckBox.setSelected(false);
    }

    private static JFormattedTextField createTimeField() {
        try {
            MaskFormatter timeFormatter = new MaskFormatter("##:##"); // Formato HH:mm
            timeFormatter.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(timeFormatter);
            field.setValue(new java.util.Date());
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
    private void createUIComponents() {
        horaInicioField = createTimeField();
        horaFinField = createTimeField();
    }
}
