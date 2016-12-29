package lib.mates.pom.components;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.NoSuchElementException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import lib.mates.pom.PlatformDriver;


/**
 * Models list of movies on "Movies". Serves as container for movie items and performs movie list 
 * scrolling. Does not perform movie-specific actions such as selecting movie, getting details etc.
 */
public class MoviePicker {
    // Wrapper of AppiumDriver
    private PlatformDriver platformDriver;
    private AppiumDriver<? extends MobileElement> driver;
    // Head element of movie list's container
    private MobileElement pickerHeadElement;
    // XPath workaround. When possible, elements are found by resource ID, though finding 
    // ImageViews by ID gives wrong results within element's context.
    private String movieItemHeadElementSelector = 
        "//android.widget.ListView[@resource-id='us.moviemates:id/listView']/android.widget.RelativeLayout[@index=%d]";
    // Collection of movies in visible portion of list
    private ArrayList<MovieItem> movies = new ArrayList<>();
    // Shorten wait time for better performance when applicable
    private int fastImplicitWait = 1000;

    /**
     * @param platformDriver  Tailored wrapper of AppiumDriver.
     */
    public MoviePicker(PlatformDriver platformDriver) {
        MoviePicker.this.platformDriver = platformDriver;
        // Instance of AndroidDriver or IOSDriver
        MoviePicker.this.driver = platformDriver.getDriver();
        // Populates "movies" with movie items
        MoviePicker.this.getMovies();
    }

    /**
     * Calls movie item builder for each movie in list and stores them as collection.
     * @return  Collection of movie items visible in movie list.
     */
    public List<MovieItem> getMovies() {
        MoviePicker.this.movies.clear();
        // Head element of container
        MoviePicker.this.pickerHeadElement = 
            MoviePicker.this.driver.findElementById("us.moviemates:id/pagerAdapterFromMovies");
        // Find number of movies in visible list
        int movieItemsNumber = 
            MoviePicker.this.pickerHeadElement.findElementsById("us.moviemates:id/rlItemFilm").
            size();
        // Build movie items
        for (int movieItemIndex = 0; movieItemIndex < movieItemsNumber; movieItemIndex++) {
            MoviePicker.this.movies.add(new MovieItem(movieItemIndex));
        }
        return MoviePicker.this.movies;
    }

    /**
     * Swipes list of movies upward
     * @param steps  Swipe distance measured in movie items
     */
    public void scrollUp(int steps) {
        MoviePicker.this.scroll(SwipeElementDirection.UP, steps);
    }

    /**
     * Swipes list of movies downward
     * @param steps  Swipe distance measured in movie items
     */
    public void scrollDown(int steps) {
        MoviePicker.this.scroll(SwipeElementDirection.DOWN, steps);
    }

    /**
     * Swipes list of movies in specified direction
     * @param direction  UP or DOWN
     * @param steps  Swipe distance measured in number of movie items (rows)
     */
    private void scroll(SwipeElementDirection direction, int steps) {
        // Do not exceed number of visible movies
        if (steps <= MoviePicker.this.movies.size() - 1) {
            int centerY = MoviePicker.this.pickerHeadElement.getCenter().y;
            // Obtain height of one movie row
            int movieItemHeight = MoviePicker.this.movies.get(1).headElement.getSize().height;
            int startY;
            int endY;
            if (direction == SwipeElementDirection.UP) {
                // Move starting point half-way down from list's center
                startY = centerY + (movieItemHeight * steps / 2);
                // Swipe ends at half-way up from center
                endY = startY - (movieItemHeight * steps);
            }
            else {
                startY = centerY - (movieItemHeight * steps / 2);
                endY = startY + (movieItemHeight * steps);
            }
            int startX = MoviePicker.this.pickerHeadElement.getCenter().x;
            MoviePicker.this.driver.swipe(startX, startY, startX, endY, 500 * steps);
            // Update list of movies as they have been changed
            MoviePicker.this.getMovies();
        }
        else {
            throw new IndexOutOfBoundsException(String.format("Scroll cannot exceed %d steps.", 
                MoviePicker.this.movies.size() - 1));
        }
    }


    /**
     * Implements individual movie in the list. Performs movie-specific actions (getting details, 
     * selecting etc.)
     */
    public class MovieItem {
        // android.widget.RelativeLayout[@resource-id="us.moviemates:id/rlItemFilm"]
        private String headElementSelector;
        private MobileElement headElement;
        // Ex. "2hr 34min"
        private Pattern runTimePattern = Pattern.compile("([0-2]?\\d)hr\\s([0-5]?\\d)min");
        // Ex. "2 interested"
        private Pattern peopleCountPattern = Pattern.compile("(\\d*)\\sinterested");
        
