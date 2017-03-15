package lib.mates.pom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;


/**
 * Wrapper of AppiumDriver. Creates AndroidDriver or IOSDriver with desired capabilities based on 
 * device data sheet.
 */
public class PlatformDriver {
    private String deviceId;
    private String protocol;
    private String host;
    private int port;
    private String file;
    public int implicitWait;
    private AppiumDriver<? extends MobileElement> driver;

    /**
     * @param deviceId  Universal Device ID of device under test.
     * @param protocol  Protocol ("http", "https", "ftp")
     * @param host  Running Appium Server's host name.
     * @param port  Appium Server's port.
     * @param file  File name on the host ("wd/hub").
     * @param implicitWait  Default implicit wait time (milliseconds) for find... family of methods.
     * @throws IOException
     * @throws InterruptedException
     */
    public PlatformDriver(String deviceId, String protocol, String host, int port, String file, int implicitWait) throws IOException, InterruptedException {
        PlatformDriver.this.deviceId = deviceId;
        PlatformDriver.this.protocol = protocol;
        PlatformDriver.this.host = host;
        PlatformDriver.this.port = port;
        PlatformDriver.this.file = file;
        PlatformDriver.this.implicitWait = implicitWait;
        PlatformDriver.this.createDriver();
    }

    /**
     * Checks if device with specified ID is discoverable by adb.
     * @return  True if device is found mounted, false if not.
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean isDeviceMounted() throws IOException, InterruptedException {
        // Get list of IDs of mounted devices.
        Process process = Runtime.getRuntime().exec("adb devices");
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        List<String> commandOutput = new ArrayList<>();
        String outputLine;
        while ((outputLine = reader.readLine()) != null) {
            commandOutput.add(outputLine);
        }
        boolean isDeviceMounted = false;
        if (commandOutput.size() > 1 && commandOutput.get(0).contains("List of devices attached")) {
            String[] deviceDetails = commandOutput.get(1).trim().split("\\s+");
            // If specified UDID is presented in adb's output.
            if (deviceDetails.length == 2 && deviceDetails[0].equals(PlatformDriver.this.deviceId) && deviceDetails[1].equals("device")) {
                isDeviceMounted = true;
            }
        }
        return isDeviceMounted;
    }
    
    /**
     * Obtains device info from JSON data sheet.
     * @return  JSON object with info relevant to specified UDID.
     * @throws IOException
     */
    private JsonObject getDeviceInfo() throws IOException {
        Path deviceInfoFilePath = Paths.get(System.getProperty("user.dir"), "src", "data", "devices.json");
        JsonReader jsonReader = Json.createReader(new StringReader(new String(Files.readAllBytes(deviceInfoFilePath))));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        JsonObject deviceInfo = jsonObject.getJsonObject(PlatformDriver.this.deviceId);
        return deviceInfo;
    }
    
    /**
     * Creates instance of AndroidDriver or IOSDriver with relevant desired capabilities. (Use src/data/devices.json as example.)
     * @throws IOException
     * @throws InterruptedException
     */
    private void createDriver() throws IOException, InterruptedException {
        if (PlatformDriver.this.isDeviceMounted()) {
            JsonObject deviceInfo = PlatformDriver.this.getDeviceInfo();
            if (deviceInfo != null) {
                DesiredCapabilities capabilities = new DesiredCapabilities();
                // "Appium"
                capabilities.setCapability("automationName", deviceInfo.getString("automationName"));
                // "PORTRAIT" or "LANDSCAPE"
                capabilities.setCapability("orientation", deviceInfo.getString("orientation"));
                // True or false
                capabilities.setCapability(CapabilityType.ROTATABLE, deviceInfo.getBoolean("rotatable"));
                // True or false
                capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, deviceInfo.getBoolean("takesScreenshot"));
                // Mobile OS version ("7.1", "4.4"...)
                capabilities.setCapability("platformVersion", deviceInfo.getString("platformVersion"));
                // Mobile device name
                capabilities.setCapability("deviceName", deviceInfo.getString("deviceName"));
                // How long Appium Server will wait for new command from client before session end (seconds)
                capabilities.setCapability("newCommandTimeout", 300);
                // "Android", "iOS"
                String platformName = deviceInfo.getString("platformName");
                capabilities.setCapability("platformName", platformName);
                if (platformName.equals("Android")) {
                    // Activity name to launch from package (Android only)
                    capabilities.setCapability("appActivity", "us.moviemates.Activities.MainActivity");
//                    capabilities.setCapability("appActivity", "com.webview.mm.webviewtest.MainActivity");
                    // Java package of app to run
                    capabilities.setCapability("appPackage", "us.moviemates");
//                    capabilities.setCapability("appPackage", "com.webview.mm.webviewtest");
                    capabilities.setCapability(CapabilityType.PLATFORM, "ANDROID");
                    capabilities.setCapability("useKeystore", false);
                    PlatformDriver.this.driver = new AndroidDriver<AndroidElement>(new URL(PlatformDriver.this.protocol, PlatformDriver.this.host, PlatformDriver.this.port, PlatformDriver.this.file), capabilities);
                }
                else if (platformName.equals("iOS")) {
                    PlatformDriver.this.driver = new IOSDriver<IOSElement>(new URL(PlatformDriver.this.protocol, PlatformDriver.this.host, PlatformDriver.this.port, PlatformDriver.this.file), capabilities);
                }
                else {
                    throw new RuntimeException("Invalid platform name: " + platformName);
                }
                // Set default implicit wait time
                PlatformDriver.this.driver.manage().timeouts().implicitlyWait(PlatformDriver.this.implicitWait, TimeUnit.MILLISECONDS);
            }
            else {
                throw new RuntimeException("Target device info not found: " + PlatformDriver.this.deviceId);
            }
        }
        else {
            throw new RuntimeException("Target device not mounted: " + PlatformDriver.this.deviceId);
        }
    }

    /**
     * Returns instance of AndroidDriver or IOSDriver with desired capabilities.
     * @return  instance of AndroidDriver or IOSDriver.
     */
    public AppiumDriver<? extends MobileElement> getDriver() {
        return PlatformDriver.this.driver;
    }

    /**
     * Quits current session.
     */
    public void quit() {
        PlatformDriver.this.driver.quit();
    }
}