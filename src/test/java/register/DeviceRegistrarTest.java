package register;

import entity.DeviceDetails;
import helpers.DeviceHelper;
import org.junit.Test;
import org.mockito.Mockito;
import reader.RedisReader;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class DeviceRegistrarTest {

    @Test
    public void setUpDevicesForSingleAppTests() throws Exception {
        DeviceHelper deviceHelper = Mockito.mock(DeviceHelper.class);
        List<String> deviceList1 = Arrays.asList("101", "102");
        when(deviceHelper.getDevices()).thenReturn(deviceList1);

        final String[] args = new String[]{"emulator", "single"};
        final DeviceRegistrar deviceRegistrar = new DeviceRegistrar(args);
        deviceRegistrar.setUpDevices(deviceHelper.getDevices());
        assertEquals(deviceHelper.getDevices().get(0), "101");

    }

    @Test
    public void setUpDevicesForInterAppTests() throws Exception {
        DeviceHelper deviceHelper = Mockito.mock(DeviceHelper.class);
        List<String> deviceList1 = Arrays.asList("101", "102");
        when(deviceHelper.getDevices()).thenReturn(deviceList1);

        final String[] args = new String[]{"emulator", "interApp"};
        final DeviceRegistrar deviceRegistrar = new DeviceRegistrar(args);
        deviceRegistrar.setUpDevices(deviceHelper.getDevices());
        List<DeviceDetails> devicesForTestRun = new RedisReader().getDevicesForTestRun();
        assertEquals(devicesForTestRun.get(0).getStatus(), "Available");
        assertEquals(devicesForTestRun.get(1).getStatus(), "Available");
        assertEquals(devicesForTestRun.get(0).getBelongsTo(), "Rider");
        assertEquals(devicesForTestRun.get(1).getBelongsTo(), "Driver");

    }


}