package Util;

public class OidRelation {

    public static void initNodes(TreeNode.OidTreeNode root){
        TreeNode.OidTreeNode mgmt = new TreeNode.OidTreeNode("mgmt", ".2");
        root.add(mgmt);
        TreeNode.OidTreeNode snmpv2 = new TreeNode.OidTreeNode("snmpv2", ".6");
        root.add(snmpv2);

        mgmt.add(initMib2());
        snmpv2.add(initSnmpModule());
    }

    private static TreeNode.OidTreeNode initMib2(){
        TreeNode.OidTreeNode mib2 = new TreeNode.OidTreeNode("mib2", ".1");
        mib2.add(initSystem());
        mib2.add(initInterfaces());
        mib2.add(initAt());
        mib2.add(initIp());
        return mib2;
    }

    private static TreeNode.OidTreeNode initSnmpModule(){
        TreeNode.OidTreeNode snmpmodule = new TreeNode.OidTreeNode("snmpmodule", ".3");
        //snmpmodule.add(initSystem());
        return snmpmodule;
    }

    private static TreeNode.OidTreeNode initSystem(){
        TreeNode.OidTreeNode system = new TreeNode.OidTreeNode("system", ".1");
        system.add(new TreeNode.OidTreeNode("sysDescr", ".1.0", TreeNode.OidTreeNode.SIGNAL));
        system.add(new TreeNode.OidTreeNode("sysObjID", ".2.0", TreeNode.OidTreeNode.SIGNAL));
        system.add(new TreeNode.OidTreeNode("sysUpTime", ".3.0", TreeNode.OidTreeNode.SIGNAL));
        system.add(new TreeNode.OidTreeNode("sysName", ".5.0", TreeNode.OidTreeNode.SIGNAL));
        return system;
    }

    private static TreeNode.OidTreeNode initInterfaces(){
        TreeNode.OidTreeNode interfaces = new TreeNode.OidTreeNode("interfaces", ".2");
        interfaces.add(new TreeNode.OidTreeNode("number", ".1.0", TreeNode.OidTreeNode.SIGNAL));
        interfaces.add(new TreeNode.OidTreeNode("ifDescr", ".2.1.2", TreeNode.OidTreeNode.MULTIPLE));
        interfaces.add(new TreeNode.OidTreeNode("ifType", ".2.1.3", TreeNode.OidTreeNode.MULTIPLE));
        interfaces.add(new TreeNode.OidTreeNode("ifMtu", ".2.1.4", TreeNode.OidTreeNode.MULTIPLE));
        interfaces.add(new TreeNode.OidTreeNode("ifAdminStatus", ".2.1.7", TreeNode.OidTreeNode.MULTIPLE));
        interfaces.add(new TreeNode.OidTreeNode("iflastChange", ".2.1.9", TreeNode.OidTreeNode.MULTIPLE));
        return interfaces;
    }

    private static TreeNode.OidTreeNode initAt(){
        TreeNode.OidTreeNode at = new TreeNode.OidTreeNode("at", ".3");
        at.add(new TreeNode.OidTreeNode("atPhyAddress", ".1.1.2", TreeNode.OidTreeNode.MULTIPLE));
        at.add(new TreeNode.OidTreeNode("atNetAddress", ".1.1.3", TreeNode.OidTreeNode.MULTIPLE));
        return at;
    }

    private static TreeNode.OidTreeNode initIp(){
        TreeNode.OidTreeNode ip = new TreeNode.OidTreeNode("ip", ".4");
        ip.add(new TreeNode.OidTreeNode("ipAdEntAddr", ".20.1.1", TreeNode.OidTreeNode.MULTIPLE));
        ip.add(new TreeNode.OidTreeNode("ipAdEntNetMask", ".20.1.3", TreeNode.OidTreeNode.MULTIPLE));
        ip.add(new TreeNode.OidTreeNode("ipRouteDest", ".21.1.1", TreeNode.OidTreeNode.MULTIPLE));
        ip.add(new TreeNode.OidTreeNode("ipRouteNextHop", ".21.1.7", TreeNode.OidTreeNode.MULTIPLE));
        ip.add(new TreeNode.OidTreeNode("ipRouteType", ".21.1.8", TreeNode.OidTreeNode.MULTIPLE));
        ip.add(new TreeNode.OidTreeNode("ipRouteProto", ".21.1.9", TreeNode.OidTreeNode.MULTIPLE));
        ip.add(new TreeNode.OidTreeNode("ipRouteMask", ".21.1.11", TreeNode.OidTreeNode.MULTIPLE));
        return ip;
    }

}
