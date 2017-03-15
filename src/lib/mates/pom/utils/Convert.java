package lib.mates.pom.utils;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Format conversion.
 */
public class Convert {
    /**
     * Converts string representing time to instance of LocalTime.
     * @param text  String representation of time ("2hr 34min").
     * @return  Time as instance of LocalTime.
     */
    public static LocalTime parseRunTime(String text) {
        LocalTime runTime = null;
        // Ex. "2hr 34min"
        Pattern runTimePattern = Pattern.compile("([0-2]?\\d)hr\\s([0-5]?\\d)min");
        Matcher runTimeMatcher = runTimePattern.matcher(text);
        if (runTimeMatcher.matches()) {
            runTime = LocalTime.of(Integer.parseInt(runTimeMatcher.group(1)), Integer.
                parseInt(runTimeMatcher.group(2)));
        }
        return runTime;
    }

    /**
     * Returns number portion of string '<number> interested' as integer number. 
     * @param text  String representing number of people with interest to movie.
     * @return  Number of interesting people.
     */
    public static int parsePeopleCount(String text) {
        int peopleCount = 0;
        // Ex. "2 interested"
        Pattern peopleCountPattern = Pattern.compile("(\\d*)\\sinterested");
        Matcher peopleCountMatcher = peopleCountPattern.matcher(text);
        if (peopleCountMatcher.matches()) {
            peopleCount = Integer.parseInt(peopleCountMatcher.group(1));
        }
        return peopleCount;
    }
}
