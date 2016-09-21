package updater;

import entity.DeviceDetails;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import reader.RedisReader;

import java.util.Collections;
import java.util.Comparator;
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
        RLock lock = redisson.getLock("writeLock");
        lock.lock(5, TimeUnit.SECONDS);
        try {
            RList<DeviceDetails> deviceRList = redisson.getList("deviceList");
            List<DeviceDetails> readAll = deviceRList.readAll();
            readAll.sort((a, b) -> Integer.compare(b.getPort(), a.getPort()));

            for (DeviceDetails deviceDetails : readAll) {
                if (deviceDetails.getStatus().equalsIgnoreCase("Available")) {
                    removeDeviceFromRedisList(deviceDetails, deviceRList);
                    deviceDetails.setStatus("Engaged");
                    deviceRList.add(deviceDetails);
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

    public DeviceDetails getFirstAvailableDriverDeviceAndUpdateToEngaged() {
        RLock lock = redisson.getLock("writeLock");
        lock.lock(5, TimeUnit.SECONDS);
        try {
            RList<DeviceDetails> deviceRList = redisson.getList("deviceList");
            List<DeviceDetails> readAll = deviceRList.readAll();

            for (DeviceDetails deviceDetails : readAll) {
                if (deviceDetails.getStatus().equalsIgnoreCase("Available") &&
                        deviceDetails.getBelongsTo().equals("Driver")) {
                    removeDeviceFromRedisList(deviceDetails, deviceRList);
                    deviceDetails.setStatus("Engaged");
                    deviceRList.add(deviceDetails);
                    return deviceDetails;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            redisson.shutdown();
        }
        throw new RuntimeException("No Driver Device Avalable");
    }

    public DeviceDetails getFirstAvailableRiderDeviceAndUpdateToEngaged() {
        RLock lock = redisson.getLock("writeLock");
        lock.lock(5, TimeUnit.SECONDS);
        try {
            RList<DeviceDetails> deviceRList = redisson.getList("deviceList");
            List<DeviceDetails> readAll = deviceRList.readAll();

            for (DeviceDetails deviceDetails : readAll) {
                if (deviceDetails.getStatus().equalsIgnoreCase("Available") &&
                        deviceDetails.getBelongsTo().equals("Rider")) {
                    removeDeviceFromRedisList(deviceDetails, deviceRList);
                    deviceDetails.setStatus("Engaged");
                    deviceRList.add(deviceDetails);
                    return deviceDetails;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            redisson.shutdown();
        }
        throw new RuntimeException("No Rider Device Avalable");
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
