package io.github.softeng3062021.team21.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import io.github.softeng3062021.team21.Activities.Helper.ActivityHelper;
import io.github.softeng3062021.team21.Activities.Helper.ZoomOutPageTransformer;
import io.github.softeng3062021.team21.Adaptors.ImagePagerAdapter;
import io.github.softeng3062021.team21.Models.Album;
import io.github.softeng3062021.team21.Models.IItem;
import io.github.softeng3062021.team21.Models.IncorrectSubTypeException;
import io.github.softeng3062021.team21.Models.Podcast;
import io.github.softeng3062021.team21.Models.Song;
import io.github.softeng3062021.team21.R;

/**
 *  The DetailsActivity class handles the logic for all the DetailActivity screens, which displays
 *  detailed information about a particular object, and includes the option to add/remove the items from
 *  the users wishlist. there is the added Functionality of being able to play an audio sample
 *
 * @author Flynn Fromont
 * @author Kinan Cheung
 * @author Maggie Pedersen
 */
public class DetailsActivity extends AppCompatActivity {

    ViewHolder viewHolder;
    MediaPlayer mediaPlayer;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * This method is overriden from the AppCompatActivity and it
     * Intialises the view of the detailActivity, and queries the database for relevant information
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        viewHolder = new ViewHolder();

        String category = getIntent().getStringExtra("CATEGORY");
        String itemName = getIntent().getStringExtra("ITEM_TITLE");

