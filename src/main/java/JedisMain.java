import register.DeviceRegistrar;

import java.sql.Array;
import java.util.Arrays;

public class JedisMain {


    public static void main(String[] args) {
        new DeviceRegistrar().setUpDevices(args);
//        new DeviceRegistrar().setUpDevices(new String[]{"emulator","interApp"});
    }

}
