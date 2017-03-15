package lib.mates.pom.components;

import java.time.LocalTime;
import org.openqa.selenium.By;
import lib.mates.pom.utils.Convert;


/**
 * Title section on movie details page.
 */
public class TitleSection extends Section {
    public Element titleTextElement;
    public Element runTimeTextElement;
    public Element peopleCountTextElement;
    public Element actorsTextElement;
    public Element interestedTextElement;
    public Element interestedButtonElement;

    public TitleSection(MovieDetails movieDetails) {
        super(movieDetails);
    }

    protected void defineElements() {
        this.headElement = new Element(this.movieDetails.platformDriver, new By.ById("us.moviemates:id/rlContentMovieDetails"));
        this.topElement = this.headElement.findElement(new By.ById("us.moviemates:id/tvTitleFilm"));
        this.titleTextElement = this.topElement;
        this.runTimeTextElement = this.headElement.findElement(new By.ById("us.moviemates:id/tvWatch"));
        this.peopleCountTextElement = this.headElement.findElement(new By.ById("us.moviemates:id/tvPeopleCount"));
        this.actorsTextElement = this.headElement.findElement(new By.ById("us.moviemates:id/tvActors"));
        this.interestedTextElement = this.headElement.findElement(new By.ById("us.moviemates:id/tvButtonTitle"));
        this.interestedButtonElement = this.headElement.findElement(new By.ById("us.moviemates:id/ibInterested"));
    }

    /**
     * Returns text of top element of title section.
     * @return  Movie name.
     */
    public String getTitle() {
        return this.titleTextElement.getMobileElement().getText();
    }

    /**
     * Returns run time of movie.
     * @return  Hours (use .getHour()) and minutes (.getMinute()).
     */
    public LocalTime getRunTime() {
        // android.widget.TextView[@resource-id="us.moviemates:id/tvWatch"]
        String elementText = this.runTimeTextElement.getMobileElement().getText();
        return Convert.parseRunTime(elementText);
    }

    /**
     * Returns number of people who marked movie as interested.
     * @return  Number of interested people.
     */
    public int getPeopleCount() {
        // android.widget.TextView[@resource-id="us.moviemates:id/tvPeopleCount"]
        String elementText = this.peopleCountTextElement.getMobileElement().getText();
        return Convert.parsePeopleCount(elementText);
    }

    /**
     * Returns actors line of title section.
     * @return  Names of actors.
     */
    public String getActors() {
        // android.widget.TextView[@resource-id="us.moviemates:id/tvActors"]
        return this.actorsTextElement.getMobileElement().getText();
    }

    /**
     * Inspects if button 'Interested' is selected (orange).
     * @return  True if movie is marked interested by user, false if not.
     */
    public boolean isInterested() {
        boolean isInterested = false;
        if (this.headElement.findElement(new By.ById("us.moviemates:id/ivShadowInterested")).isPresent()) {
            isInterested = true;
        }
        return isInterested;
    }

    /**
     * Mark movie as interested (orange) by tapping on button 'Interested'.
     */
    public void interest() {
        this.switchInterest(true);
    }

    /**
     * Mark movie as not interested by tapping on button 'Interested' if it's marked orange.
     */
    public void uninterest() {
        this.switchInterest(false);
    }

    /**
     * Switch movie status (interested or not).
     * @param state  True if to interest in movie, false if disinterest.
     */
    private void switchInterest(boolean state) {
        // Perform action only if actual interest state is negative to required
        if (state != this.isInterested()) {
            // android.widget.ToggleButton[@resource-id="us.moviemates:id/tbButtonInterested"]
            this.interestedButtonElement.getMobileElement().tap(1, 0);
        }
    }
}
