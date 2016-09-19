package reader;

import entity.DeviceDetails;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import updater.RedisUpdater;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisReader {

    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
    RedissonClient redisson;


    public RedisReader() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisHost + ":" + redisPort);
        redisson = Redisson.create(config);
    }

    public List<DeviceDetails> getDevicesForTestRun() {
        RLock lock = redisson.getLock("readLock");
        lock.lock(5, TimeUnit.SECONDS);
        try {
            RList<DeviceDetails> deviceRList = redisson.getList("deviceList");
            List<DeviceDetails> deviceList = deviceRList.readAll();
            return deviceList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            redisson.shutdown();
        }
    }

    public List<DeviceDetails> getDriverDevicesForTestRun() {
        try {
            List<DeviceDetails> devicesForTestRun = getDevicesForTestRun();
            List<DeviceDetails> driverDevices = new ArrayList<>();

            for (DeviceDetails deviceDetails : devicesForTestRun) {
                if (deviceDetails.getBelongsTo().equalsIgnoreCase("Driver"))
                    driverDevices.add(deviceDetails);
            }
            return driverDevices;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            redisson.shutdown();
        }


    }

    public List<DeviceDetails> getRiderDevicesForTestRun() {
        RList<DeviceDetails> check = redisson.getList("deviceList");
        List<DeviceDetails> deviceDetailsList = new ArrayList<>();

        for (DeviceDetails deviceDetails : check) {
            if (deviceDetails.getBelongsTo().equalsIgnoreCase("Rider"))
                deviceDetailsList.add(deviceDetails);
        }
        return deviceDetailsList;
    }



}
