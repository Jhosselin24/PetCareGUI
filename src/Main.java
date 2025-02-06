import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        if (!Pag1_Inicio.frame.isUndecorated()){
            Pag1_Inicio.frame.setUndecorated(true);
        }
        Pag1_Inicio.frame.setContentPane(new Pag1_Inicio().login);
        Pag1_Inicio.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Pag1_Inicio.frame.setSize(500, 600);
        Pag1_Inicio.frame.setVisible(true);
        Pag1_Inicio.frame.setLocationRelativeTo(null);

    }
}