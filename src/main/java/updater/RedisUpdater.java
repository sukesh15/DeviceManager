package updater;

import entity.DeviceDetails;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisUpdater {

    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
    RedissonClient redisson;

    public RedisUpdater() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisHost + ":" + redisPort);
        redisson = Redisson.create(config);
    }

    public DeviceDetails getFirstAvailableDeviceAndUpdateToEngaged() {
        RLock lock = redisson.getLock("myLock");
        lock.lock(5, TimeUnit.SECONDS);
        try {
            RList<DeviceDetails> check = redisson.getList("deviceList");
            List<DeviceDetails> readAll = check.readAll();

            for (DeviceDetails deviceDetails : readAll) {
                if (deviceDetails.getStatus().equalsIgnoreCase("Available")) {
                    updateStatusToEngagedForDevice(deviceDetails);
                    return deviceDetails;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            redisson.shutdown();
        }

        throw new RuntimeException("No Device Available");
    }

    private void updateStatusToEngagedForDevice(DeviceDetails deviceToBeUpdated) {
        RLock lock = redisson.getLock("writeLock");
        lock.lock(5, TimeUnit.SECONDS);
        try {
            RList<DeviceDetails> deviceList = redisson.getList("deviceList");
            removeDeviceFromRedisList(deviceToBeUpdated, deviceList);
            deviceToBeUpdated.setStatus("Engaged");
            deviceList.add(deviceToBeUpdated);
        } catch (Exception e) {
            throw new RuntimeException("Device status not updated correctly");
        } finally {
            lock.unlock();
            redisson.shutdown();
        }
    }

    private void removeDeviceFromRedisList(DeviceDetails deviceToBeUpdated, RList<DeviceDetails> deviceList) {
        for (DeviceDetails details : deviceList) {
            if (details.getUdid().equals(deviceToBeUpdated.getUdid())) {
                deviceList.remove(details);
                break;
            }
        }
    }

    public void updateStatusToAvailableForDevice(DeviceDetails deviceToBeUpdated) {
        try {
            RList<DeviceDetails> deviceList = redisson.getList("deviceList");
            removeDeviceFromRedisList(deviceToBeUpdated, deviceList);
            deviceToBeUpdated.setStatus("Available");
            deviceList.add(deviceToBeUpdated);
        } catch (Exception e) {
            throw new RuntimeException("Device status not updated correctly");
        } finally {
            redisson.shutdown();
        }
    }
}
