package lib.mates.pom.components;

import java.util.ArrayList;
import java.util.List;
import io.appium.java_client.MobileElement;


/**
 * Models separate theater on movie details page.
 */
public class Cinema {
    // Element enclosing all child elements
    public MobileElement headElement;

    public Cinema(MobileElement headElement) {
        // android.widget.LinearLayout[@resource-id="us.moviemates:id/rlBigLayout"]
        this.headElement = headElement;
    }

    /**
     * Returns name of theater.
     * @return  Name of theater.
     */
    public String getName() {
        return this.headElement.findElementById("us.moviemates:id/tvAddress").getText();
    }

    /**
     * Opens a map app with location of theater.
     */
    public void openLocation() {
        this.headElement.findElementById("us.moviemates:id/tvAddress").tap(1, 0);
    }

    /**
     * Returns list of start times of movie.
     * @return  List of times.
     */
    public List<String> getTimes() {
        List<String> times = new ArrayList<String>();
        for (MobileElement containerElement : this.headElement.findElementsById("us.moviemates:id/llItemTime")) {
            times.add(containerElement.findElementById("us.moviemates:id/tvTime").getText());
        }
        return times;
    }

    /**
     * Opens Fandango page for buying ticket by tapping on movie start time.
     * @param time  Time to select.
     */
    public void selectTime(String time) {
        time = time.toUpperCase();
        for (MobileElement containerElement : this.headElement.findElementsById("us.moviemates:id/llItemTime")) {
            MobileElement itemElement = containerElement.findElementById("us.moviemates:id/tvTime");
            if (itemElement.getText().equals(time)) {
                itemElement.tap(1, 0);
                break;
            }
        }
    }

    /**
     * Inspects if specified time available or not in the theater.
     * @return  True if time available (marked red) or not (green)
     */
    public boolean isTimeAvailable(String time) {
        // Placeholder for future reference: on UI, there is no way to tell if time available 
        // (red) or not (green)
        return false;
    }
}
