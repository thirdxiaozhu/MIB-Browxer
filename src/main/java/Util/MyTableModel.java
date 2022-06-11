package Util;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MyTableModel extends DefaultTableModel {
    private List<RowEntity> results;
    private static final String[] heads = {"Name/OID", "Value", "Type", "IP"};

    public MyTableModel(){
        super(null, heads);
        results = new ArrayList<>();
    }


    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    public void addRow(RowEntity rowEntity) {

        this.results.add(rowEntity);
        Object[] rowdata = new Object[4];
        rowdata[0] = rowEntity.getOid();
        rowdata[1] = rowEntity.getValue();
        rowdata[2] = rowEntity.getType();
        rowdata[3] = rowEntity.getIp();
        super.addRow(rowdata);
    }

    public void removeAll(){
        getDataVector().removeAllElements();
        results.clear();
    }
}
