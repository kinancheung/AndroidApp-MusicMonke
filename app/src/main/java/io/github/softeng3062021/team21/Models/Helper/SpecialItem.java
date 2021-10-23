package io.github.softeng3062021.team21.Models.Helper;

import java.util.List;

/**
 * This class is the abstract class that implements ISpecialItem. It is the parent of the
 * WishlistItem class and TopPick class whose methods are inherited for populating/ initialising
 * the items to add to the database.
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public abstract class SpecialItem implements ISpecialItem {

    protected String name;
    protected String logoImage;
    protected String category;

    public SpecialItem() {
    }

    public SpecialItem(String name, String logoImage, String category) {
        this.name = name;
        this.logoImage = logoImage;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public String getCategory() {
        return category;
    }

    public abstract String getId();

    public abstract List<String> getAuthors();

    public abstract long getTimestamp();
}
