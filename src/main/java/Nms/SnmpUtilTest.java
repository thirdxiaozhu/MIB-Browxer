package Nms;

import Util.SnmpConfig;
import Util.SnmpUtil;
import Util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * snmp协议工具类
 * 获取服务器cpu,内存,及硬盘占用
 */
public class SnmpUtilTest {
    private static Logger logger = LogManager.getLogger(SnmpUtilTest.class);
    private static Snmp snmp = null;
    private CommunityTarget target;

    @SuppressWarnings("squid:S3010")
    public SnmpUtilTest(String intranetDeviceIp, Integer snmpPort) throws IOException {
        if (snmp == null) {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            snmp.listen();
        }
        //初始化CommunityTarget
        target = new CommunityTarget();
        target.setCommunity(new OctetString("jiaxv"));
        target.setVersion(SnmpConstants.version2c);
        target.setAddress(new UdpAddress(intranetDeviceIp + "/" + snmpPort));
        target.setTimeout(1000);
        target.setRetries(1);
    }

    private PDU createPDU(int version, int type, String oid){
        PDU pdu = null;
        if (version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        }else {
            pdu = new PDU();
        }
        pdu.setType(type);
        //可以添加多个变量oid
        pdu.add(new VariableBinding(new OID(oid)));
        return pdu;
    }

    private ResponseEvent snmpGet(String oid) {
        PDU pdu = createPDU(target.getVersion(), PDU.GET, oid);
        ResponseEvent re = null;
        try {
            re = snmp.send(pdu, target);
        } catch (Exception e) {
            logger.info("snmpGet 异常" + e.getMessage());
        }
        return re;
    }

    private void snmpWalk(String oid) {
        TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
        OID[] columnOid = new OID[]{new OID(oid)};
        List<TableEvent> list = utils.getTable(target, columnOid, null, null);
        for(TableEvent t: list){
            System.out.println(t.toString().split("=")[3].split("]")[0].trim());
        }
    }

    public void getTable(String oid){
        snmpWalk(oid);
    }

    public void getSimple(){
        ResponseEvent responseEvent = snmpGet("1.3.6.1.2.1.1.3.0");
        if (responseEvent != null && responseEvent.getResponse() != null) {
            try {
                for(VariableBinding r: responseEvent.getResponse().getVariableBindings()){
                    System.out.println(r.getVariable());
                    System.out.println(((TimeTicks)r.getVariable()).getValue());
                }
                System.out.println((responseEvent.getResponse().toString().split("=")[4].split("]")[0].trim()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public List<String> getIPs(String oid){
        List<String> ipList = new ArrayList<>();
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

    private PDU createPDU(int version, int getnext) {
        PDU pdu = null;
        if (version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        }else {
            pdu = new PDU();
        }
        pdu.setType(getnext);
        //可以添加多个变量oid
        return pdu;
    }


    public static void main(String[] args) throws IOException {
        String snmpIp = "192.168.122.2";
        Integer snmpPort = 161;
        SnmpUtilTest snmpUtil = new SnmpUtilTest(snmpIp, snmpPort);

        String oid = SnmpConfig.IP_INFO;

        snmpUtil.getTable(oid);

        System.out.println("--------");
        List<String> l =  snmpUtil.getIPs(oid);
        for(String s:l){
            System.out.println(s);
        }
    }
}