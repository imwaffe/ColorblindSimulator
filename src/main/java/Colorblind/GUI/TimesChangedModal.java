package Colorblind.GUI;

import javax.swing.*;

public class TimesChangedModal extends JOptionPane {
    public TimesChangedModal(JFrame owner){
        super("The only way to close this dialog is by\n"
                + "pressing one of the following buttons.\n"
                + "Do you understand?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
    }
}
