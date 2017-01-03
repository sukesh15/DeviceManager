package entity;

import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

@REntity
public class DeviceDetails {

    @RId
    private String udid;
    private int port;
    private String status;
    private String belongsTo;
    private String APILevel;
    private String androidVersion;
    private String model;

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAPILevel() {
        return APILevel;
    }

    public void setAPILevel(String APILevel) {
        this.APILevel = APILevel;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }
}
