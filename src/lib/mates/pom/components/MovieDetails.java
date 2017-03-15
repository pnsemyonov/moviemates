package lib.mates.pom.components;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import lib.mates.pom.PlatformDriver;
import lib.mates.pom.utils.Scroll;


/**
 * Details page of given movie.
 */
public class MovieDetails {
    public PlatformDriver platformDriver;
    public AppiumDriver<? extends MobileElement> driver;
    // Frame element enclosing all details content.
    public Element contentView;
    // Single instance of title section.
    public TitleSection titleSection;
    public InfoSection infoSection;
    public TimesSection timesSection;

    public MovieDetails(PlatformDriver platformDriver) {
        this.platformDriver = platformDriver;
        this.driver = this.platformDriver.getDriver();
        this.contentView = new Element(this.platformDriver, new By.
            ById("us.moviemates:id/scrollViewContent"));
        this.defineSections();
    }

    private void defineSections() {
        this.titleSection = new TitleSection(this);
        this.infoSection = new InfoSection(this);
        this.timesSection = new TimesSection(this);
    }

    /**
     * Scrolls to top element of specified section.
     * @param section  Instance of Section.*
     * @param timeout  Timeout after which to quit scroll if no section found.
     */
    public void scrollToSection(Section section, int timeout) {
        this.driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
        switch (section.getClass().getSimpleName()) {
            case "TitleSection":
                if (this.titleSection.topElement.isPresent()) {
                    Scroll.alignElement(this.contentView, this.titleSection.topElement);
                }
                else {
                    Scroll.scrollToElement(this.contentView, this.titleSection.topElement, 
                        SwipeElementDirection.DOWN, timeout);
                }
                break;
            case "InfoSection":
                if (this.infoSection.topElement.isPresent()) {
                    Scroll.alignElement(this.contentView, this.infoSection.topElement);
                }
                else {
                    if (this.titleSection.topElement.isPresent()) {
                        Scroll.scrollToElement(this.contentView, this.infoSection.topElement, 
                            SwipeElementDirection.UP, timeout);
                    }
                    else {
                        Scroll.scrollToElement(this.contentView, this.infoSection.topElement, 
                            SwipeElementDirection.DOWN, timeout);
                    }
                }
                break;
            case "TimesSection":
                if (this.timesSection.topElement.isPresent()) {
                    Scroll.alignElement(this.contentView, this.timesSection.topElement);
                }
                else {
                    if (this.titleSection.topElement.isPresent() || this.infoSection.topElement.
                        isPresent()) {
                        Scroll.scrollToElement(this.contentView, this.timesSection.topElement, 
                            SwipeElementDirection.UP, timeout);
                    }
                    else {
                        Scroll.scrollToElement(this.contentView, this.timesSection.topElement, 
                            SwipeElementDirection.DOWN, timeout);
                    }
                }
                break;
        }
        this.driver.manage().timeouts().implicitlyWait(this.platformDriver.implicitWait, TimeUnit.
            MILLISECONDS);
    }
}
