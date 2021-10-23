package io.github.softeng3062021.team21.Models;

/**
 * This Exception class is thrown when an Item method is called on the incorrect category, with
 * Songs, Albums and Podcasts having unique methods that are not shared
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public class IncorrectSubTypeException extends Exception {
    public IncorrectSubTypeException(String errorMessage) {
        super(errorMessage);
    }
}
