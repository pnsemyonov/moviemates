package tests;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import io.appium.java_client.MobileElement;
import lib.mates.pom.PlatformDriver;
import lib.mates.pom.components.Cinema;
import lib.mates.pom.components.DatePicker;
import lib.mates.pom.components.MovieDetails;
import lib.mates.pom.components.MoviePicker;
import lib.mates.pom.components.MoviePicker.MovieItem;
import lib.mates.pom.components.MoviesTabBar;


public class MoviesPageBasicTest {
    private static PlatformDriver driver;

    @BeforeClass
    public static void suiteSetup() throws IOException, InterruptedException {
        MoviesPageBasicTest.driver = new PlatformDriver("HT44NWM01874", "http", "127.0.0.1", 4723, "/wd/hub", 15000);
    }

    @Ignore
    @Test
    public void signInGoogle() throws InterruptedException {
        MobileElement buttonSignInGoogle = driver.getDriver().findElementById("us.moviemates:id/btnGoogleLogin");
        buttonSignInGoogle.tap(1, 0);
        List<? extends MobileElement> accounts = driver.getDriver().findElementsByClassName("android.widget.CheckedTextView");
        accounts.get(1).tap(1, 0);
        driver.getDriver().findElementById("android:id/button1").tap(1, 0);
        Set<String> contexts = driver.getDriver().getContextHandles();
        for (String context : contexts) {
            System.out.println(context);
        }
        driver.getDriver().context(contexts.toArray()[1].toString());
        System.out.println("Context: WEBVIEW_us.moviemates");
        System.out.println("Trying to find edit box...");
        driver.getDriver().findElementById("identifierId").sendKeys("psemyonov");
        System.out.println("Edit box found");
    }

    /**
     * Asserts tab bar is present and tabs have proper titles.
     */
    @Test
    public void verifyTabBarPresent() {
        MoviesTabBar moviesTabBar = new MoviesTabBar(MoviesPageBasicTest.driver);
        Assert.assertTrue(moviesTabBar.moviesTab.getText().toUpperCase().equals("MOVIES"));
        Assert.assertTrue(moviesTabBar.matesTab.getText().toUpperCase().equals("MATES"));
        Assert.assertTrue(moviesTabBar.chatsTab.getText().toUpperCase().equals("CHATS"));
    }

    /**
     * Asserts date picker is populated with dates starting from current.
     */
    @Test
    public void verifyDatePickerPresent() {
        DatePicker datePicker = new DatePicker(MoviesPageBasicTest.driver);
        datePicker.scrollRight(3);
        datePicker.scrollRight(3);
        LocalDate checkDate = LocalDate.now();
        for (DatePicker.DateItem date : datePicker.getDates()) {
            if (date.isComplete()) {
                String checkMonthName = checkDate.getMonth().toString().substring(0, 3);
                String checkDayName = checkDate.getDayOfWeek().toString();
                int checkDay = checkDate.getDayOfMonth();
                Assert.assertTrue(date.getMonthName().equals(checkMonthName));
                Assert.assertTrue(date.getDayName().equals(checkDayName));
                Assert.assertTrue(date.getDay() == checkDay);
            }
            checkDate = checkDate.plusDays(1);
        }
    }

    /**
     * Asserts marking movie as interested causes marking appropriate date in date line.
     * @throws InterruptedException
     */
    @Ignore
    @Test
    public void verifyMoviesMarkedInterested() throws InterruptedException {
        for (DatePicker.DateItem date : new DatePicker(MoviesPageBasicTest.driver).getDates()) {
            date.select();
            new MoviePicker(MoviesPageBasicTest.driver).getMovies().get(0).interest();
            Assert.assertTrue(date.isSelected());
            Assert.assertTrue(date.isActive());
            new MoviePicker(MoviesPageBasicTest.driver).getMovies().get(0).uninterest();
            Assert.assertFalse(date.isSelected());
        }
    }

    @Test
    public void verifyMovieTime() throws InterruptedException {
        String movieName = "Fifty Shades Darker";
        MoviePicker moviePicker = new MoviePicker(MoviesPageBasicTest.driver);

        System.out.println("Finding movie...");
        MovieItem movie = moviePicker.findMovie(movieName);
        Assert.assertTrue(movie.getTitle().equals(movieName));
        System.out.println("Movie title: " + movie.getTitle());
        System.out.println("Movie run time: " + Integer.toString(movie.getRunTime().getHour()) + 
            ":" + Integer.toString(movie.getRunTime().getMinute()));
        System.out.println("People interested: " + Integer.toString(movie.getPeopleCount()));
        System.out.println("Is movie interested: " + Boolean.toString(movie.isInterested()));
        movie.interest();
        movie.uninterest();
        
        System.out.println("\nSelecting movie...");
        movie.select();
        MovieDetails movieDetails = new MovieDetails(MoviesPageBasicTest.driver);
        System.out.println("Locating times section...");
        movieDetails.timesSection.locate();
        String cinemaName = "AMC Saratoga 14";
        String movieTime = "8:40PM";
        
        System.out.println("\nFinding cinema...");
        Cinema cinema = movieDetails.timesSection.findCinema(cinemaName, 20);
        Assert.assertTrue(cinema.getName().equals(cinemaName));
        Assert.assertTrue(cinema.getTimes().contains(movieTime));
        System.out.println("Cinema name: " + cinema.getName());
        System.out.println("Times in cinema: " + cinema.getTimes().toString());

        System.out.println("\nSelecting time...");
        Assert.assertTrue(cinema != null);
        cinema.selectTime(movieTime);
        Thread.sleep(15000);
    }

    @AfterClass
    public static void suiteTeardown() {
        MoviesPageBasicTest.driver.quit();
    }
}