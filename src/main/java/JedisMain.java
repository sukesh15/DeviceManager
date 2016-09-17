import helpers.DeviceHelper;
import register.DeviceRegistrar;

public class JedisMain {


    public static void main(String[] args) {
        new DeviceRegistrar(args).setUpDevices(new DeviceHelper(args[0]).getDevices());
//        new DeviceRegistrar().setUpDevices(new String[]{"emulator", "single"});

    }

}
