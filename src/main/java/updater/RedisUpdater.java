package updater;

import entity.DeviceDetails;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisUpdater {

    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
    RedissonClient redisson;

    public RedisUpdater() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisHost + ":" + redisPort);
        redisson = Redisson.create(config);
    }

    public void updateStatusToEngagedForDevice(DeviceDetails deviceToBeUpdated) {
        try {
            RList<DeviceDetails> deviceList = redisson.getList("deviceList");
            removeDeviceFromRedisList(deviceToBeUpdated, deviceList);
            deviceToBeUpdated.setStatus("Engaged");
            deviceList.add(deviceToBeUpdated);
        } catch (Exception e) {
            throw new RuntimeException("Device status not updated correctly");
        } finally {
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
