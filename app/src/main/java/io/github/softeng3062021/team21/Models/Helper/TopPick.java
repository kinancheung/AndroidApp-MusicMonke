package io.github.softeng3062021.team21.Models.Helper;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * This class extends SpecialItem and is responsible for populating the items in the FireStore
 * database handling the TopPicks.
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public class TopPick extends SpecialItem {

    @DocumentId
    private String id;
    private long timestamp;

    public TopPick() {
    }

    public TopPick(String category, String logoImage, String name, long timestamp) {
        super(name, logoImage, category);
        this.timestamp = timestamp;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @Exclude
    public List<String> getAuthors() {
        return null;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
