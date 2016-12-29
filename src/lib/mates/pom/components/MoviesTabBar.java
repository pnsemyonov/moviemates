package lib.mates.pom.components;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import lib.mates.pom.PlatformDriver;


/**
 * Models tab bar at top of screen. Stores tab objects as collection. Provides accessors to tabs by 
 * their names.
 */
public class MoviesTabBar {
    // Wrapper of AppiumDriver
    private PlatformDriver platformDriver;
    private AppiumDriver<? extends MobileElement> driver;
    // Tab "Profile"
    public MobileElement profileTab;
    // Tab "Movies"
    public TabItem moviesTab;
    // Tab "Mates"
    public TabItem matesTab;
    // Tab "Chats"
    public TabItem chatsTab;
    
    /**
     * @param platformDriver  Tailored wrapper of AppiumDriver.
     */
    public MoviesTabBar(PlatformDriver platformDriver) {
        MoviesTabBar.this.platformDriver = platformDriver;
        // Instance of AndroidDriver or IOSDriver
        MoviesTabBar.this.driver = MoviesTabBar.this.platformDriver.getDriver();
        // android.widget.Button[@resource-id="us.moviemates:id/btnHamburger"]
        MoviesTabBar.this.profileTab = MoviesTabBar.this.driver.findElementById("us.moviemates:id/llHamburger");
        MoviesTabBar.this.moviesTab = new TabItem(0);
        MoviesTabBar.this.matesTab = new TabItem(1);
        MoviesTabBar.this.chatsTab = new TabItem(2);
    }
    

    /**
     * Models individual tab on tab bar. Provides tab selection and obtaining text and state.
     *
     */
    public class TabItem {
        // android.support.v7.app.ActionBar$Tab[@index=0]
        private MobileElement headElement;
        private MobileElement textElement;

        public TabItem(int index) {
            TabItem.this.headElement = MoviesTabBar.this.driver.findElementByXPath(String.
                format("//android.support.v7.app.ActionBar.Tab[@index=%d]", index));
            // android.widget.TextView[@resource-id="us.moviemates:id/tvTitle"]
            TabItem.this.textElement = this.headElement.
                findElementById("us.moviemates:id/tvTitle");
        }

        /**
         * Selects tab by tapping on it.
         */
        public void select() {
            TabItem.this.headElement.tap(1, 0);
        }

        /**
         * Checks if tab is active (selected).
         * @return  True if active, false if not.
         */
        public boolean isActive() {
            return TabItem.this.headElement.getAttribute("selected").equals("true") ? true : false;
        }

        /**
         * Returns name (title) of tab.
         * @return Name of tab.
         */
        public String getText() {
            return TabItem.this.textElement.getText();
        }
    }
}