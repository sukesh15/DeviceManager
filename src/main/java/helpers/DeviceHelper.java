package helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DeviceHelper {

    private String deviceType;

    public DeviceHelper(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<String> getDevices() {
        if (deviceType == null)
            return attachedDevices();
        if (deviceType.equalsIgnoreCase("any"))
            return attachedDevicesAndEmulators();
        if (deviceType.equalsIgnoreCase("device"))
            return attachedDevices();
        if (deviceType.equalsIgnoreCase("emulator"))
            return attachedEmulators();
        return attachedDevices();

    }

    public List<String> attachedDevicesAndEmulators() {
        List<String> devices = new ArrayList<>();
        Scanner scan = new Scanner(String.valueOf(readAdbLog("adb devices")));
        while (scan.hasNextLine()) {
            String oneLine = scan.nextLine();
            if (oneLine.endsWith("device")) {
                devices.add(oneLine.replace("device", "").trim());
            }
        }
        return devices;
    }

    public List<String> attachedDevices() {
        List<String> devices = new ArrayList<>();
        Scanner scan = new Scanner(String.valueOf(readAdbLog("adb devices")));
        while (scan.hasNextLine()) {
            String oneLine = scan.nextLine();
            if (oneLine.endsWith("device") && !oneLine.contains(".")) {
                devices.add(oneLine.replace("device", "").trim());
            }
        }
        return devices;
    }

    public List<String> attachedEmulators() {
        List<String> devices = new ArrayList<>();
        Scanner scan = new Scanner(String.valueOf(readAdbLog("adb devices")));
        while (scan.hasNextLine()) {
            String oneLine = scan.nextLine();
            if (oneLine.endsWith("device") && oneLine.contains(".")) {
                devices.add(oneLine.replace("device", "").trim());
            }
        }
        return devices;
    }

    public StringBuilder readAdbLog(String cmd) {
        String command = cmd;
        String line;
        StringBuilder log = new StringBuilder();
        Process process;
        Runtime rt = Runtime.getRuntime();
        try {
            process = rt.exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));


            while ((line = stdInput.readLine()) != null) {
                log.append(line);
                log.append(System.getProperty("line.separator"));
            }
            while ((line = stdError.readLine()) != null) {
                log.append(line);
                log.append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return log;
    }

}
