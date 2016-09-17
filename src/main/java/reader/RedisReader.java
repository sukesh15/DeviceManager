package reader;

import entity.DeviceDetails;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.List;

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
        try {
            RList<DeviceDetails> deviceRList = redisson.getList("deviceList");
            List<DeviceDetails> deviceList = convertRListToStandardJavaList(deviceRList);
            redisson.shutdown();
            return deviceList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        List<DeviceDetails> deviceDetailsList = new ArrayList<>();
//
//        for (DeviceDetails deviceDetails : check) {
//            deviceDetailsList.add(deviceDetails);
//        }
//        return deviceDetailsList;
    }

    private List<DeviceDetails> convertRListToStandardJavaList(RList<DeviceDetails> rList) {
        List<DeviceDetails> standardJavaList = new ArrayList<>();
        for (DeviceDetails deviceDetails : rList) {
            standardJavaList.add(deviceDetails);
        }
        return standardJavaList;
    }

    public List<DeviceDetails> getDriverDevicesForTestRun() {
        RList<DeviceDetails> check = redisson.getList("deviceList");
        List<DeviceDetails> deviceDetailsList = new ArrayList<>();

        for (DeviceDetails deviceDetails : check) {
            if (deviceDetails.getBelongsTo().equalsIgnoreCase("Driver"))
                deviceDetailsList.add(deviceDetails);
        }
        return deviceDetailsList;
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
