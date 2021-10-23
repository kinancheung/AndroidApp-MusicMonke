package io.github.softeng3062021.team21.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.github.softeng3062021.team21.Adaptors.ItemAdaptor;
import io.github.softeng3062021.team21.Models.Album;
import io.github.softeng3062021.team21.Models.IItem;
import io.github.softeng3062021.team21.Models.Podcast;
import io.github.softeng3062021.team21.Models.Song;
import io.github.softeng3062021.team21.R;

/**
 * This class encapsulates the logic for the ListActivity screen, which displays items from
 * different categories.
 *
 * @author  Kinan Cheung
 * @author  Flynn Fromont
 * @author  Maggie Pedersen
 */
public class ListActivity extends AppCompatActivity {

    ViewHolder viewHolder;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Initialise the view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Set up support for custom action bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        viewHolder = new ViewHolder();

        // Set correct category title, based on selected category
        String category = getIntent().getStringExtra("CATEGORY");
        viewHolder.categoryTitle.setText(category);

        // Fetch correct data, based on selected category
        switch (category) {
            case Song.SONG:
                fetchSongData();
                break;
            case Album.ALBUM:
                fetchAlbumData();
                break;
            case Podcast.PODCAST:
                fetchPodcastData();
                break;
            default:
                break;
        }
    }

    /**
     * Inflate menu resource.
     *
     * @param menu the menu to inflate.
     * @return boolean determined by superclass.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Execute correct action when an action item in the action bar is selected.
     *
      * @param item the selected action bar item.
     * @return boolean determined by superclass.
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
     * Override onStart to ensure that when an activity is resumed, the spinner does not show.
     */
    @Override
    protected void onStart() {
        viewHolder.spinner.setVisibility(View.GONE);
        super.onStart();
    }

    /**
     * Fetch data from the song collection in Firebase.
     */
    private void fetchSongData() {
        List<IItem> songs = new ArrayList<>();

        db.collection("songs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();

                for (IItem item : results.toObjects(Song.class)) {
                    songs.add(item);
                }

                if (songs.size() == 0) {
                    Toast.makeText(getBaseContext(), "Song collection was empty!", Toast.LENGTH_LONG).show();
                }

                // Propagate adaptor
                propagateSongAndAlbumAdaptor(songs);
                viewHolder.spinner.setVisibility(View.GONE);
            } else {
                Toast.makeText(getBaseContext(), "Loading song collection failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Fetch data from the album collection in Firebase.
     */
    private void fetchAlbumData() {
        List<IItem> albums = new ArrayList<>();

        db.collection("albums").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();

                for (IItem item : results.toObjects(Album.class)) {
                    albums.add(item);
                }

                if (albums.size() == 0) {
                    Toast.makeText(getBaseContext(), "Album collection was empty!", Toast.LENGTH_LONG).show();
                }

                // Propagate adaptor
                propagateSongAndAlbumAdaptor(albums);
                viewHolder.spinner.setVisibility(View.GONE);
            } else {
                Toast.makeText(getBaseContext(), "Loading album collection failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Fetch data from the podcast collection in Firebase.
     */
    private void fetchPodcastData() {
        List<IItem> podcasts = new ArrayList<>();

        db.collection("podcasts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();

                for (IItem item : results.toObjects(Podcast.class)) {
                    podcasts.add(item);
                }

                if (podcasts.size() == 0) {
                    Toast.makeText(getBaseContext(), "Podcast collection was empty!", Toast.LENGTH_LONG).show();
                }

                // Propagate adaptor
                propagatePodcastAdaptor(podcasts);
                viewHolder.spinner.setVisibility(View.GONE);
            } else {
                Toast.makeText(getBaseContext(), "Loading podcast collection failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Propagate item adaptor and set layout manager for recycler view.
     *
     * @param data the data to be adapted to a custom card view.
     */
    private void propagateSongAndAlbumAdaptor(List<IItem> data) {
        // Set layout manager to be linear, so we can display cards one after another
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewHolder.listView.setLayoutManager(layoutManager);

        ItemAdaptor itemAdaptor = new ItemAdaptor(data);
        viewHolder.listView.setAdapter(itemAdaptor);

        viewHolder.listView.setVisibility(View.VISIBLE);
    }

    /**
     * Propagate item adaptor and set layout manager for recycler view.
     *
     * @param data the data to be adapted to a custom card view.
     */
    private void propagatePodcastAdaptor(List<IItem> data) {
        // Set layout manager to be grid, so we can display cards side by side
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        viewHolder.podcastListView.setLayoutManager(layoutManager);

        ItemAdaptor itemAdaptor = new ItemAdaptor(data);
        viewHolder.podcastListView.setAdapter(itemAdaptor);

        viewHolder.podcastListView.setVisibility(View.VISIBLE);
    }

    /**
     * Encapsulate the view of the activity.
     */
    private class ViewHolder {
        RecyclerView listView;
        RecyclerView podcastListView;
        ProgressBar spinner;
        TextView categoryTitle;

        public ViewHolder() {
            listView = findViewById(R.id.list_view);
            podcastListView = findViewById(R.id.podcast_list_view);
            spinner = findViewById(R.id.spinner);
            categoryTitle = findViewById(R.id.category_title);
        }
    }
}