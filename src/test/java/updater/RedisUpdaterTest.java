package updater;

import entity.DeviceDetails;
import helpers.DeviceHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import reader.RedisReader;
import register.DeviceRegistrar;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RedisUpdaterTest {
    private DeviceHelper deviceHelper;

    @Before()
    public void setUp() {
        deviceHelper = Mockito.mock(DeviceHelper.class);
        List<String> deviceList1 = Arrays.asList("101", "102");
        when(deviceHelper.getDevices()).thenReturn(deviceList1);

        final String[] args = new String[]{"emulator", "singleApp"};
        final DeviceRegistrar deviceRegistrar = new DeviceRegistrar(args);
        deviceRegistrar.setUpDevices(deviceHelper.getDevices());
    }


    private void sortList(List<DeviceDetails> devicesForTestRun) {
        Comparator<DeviceDetails> comparator = (c1, c2) -> Integer.parseInt(c2.getUdid()) - Integer.parseInt(c1.getUdid());
        Collections.sort(devicesForTestRun, comparator);
    }

    @Test
    public void shouldBeAbleToUpdateStatusOfDevices() throws Exception {
        List<DeviceDetails> devicesForTestRun = new RedisReader().getDevicesForTestRun();
        sortList(devicesForTestRun);

        new RedisUpdater().updateStatusToEngagedForDevice(devicesForTestRun.get(0));
        List<DeviceDetails> updatedDeviceList = new RedisReader().getDevicesForTestRun();

        sortList(updatedDeviceList);
        assertEquals("Engaged", updatedDeviceList.get(0).getStatus());

        new RedisUpdater().updateStatusToAvailableForDevice(updatedDeviceList.get(0));
        List<DeviceDetails> updatedDeviceList1 = new RedisReader().getDevicesForTestRun();

        sortList(updatedDeviceList1);
        assertEquals("Available", updatedDeviceList1.get(0).getStatus());

    }

}