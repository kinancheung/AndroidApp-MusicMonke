package io.github.softeng3062021.team21.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.softeng3062021.team21.Activities.DetailsActivity;
import io.github.softeng3062021.team21.Models.Album;
import io.github.softeng3062021.team21.Models.IItem;
import io.github.softeng3062021.team21.Models.IncorrectSubTypeException;
import io.github.softeng3062021.team21.Models.Podcast;
import io.github.softeng3062021.team21.Models.Song;
import io.github.softeng3062021.team21.R;

/**
 * This class is used to adapt items to fit the custom card view for their category.
 *
 * @author  Kinan Cheung
 * @author  Flynn Fromont
 * @author  Maggie Pedersen
 */
public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ViewHolder> {

    private final List<IItem> mItems;
    private Context mContext;

    public ItemAdaptor(List<IItem> items) {
        mItems = items;
    }

    /**
     * Create the correct ViewHolder for the card to be displayed.
     *
     * @param parent the parent view.
     * @param viewType the type of the view.
     * @return the view holder that encapsulates the card view.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the correct card view based on the item (view) type
        switch (viewType) {
            case Song.SONG_VIEW_TYPE:
                View songView = inflater.inflate(R.layout.list_activity_song_card, parent, false);
                return new SongViewHolder(songView);
            case Album.ALBUM_VIEW_TYPE:
                View albumView = inflater.inflate(R.layout.list_activity_album_card, parent, false);
                return new AlbumViewHolder(albumView);
            case Podcast.PODCAST_VIEW_TYPE:
                View podcastView = inflater.inflate(R.layout.list_activity_podcast_card, parent, false);
                return new PodcastViewHolder(podcastView);
        }

        return null;
    }

    /**
     * Bind data to the card.
     *
     * @param holder the ViewHolder that encapsulates the card view.
     * @param position the position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IItem currentItem = mItems.get(position);

        // Populate correct card view attributes based on item class
        if (currentItem.getClass() == Song.class) {
            populateSongListItem(currentItem, holder);
        } else if (currentItem.getClass() == Album.class) {
            populateAlbumListItem(currentItem, holder);
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
     * Get the view type of a specific item.
     *
     * @param position the position of the item in the list.
     * @return an integer representation of the item view type.
     */
    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    /**
     * Populate card if item is a song.
     *
     * @param currentItem the item whose data is to be displayed.
     * @param songViewHolder the view holder that encapsulates the song card view.
     */
    private void populateSongListItem(IItem currentItem, ViewHolder songViewHolder) {
        int logoResource = mContext.getResources().getIdentifier(currentItem.getLogoImage(), "drawable", mContext.getPackageName());
        songViewHolder.itemImage.setImageResource(logoResource);

        songViewHolder.itemTitle.setText(currentItem.getName());

        try {
            songViewHolder.itemArtist.setText(currentItem.getArtist());
        } catch (IncorrectSubTypeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populate card if item is an album.
     *
     * @param currentItem the item whose data is to be displayed.
     * @param albumViewHolder the view holder that encapsulates the album card view.
     */
    private void populateAlbumListItem(IItem currentItem, ViewHolder albumViewHolder) {
        int logoResource = mContext.getResources().getIdentifier(currentItem.getLogoImage(), "drawable", mContext.getPackageName());
        albumViewHolder.itemImage.setImageResource(logoResource);

        albumViewHolder.itemTitle.setText(currentItem.getName());

        List<String> songs = new ArrayList<>();
        try {
            albumViewHolder.itemArtist.setText(currentItem.getArtist());
            songs = currentItem.getSongs();
        } catch (IncorrectSubTypeException e) {
            e.printStackTrace();
        }

        String outputSongList = songs.size() + " songs";

        albumViewHolder.itemSongs.setText(outputSongList);
    }

    /**
     * Populate card if item is a podcast.
     *
     * @param currentItem the item whose data is to be displayed.
     * @param podcastViewHolder the view holder that encapsulates the podcast card view.
     */
    private void populatePodcastListItem(IItem currentItem, ViewHolder podcastViewHolder) {
        int logoResource = mContext.getResources().getIdentifier(currentItem.getLogoImage(), "drawable", mContext.getPackageName());

        podcastViewHolder.itemImage.setImageResource(logoResource);
        podcastViewHolder.itemTitle.setText(currentItem.getName());
    }

    /**
     * This method is used to load the DetailsActivity for a particular card if it is clicked.
     *
     * @param category the category the item is apart of.
     * @param itemName the name of the item.
     */
    private void loadDetailsActivity(String category, CharSequence itemName) {
        Intent detailIntent = new Intent(mContext, DetailsActivity.class);

        detailIntent.putExtra("CATEGORY", category);
        detailIntent.putExtra("ITEM_TITLE", itemName);

        mContext.startActivity(detailIntent);
    }

    /**
     * Encapsulate the card view.
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemArtist;
        TextView itemSongs;

        public ViewHolder(View currentListViewItem) {
            super(currentListViewItem);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.card_animation);
            currentListViewItem.startAnimation(animation);

            itemImage = currentListViewItem.findViewById(R.id.item_image);
            itemTitle = currentListViewItem.findViewById(R.id.item_title);
        }
    }

    /**
     * Encapsulate the song card view.
     */
    private class SongViewHolder extends ViewHolder implements View.OnClickListener {

        public SongViewHolder(View currentListViewItem) {
            super(currentListViewItem);
            currentListViewItem.setOnClickListener(this);
            itemArtist = currentListViewItem.findViewById(R.id.item_artist);
        }

        /**
         * Load song details on click.
         * @param view the card view.
         */
        @Override
        public void onClick(View view) {
            loadDetailsActivity(Song.SONG_COLLECTION, itemTitle.getText());
        }
    }

    /**
     * Encapsulate the podcast card view.
     */
    private class PodcastViewHolder extends ViewHolder implements View.OnClickListener {

        public PodcastViewHolder(View currentListViewItem) {
            super(currentListViewItem);
            currentListViewItem.setOnClickListener(this);
        }

        /**
         * Load podcast details on click.
         * @param view the card view.
         */
        @Override
        public void onClick(View view) {
            loadDetailsActivity(Podcast.PODCAST_COLLECTION, itemTitle.getText());
        }
    }

    /**
     * Encapsulate the album card view.
     */
    private class AlbumViewHolder extends ViewHolder implements View.OnClickListener {

        public AlbumViewHolder(View currentListViewItem) {
            super(currentListViewItem);
            currentListViewItem.setOnClickListener(this);
            itemArtist = currentListViewItem.findViewById(R.id.item_artist);
            itemSongs = currentListViewItem.findViewById(R.id.item_songs);
        }

        /**
         * Load album details on click.
         * @param view the card view.
         */
        @Override
        public void onClick(View view) {
            loadDetailsActivity(Album.ALBUM_COLLECTION, itemTitle.getText());
        }
    }
}
