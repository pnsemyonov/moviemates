package lib.mates.pom.components;

import org.openqa.selenium.By;


/**
 * Info section of movie details page.
 */
public class InfoSection extends Section {
    public Element synopsisLabel;
    public Element synopsisText;

    public InfoSection(MovieDetails movieDetails) {
        super(movieDetails);
    }

    protected void defineElements() {
        this.headElement = new Element(this.movieDetails.platformDriver, new By.ById("us.moviemates:id/llInformation"));
        this.topElement = this.headElement.findElement(new By.ById("us.moviemates:id/tvInformationTitle"));
        this.synopsisLabel = this.topElement;
        this.synopsisText = this.headElement.findElement(new By.ById("us.moviemates:id/expandable_text"));
    }

    /**
     * Returns synopsis of movie.
     * @return  Brief summary of movie.
     */
    public String getSynopsis() {
        return this.synopsisText.getMobileElement().getText();
    }
}
