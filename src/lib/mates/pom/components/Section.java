package lib.mates.pom.components;


/**
 *  Base class for sections on movie details page (title, info, times sections).
 */
public abstract class Section {
    public MovieDetails movieDetails;
    // Element of hierarchy enclosing all other elements of given section
    public Element headElement;
    // Top visible element of given section
    public Element topElement;

    public Section(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
        this.defineElements();
    }

    protected abstract void defineElements();

    /**
     * Scrolls to top element of given section.
     */
    public void locate() {
        this.movieDetails.scrollToSection(this, 20);
    }
}
