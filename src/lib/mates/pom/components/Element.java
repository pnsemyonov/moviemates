package lib.mates.pom.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import lib.mates.pom.PlatformDriver;


/**
 * Wraps MobileElement to provide, mainly, NoSuchElementException handling.
 * Allows to construct and store UI element even when its underlying MobileElement is not present 
 * in XML.
 */
public class Element {
    public PlatformDriver platformDriver;
    public AppiumDriver<? extends MobileElement> driver;
    // List of By.* locators of target element evaluated in specified order. Ex. By.ById("id_1")/By.ByClassName("classname_1")/By...
    public List<By> locators = new ArrayList<>();

    /**
     * Constructs Element by specifying multiple By locators (grandparent/parent/child).  
     * @param platformDriver  Instance of PlatformDriver.
     * @param locators  List of hierarchical By locators.
     */
    public Element(PlatformDriver platformDriver, List<By> locators) {
        this.platformDriver = platformDriver;
        this.driver = this.platformDriver.getDriver();
        this.locators.addAll(locators);
    }

    /**
     * Constructs Element by specifying single By locator.  
     * @param platformDriver  Instance of PlatformDriver.
     * @param locators  By locator.
     */
    public Element(PlatformDriver platformDriver, By locator) {
        this.platformDriver = platformDriver;
        this.driver = this.platformDriver.getDriver();
        this.locators.add(locator);
    }

    /**
     * Returns underlying MobileElement.
     * @return  Instance of MobileElement.
     */
    public MobileElement getMobileElement() {
        this.driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
        MobileElement parentElement;
        try {
            parentElement = this.driver.findElement(this.locators.get(0));
            // Finds child element in grandparent element's context, then child in parent element's 
            // context and vise versa down the hierarchy.
            for (int index = 1; index < this.locators.size(); ++index) {
                parentElement = parentElement.findElement(this.locators.get(index));
            }
        }
        catch (NoSuchElementException e) {
            parentElement = null;
        }
        this.driver.manage().timeouts().implicitlyWait(this.platformDriver.implicitWait, TimeUnit.
            MILLISECONDS);
        return parentElement;
    }
    
    /**
     * Finds child element of this Element by specifying single By locator. 
     * @param locator  By locator.
     * @return  Instance of child Element.
     */
    public Element findElement(By locator) {
        List<By> newLocators = new ArrayList<By>(this.locators);
        newLocators.add(locator);
        return new Element(this.platformDriver, newLocators);
    }

    /**
     * Finds child element of this Element by specifying multiple By locators. 
     * @param locators  List of By locators.
     * @return  Instance of child Element.
     */
    public Element findElement(List<By> locators) {
        List<By> newLocators = new ArrayList<By>(this.locators);
        newLocators.addAll(locators);
        return new Element(this.platformDriver, newLocators);
    }

    /**
     * Core function of the class. Inspects if the underlying MobileElement is present in XML.
     * @return
     */
    public boolean isPresent() {
        return this.getMobileElement() != null ? true : false;
    }
}
