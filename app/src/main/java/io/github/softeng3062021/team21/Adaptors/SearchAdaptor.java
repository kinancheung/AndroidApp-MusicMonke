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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import io.github.softeng3062021.team21.Activities.DetailsActivity;
import io.github.softeng3062021.team21.Activities.Helper.ActivityHelper;
import io.github.softeng3062021.team21.Models.Album;
import io.github.softeng3062021.team21.Models.IItem;
import io.github.softeng3062021.team21.Models.IncorrectSubTypeException;
import io.github.softeng3062021.team21.Models.Podcast;
import io.github.softeng3062021.team21.Models.Song;
import io.github.softeng3062021.team21.R;

/**
 * This class is used to adapt items to fit the custom card view for the search activity.
 *
 * @author Kinan Cheung
 * @author Flynn Fromont
 * @author Maggie Pedersen
 */
public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.ViewHolder> {

    List<IItem> mItems;
    Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SearchAdaptor(List<IItem> objects) {
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

        View searchView = inflater.inflate(R.layout.universal_item_card, parent, false);
        return new ViewHolder(searchView);
    }

    /**
     * Bind data to the card.
     *
     * @param holder   the ViewHolder that encapsulates the card view.
     * @param position the position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IItem currentItem = mItems.get(position);

        // Populate view based on the type of the item
        if (currentItem.getClass() == Song.class || currentItem.getClass() == Album.class) {
            populateMusicListItem(currentItem, holder);
        } else if (currentItem.getClass() == Podcast.class) {
            populatePodcastListItem(currentItem, holder);
        }
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
     * Populate view with song or album data.
     *
     * @param currentItem the current item whose data is being added to the card view.
     * @param viewHolder  the view holder that encapsulates the card view.
     */
    private void populateMusicListItem(IItem currentItem, ViewHolder viewHolder) {
        populateCommonElements(currentItem, viewHolder);

        String artist = "";
        try {
            artist = currentItem.getArtist();
        } catch (IncorrectSubTypeException e) {
            e.printStackTrace();
        }
        viewHolder.itemAuthor.setText(artist);
    }

    /**
     * Populate view with podcast data.
     *
     * @param currentItem the current item whose data is being added to the card view.
     * @param viewHolder  the view holder that encapsulates the card view.
     * @return the updated view with the item data.
     */
    private void populatePodcastListItem(IItem currentItem, ViewHolder viewHolder) {
        populateCommonElements(currentItem, viewHolder);

        List<String> hosts = null;
        try {
            hosts = currentItem.getHosts();
        } catch (IncorrectSubTypeException e) {
            e.printStackTrace();
        }
        String hostString = "";

        int counter = 0;
        for (String host : hosts) {
            if (counter == hosts.size() - 1) {
                hostString += host;
            } else if (counter == hosts.size() - 2) {
                hostString += host + " and ";
            } else {
                hostString += host + ", ";
            }
            counter++;
        }
        viewHolder.itemAuthor.setText(hostString);
    }

    /**
     * Populate common elements of song, album and podcast items in card view.
     *
     * @param currentItem the current item whose data is being added to the card view.
     * @param viewHolder  view holder that encapsulates the custom card view.
     */
    private void populateCommonElements(IItem currentItem, ViewHolder viewHolder) {
        int logoResource = mContext.getResources().getIdentifier(currentItem.getLogoImage(), "drawable", mContext.getPackageName());
        viewHolder.itemImage.setImageResource(logoResource);

        if (currentItem.getNumHearts() == 1) {
            int heartResource = mContext.getResources().getIdentifier("ic_baseline_favorite_30", "drawable", mContext.getPackageName());
            viewHolder.wishLikeButton.setImageResource(heartResource);
        } else {
            int heartResource = mContext.getResources().getIdentifier("ic_baseline_favorite_border_30", "drawable", mContext.getPackageName());
            viewHolder.wishLikeButton.setImageResource(heartResource);
        }

        viewHolder.itemTitle.setText(currentItem.getName());
        viewHolder.itemCategory.setText(currentItem.getCategory());

        viewHolder.wishLikeButton.setOnClickListener(task -> {
            try {
                ActivityHelper activityHelper = new ActivityHelper(currentItem, db, mContext);
                activityHelper.toggleWishlist(viewHolder.wishLikeButton);
            } catch (IncorrectSubTypeException e) {
                e.printStackTrace();
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
