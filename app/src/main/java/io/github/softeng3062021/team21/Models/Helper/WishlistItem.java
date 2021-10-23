package io.github.softeng3062021.team21.Models.Helper;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * This class extends SpecialItem and is responsible for populating the items in the FireStore
 * database handling the Wishlist.
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public class WishlistItem extends SpecialItem {

    @DocumentId
    private String id;
    private List<String> authors;

    public WishlistItem() {
    }

    public WishlistItem(List<String> authors, String category, String logoImage, String name) {
        super(name, logoImage, category);
        this.authors = authors;
    }

    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @Exclude
    public long getTimestamp() {
        return 0;
    }
}
