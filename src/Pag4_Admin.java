import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Pag4_Admin {
    JPanel admin_pag;
    private JButton cerrarButton;
    private JLabel logo;
    private JButton usuariosButton;
    private JButton veterinariosButton;
    private JPanel barraTop;
    static JFrame frameAdminP = new JFrame("CICLOSHOP");
    int xMouse, yMouse;

    public Pag4_Admin() {
        cerrarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        usuariosButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        veterinariosButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
                frameAdminP.setLocation(x - xMouse,y - yMouse);
            }
        });
        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAdminP.dispose();
                Pag1_Inicio.frame.setVisible(true);
            }
        });
        usuariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAdminP.setVisible(false);
                if (!Gestionar_Usuarios.gestionarUsuariosFrame.isUndecorated()){
                    Gestionar_Usuarios.gestionarUsuariosFrame.setUndecorated(true);
                }
                Gestionar_Usuarios.gestionarUsuariosFrame.setContentPane(new Gestionar_Usuarios().gestionarUsuariosPanel);
                Gestionar_Usuarios.gestionarUsuariosFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Gestionar_Usuarios.gestionarUsuariosFrame.setSize(900, 500);
                Gestionar_Usuarios.gestionarUsuariosFrame.setVisible(true);
                Gestionar_Usuarios.gestionarUsuariosFrame.setLocationRelativeTo(null);
            }
        });
        veterinariosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameAdminP.setVisible(false);
                if (!Gestionar_Veterinarios.gestionarVetFrame.isUndecorated()){
                    Gestionar_Veterinarios.gestionarVetFrame.setUndecorated(true);
                }
                Gestionar_Veterinarios.gestionarVetFrame.setContentPane(new Gestionar_Veterinarios().gestionarVetPanel);
                Gestionar_Veterinarios.gestionarVetFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Gestionar_Veterinarios.gestionarVetFrame.setSize(900, 500);
                Gestionar_Veterinarios.gestionarVetFrame.setVisible(true);
                Gestionar_Veterinarios.gestionarVetFrame.setLocationRelativeTo(null);
            }
        });

    }
}
