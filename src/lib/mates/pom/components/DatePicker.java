package lib.mates.pom.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.NoSuchElementException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import lib.mates.pom.PlatformDriver;


/**
 * Models date picker line above movie list on "Movies" page. Stores date objects in collection and 
 * performs scrolling.
 */
public class DatePicker {
    // Wrapper of AppiumDriver
    private PlatformDriver platformDriver;
    private AppiumDriver<? extends MobileElement> driver;
    // Head element of date picker container
    private MobileElement pickerHeadElement;
    // XPath workaround. When possible, elements are found by resource ID, though finding 
    // ImageViews by ID gives wrong results within element's context.
    private String dateItemHeadElementSelector = 
        "//android.support.v7.widget.RecyclerView/android.widget.RelativeLayout[@index=%d]";
    // Collection of date objects visible on date line
    private ArrayList<DateItem> dates = new ArrayList<>();
    // Shorten wait time for better performance when applicable
    private int fastImplicitWait = 1000;

    /**
     * @param platformDriver  Tailored wrapper of AppiumDriver.
     */
    public DatePicker(PlatformDriver platformDriver) {
        DatePicker.this.platformDriver = platformDriver;
        // Instance of AndroidDriver or IOSDriver
        DatePicker.this.driver = platformDriver.getDriver();
        DatePicker.this.getDates();
    }

    /**
     * Calls date item builder for each date in line and stores them as collection.
     */
    public List<DateItem> getDates() {
        DatePicker.this.dates.clear();
        // Head element of dates container
        DatePicker.this.pickerHeadElement = DatePicker.this.driver.
            findElementById("us.moviemates:id/date_list");
        // Find number of visible dates
        int dateItemsNumber = DatePicker.this.pickerHeadElement.
            findElementsById("us.moviemates:id/rl_date_picker_item").size();
        // Populate collection of date objects
        for (int dateItemIndex = 0; dateItemIndex < dateItemsNumber; dateItemIndex++) {
            DatePicker.this.dates.add(new DateItem(dateItemIndex));
        }
        return DatePicker.this.dates;
    }
    
    /**
     * Swipes line of dates leftward
     * @param steps  Swipe distance measured in date items
     */
    public void scrollLeft(int steps) {
        DatePicker.this.scroll(SwipeElementDirection.LEFT, steps);
    }

    /**
     * Swipes line of dates rightward
     * @param steps  Swipe distance measured in date items
     */
    public void scrollRight(int steps) {
        DatePicker.this.scroll(SwipeElementDirection.RIGHT, steps);
    }

    /**
     * Swipes date line in specified direction
     * @param direction  LEFT or RIGHT
     * @param steps  Swipe distance measured in number of date items (tiles)
     */
    private void scroll(SwipeElementDirection direction, int steps) {
        // Do not exceed number of visible dates
        if (steps <= DatePicker.this.dates.size() - 1) {
            int centerX = DatePicker.this.pickerHeadElement.getCenter().x;
            // Obtain width of individual date tile
            int dateItemWidth = DatePicker.this.dates.get(1).headElement.getSize().width;
            int startX;
            int endX;
            if (direction == SwipeElementDirection.LEFT) {
                // Move starting point half-way right from line's center
                startX = centerX + (dateItemWidth * steps / 2);
                // Swipe ends at half-way left from center
                endX = startX - (dateItemWidth * steps);
            }
            else {
                startX = centerX - (dateItemWidth * steps / 2);
                endX = startX + (dateItemWidth * steps);
            }
            int startY = DatePicker.this.pickerHeadElement.getCenter().y;
            DatePicker.this.driver.swipe(startX, startY, endX, startY, 500 * steps);
            // Update set of dates as they have been changed
            DatePicker.this.getDates();
        }
        else {
            throw new IndexOutOfBoundsException(String.format("Scroll cannot exceed %d steps.", 
                DatePicker.this.dates.size() - 1));
        }
    }


    /**
     * Implements individual date in the line. Performs date-specific actions (picking, getting 
     * details/status etc.)
     */
    public class DateItem {
        // android.widget.RelativeLayout[@resource-id="us.moviemates:id/rl_date_picker_item"]
        private String headElementSelector;
        public MobileElement headElement;

