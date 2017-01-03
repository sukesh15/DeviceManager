package register;

import entity.DeviceDetails;
import helpers.DeviceHelper;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.List;

public class DeviceRegistrar {

    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
    RedissonClient redisson;
    private String[] args;


    public DeviceRegistrar(String[] args) {
        this.args = args;
        Config config = new Config();
        config.useSingleServer().setAddress(redisHost + ":" + redisPort);
        redisson = Redisson.create(config);
    }

    public void setUpDevicesForSingleAppTests(List<String> deviceList) {
        try {
            System.out.println("----setup started -- single app -----");
            redisson.getList("deviceList").delete();
            RList<DeviceDetails> deviceDetailsList = redisson.getList("deviceList");
            int port = 4723;
            List<String> devices = deviceList;
            System.out.println("--Found connected device-- " + devices.size());
            for (String device : devices) {
                DeviceDetails deviceObj = new DeviceDetails();
                deviceObj.setUdid(device);
                deviceObj.setPort(port);
                deviceObj.setStatus("Available");
                deviceObj.setBelongsTo("NA");
                deviceObj.setAPILevel(new DeviceHelper("any").getDeviceAPILevel(device));
                deviceObj.setAndroidVersion(new DeviceHelper("any").getDeviceAndroidVersion(device));
                deviceObj.setModel(new DeviceHelper("any").getDeviceModel(device));
                deviceDetailsList.add(deviceObj);

                port = port + 20;
            }

            System.out.println("----setup complete-----");
        } catch (Exception e) {
            throw new RuntimeException("Device list could not be created");
        } finally {
            redisson.shutdown();
        }
    }


    public void setUpDevicesForInterAppTests(List<String> deviceList) {
        try {
            System.out.println("----setup started -- inter app -----");
            redisson.getList("deviceList").delete();
            RList<DeviceDetails> deviceDetailsList = redisson.getList("deviceList");
            int port = 4723;
            int iterator = 1;
            List<String> devices = deviceList;
            System.out.println("--Found connected device-- " + devices.size());
            for (String device : devices) {
                DeviceDetails deviceObj = new DeviceDetails();
                deviceObj.setUdid(device);
                deviceObj.setPort(port);
                deviceObj.setStatus("Available");
                if (isEven(iterator))
                    deviceObj.setBelongsTo("Driver");
                else
                    deviceObj.setBelongsTo("Rider");
                deviceObj.setAPILevel(new DeviceHelper("any").getDeviceAPILevel(device));
                deviceObj.setAndroidVersion(new DeviceHelper("any").getDeviceAndroidVersion(device));
                deviceObj.setModel(new DeviceHelper("any").getDeviceModel(device));
                deviceDetailsList.add(deviceObj);

                port = port + 20;
                iterator++;
            }
            System.out.println("----setup complete-----");
        } catch (Exception e) {
            throw new RuntimeException("Device list could not be created");
        } finally {
            redisson.shutdown();
        }
    }

    private boolean isEven(double num) {
        return ((num % 2) == 0);
    }

    public void setUpDevices(List<String> deviceList) {
        if (args[1].equals("interApp"))
            setUpDevicesForInterAppTests(deviceList);
        else
            setUpDevicesForSingleAppTests(deviceList);
    }
}
