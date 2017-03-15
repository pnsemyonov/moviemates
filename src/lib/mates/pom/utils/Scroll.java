package lib.mates.pom.utils;

import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import lib.mates.pom.components.Element;


/**
 *  Helper for list scroll on different pages by utilizing swipe(). 
 */
public class Scroll {
    /**
     * 
     * @param contentView  Frame element enclosing scrollable content.
     * @param element  Element to be scrolled to.
     * @param direction  Scroll direction: UP or DOWN.
     * @param timeout  Used to quit scroll after specified time (sec) when element not found.
     * @return  True if element found, false if not.
     */
    public static boolean scrollToElement(Element contentView, Element element, SwipeElementDirection direction, int timeout) {
        boolean result = false;
        long startTime = System.currentTimeMillis();
        // Scroll until element found or timeout reached
        while ((System.currentTimeMillis() - startTime) / 1000 < timeout) {
            if (element.isPresent()) {
                Scroll.alignElement(contentView, element);
                result = true;
                break;
            }
            Scroll.scrollHalfScreen(contentView, direction);
        }
        return result;
    }

    /**
     * Scroll content to that element be located at the top
     * @param contentView  Frame element enclosing scrollable content.
     * @param element  Element to be aligned with top border.
     */
    public static void alignElement(Element contentView, MobileElement element) {
        int startX = contentView.getMobileElement().getCenter().x;
        int startY = element.getLocation().y;
        int endY = contentView.getMobileElement().getLocation().y;
        // Swipe rate is 0.3 px/millisecond to avoid momentum scrolling
        int duration = Math.abs(endY - startY) * 3;
        contentView.driver.swipe(startX, startY, startX, endY, duration);
    }

    /**
     * Overloaded to use instance of Element class rather than MobileElement.
     * @param contentView  Frame element enclosing scrollable content.
     * @param element  Element to be aligned with top border.
     */
    public static void alignElement(Element contentView, Element element) {
        Scroll.alignElement(contentView, element.getMobileElement());
    }

    /**
     * Scrolls content in frame for 1/2 height in specified direction.
     * @param contentView  Frame element enclosing scrollable content.
     * @param direction  Scroll direction: UP or DOWN.
     */
    public static void scrollHalfScreen(Element contentView, SwipeElementDirection direction) {
        MobileElement contentElement = contentView.getMobileElement();
        int startX = contentElement.getCenter().x;
        int endY;
        if (direction == SwipeElementDirection.UP) {
            endY = contentElement.getLocation().y + 1;
        }
        else {
            endY = contentElement.getLocation().y + contentElement.getSize().height - 1;
        }
        int startY = contentElement.getCenter().y;
        // Swipe rate is 0.3 px/millisecond to avoid momentum scrolling
        int duration = Math.abs(endY - startY) * 3;
        contentView.driver.swipe(startX, startY, startX, endY, duration);
    }
}