        /**
         * @param movieItemIndex  Index of movie in list (first - 0).
         */
        public MovieItem(int movieItemIndex) {
            // Ex. android.widget.RelativeLayout[@index=0]
            MovieItem.this.headElementSelector = String.format(MoviePicker.this.
                movieItemHeadElementSelector, movieItemIndex);
            MovieItem.this.headElement = MoviePicker.this.driver.findElementByXPath(MovieItem.this.
                headElementSelector);
        }

        /**
         * Returns title of movie.
         * @return  Movie title.
         */
        public String getTitle() {
            String title = null;
            try {
                // android.widget.TextView[@resource-id="us.moviemates:id/tvTitle"]
                title = MovieItem.this.headElement.findElementById("us.moviemates:id/tvTitle").
                    getText();
            }
            catch (NoSuchElementException e) {
            }
            return title;
        }

        /**
         * Returns run time of movie.
         * @return  Hours (.getHour()) and minutes (.getMinute()).
         */
        public LocalTime getRunTime() {
            LocalTime runTime = null;
            try {
                // android.widget.TextView[@resource-id="us.moviemates:id/tvRunTime"]
                String elementText = MovieItem.this.headElement.
                    findElementById("us.moviemates:id/tvRunTime").getText();
                Matcher runTimeMatcher = MovieItem.this.runTimePattern.matcher(elementText);
                if (runTimeMatcher.matches()) {
                    runTime = LocalTime.of(Integer.parseInt(runTimeMatcher.group(1)), 
                        Integer.parseInt(runTimeMatcher.group(2)));
                }
            }
            catch (NoSuchElementException e) {
            }
            return runTime;
        }

        /**
         * Returns number of people interested in watching movie.
         * @return  Number of people.
         */
        public int getPeopleCount() {
            int peopleCount = 0;
            try {
                // android.widget.TextView[@resource-id="us.moviemates:id/tvPeopleCount"]
                String elementText = MovieItem.this.headElement.
                    findElementById("us.moviemates:id/tvPeopleCount").getText();
                Matcher peopleCountMatcher = MovieItem.this.peopleCountPattern.
                    matcher(elementText);
                if (peopleCountMatcher.matches()) {
                    peopleCount = Integer.parseInt(peopleCountMatcher.group(1));
                }
            }
            catch (NoSuchElementException e) {
            }
            return peopleCount;
        }

        /**
         * Checks if movie is marked as Interested.
         * @return  True if interested, false if not.
         */
        public boolean isInterested() {
            boolean isInterested = false;
            try {
                // Element marking interest may be found or may not, it's not expected to be.
                // At this point XML tree is fully rendered, so it's fine to decrease implicit wait 
                // time for better performance.
                MoviePicker.this.driver.manage().timeouts().implicitlyWait(MoviePicker.this.
                    fastImplicitWait, TimeUnit.MILLISECONDS);
                // XPath workaround. Appium has bug not allowing finding ImageViews by class 
                // name/ID within element's context.
                MoviePicker.this.driver.findElementByXPath(MovieItem.this.headElementSelector + 
                "//android.widget.ImageView[@resource-id='us.moviemates:id/ivShadow']");
                isInterested = true;
            }
            catch (NoSuchElementException e) {
            }
            finally {
                MoviePicker.this.driver.manage().timeouts().implicitlyWait(MoviePicker.this.
                    platformDriver.implicitWait, TimeUnit.MILLISECONDS);
            }
            return isInterested;
        }

        /**
         * Opens movie details page by tapping on movie.
         */
        public void select() {
            MovieItem.this.headElement.tap(1, 0);
        }

        /**
         * Mark movie as interested by tapping on button.
         */
        public void interest() {
            MovieItem.this.switchInterest(true);
        }

        /**
         * Mark movie as not interested by tapping on button.
         */
        public void uninterest() {
            MovieItem.this.switchInterest(false);
        }

        private void switchInterest(boolean state) {
            // Perform action only if actual interest state is opposite to required
            if (state != MovieItem.this.isInterested()) {
                // android.widget.ToggleButton[@resource-id="us.moviemates:id/tbButtonInterested"]
                MovieItem.this.headElement.findElementById("us.moviemates:id/tbButtonInterested").
                    tap(1, 0);
            }
        }
    }
}