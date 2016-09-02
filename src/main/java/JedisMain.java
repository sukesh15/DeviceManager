import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class JedisMain {

    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;

    private static JedisPool pool = null;

    public JedisMain() {
        //configure our pool connection
        pool = new JedisPool(redisHost, redisPort);


    }

    public static void main(String[] args) {
        JedisMain main = new JedisMain();
        main.addDevices();
        main.addDeviceToPortHash();
        main.addDeviceToStatusHash();

    }

    public List<String> getUdidOfAttachedDevices() {
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

    public Map<String, String> addDeviceToStatusHash() {
        //add some values in Redis HASH
        String key = "deviceToStatus";
        Map<String, String> map = new HashMap<>();
        List<String> attachedDevices = getUdidOfAttachedDevices();
        for (String attachedDevice : attachedDevices) {
            map.put(attachedDevice, "Available");
        }

        Jedis jedis = pool.getResource();
        Map<String, String> retrieveMap = null;
        try {
            //save to redis
            jedis.hmset(key, map);

            //after saving the data, lets retrieve them to be sure that it has really added in redis
            retrieveMap = jedis.hgetAll(key);
            for (String keyMap : retrieveMap.keySet()) {
                System.out.println(keyMap + " " + retrieveMap.get(keyMap));
            }

        } catch (JedisException e) {
            //if something wrong happen, return it back to the pool
            if (null != jedis) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            ///it's important to return the Jedis instance to the pool once you've finished using it
            if (null != jedis)
                pool.returnResource(jedis);
        }
        return retrieveMap;
    }


    public Map<String, String> addDeviceToPortHash() {
        //add some values in Redis HASH
        String key = "deviceToPort";

        Map<String, String> map = new HashMap<>();
        List<String> attachedDevices = getUdidOfAttachedDevices();
        String startingPort = "4723";
        for (String attachedDevice : attachedDevices) {
            map.put(attachedDevice, startingPort);
            startingPort = String.valueOf(Integer.parseInt(startingPort) + 20);
        }

        Jedis jedis = pool.getResource();
        jedis.del(key);
        Map<String, String> retrieveMap = null;
        try {
            //save to redis
            jedis.hmset(key, map);

            //after saving the data, lets retrieve them to be sure that it has really added in redis
            retrieveMap = jedis.hgetAll(key);
            for (String keyMap : retrieveMap.keySet()) {
                System.out.println(keyMap + " " + retrieveMap.get(keyMap));
            }

        } catch (JedisException e) {
            //if something wrong happen, return it back to the pool
            if (null != jedis) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            ///it's important to return the Jedis instance to the pool once you've finished using it
            if (null != jedis)
                pool.returnResource(jedis);
        }
        return retrieveMap;
    }


    public void addDevices() {
        String key = "devices";
        List<String> attachedDevices = getUdidOfAttachedDevices();

        Jedis jedis = pool.getResource();
        jedis.flushAll();
        try {
            //save to redis
            jedis.del(key);
            for (String attachedDevice : attachedDevices) {
                jedis.sadd(key, attachedDevice);
            }

            //after saving the data, lets retrieve them to be sure that it has really added in redis

            Set members = jedis.smembers(key);
            for (Object member : members) {
                System.out.println(member.toString());
            }
        } catch (JedisException e) {
            //if something wrong happen, return it back to the pool
            if (null != jedis) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            ///it's important to return the Jedis instance to the pool once you've finished using it
            if (null != jedis)
                pool.returnResource(jedis);
        }


    }

}
