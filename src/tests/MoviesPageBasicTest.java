package tests;

import java.io.IOException;
import java.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import lib.mates.pom.PlatformDriver;
import lib.mates.pom.components.DatePicker;
import lib.mates.pom.components.MoviePicker;
import lib.mates.pom.components.MoviesTabBar;


public class MoviesPageBasicTest {
    private static PlatformDriver driver;

    @BeforeClass
    public static void suiteSetup() throws IOException, InterruptedException {
        MoviesPageBasicTest.driver = new PlatformDriver("HT44NWM01874", "http", "127.0.0.1", 4723, "/wd/hub", 5000);
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
    @Test
    public void verifyMoviesMarkedInterested() throws InterruptedException {
        for (DatePicker.DateItem date : new DatePicker(MoviesPageBasicTest.driver).getDates()) {
            date.select();
            new MoviePicker(MoviesPageBasicTest.driver).getMovies().get(0).interest();
            Thread.sleep(500);
            Assert.assertTrue(date.isSelected());
            Assert.assertTrue(date.isActive());
            new MoviePicker(MoviesPageBasicTest.driver).getMovies().get(0).uninterest();
            Thread.sleep(500);
            Assert.assertFalse(date.isSelected());
        }
    }

    @AfterClass
    public static void suiteTeardown() {
        MoviesPageBasicTest.driver.quit();
    }
}