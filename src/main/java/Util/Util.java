package Util;

import org.snmp4j.CommunityTarget;

public class Util {

    public static String hexIp2Decimal(String hexip){
        String[] each = hexip.split(":");
        StringBuilder result = new StringBuilder();
        for (String s : each) {
            result.append((Character.digit(s.charAt(0), 16) << 4) + Character.digit(s.charAt(1), 16)).append(".");
        }

        return result.substring(0, result.length()-1);
    }

}
