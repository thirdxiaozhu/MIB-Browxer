package Nms;

import Util.SnmpUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class Set {
    public JPanel mainDialog;
    private JTextField oidField;
    private JButton confirmButton;
    private JButton cancelButton;
    private JTextField setField;
    private JComboBox typeCombo;
    private JFrame frame;
    private MainForm mainForm;
    private String oid;

    public Set(JFrame frame, MainForm mainForm, String oid){
        this.oid = oid;
        this.frame = frame;
        this.mainForm = mainForm;

        initDialog();
    }

    private void initDialog(){
        oidField.setText(oid);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainForm.snmpSetRequest(oidField.getText(), setField.getText(), typeCombo.getSelectedIndex());
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

}
