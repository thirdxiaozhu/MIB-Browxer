package Nms;

import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;

public class Advance {
    public JPanel mainDialog;
    private JTextField portField;
    private JTextField readComField;
    private JTextField writeComField;
    private JComboBox versionCombo;
    private JButton confirmButton;
    private JTextField usmField;
    private JPanel SnmpV3Panel;
    private JComboBox securityCombo;
    private JComboBox authAlgoCombo;
    private JTextField authPassField;
    private JComboBox privAlgoCombo;
    private JTextField privPassField;
    private JTextField contextNameField;

    public Advance(JFrame frame, MainForm mainForm){

        snmpv3ButtonState(false);
        confirmButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainForm.setPort(Integer.parseInt(portField.getText()));
                mainForm.setReadCom(readComField.getText());
                mainForm.setWriteCom(writeComField.getText());
                mainForm.setVersion(Objects.requireNonNull(versionCombo.getSelectedItem()).toString());
                mainForm.setAuthAlgo(authAlgoCombo.getSelectedIndex());
                mainForm.setPrivAlgo(privAlgoCombo.getSelectedIndex());
                mainForm.setAuthPass(authPassField.getText());
                mainForm.setPrivPass(privPassField.getText());
                mainForm.setUsername(usmField.getText());
                mainForm.setLevel(securityCombo.getSelectedIndex());
                mainForm.advanceConfirm();
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        versionCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                snmpv3ButtonState(versionCombo.getSelectedIndex() == 2);
            }
        });

        securityCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int index = securityCombo.getSelectedIndex();
                if(index == 0){
                    authAlgoCombo.setEnabled(true);
                    authPassField.setEnabled(true);
                    privAlgoCombo.setEnabled(true);
                    privPassField.setEnabled(true);
                }else if(index == 1){
                    authAlgoCombo.setEnabled(true);
                    authPassField.setEnabled(true);
                    privAlgoCombo.setEnabled(false);
                    privPassField.setEnabled(false);
                }else {
                    authAlgoCombo.setEnabled(false);
                    authPassField.setEnabled(false);
                    privAlgoCombo.setEnabled(false);
                    privPassField.setEnabled(false);
                }
            }
        });
    }

    private void snmpv3ButtonState(boolean state){
        this.SnmpV3Panel.setEnabled(state);
        this.usmField.setEnabled(state);
        this.securityCombo.setEnabled(state);
        this.authAlgoCombo.setEnabled(state);
        this.authPassField.setEnabled(state);
        this.privAlgoCombo.setEnabled(state);
        this.privPassField.setEnabled(state);
        this.contextNameField.setEnabled(state);
    }
}
