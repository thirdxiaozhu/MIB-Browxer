package Util;

import org.snmp4j.*;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNode{

    public static class OidTreeNode extends DefaultMutableTreeNode{
        public static final int NOTEND = 0;
        public static final int SIGNAL = 1;
        public static final int MULTIPLE = 2;

        private String ID;
        private int mode;
        private int type;

        public OidTreeNode(String title, String ID) {
            this(title, ID, NOTEND);
        }

        public OidTreeNode(String title, String ID, int mode) {
            setUserObject(title);
            this.ID = ID;
            this.mode = mode;
        }

        public String getID() {
            return ID;
        }


        public int getMode(){
            return mode;
        }
    }

    public static class DeviceTreeNode extends DefaultMutableTreeNode {
        private boolean isRooot = false;
        private boolean isEnd = false;

        private static final int NULL = 1000;

        private CommunityTarget communityTarget;
        private UserTarget userTarget;
        private String ip;
        private Integer port;
        private String readCom;
        private String writeCom;
        private String version;
        private int level;
        private String authPass;
        private OID authAlgo;
        private AuthenticationProtocol authProtocol;
        private String privPass;
        private OID privAlgo;
        private PrivacyProtocol privProtocol;
        private SnmpUtil snmpUtil;
        private String username;


        public DeviceTreeNode(){
            isRooot = true;
            communityTarget = null;
            setUserObject("Devices");
        }

        public DeviceTreeNode(String ip){
            isEnd = true;
            communityTarget = null;
            setUserObject(ip);
        }

        public DeviceTreeNode(String ip, Integer port, String readCom, String writeCom, String version){
            this.ip = ip;
            this.port = port;
            this.readCom = readCom;
            this.writeCom = writeCom;
            this.version = version;
            setUserObject(ip);
            initTarget();
        }

        public DeviceTreeNode(String ip, Integer port, String readCom, String writeCom,
                              String version, String authPass, int authAlgo, String privPass,
                              int privAlgo, SnmpUtil snmpUtil, String username, int level){
            this.ip = ip;
            this.port = port;
            this.readCom = readCom;
            this.writeCom = writeCom;
            this.version = version;
            this.authPass = authPass;
            this.privPass = privPass;
            this.level = level;

            setAuthAlgo(authAlgo);
            setPrivAlgo(privAlgo);

            this.snmpUtil = snmpUtil;
            this.username = username;
            setUserObject(ip);
            initTarget();
        }

        private void initTarget(){
            //初始化CommunityTarget
            switch (version){
                case "1" -> version1and2Init(SnmpConstants.version1);
                case "2c" -> version1and2Init(SnmpConstants.version2c);
                case "3" -> version3Init();
            }
        }
        private void version1and2Init(int version){
            communityTarget = new CommunityTarget();
            communityTarget.setCommunity(new OctetString(readCom));
            communityTarget.setVersion(version);
            communityTarget.setAddress(new UdpAddress(ip + "/" + port));
            communityTarget.setTimeout(1000);
            communityTarget.setRetries(1);
            System.out.println(DeviceTreeNode.this);
        }


        private void version3Init(){
            SecurityProtocols securityProtocols = SecurityProtocols.getInstance();
            securityProtocols.addAuthenticationProtocol(this.authProtocol);
            securityProtocols.addPrivacyProtocol(this.privProtocol);
            USM usm = new USM(securityProtocols, new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);

            // Add User
            UsmUser user = new UsmUser(
                    new OctetString(this.username),
                    this.authAlgo, new OctetString(this.authPass),
                    this.privAlgo, new OctetString(this.privPass));
            //If the specified SNMP engine id is specified, this user can only be used with the specified engine ID
            //So if it's not correct, will get an error that can't find a user from the user table.
            //snmp.getUSM().addUser(new OctetString("nmsAdmin"), new OctetString("0002651100"), user);
            snmpUtil.snmpAddUser(new OctetString(this.username), user);

            userTarget = new UserTarget();
            userTarget.setVersion(SnmpConstants.version3);
            userTarget.setAddress(new UdpAddress(ip + "/" + port));
            if(this.level == 2){
                System.out.println("aaa");
                userTarget.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
            }else if(this.level == 1){
                System.out.println("bbb");
                userTarget.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
            }else{
                System.out.println("ccc");
                userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            }

            userTarget.setSecurityName(new OctetString(this.username));
            userTarget.setTimeout(3000);    //3s
            userTarget.setRetries(0);
        }

        private void setAuthAlgo(int index){
            switch (index){
                case 0 -> {
                    this.authAlgo = AuthMD5.ID;
                    this.authProtocol = new AuthMD5();
                }
                case 1 ->{
                    this.authAlgo = AuthSHA.ID;
                    this.authProtocol = new AuthSHA();
                }
                case 2 ->{
                    this.authAlgo = AuthHMAC192SHA256.ID;
                    this.authProtocol = new AuthHMAC192SHA256();
                }
                case 3 ->{
                    this.authAlgo = AuthHMAC384SHA512.ID;
                    this.authProtocol = new AuthHMAC384SHA512();
                }
            }
        }

        private void setPrivAlgo(int index){
            switch (index){
                case 0 ->{
                    this.privAlgo = PrivAES128.ID;
                    this.privProtocol = new PrivAES128();
                }
                case 1 ->{
                    this.privAlgo = PrivDES.ID;
                    this.privProtocol = new PrivDES();
                }
                case 2 ->{
                    this.privAlgo = Priv3DES.ID;
                    this.privProtocol = new Priv3DES();
                }
            }
        }


        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }


        public Target getTarget() {
            System.out.println(communityTarget + " " + userTarget);
            if(communityTarget != null){
                return communityTarget;
            }
            return userTarget;
        }

        public void setTarget(CommunityTarget target) {
            this.communityTarget = target;
        }

    }
}
