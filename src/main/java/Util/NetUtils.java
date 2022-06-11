package Util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description 测试ip及端口连通性
 * @ClassName NetUtils
 * @Author yuhuofei
 * @Date 2022/3/13 17:03
 * @Version 1.0
 */
public class NetUtils {

    /**
     * @param ipAddress 待检测IP地址
     * @param timeout   检测超时时间
     * @return
     */
    private static Boolean ipDetection(String ipAddress, Integer timeout) {
        // 当返回值是true时，说明host是可用的，false则不可。
        boolean status = false;
        try {
            status = InetAddress.getByName(ipAddress).isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 通过socket检测ip:port是否能够通信
     *
     * @param ipAddress
     * @param port
     * @param timeout
     * @return
     */
    private static Boolean ipDetection(String ipAddress, Integer port, Integer timeout) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(InetAddress.getByName(ipAddress), port), timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * @param ipAddress  待检测IP地址
     * @param port       待检测port
     * @param retryCount 重试次数
     * @param timeout    检测超时时间（超时应该在3钞以上）
     * @param detectionFlag   标志位 0检测IP  1检测IP:PORT
     * @return
     */
    public static Boolean retryIPDetection(String ipAddress, Integer port, Integer retryCount, Integer timeout, Integer detectionFlag) {
        // 当返回值是true时，说明host是可用的，false则不可。
        boolean status = false;
        Integer tryCount = 1;

        //重试机制
        while (tryCount <= retryCount && status == false) {
            if (detectionFlag.equals(0)) {
                status = ipDetection(ipAddress, timeout);
            } else {
                status = ipDetection(ipAddress, port, timeout);
            }
            if (!status) {
                System.out.printf("第[" + tryCount + "]次连接 " + ipAddress + ":" + port + " 失败！");
                tryCount++;
                continue;
            }
            System.out.printf("连接 " + ipAddress + ":" + port + " 成功！");
            return true;
        }
        return false;
    }

}
