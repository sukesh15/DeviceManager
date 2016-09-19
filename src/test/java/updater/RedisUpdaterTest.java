package updater;

import entity.DeviceDetails;
import helpers.DeviceHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import reader.RedisReader;
import register.DeviceRegistrar;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    @Test
    public void shouldBeAbleToUpdateDevicesFromDifferentThreads() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int iterator = 0; iterator < 2; iterator++) {
            Runnable worker = new DeviceThread();
            executor.execute(worker);
        }

        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }
        System.out.println("\nFinished all threads");
        List<DeviceDetails> devicesForTestRun = new RedisReader().getDevicesForTestRun();
        for (DeviceDetails deviceDetails : devicesForTestRun) {
            assertEquals("Engaged",deviceDetails.getStatus());
        }

        DeviceDetails deviceToBeUpdated = devicesForTestRun.get(0);
        new RedisUpdater().updateStatusToAvailableForDevice(deviceToBeUpdated);
        assertEquals("Available", deviceToBeUpdated.getStatus());
    }


    private class DeviceThread implements Runnable {


        public DeviceThread() {
        }

        @Override
        public void run() {
            System.out.println("inside thread run");
            DeviceDetails engagedDevice = new RedisUpdater().getFirstAvailableDeviceAndUpdateToEngaged();
            System.out.println("status updated for device -- " + engagedDevice.getUdid());
        }

    }
}