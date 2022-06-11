package Nms;

import Util.*;
import Util.TreeNode.OidTreeNode;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;

public class MainForm {
    public JPanel mainPanel;
    private JButton advancedButton;
    private JButton refreshButton;
    private JButton confirmButton;
    private JTextField hostField;
    private JTree mibTree;
    private JTree deviceTree;
    private JTable infoTable;
    private JButton emptyButton;
    private JButton findButton;
    private JComboBox oidCombo;
    private JButton updateButton;
    private JComboBox methodCombo;
    private JButton reduceButton;
    private JTextField findField;
    private OidTreeNode oidTop;
    private TreeNode.DeviceTreeNode deviceTop;
    private String host;
    private int port;
    private String toFind;
    private String readCom;
    private String writeCom;
    private String version;
    public SnmpUtil snmpUtil;
    private MyTableModel model;
    private TreeNode.DeviceTreeNode currentDevNode;
    private TreeNode.OidTreeNode currentOidNode;
    private TableRowSorter<TableModel> sorter;

    private int level;
    private int authAlgo;
    private int privAlgo;
    private String authPass;
    private String privPass;
    private String username;

    public MainForm(){
        model = new MyTableModel();
        snmpUtil = new SnmpUtil(model);
        initPanel();
        initTable();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        oidTop = new OidTreeNode("OID List", "1.3.6.1");
        mibTree = new JTree(oidTop);
        deviceTop = new TreeNode.DeviceTreeNode();
        deviceTree = new JTree(deviceTop);
    }

    private void initDeviceTree(){
        DeviceTopology deviceTopology = new DeviceTopology(snmpUtil, port, readCom, writeCom, version);
        deviceTopology.initNodes(deviceTop, host);

        currentDevNode = deviceTop;
    }


