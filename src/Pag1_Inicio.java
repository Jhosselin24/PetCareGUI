import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class Pag1_Inicio {
    public JPanel login;
    private JButton LOGINButton;
    private JButton REGISTROButton;
    private JPanel barraTop;
    private JButton cerrarButton;
    static JFrame frame = new JFrame("CICLOSHOP");
    int xMouse,yMouse;

    public Pag1_Inicio() {
        cerrarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        LOGINButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        REGISTROButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
                frame.setLocation(x - xMouse,y - yMouse);
            }
        });
        REGISTROButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                if (!Pag2_Registro.frameRegistro.isUndecorated()){
                    Pag2_Registro.frameRegistro.setUndecorated(true);
                }
                Pag2_Registro.frameRegistro.setVisible(true);
                Pag2_Registro.frameRegistro.setContentPane(new Pag2_Registro().registro);
                Pag2_Registro.frameRegistro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Pag2_Registro.frameRegistro.setSize(450,580);
                Pag2_Registro.frameRegistro.setLocationRelativeTo(null);
            }
        });
        LOGINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                if (!Pag3_Login.frameLogin.isUndecorated()){
                    Pag3_Login.frameLogin.setUndecorated(true);
                }
                Pag3_Login.frameLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Pag3_Login.frameLogin.setContentPane(new Pag3_Login().baselogin);
                Pag3_Login.frameLogin.setSize(450,560);
                Pag3_Login.frameLogin.setVisible(true);
                Pag3_Login.frameLogin.setLocationRelativeTo(null);
            }
        });
        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
