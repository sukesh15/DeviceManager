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


    public DeviceRegistrar() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisHost + ":" + redisPort);
        redisson = Redisson.create(config);
    }

    public void setUpDevicesForSingleAppTests(String deviceType) {
        try {
            System.out.println("----setup started -- single app -----");
            redisson.getList("deviceList").delete();
            RList<DeviceDetails> deviceDetailsList = redisson.getList("deviceList");
            int port = 4723;
            List<String> devices = new DeviceHelper(deviceType).getDevices();
            System.out.println("--Found connected device-- " + devices.size());
            for (String device : devices) {
                DeviceDetails deviceObj = new DeviceDetails();
                deviceObj.setId(device);
                deviceObj.setPort(port);
                deviceObj.setStatus("Available");
                deviceObj.setBelongsTo("Rider");
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


    public void setUpDevicesForInterAppTests(String deviceType) {
        try {
            System.out.println("----setup started -- inter app -----");
            redisson.getList("deviceList").delete();
            RList<DeviceDetails> deviceDetailsList = redisson.getList("deviceList");
            int port = 4723;
            int iterator = 1;
            List<String> devices = new DeviceHelper(deviceType).getDevices();
            System.out.println("--Found connected device-- " + devices.size());
            for (String device : devices) {
                DeviceDetails deviceObj = new DeviceDetails();
                deviceObj.setId(device);
                deviceObj.setPort(port);
                deviceObj.setStatus("Available");
                if (isEven(iterator))
                    deviceObj.setBelongsTo("Driver");
                else
                    deviceObj.setBelongsTo("Rider");
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

    public void setUpDevices(String[] args) {
        if (args[1].equals("interApp"))
            setUpDevicesForInterAppTests(args[0]);
        else
            setUpDevicesForSingleAppTests(args[0]);
    }
}
