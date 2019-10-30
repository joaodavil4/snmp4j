public class Configs {
    private String  ipAddress;
    private String  port;
    private String  oidValue;
    private String  community;

    public Configs(String ipAddress, String community, String oidValue, String port){
        this.ipAddress = ipAddress;
        this.community = community;
        this.oidValue = oidValue;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getOidValue() {
        return oidValue;
    }

    public void setOidValue(String oidValue) {
        this.oidValue = oidValue;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }
}
