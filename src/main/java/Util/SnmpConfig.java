package Util;

import java.math.BigDecimal;

public class SnmpConfig
{
    private SnmpConfig(){}

    /**
     * get cpu描述信息
     */
    public static final String SNMPGET_CPUDESC=".1.3.6.1.2.1.1.1.0.255.1";

    /**
     * walk 网络接口描述
     */
    public static final String SNMPWALK_IFDESCR=".1.3.6.1.2.1.2.2.1.2";

    /**
     * walk 接口物理地址
     */
    public static final String SNMPWALK_IFPHYSADDRESS=".1.3.6.1.2.1.2.2.1.6";

    /**
     * get IO负载
     */
    public static final String SNMPGET_IOLOAD=".1.3.6.1.2.1.1.1.0.255.0";

    /**
     * 硬盘大小
     */
    public static final String SNMPGET_DSKTOTAL=".1.3.6.1.4.1.2021.9.1.6";

    /**
     * walk cpu 负载
     */
    public static final String SNMPWALK_HRPROCESSLOAD=".1.3.6.1.2.1.25.3.3.1.2";

    /**
     * walk cpu 空闲率
     */
    public static final String SNMPWALK_SSCPUIDLE=".1.3.6.1.4.1.2021.11.11.0";

    /**
     * walk 存储设备描述
     */
    public static final String SNMPWALK_HRSTORAGEDESCR=".1.3.6.1.2.1.25.2.3.1.3";
    /**
     * walk 存储设备描述
     */
    public static final String SNMPWALK_AHU=".1.3.6.1.2.1.25.2.3.1.4";
    /**
     * walk 存储设备描述
     */
    public static final String SNMPWALK_AUR=".1.3.6.1.2.1.25.2.3.1.5";

    /**
     *walk 存储设备使用大小
     * 内存使用多少，跟总容量相除就是占用率
     */
    public static final String SNMPWALK_HRSTORAGEUSED=".1.3.6.1.2.1.25.2.3.1.6";
    /**
     *walk 内存占用
     * 各个进程占用的内存
     */
    public static final String SNMPWALK_MEMORY_WIN=".1.3.6.1.2.1.25.5.1.1.2";

    /**
     *walk 存储设备使用率
     *
     */
    public static final String SNMPWALK_DSKPERCENT=".1.3.6.1.4.1.2021.9.1.9";

    /**
     * get 获取内存大小
     */
    public static final String SNMPGET_HRMEMORYSIZE=".1.3.6.1.2.1.25.2.2.0";

    /**
     * 获取机器名
     */
    public static final String SNMPWALK_SYSNAME=".1.3.6.1.2.1.1.5";

    /**
     * 性能参数ID-cpu使用率
     */
    public static final Integer PERFORMANCE_PARAM_CPUUSAGE=1;

    /**
     * 性能参数ID-内存使用率
     */
    public static final Integer PERFORMANCE_PARAM_MEMORYUSAGE=2;

    /**
     * 性能参数ID-io带宽占用率
     */
    public static final Integer PERFORMANCE_PARAM_IOUSAGE=3;

    /**
     * decimal 类型1024,用于除法计算
     */
    public static final BigDecimal DEVIDE_NUM=new BigDecimal("1024");

    /**
     * get方式获取内存大小
     */
    public static final String GET_MEMORY="1.3.6.1.2.1.25.2.2.0";

    /**
     * 获取系统描述
     */
    public static final String SYS_DSC="1.3.6.1.2.1.1.1.0";

    /**
     * 获取网络接口数量
     */
    public static final String IF_NUM="1.3.6.1.2.1.2.1.0";

    /**
     * 获取cpu核数
     */
    public static final String CPU_NUM="1.3.6.1.2.1.25.3.3.1.2";


    public static final String IP_INFO="1.3.6.1.2.1.4.20.1.1";

    public static final String ARP = "1.3.6.1.2.1.4.22.1.3";
    public static final String CDPID = "1.3.6.1.4.1.9.9.23.1.2.1.1.6";
    public static final String CDPADDRESS = "1.3.6.1.4.1.9.9.23.1.2.1.1.4";
}
