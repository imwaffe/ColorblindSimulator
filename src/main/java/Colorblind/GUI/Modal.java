package Colorblind.GUI;

import javax.swing.*;
import java.awt.*;

public class Modal extends JDialog {
    protected final JDialog dialog;
    private final JLabel message = new JLabel();
    private final JTextField textField = new JTextField(2);

    public Modal(Frame owner, String title, boolean modal){
        dialog = new JDialog(owner,title,modal);
        dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        dialog.setSize(200,300);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        Container cp = dialog.getContentPane();
        message.setText("How many times the image's colors changed?");
        cp.add(message);
        //cp.add(textField);
    }

    public Modal(Frame owner, String title){
        this(owner,title,true);
    }

    public void display(){
        dialog.validate();
        dialog.repaint();
        dialog.setVisible(true);
    }

    public void enableClose(){
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void disableClose(){
        dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
}
