package Util;

public class RowEntity {
    private String ip;
    private String oid;
    private Object value;
    private String  type;

    public RowEntity(String oid, Object value, String type, String ip){
        this.ip = ip;
        this.oid = oid;
        this.value = value;
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public String getOid() {
        return oid;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
