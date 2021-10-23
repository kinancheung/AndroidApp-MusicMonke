package io.github.softeng3062021.team21.Adaptors;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import io.github.softeng3062021.team21.Activities.DetailsActivity;
import io.github.softeng3062021.team21.Models.Helper.ISpecialItem;
import io.github.softeng3062021.team21.Models.Helper.TopPick;
import io.github.softeng3062021.team21.Models.Helper.WishlistItem;
import io.github.softeng3062021.team21.Models.IncorrectSubTypeException;
import io.github.softeng3062021.team21.R;

/**
 * This class is used to adapt items to fit the custom card view for the wishlist activity.
 *
 * @author Kinan Cheung
 * @author Flynn Fromont
 * @author Maggie Pedersen
 */
public class WishlistAdaptor extends RecyclerView.Adapter<WishlistAdaptor.ViewHolder> {

    List<ISpecialItem> mItems;
    Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public WishlistAdaptor(List<ISpecialItem> objects) {
        mItems = objects;
    }

    /**
     * Create the correct ViewHolder for the card to be displayed.
     *
     * @param parent   the parent view.
     * @param viewType the type of the view.
     * @return the view holder that encapsulates the card view.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View wishlistView = inflater.inflate(R.layout.universal_item_card, parent, false);
        return new ViewHolder(wishlistView);
    }

    /**
     * Bind data to the card.
     *
     * @param holder   the ViewHolder that encapsulates the card view.
     * @param position the position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ISpecialItem currentItem = mItems.get(position);

        populateListItem(currentItem, holder);
    }

    /**
     * Get the number of items in the list.
     *
     * @return the size of the list.
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Populate view for the wishlist card layout.
     *
     * @param currentItem the current item whose details will be added to the card.
     * @param viewHolder  the view holder that encapsulates the card view.
     */
    private void populateListItem(ISpecialItem currentItem, ViewHolder viewHolder) {

        int logoResource = mContext.getResources().getIdentifier(currentItem.getLogoImage(), "drawable", mContext.getPackageName());
        viewHolder.itemImage.setImageResource(logoResource);

        viewHolder.itemTitle.setText(currentItem.getName());
        viewHolder.itemCategory.setText(this.getCardCategoryOutput(currentItem.getCategory()));
        int heartResource = mContext.getResources().getIdentifier("ic_baseline_favorite_30", "drawable", mContext.getPackageName());
        viewHolder.wishLikeButton.setImageResource(heartResource);

        // Toggle wishlist on click of the card view
        viewHolder.wishLikeButton.setOnClickListener(view -> {
            try {
                toggleWishlist(currentItem, viewHolder);
            } catch (IncorrectSubTypeException e) {
                e.printStackTrace();
            }
        });

        List<String> authors = currentItem.getAuthors();

        String authorString = "";

        int counter = 0;
        for (String host : authors) {
            if (counter == authors.size() - 1) {
                authorString += host;
            } else if (counter == authors.size() - 2) {
                authorString += host + " and ";
            } else {
                authorString += host + ", ";
            }
            counter++;
        }
        viewHolder.itemAuthor.setText(authorString);
    }

    /**
     * Return the item category formatted in a nice way for the card view.
     *
     * @param category the category of the item in string representation.
     * @return the formatted string.
     */
    public String getCardCategoryOutput(String category) {
        return category.substring(0, 1).toUpperCase() + category.substring(1, category.length() - 1);
    }

