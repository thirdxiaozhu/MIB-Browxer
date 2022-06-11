package Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * snmp协议工具类
 * 获取服务器cpu,内存,及硬盘占用
 */
public class SnmpUtil {
    private static Logger logger = LogManager.getLogger(SnmpUtil.class);
    private static Snmp snmp = null;
    private MyTableModel model;

    public SnmpUtil(MyTableModel model){
        this.model = model;
        try {
            if (snmp == null) {
                snmp = new Snmp(new DefaultUdpTransportMapping());
                snmp.listen();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static PDU createPDU(int version, int type){
        PDU pdu = null;
        if (version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        }else {
            pdu = new PDU();
        }
        pdu.setType(type);
        //可以添加多个变量oid
        return pdu;
    }

    private ResponseEvent snmpGet(Target target, String oid) {
        if(target == null){
            JOptionPane.showMessageDialog( null,"Not SNMP version1 or version2c!", "error!", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        PDU pdu = createPDU(target.getVersion(), PDU.GET);
        pdu.add(new VariableBinding(new OID(oid)));
        ResponseEvent re = null;
        try {
            re = snmp.get(pdu, target);
        } catch (Exception e) {
            logger.info("snmpGet 异常" + e.getMessage());
        }
        return re;
    }

    private ResponseEvent snmpSet(Target target, String oid, Variable var) {
        if(target == null){
            JOptionPane.showMessageDialog( null,"Not SNMP version1 or version2c!", "error!", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        PDU pdu = createPDU(target.getVersion(), PDU.SET);
        pdu.add(new VariableBinding(new OID(oid), var));
        ResponseEvent re = null;
        try {
            re = snmp.set(pdu, target);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("snmpGet 异常" + e.getMessage());
        }
        return re;
    }

    private List<TableEvent> snmpWalk(Target target, String oid) {
        TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
        OID[] columnOid = new OID[]{new OID(oid)};
        return utils.getTable(target, columnOid, null, null);
    }

    public List<String> getGateWayAddress(CommunityTarget target){
        List<TableEvent> list = snmpWalk(target, SnmpConfig.IP_INFO);
        List<String> ipList = new ArrayList<>();
        for(TableEvent t: list){
            ipList.add(t.toString().split("=")[3].split("]")[0].trim());
        }

        return ipList;
    }

    public List<String> getIPs(Target target, String oid){
        List<String> ipList = new ArrayList<>();
        if(target == null){
            JOptionPane.showMessageDialog( null,"Not SNMP version1 or version2c!", "error!", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        PDU pdu = createPDU(target.getVersion(), PDU.GETNEXT);
        pdu.add(new VariableBinding(new OID(oid)));
        boolean matched = true;
        while (matched) {
            try {
                ResponseEvent responseEvent = snmp.send(pdu, target);
                if (responseEvent == null || responseEvent.getResponse() == null) {
                    break;
                }
                PDU response = responseEvent.getResponse();
                String nextOid = null;
                List<? extends VariableBinding> variableBindings = response.getVariableBindings();
                for (VariableBinding v : variableBindings) {
                    nextOid = v.getOid().toDottedString();
                    if (!nextOid.startsWith(oid)) {
                        matched = false;
                        break;
                    }
                    if(SnmpConfig.CDPADDRESS.equals(oid)){
                        ipList.add(Util.hexIp2Decimal(v.getVariable().toString()));
                    }else {
                        ipList.add(v.getVariable().toString());
                    }
                }
                if (!matched) {
                    break;
                }
                pdu.clear();
                pdu.add(new VariableBinding(new OID(nextOid)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return ipList;
    }

    public void getTableWalkMsg(TreeNode.DeviceTreeNode devNod, String oid){
        Target target = devNod.getTarget();
        if(target == null){
            JOptionPane.showMessageDialog( null,"Not SNMP version1 or version2c!", "error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PDU pdu = createPDU(target.getVersion(), PDU.GETNEXT);
        pdu.add(new VariableBinding(new OID(oid)));
        boolean matched = true;
        while (matched) {
            try {
                ResponseEvent responseEvent = snmp.send(pdu, target);
                if (responseEvent == null || responseEvent.getResponse() == null) {
                    break;
                }
                PDU response = responseEvent.getResponse();
                String nextOid = null;
                List<? extends VariableBinding> variableBindings = response.getVariableBindings();
                for (VariableBinding v : variableBindings) {
                    nextOid = v.getOid().toDottedString();

                    if (!nextOid.startsWith(oid)) {
                        matched = false;
                        break;
                    }

                    String newOid = v.getOid().toString();
                    Object value = v.getVariable();
                    String mode = v.getVariable().getClass().toString().split("\\.")[3];
                    String ip = devNod.getIp();
                    model.addRow(new RowEntity(newOid, value, mode, ip));
                }
                if (!matched) {
                    break;
                }
                pdu.clear();
                pdu.add(new VariableBinding(new OID(nextOid)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String getSingalMsg(TreeNode.DeviceTreeNode devNod, String oid){
        ResponseEvent responseEvent = snmpGet(devNod.getTarget(), oid);
        if (responseEvent.getResponse() != null) {
            try {
                for(VariableBinding v: responseEvent.getResponse().getVariableBindings()){
                    String errorMsg = v.getVariable().toString();
                    System.out.println(errorMsg);
                    if("noSuchInstance".equals(errorMsg) || "noSuchObject".equals(errorMsg)){
                        return "error";
                    }
                    String newOid = v.getOid().toString();
                    Object value = v.getVariable();
                    String mode = v.getVariable().getClass().toString().split("\\.")[3];
                    String ip = devNod.getIp();
                    model.addRow(new RowEntity(newOid, value, mode, ip));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public void getInfos(TreeNode.DeviceTreeNode devNode,  TreeNode.OidTreeNode oidNode, String oid){
        switch (oidNode.getMode()){
            case TreeNode.OidTreeNode.SIGNAL -> getSingalMsg(devNode, oid);
            case TreeNode.OidTreeNode.MULTIPLE -> getTableWalkMsg(devNode, oid);
        }
    }

    public void setRequest(TreeNode.DeviceTreeNode devNode, String oid, String value, int index){
        Variable variable = null;
        switch (index){
            case 0 -> variable = new OctetString(value);
            case 1 -> variable = new Integer32(Integer.parseInt(value));
            case 2 -> variable = new OID(value);
            case 3 -> variable = new Gauge32(Long.parseLong(value));
            case 4 -> variable = new Counter32(Long.parseLong(value));
            case 5 -> variable = new IpAddress(value);
            case 6 -> variable = new TimeTicks(Long.parseLong(value));
            case 7 -> variable = new Counter64(Long.parseLong(value));
            case 8 -> variable = new UnsignedInteger32(Long.parseLong(value));
        }
        ResponseEvent responseEvent = snmpSet(devNode.getTarget(), oid, variable);
        if (responseEvent.getResponse() != null) {
            try {
                for(VariableBinding v: responseEvent.getResponse().getVariableBindings()){
                    String errorMsg = v.getVariable().toString();
                    System.out.println(errorMsg);
                    String newOid = v.getOid().toString();
                    Object valu = v.getVariable();
                    String mode = v.getVariable().getClass().toString().split("\\.")[3];
                    System.out.println(newOid + " " + valu + " " + mode);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void snmpAddUser(OctetString username, UsmUser user){
        snmp.getUSM().addUser(username, user);
    }

}