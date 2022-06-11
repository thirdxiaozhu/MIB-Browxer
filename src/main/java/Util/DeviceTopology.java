package Util;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

public class DeviceTopology {
    private List<String> routers;
    private int port;
    private String readCom;
    private String writeCom;
    private String version;
    private SnmpUtil util;

    public DeviceTopology(SnmpUtil util, Integer port, String readCom, String writeCom, String version){
        routers = new ArrayList<>();
        this.port = port;
        this.readCom = readCom;
        this.writeCom = writeCom;
        this.version = version;
        this.util = util;
    }

    public void initNodes(TreeNode.DeviceTreeNode root, String addr){
        List<String> cdpAddrList;

        if(!routers.contains(addr)) {
            TreeNode.DeviceTreeNode hostNode = addNode(root, addr);
            cdpAddrList = util.getIPs(hostNode.getTarget(), SnmpConfig.CDPADDRESS);
        }else {
            cdpAddrList = util.getIPs(root.getTarget(), SnmpConfig.CDPADDRESS);
        }
        //Router
        for (String s : cdpAddrList) {
            if (!routers.contains(s)) {
                TreeNode.DeviceTreeNode node = addNode(root, s);
                initNodes(node, s);
            }
        }
    }

    private TreeNode.DeviceTreeNode addNode(TreeNode.DeviceTreeNode root, String addr){
        TreeNode.DeviceTreeNode node = new TreeNode.DeviceTreeNode(addr, port, readCom, writeCom, version);
        root.add(node);
        routers.add(addr);

        System.out.println(node);
        List<String> gatewayList = util.getIPs(node.getTarget(), SnmpConfig.IP_INFO);
        routers.addAll(gatewayList);

        //ARP
        List<String> arpList = util.getIPs(node.getTarget(), SnmpConfig.ARP);
        for (String s : arpList) {
            if(!routers.contains(s)){
                node.add(new TreeNode.DeviceTreeNode(s));
            }
        }
        return node;
    }
}