    private void initPanel(){
        this.advancedButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                host = hostField.getText();
                boolean isReachable = NetUtils.retryIPDetection(host, null, 5, 100, 0);

                if(!isReachable){
                    JOptionPane.showMessageDialog( mainPanel,"Invalid Host!", "Error!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFrame frame = new JFrame("Advance");
                //构建窗口
                frame.setContentPane(new Advance(frame, MainForm.this).mainDialog);
                frame.pack();
                frame.setVisible(true);
                //在屏幕中间显示
                frame.setLocationRelativeTo(null);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        advancedButton.setEnabled(true);
                    }
                });
                //禁止调整大小
                frame.setResizable(false);
                frame.setSize(600, 500);
                advancedButton.setEnabled(false);
            }
        });

        mibTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                StringBuilder nodeOid = new StringBuilder();
                if (!mibTree.isSelectionEmpty()) {// 查看是否存在被选中的节点
                    // 获得所有被选中节点的路径
                    TreePath selectionPath = mibTree.getSelectionPath();
                        // 以Object数组的形式返回该路径中所有节点的对象
                    Object[] path = selectionPath.getPath();
                    for (Object o : path) {
                        OidTreeNode node;// 获得节点
                        currentOidNode = (OidTreeNode) o;
                        nodeOid.append(currentOidNode.getID());
                    }
                    //oidCombo.setText(String.valueOf(nodePath));
                    oidCombo.getEditor().setItem(String.valueOf(nodeOid));
                    snmpUtil.getInfos(currentDevNode, currentOidNode, String.valueOf(nodeOid));

                }
            }
        });

        deviceTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (!deviceTree.isSelectionEmpty()) {// 查看是否存在被选中的节点
                    // 获得所有被选中节点的路径
                    TreePath selectionPath =  deviceTree.getSelectionPath();
                    // 以Object数组的形式返回该路径中所有节点的对象
                    assert selectionPath != null;
                    Object[] path = selectionPath.getPath();
                    currentDevNode = (TreeNode.DeviceTreeNode) path[path.length-1];
                }
            }
        });

        //
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oid = oidCombo.getEditor().getItem().toString();
                oidCombo.addItem(oidCombo.getEditor().getItem());
                //String error = snmpUtil.getSingalMsg(currentDevNode, oid);

                switch (methodCombo.getSelectedIndex()){
                    case 0 -> {
                        String error = snmpUtil.getSingalMsg(currentDevNode, oid);
                        if(error != null){
                            JOptionPane.showMessageDialog( mainPanel,"No Data!", "Warning!", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    case 1 -> snmpUtil.getTableWalkMsg(currentDevNode, oid);
                }
            }
        });

        //Empty the hole data table
        emptyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.removeAll();
                model.fireTableDataChanged();
            }
        });

        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toFind = findField.getText();
                if (toFind.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    //调用方法实现过滤内容
                    sorter.setRowFilter(RowFilter.regexFilter(toFind));
                }
            }
        });

        reduceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findField.setText("");
                toFind = "";
                sorter.setRowFilter(null);
            }
        });

        methodCombo.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    if(methodCombo.getSelectedIndex() == 2){
                        JFrame frame = new JFrame("Set");
                        //构建窗口
                        frame.setContentPane(new Set(frame, MainForm.this, oidCombo.getEditor().getItem().toString()).mainDialog);
                        frame.pack();
                        frame.setVisible(true);
                        //在屏幕中间显示
                        frame.setLocationRelativeTo(null);
                        //禁止调整大小
                        frame.setResizable(false);
                        frame.setSize(600, 300);
                        methodCombo.setSelectedIndex(0);
                    }
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deviceTop.removeAllChildren();
                deviceTree.updateUI();
                initDeviceTree();
            }
        });
    }

    private void initTable(){
        infoTable.setModel(model);
        infoTable.setRowSelectionAllowed(true);
        infoTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()){
                    oidCombo.getEditor().setItem(String.valueOf(infoTable.getValueAt(infoTable.getSelectedRow(),0)));
                }
            }
        });

        infoTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {

            }

        });

        JTableHeader tableHeader = infoTable.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 25));
        tableHeader.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        setColumnColor(infoTable);

        //创建可排序表对象
        sorter = new TableRowSorter<TableModel>(model);
        //将可排序表对象设置到表中
        infoTable.setRowSorter(sorter);
    }

    public void setColumnColor(JTable table) {
        try
        {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer(){
                @Serial
                private static final long serialVersionUID = 1L;
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                    if(row%2 == 0) {
                        setBackground(Color.WHITE);//设置奇数行底色
                    } else if(row%2 == 1) {
                        setBackground(new Color(220,230,241));//设置偶数行底色
                    }
                    return super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
                }
            };
            for(int i = 0; i < table.getColumnCount(); i++) {
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
            }
            tcr.setHorizontalAlignment(JLabel.CENTER);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void advanceConfirm(){
        try {
            if("1".equals(version) || "2c".equals(version)){
                initDeviceTree();
                deviceTree.setEnabled(true);
            }else {
                currentDevNode = new TreeNode.DeviceTreeNode(host, port, readCom, writeCom, version,
                        authPass, authAlgo, privPass, privAlgo, snmpUtil, username, level);
                deviceTree.setEnabled(false);
            }
            OidRelation.initNodes(oidTop);
            refreshButton.setEnabled(true);
        }catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog( mainPanel,"Invalid!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setReadCom(String readCom) {
        this.readCom = readCom;
    }

    public void setWriteCom(String writeCom) {
        this.writeCom = writeCom;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void snmpSetRequest(String oid, String value, int index){
        snmpUtil.setRequest(currentDevNode, oid, value, index);
    }

    public void setAuthAlgo(int authAlgo) {
        this.authAlgo = authAlgo;
    }

    public void setPrivAlgo(int privAlgo) {
        this.privAlgo = privAlgo;
    }

    public void setAuthPass(String authPass) {
        this.authPass = authPass;
    }

    public void setPrivPass(String privPass) {
        this.privPass = privPass;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLevel(int level){
        this.level = level;
    }
}
