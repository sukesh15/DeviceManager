package reader;

import entity.DeviceDetails;
import helpers.DeviceHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import register.DeviceRegistrar;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RedisReaderTest {

    private DeviceHelper deviceHelper;

    @Before
    public void setUp() {
        deviceHelper = Mockito.mock(DeviceHelper.class);
        List<String> deviceList1 = Arrays.asList("101", "102");
        when(deviceHelper.getDevices()).thenReturn(deviceList1);
    }

    @Test
    public void getDevicesForTestRun() throws Exception {
        final String[] args = new String[]{"emulator", "singleApp"};
        final DeviceRegistrar deviceRegistrar = new DeviceRegistrar(args);
        deviceRegistrar.setUpDevices(deviceHelper.getDevices());
        List<DeviceDetails> devicesForTestRun = new RedisReader().getDevicesForTestRun();
        for (DeviceDetails deviceDetails : devicesForTestRun) {
            assertEquals("Available", deviceDetails.getStatus());
        }
    }

    @Test
    public void getDriverDevicesForTestRun() throws Exception {
        final String[] args = new String[]{"emulator", "interApp"};
        final DeviceRegistrar deviceRegistrar = new DeviceRegistrar(args);
        deviceRegistrar.setUpDevices(deviceHelper.getDevices());
        List<DeviceDetails> driverDevices = new RedisReader().getDriverDevicesForTestRun();

        for (DeviceDetails deviceDetails : driverDevices) {
            System.out.println("device udid -- " + deviceDetails.getUdid());
            assertEquals("Driver", deviceDetails.getBelongsTo());
        }
    }

    @Test
    public void getRiderDevicesForTestRun() throws Exception {
        final String[] args = new String[]{"emulator", "interApp"};
        final DeviceRegistrar deviceRegistrar = new DeviceRegistrar(args);
        deviceRegistrar.setUpDevices(deviceHelper.getDevices());
        List<DeviceDetails> driverDevices = new RedisReader().getRiderDevicesForTestRun();

        for (DeviceDetails deviceDetails : driverDevices) {
            System.out.println("device udid -- " + deviceDetails.getUdid());
            assertEquals("Rider", deviceDetails.getBelongsTo());
        }

    }

}