        //Firebase query to get the information for the required item from the correct category
        db.collection(category).whereEqualTo("name", itemName).get().addOnCompleteListener(task -> {
            IItem detailItem = null;

            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();

                switch (category) {
                    case Song.SONG_COLLECTION:
                        for (IItem item : results.toObjects(Song.class)) {
                            detailItem = item;
                        }
                        break;
                    case Album.ALBUM_COLLECTION:
                        for (IItem item : results.toObjects(Album.class)) {
                            detailItem = item;
                        }
                        break;
                    case Podcast.PODCAST_COLLECTION:
                        for (IItem item : results.toObjects(Podcast.class)) {
                            detailItem = item;
                        }
                        break;
                    default:
                        detailItem = null;
                        break;
                }

                IItem finalDetailItem = detailItem;

                viewHolder.likeItem.setOnClickListener(view -> {
                    try {
                        ActivityHelper activityHelper = new ActivityHelper(finalDetailItem, db, getBaseContext());
                        activityHelper.toggleWishlist(viewHolder.likeItem);
                    } catch (IncorrectSubTypeException e) {
                        e.printStackTrace();
                    }
                });
                populateDetailsActivity(detailItem);
            } else {
                Toast.makeText(getBaseContext(), "Loading item details failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method is overriden from the parent AppCompatActivity and it
     * Inflates the menu bar resource at the top of the screen
     *
     * @param menu the menu to inflate
     * @return boolean determined by the superclass
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is overriden from the parent AppCompatActivity and it
     * Executes the correct action for the selected action item,
     *
     * @param item the selected item in the actionbar
     * @return boolean determined by the superclass
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_wishlist:
                Intent wishlistIntent = new Intent(this, WishlistActivity.class);
                startActivity(wishlistIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Ensures that the mediaPlayer stops playing audio if the user clicks the back button, this is
     * an overriden method from the AppCompactActivity, and is called when the user selects the back button
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     *  This will populate the DetailActivity views with the information of the selected item
     * @param detailItem the item with the information to use in popoulating
     */
    private void populateDetailsActivity(IItem detailItem) {

        String creators = "";
        //Get the correct creator string from the item depending on its type
        if (detailItem instanceof Song || detailItem instanceof Album) {
            try {
                creators = detailItem.getArtist();
            } catch (IncorrectSubTypeException e) {
                e.printStackTrace();
            }
        } else {
            int counter = 0;
            try {
                List<String> hosts = detailItem.getHosts();
                for (String host : hosts) {
                    if (counter == hosts.size() - 2) {
                        creators += host + " and ";
                    } else if (counter == hosts.size() - 1) {
                        creators += host;
                    } else {
                        creators += host + ", ";
                    }
                    counter++;
                }
            } catch (IncorrectSubTypeException e) {
                e.printStackTrace();
            }
        }

        String description = detailItem.getDescription();

        //include the list of songs in the description
        if (detailItem instanceof Album) {
            String outputSongList = "";
            int counter = 1;
            try {
                for (String song : detailItem.getSongs()) {
                    outputSongList += counter + ". " + song + "\n";
                    counter++;
                }
                description += "\n\n This album includes the following songs:\n" + outputSongList;
            } catch (IncorrectSubTypeException e) {
                e.printStackTrace();
            }
        //includes the number of episodes into the description
        } else if (detailItem instanceof Podcast) {
            try {
                long numOfEpisodes = detailItem.getNumEpisodes();
                description += "\n\n This podcast has " + numOfEpisodes + " episodes.";
            } catch (IncorrectSubTypeException e) {
                e.printStackTrace();
            }
        }
        viewHolder.itemDescription.setText(description);
        viewHolder.itemDescription.setMovementMethod(new ScrollingMovementMethod());

        viewHolder.itemCreator.setText(creators);
        viewHolder.itemName.setText(detailItem.getName());

        viewHolder.itemPrice.setText("$" + detailItem.getPrice());

        if (detailItem.getNumHearts() == 1) {
            int heartResource = this.getResources().getIdentifier("ic_baseline_favorite_30", "drawable", this.getPackageName());
            viewHolder.likeItem.setImageResource(heartResource);
        }

        //setup the viewPager adapter, which handles the setup and display of images
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, detailItem.getImages());
        viewHolder.viewPager.setAdapter(imagePagerAdapter);
        //the page transformer adds an animation to the sliding of the viewPager
        viewHolder.viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        //the image tabs show which image out of all of them is currently displayed
        viewHolder.imgTabs.setupWithViewPager(viewHolder.viewPager, true);

        setupPlaySampleBtn(detailItem.getAudioSample());

        viewHolder.spinner.setVisibility(View.GONE);
        viewHolder.detailContainer.setVisibility(View.VISIBLE);

    }

    /**
     * setup for the handling of playing the audio sample
     * @param song
     */
    private void setupPlaySampleBtn(String song) {

        viewHolder.playSample.setOnClickListener(view -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            int songResource = getResources().getIdentifier(song, "raw", getPackageName());
            mediaPlayer = MediaPlayer.create(this, songResource);

            viewHolder.playSample.setImageResource(R.drawable.detail_pause_sample);
            mediaPlayer.start();

            // While the audio is playing, set the listener to stop the media player, and reset the listener
            viewHolder.playSample.setOnClickListener(pauseView -> {
                mediaPlayer.stop();
                viewHolder.playSample.setImageResource(R.drawable.detail_play_sample);
                setupPlaySampleBtn(song);
            });

            // Setup the image to change back to the play button, upon finishing the audio
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                viewHolder.playSample.setImageResource(R.drawable.detail_play_sample);
            });
        });
    }

    /**
     * Encapsulate the view of the activity.
     */
    private class ViewHolder {
        TextView itemName;
        ImageButton likeItem;
        TextView itemCreator;
        TextView itemPrice;
        TextView itemDescription;
        ViewPager viewPager;
        ProgressBar spinner;
        LinearLayout detailContainer;
        TabLayout imgTabs;
        ImageButton playSample;

        public ViewHolder() {
            itemName = findViewById(R.id.detail_item_name);
            likeItem = findViewById(R.id.detail_like_button);
            itemCreator = findViewById(R.id.detail_creator);
            itemPrice = findViewById(R.id.detail_price);
            itemDescription = findViewById(R.id.detail_item_description);
            viewPager = findViewById(R.id.detail_viewpager);
            spinner = findViewById(R.id.detail_spinner);
            detailContainer = findViewById(R.id.detail_data_container);
            imgTabs = findViewById(R.id.detail_img_tabs);
            playSample = findViewById(R.id.detail_play_sample);
        }

    }


}
