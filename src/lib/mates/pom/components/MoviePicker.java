package lib.mates.pom.components;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.NoSuchElementException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import lib.mates.pom.PlatformDriver;
import lib.mates.pom.utils.Convert;


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
        this.platformDriver = platformDriver;
        // Instance of AndroidDriver or IOSDriver
        this.driver = platformDriver.getDriver();
        // Populates "movies" with movie items
        this.getMovies();
    }

    /**
     * Calls movie item builder for each movie in list and stores them as collection.
     * @return  Collection of movie items visible in movie list.
     */
    public List<MovieItem> getMovies() {
        this.movies.clear();
        // Head element of container
        this.pickerHeadElement = 
            this.driver.findElementById("us.moviemates:id/pagerAdapterFromMovies");
        // Find number of movies in visible list
        int movieItemsNumber = 
            this.pickerHeadElement.findElementsById("us.moviemates:id/rlItemFilm").
            size();
        // Build movie items
        for (int movieItemIndex = 0; movieItemIndex < movieItemsNumber; movieItemIndex++) {
            this.movies.add(new MovieItem(movieItemIndex));
        }
        return this.movies;
    }

    public MovieItem findMovie(String name) {
        String firstMovieName = "";
        String movieName;
        while (true) {
            this.scrollDown(3);
            movieName = new MovieItem(0).getTitle();
            if (movieName.equals(firstMovieName)) {
                break;
            }
            else {
                firstMovieName = movieName;
            }
        }
        List<MovieItem> movies;
        String lastMovieName = "";
        MovieItem result = null;
        while (true) {
            movies = this.getMovies();
            for (MovieItem movie : movies) {
                if (movie.getTitle().toUpperCase().contains(name.toUpperCase())) {
                    result = movie;
                    break;
                }
            }
            if (result != null) {
                break;
            }
            else {
                if (movies.get(movies.size() - 1).getTitle().equals(lastMovieName)) {
                    break;
                }
                else {
                    lastMovieName = movies.get(movies.size() - 1).getTitle();
                    this.scrollUp(3);
                }
            }
        }
        return result;
    }

    /**
     * Swipes list of movies upward
     * @param steps  Swipe distance measured in movie items
     */
    public void scrollUp(int steps) {
        this.scroll(SwipeElementDirection.UP, steps);
    }

    /**
     * Swipes list of movies downward
     * @param steps  Swipe distance measured in movie items
     */
    public void scrollDown(int steps) {
        this.scroll(SwipeElementDirection.DOWN, steps);
    }

    /**
     * Swipes list of movies in specified direction
     * @param direction  UP or DOWN
     * @param steps  Swipe distance measured in number of movie items (rows)
     */
    private void scroll(SwipeElementDirection direction, int steps) {
        // Do not exceed number of visible movies
        if (steps <= this.movies.size() - 1) {
            int centerY = this.pickerHeadElement.getCenter().y;
            // Obtain height of one movie row
            int movieItemHeight = this.movies.get(1).headElement.getSize().height;
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
            int startX = this.pickerHeadElement.getCenter().x;
            this.driver.swipe(startX, startY, startX, endY, 500 * steps);
            // Update list of movies as they have been changed
            this.getMovies();
        }
        else {
            throw new IndexOutOfBoundsException(String.format("Scroll cannot exceed %d steps.", 
                this.movies.size() - 1));
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
                runTime = Convert.parseRunTime(elementText);
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
                peopleCount = Convert.parsePeopleCount(elementText);
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