        /**
         * @param dateItemIndex  Index of date in line (first - 0).
         */
        public DateItem(int dateItemIndex) {
            // Ex. android.widget.RelativeLayout[@index=0]
            DateItem.this.headElementSelector = String.format(DatePicker.this.
                dateItemHeadElementSelector, dateItemIndex);
            DateItem.this.headElement = DatePicker.this.driver.findElementByXPath(DateItem.this.
                headElementSelector);
        }

        /**
         * Returns name of day.
         * @return  Name of day.
         */
        public String getDayName() {
            String dayName = "";
            try {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                        fastImplicitWait, TimeUnit.MILLISECONDS);
                // android.widget.TextView[@resource-id="us.moviemates:id/tv_date_picker_day_name"]
                dayName = DateItem.this.headElement.
                    findElementById("us.moviemates:id/tv_date_picker_day_name").getText();
            }
            catch (NoSuchElementException e) {
            }
            finally {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                    platformDriver.implicitWait, TimeUnit.MILLISECONDS);
            }
            return dayName;
        }
        
        /**
         * Returns number of day.
         * @return  Number of day.
         */
        public int getDay() {
            int day = 0;
            try {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                        fastImplicitWait, TimeUnit.MILLISECONDS);
                // android.widget.TextView[@resource-id="us.moviemates:id/tv_date_picker_day"]
                day = Integer.parseInt(DateItem.this.headElement.
                    findElementById("us.moviemates:id/tv_date_picker_day").getText());
            }
            catch (NoSuchElementException e) {
            }
            finally {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                    platformDriver.implicitWait, TimeUnit.MILLISECONDS);
            }
            return day;
        }

        /**
         * Returns name of month.
         * @return  Name of month.
         */
        public String getMonthName() {
            String monthName = "";
            try {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                        fastImplicitWait, TimeUnit.MILLISECONDS);
                // android.widget.TextView[@resource-id="us.moviemates:id/tv_date_picker_month_name"]
                monthName = DateItem.this.headElement.
                    findElementById("us.moviemates:id/tv_date_picker_month_name").getText();
            }
            catch (NoSuchElementException e) {
            }
            finally {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                    platformDriver.implicitWait, TimeUnit.MILLISECONDS);
            }
            return monthName;
        }

        /**
         * Checks if date is marked as having interested movies.
         * @return  True if marked, false if not.
         */
        public boolean isSelected() {
            boolean isSelected = false;
            try {
                // It's fine to shorten implicit wait time as XML tree is already populated
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                    fastImplicitWait, TimeUnit.MILLISECONDS);
                // XPath workaround. Appium has bug not allowing finding ImageViews by class 
                // name/ID within element's context.
                DatePicker.this.driver.findElementByXPath(DateItem.this.headElementSelector + 
                    "//android.widget.ImageView[@resource-id='us.moviemates:id/iv_movie_date_select' or @resource-id='us.moviemates:id/iv_movie_date_active_select']"); 
                isSelected = true;
            }
            catch (NoSuchElementException e) {
            }
            finally {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                    platformDriver.implicitWait, TimeUnit.MILLISECONDS);
            }
            return isSelected;
        }

        /**
         * Checks if date is marked active.
         * @return  True if day is active, false if not.
         */
        public boolean isActive() {
            boolean isActive = false;
            try {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                    fastImplicitWait, TimeUnit.MILLISECONDS);
                // android.widget.ImageView[@resource-id="us.moviemates:id/iv_orange_background"]
                DatePicker.this.driver.findElementByXPath(DateItem.this.headElementSelector + 
                    "//android.widget.ImageView[@resource-id='us.moviemates:id/iv_orange_background']");
                isActive = true;
            }
            catch (NoSuchElementException e) {
            }
            finally {
                DatePicker.this.driver.manage().timeouts().implicitlyWait(DatePicker.this.
                    platformDriver.implicitWait, TimeUnit.MILLISECONDS);
            }
            return isActive;
        }

        /**
         * Checks if date's XML sub-tree is populated (rendered on screen). If only small portion 
         * of date item is rendered, its XML code is truncated.
         * @return True if complete, false if not.
         */
        public boolean isComplete() {
            boolean isComplete = true;
            if (DateItem.this.getMonthName().equals("") || DateItem.this.getDayName().equals("") || DateItem.this.getDay() == 0) {
                isComplete = false;
            }
            return isComplete;
        }

        /**
         * Selects date by tapping on it.
         */
        public void select() {
            if (! DateItem.this.isSelected()) {
                DateItem.this.headElement.tap(1, 0);
            }
        }
    }
}