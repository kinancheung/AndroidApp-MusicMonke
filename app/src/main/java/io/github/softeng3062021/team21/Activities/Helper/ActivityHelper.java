package io.github.softeng3062021.team21.Activities.Helper;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.github.softeng3062021.team21.Models.Album;
import io.github.softeng3062021.team21.Models.Helper.ISpecialItem;
import io.github.softeng3062021.team21.Models.Helper.TopPick;
import io.github.softeng3062021.team21.Models.Helper.WishlistItem;
import io.github.softeng3062021.team21.Models.IItem;
import io.github.softeng3062021.team21.Models.IncorrectSubTypeException;
import io.github.softeng3062021.team21.Models.Song;

/**
 * The ActivityHelper class is used to remove duplicate code between some of the activity classes.
 * Where this duplicate code needs to be used, the activity class will create an instance of it
 * with the specific item, and then conduct the relevant tasks.
 *
 * @author  Kinan Cheung
 * @author  Flynn Fromont
 * @author  Maggie Pedersen
 */
public class ActivityHelper {

    private final IItem item;
    private final FirebaseFirestore db;
    private final Context context;

    public ActivityHelper(IItem item, FirebaseFirestore db, Context context) {
        this.item = item;
        this.db = db;
        this.context = context;
    }

    /**
     * Remove item from top picks collection in Firebase.
     */
    private void removeTopPicks() {
        db.collection("topPicks").document("topPicks1").collection("items")
                .document(item.getId()).delete().addOnCompleteListener(taskDeleted -> {
            if (!taskDeleted.isSuccessful()) {
                Toast.makeText(context, "Could not remove from top picks", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Add item to top picks collection in Firebase.
     */
    private void addTopPicks() {
        TopPick topPick = new TopPick(item.getCategoryCollection(), item.getLogoImage(), item.getName(), Timestamp.now().getSeconds());
        db.collection("topPicks").document("topPicks1").collection("items")
                .document(item.getId()).set(topPick).addOnCompleteListener(taskAddTopPick -> {
            if (!taskAddTopPick.isSuccessful()) {
                Toast.makeText(context, "Could not add to top picks", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Remove item from wishlist collection in Firebase.
     */
    private void removeItemFromWishlist() {
        // Decrease heart count for item
        db.collection(item.getCategoryCollection()).document(item.getId())
                .update("numHearts", 0).addOnCompleteListener(taskRemoveHeart -> {
            if (!taskRemoveHeart.isSuccessful()) {
                Toast.makeText(context, "Could not remove heart", Toast.LENGTH_LONG).show();
            }
        });

        // Delete item
        db.collection("wishlists").document("wishlist1")
                .collection("items").document(item.getId()).delete()
                .addOnCompleteListener(taskDeleteItem -> {
                    if (!taskDeleteItem.isSuccessful()) {
                        Toast.makeText(context, "Could not delete item", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Add item to wishlist.
     *
     * @param newItem a wishlist specific representation of the item to add.
     */
    private void addItemToWishlist(ISpecialItem newItem) {
        // Add item to wishlist
        db.collection("wishlists").document("wishlist1")
                .collection("items").document(item.getId()).set(newItem)
                .addOnCompleteListener(taskAddItem -> {
                    if (!taskAddItem.isSuccessful()) {
                        Toast.makeText(context, "Could not add item", Toast.LENGTH_LONG).show();
                    }
                });

        // Increase heart count of item in database
        db.collection(item.getCategoryCollection()).document(item.getId())
                .update("numHearts", 1)
                .addOnCompleteListener(taskAddHeart -> {
                    if (!taskAddHeart.isSuccessful()) {
                        Toast.makeText(context, "Could not add heart", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Add or remove item from wishlist, based on whether it is not there or there.
     *
     * @param likeButton button that toggles the wishlist.
     * @throws IncorrectSubTypeException thrown if item does not implement method.
     */
    public void toggleWishlist(ImageButton likeButton) throws IncorrectSubTypeException {
        List<String> artists = new ArrayList<>();
        if (item instanceof Song || item instanceof Album) {
            artists.add(item.getArtist());
        } else {
            artists = item.getHosts();
        }

        List<String> finalArtists = artists;

        // Retrieve all items from the wishlist
        db.collection("wishlists").document("wishlist1")
                .collection("items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();

                boolean changed = false;
                for (WishlistItem wishItem : results.toObjects(WishlistItem.class)) {
                    // If item id is the same as an item from the wishlist, remove the item
                    if (wishItem.getId().equals(item.getId())) {
                        removeItemFromWishlist();
                        removeTopPicks();
                        int heartResource = context.getResources().getIdentifier("ic_baseline_favorite_border_30", "drawable", context.getPackageName());
                        likeButton.setImageResource(heartResource);
                        changed = true;
                    }
                }

                // If item is not in the wishlist, add the item
                if (!changed) {
                    WishlistItem addItem = new WishlistItem(finalArtists, item.getCategoryCollection(),
                            item.getLogoImage(), item.getName());
                    addItemToWishlist(addItem);
                    addTopPicks();
                    int heartResource = context.getResources().getIdentifier("ic_baseline_favorite_30", "drawable", context.getPackageName());
                    likeButton.setImageResource(heartResource);
                }
            }
        });
    }
}
