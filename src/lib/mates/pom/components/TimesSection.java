package lib.mates.pom.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import lib.mates.pom.utils.Scroll;


/**
 * Section which lists times of movie in theater.
 *
 */
public class TimesSection extends Section {
    public Element titleTextElement;

    public TimesSection(MovieDetails movieDetails) {
        super(movieDetails);
    }

    protected void defineElements() {
        this.headElement = new Element(this.movieDetails.platformDriver, new By.ById("us.moviemates:id/wsListView"));
        this.topElement = new Element(this.movieDetails.platformDriver, new By.ById("us.moviemates:id/tvTitleMovieDetails"));
        this.titleTextElement = this.topElement;
    }

    /**
     * Returns text of header element of info section ("MOVIE TIMES... FEB 23").
     * @return  Text of section header.
     */
    public String getHeader() {
        return this.topElement.getMobileElement().getText();
    }

    /**
     * Scrolls to specified theater among those showing the movie.
     * @param name  Name of theater, full or partial.
     * @param timeout  Timeout after which to quit scroll if theater not found.
     * @return  Instance of Cinema if theater found, null if not.
     */
    public Cinema findCinema(String name, int timeout) {
        Cinema result = null;
        // Used to stop scrolling when last cinema in list is found.
        String lastName = "";
        boolean elementAligned = false;
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) / 1000 < timeout) {
            // Iterate over all cinemas shown on on movie details page at the moment.
            List<MobileElement> cinemas = new ArrayList<>();
            List<MobileElement> cinemaNames = new ArrayList<>();
            // Ugly workaround due to bug in Appium (finding within element context)
            for (int index = 1; index < Integer.MAX_VALUE; index++) {
                try {
                    this.movieDetails.driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
                    cinemas.add(this.movieDetails.driver.findElementByXPath(String.
                        format("//android.widget.LinearLayout[@resource-id='us.moviemates:id/rlBigLayout'][%d]//android.widget.RelativeLayout[@resource-id='us.moviemates:id/rlCard']", 
                        index)));
                    try {
                        cinemaNames.add(this.movieDetails.driver.findElementByXPath(String.
                            format("//android.widget.LinearLayout[@resource-id='us.moviemates:id/rlBigLayout'][%d]//android.widget.TextView[@resource-id='us.moviemates:id/tvAddress']", 
                            index)));
                    }
                    catch (NoSuchElementException e) {
                        cinemaNames.add(null);
                    }
                }
                catch (NoSuchElementException e) {
                    break;
                }
            }
            if (cinemas.isEmpty()) {
                throw new NoSuchElementException("Cinema elements not found.");
            }
            String cinemaName = "";
            Random randomSet = new Random();
            for (int index = 0; index < cinemas.size(); index++) {
                if (cinemaNames.get(index) != null) {
                    cinemaName = cinemaNames.get(index).getText().toLowerCase();
                }
                else {
                    cinemaName = Integer.toString(randomSet.nextInt());
                }
                // If name of the cinema found 
                if (cinemaName.contains(name.toLowerCase())) {
                    if (! elementAligned) {
                        // Adjust cinema element on screen with top border
                        Scroll.alignElement(this.movieDetails.contentView, cinemas.get(index));
                        elementAligned = true;
                    }
                    else {
                        result = new Cinema(cinemas.get(index));
                    }
                    break;
                }
            }
            // If cinema is found or scroll has reached bottom
            if ((result != null) || (lastName.equals(cinemaName))) {
                break;
            }
            if (! elementAligned) {
                lastName = cinemaName;
                Scroll.scrollHalfScreen(this.movieDetails.contentView, SwipeElementDirection.UP);
            }
        }
        this.movieDetails.driver.manage().timeouts().implicitlyWait(this.movieDetails.
            platformDriver.implicitWait, TimeUnit.MILLISECONDS);
        return result;
    }
}
