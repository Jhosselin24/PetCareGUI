import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;

public class Pag4i1_Ingreso {
    public JPanel Ingreso;
    private JButton regresarButton;
    private JTable table1;
    private JButton modificarButton;
    private JButton ingresarButtonU;
    private JButton eliminarButton;
    private JTextField idField;
    private JLabel imagenLabel;
    private JTextField nombreField;
    private JTextField precioField;
    private JTextField stockField;
    private JButton subirImagenButton;
    private JButton limpiarButton;
    private JComboBox estadoCB;
    private JPanel barraTop;
    private JPanel background;
    static JFrame frameIngresoP = new JFrame("Ingreso de Productos");
    ConexionDB conexionDB = new ConexionDB();
    int xMouse,yMouse;

    public Pag4i1_Ingreso() {
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
                frameIngresoP.setLocation(x - xMouse,y - yMouse);
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
                frameIngresoP.setVisible(false);
                Pag4_Admin.frameAdminP.setVisible(true);
            }
        });
        ingresarButtonU.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = conexionDB.ConexionLocal();
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO PRODUCTOS(id_p, nombre, precio, stock, estado_p, imagen) VALUES (?,?,?,?,?,?)");
                    ps.setInt(1,Integer.parseInt(idField.getText()));
                    ps.setString(2, nombreField.getText());
                    ps.setDouble(3, Double.parseDouble(precioField.getText()));
                    ps.setInt(4, Integer.parseInt(stockField.getText()));
                    ps.setString(5, estadoCB.getSelectedItem().toString());
                    try {
                        byte[] imagenBytes = convertirIconoABytes(imagenLabel.getIcon());
                        ps.setBytes(6,imagenBytes);
                    }catch (NullPointerException npex){
                        ps.setString(6,"NULL");
                    }
                    ps.executeUpdate();
                    actualizarTabla();
                    JOptionPane.showMessageDialog(null,"Se han ingresado los datos correctamente");
                    limpiarCampos();
                }catch (SQLException | IOException exception){
                    JOptionPane.showMessageDialog(null,exception.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null,"Complete los campos");
                }
            }
        });
        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = conexionDB.ConexionLocal();
                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE PRODUCTOS\n" +
                            "SET id_p = ?, nombre = ?, precio = ?, stock = ?, estado_p = ?, imagen = ?\n" +
                            "WHERE id_p = ?");
                    preparedStatement.setInt(1, Integer.parseInt(idField.getText()));
                    preparedStatement.setString(2, nombreField.getText());
                    preparedStatement.setDouble(3, Double.parseDouble(precioField.getText()));
                    preparedStatement.setInt(4, Integer.parseInt(stockField.getText()));
                    preparedStatement.setString(5, estadoCB.getSelectedItem().toString());
                    try {
                        byte[] imagenBytes = convertirIconoABytes(imagenLabel.getIcon());
                        preparedStatement.setBytes(6,imagenBytes);
                    }catch (NullPointerException ex){
                        preparedStatement.setBytes(6,null);
                    }
                    preparedStatement.setInt(7, Integer.parseInt(table1.getValueAt(table1.getSelectedRow(),0).toString()));
                    preparedStatement.executeUpdate();
                    actualizarTabla();
                    JOptionPane.showMessageDialog(null,"Se han modificado los datos correctamente");
                    limpiarCampos();
                }catch (SQLException | IOException ex){
                    JOptionPane.showMessageDialog(null,"Error: "+ex.getMessage(),"SQL ERROR",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        subirImagenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filtro = new FileNameExtensionFilter("JPG, JPEG","jpg","jpeg");
                    fileChooser.setFileFilter(filtro);
                    int abrir = fileChooser.showOpenDialog(null);
                    if (abrir==JFileChooser.APPROVE_OPTION){
                        File archivo = fileChooser.getSelectedFile();
                        String ruta = archivo.getAbsolutePath();
                        Image image =  new ImageIcon(ruta).getImage();
                        Icon icon = new ImageIcon(image.getScaledInstance(imagenLabel.getWidth(), imagenLabel.getHeight(), Image.SCALE_DEFAULT));
                        imagenLabel.setIcon(icon);
                        JOptionPane.showMessageDialog(null,"Se ha seleccionado correctamente la foto");
                    }
                }catch (NullPointerException exception){
                    JOptionPane.showMessageDialog(null,exception.getMessage());
                }
            }
        });
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int filaSeleccionada = table1.getSelectedRow();
                    if(filaSeleccionada==-1){
                        JOptionPane.showMessageDialog(null,"Seleccione el producto que desee eliminar!","Producto no seleccionado",JOptionPane.ERROR_MESSAGE);
                    }else {
                        Connection connection = conexionDB.ConexionLocal();
                        int value = JOptionPane.showConfirmDialog(null,"Estas seguro que deseas eliminar el producto?","PRECAUCION",JOptionPane.OK_CANCEL_OPTION);
                        if (value!=2){
                            PreparedStatement ps = connection.prepareStatement("DELETE FROM PRODUCTOS WHERE id_p = ?");
                            ps.setInt(1, Integer.parseInt(table1.getValueAt(filaSeleccionada,0).toString()));
                            ps.executeUpdate();
                            actualizarTabla();
                            JOptionPane.showMessageDialog(null,"Se ha eliminado el producto correctamente");
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
        idField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                int key = e.getKeyChar();

                boolean numero = key>= 48 && key<= 57; // Comprueba que se ingresen del 0 al 9 ASCII

                if (!numero){
                    e.consume(); //Elimina un caracter que no sea tipo entero
                }
                // Validacion de que se ingrese un máximo de 10 dígitos
                if(idField.getText().trim().length() == 10){
                    e.consume();
                }
            }
        });
        precioField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                int key = e.getKeyChar();
                boolean numero = (key>= 48 && key<= 57);
                boolean punto = (key==46); //Punto en ASCII

                if(precioField.getText().isEmpty()){ // No puede empezar en .
                    if (punto){
                        e.consume();
                    }
                }
                if (!numero){
                    if (precioField.getText().contains(".")){ //Solo se puede ingresar un .
                        e.consume();
                    }else {
                        if (!punto){
                            e.consume();
                        }
                    }
                }
                if(precioField.getText().trim().length() == 6){
                    e.consume();
                }
            }
        });
        stockField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                int key = e.getKeyChar();

                boolean numero = key>= 48 && key<= 57;

                if (!numero){
                    e.consume();
                }

                if(stockField.getText().trim().length() == 3){
                    e.consume();
                }
            }
        });
    }

    private void obtenerDatos(int filaSeleccionada){
        try {
            Connection connection = conexionDB.ConexionLocal();
            String idFilaSeleccionada=table1.getValueAt(filaSeleccionada,0).toString();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PRODUCTOS WHERE id_p=?");
            ps.setInt(1,Integer.parseInt(idFilaSeleccionada));
            ResultSet result = ps.executeQuery();
            if (result.next()){
                idField.setText(idFilaSeleccionada);
                nombreField.setText(result.getString("nombre"));
                precioField.setText(String.valueOf(result.getDouble("precio")));
                stockField.setText(String.valueOf(result.getInt("stock")));
                if (String.valueOf(result.getString("estado_p")).equals("DISPONIBLE")){
                    estadoCB.setSelectedIndex(0);
                }else {
                    estadoCB.setSelectedIndex(1);
                }
                try{
                    Blob imagen = result.getBlob("imagen");
                    InputStream in = imagen.getBinaryStream();
                    BufferedImage image = ImageIO.read(in);
                    Icon icon = new ImageIcon(image.getScaledInstance(imagenLabel.getWidth(), imagenLabel.getHeight(), Image.SCALE_DEFAULT));
                    imagenLabel.setIcon(icon);
                }catch (Exception  ex){
                    imagenLabel.setIcon(null);
                }
            }
        }catch (SQLException ex){
            JOptionPane.showMessageDialog(null,"Error: "+ex.getMessage(),"SQL ERROR",JOptionPane.ERROR_MESSAGE);
        }
    }
    private static byte[] convertirIconoABytes(Icon icon) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (baos) {
            ImageIcon imageIcon = (ImageIcon) icon;
            Image image = imageIcon.getImage();
            BufferedImage bufferedImage = new BufferedImage(
                    image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
            ImageIO.write(bufferedImage, "png", baos);
        }
        return baos.toByteArray();
    }
    public void actualizarTabla(){
        try {
            DefaultTableModel modelo = new DefaultTableModel();
            table1.setModel(modelo);
            table1.setDefaultEditor(Object.class, null);
            table1.getTableHeader().setReorderingAllowed(false) ;

            Connection connection = conexionDB.ConexionLocal();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM PRODUCTOS");
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            int cantidadColumnas = resultSetMetaData.getColumnCount();

            modelo.addColumn("ID");
            modelo.addColumn("Nombre");
            modelo.addColumn("Precio");
            modelo.addColumn("Stock");
            modelo.addColumn("Estado");
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
        idField.setText("");
        nombreField.setText("");
        precioField.setText("");
        stockField.setText("");
        imagenLabel.setIcon(null);
        estadoCB.setSelectedIndex(0);
    }
}