    /**
     * Add or remove item from wishlist, based on whether it is not there or there.
     *
     * @param item       the item that was displayed on the card view.
     * @param viewHolder the view holder that encapsulates the card view.
     * @throws IncorrectSubTypeException thrown if item does not implement method.
     */
    private void toggleWishlist(ISpecialItem item, ViewHolder viewHolder) throws IncorrectSubTypeException {
        // Retrieve all items from the wishlist
        db.collection("wishlists").document("wishlist1")
                .collection("items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();

                boolean changed = false;
                for (WishlistItem wishItem : results.toObjects(WishlistItem.class)) {
                    // If item id is the same as an item from the wishlist, remove the item
                    if (wishItem.getId().equals(item.getId())) {
                        // Decrement heart count of item in database
                        db.collection(item.getCategory()).document(item.getId())
                                .update("numHearts", 0).addOnCompleteListener(taskRemoveHeart -> {
                            if (!taskRemoveHeart.isSuccessful()) {
                                Toast.makeText(mContext, "Could not remove heart", Toast.LENGTH_LONG).show();
                            }
                        });

                        // Remove item from wishlist
                        db.collection("wishlists").document("wishlist1")
                                .collection("items").document(item.getId()).delete()
                                .addOnCompleteListener(taskDeleteItem -> {
                                    if (!taskDeleteItem.isSuccessful()) {
                                        Toast.makeText(mContext, "Couldnt delete item", Toast.LENGTH_LONG).show();
                                    }
                                });
                        removeTopPicks(item);
                        int heartResource = mContext.getResources().getIdentifier("ic_baseline_favorite_border_30", "drawable", mContext.getPackageName());
                        viewHolder.wishLikeButton.setImageResource(heartResource);
                        changed = true;
                    }
                }

                // If item is not in the wishlist, add the item
                if (!changed) {
                    WishlistItem addItem = new WishlistItem(item.getAuthors(), item.getCategory(),
                            item.getLogoImage(), item.getName());

                    // Add item to wishlist
                    db.collection("wishlists").document("wishlist1")
                            .collection("items").document(item.getId()).set(addItem)
                            .addOnCompleteListener(taskAddItem -> {
                                if (!taskAddItem.isSuccessful()) {
                                    Toast.makeText(mContext, "Could not add item", Toast.LENGTH_LONG).show();
                                }
                            });

                    // Increase heart count of item in database
                    db.collection(item.getCategory()).document(item.getId())
                            .update("numHearts", 1)
                            .addOnCompleteListener(taskAddHeart -> {
                                if (!taskAddHeart.isSuccessful()) {
                                    Toast.makeText(mContext, "Could not add heart", Toast.LENGTH_LONG).show();
                                }
                            });
                    addTopPicks(item);
                    int heartResource = mContext.getResources().getIdentifier("ic_baseline_favorite_30", "drawable", mContext.getPackageName());
                    viewHolder.wishLikeButton.setImageResource(heartResource);
                }
            }
        });
    }

    /**
     * Remove item from top picks collection in Firebase.
     */
    private void removeTopPicks(ISpecialItem item) {
        db.collection("topPicks").document("topPicks1").collection("items")
                .document(item.getId()).delete().addOnCompleteListener(taskDeleted -> {
            if (!taskDeleted.isSuccessful()) {
                Toast.makeText(mContext, "Could not remove from top picks", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Add item to top picks collection in Firebase.
     */
    private void addTopPicks(ISpecialItem item) {
        TopPick topPick = new TopPick(item.getCategory(), item.getLogoImage(), item.getName(), Timestamp.now().getSeconds());
        db.collection("topPicks").document("topPicks1").collection("items")
                .document(item.getId()).set(topPick).addOnCompleteListener(taskAddTopPick -> {
            if (!taskAddTopPick.isSuccessful()) {
                Toast.makeText(mContext, "Could not add to top picks", Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * Encapsulate the card view.
     */
    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemAuthor;
        TextView itemCategory;
        ImageButton wishLikeButton;

        public ViewHolder(View currentListViewItem) {
            super(currentListViewItem);
            currentListViewItem.setOnClickListener(this);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.card_animation);
            currentListViewItem.startAnimation(animation);

            itemImage = currentListViewItem.findViewById(R.id.item_image);
            itemTitle = currentListViewItem.findViewById(R.id.item_title);
            itemAuthor = currentListViewItem.findViewById(R.id.item_author);
            itemCategory = currentListViewItem.findViewById(R.id.item_category);
            wishLikeButton = currentListViewItem.findViewById(R.id.wish_like_button);
        }

        /**
         * Load the correct details for the clicked card view.
         *
         * @param view the card view that was clicked.
         */
        @Override
        public void onClick(View view) {
            String category = itemCategory.getText().toString();
            category = category.substring(0, 1).toLowerCase() + category.substring(1) + "s";

            Intent detailIntent = new Intent(mContext, DetailsActivity.class);

            detailIntent.putExtra("CATEGORY", category);
            detailIntent.putExtra("ITEM_TITLE", itemTitle.getText());

            mContext.startActivity(detailIntent);
        }
    }
}
