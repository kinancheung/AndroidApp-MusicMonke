package io.github.softeng3062021.team21.Models.Helper;

import java.util.List;

/**
 * This class is the interface for SpecialItem that contains methods specifically for populating
 * the WishlistItem and TopPick (Item).
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public interface ISpecialItem {

    String getLogoImage();

    String getName();

    String getCategory();

    String getId();

    List<String> getAuthors();

    long getTimestamp();
}
