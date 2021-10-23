package io.github.softeng3062021.team21.Activities;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.github.softeng3062021.team21.Adaptors.MainAdaptor;
import io.github.softeng3062021.team21.Models.Album;
import io.github.softeng3062021.team21.Models.Helper.ISpecialItem;
import io.github.softeng3062021.team21.Models.Helper.TopPick;
import io.github.softeng3062021.team21.Models.Podcast;
import io.github.softeng3062021.team21.Models.Song;
import io.github.softeng3062021.team21.R;

/**
 * This class is responsible for the views and logic within the MainActivity screen. This includes
 * initialising the TopPicks, calculated by items added to the Wishlist and date added and the
 * Categories of items Songs, Albums and Podcasts that go to their corresponding ListActivity.
 * A toolbar header is also present with the Logo, Wishlist button and Search button to find items.
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public class MainActivity extends AppCompatActivity {

    ViewHolder viewHolder;

    /**
     * This method is overriden from the parent class AppCompatActivity and initialises the views
     * first shown and logic for toppicks while setting the flow for the item categories
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        viewHolder = new ViewHolder();

        fetchTopPicksData();

        viewHolder.songsCard.setOnClickListener(view -> handleCategories(Song.SONG));
        viewHolder.albumsCard.setOnClickListener(view -> handleCategories(Album.ALBUM));
        viewHolder.podcastsCard.setOnClickListener(view -> handleCategories(Podcast.PODCAST));
    }

    /**
     * This method is overriden from the parent class AppCompatActivity and specifies the options
     * menu for the MainActivity.
     *
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView search = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is overriden from the parent class AppCompatActivity and specifies the options
     * menu logic to start the WishlistActivity or call the super default method.
     *
     * @param item
     * @return boolean
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
     * This method handles the logic for fetching the items in the TopPicks. It gets the data
     * from the FireStore database and organises the items by date added to the wishlist before
     * setting the view.
     *
     */
    private void fetchTopPicksData() {
        List<ISpecialItem> topPickItems = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("topPicks").document("topPicks1").collection("items")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot results = task.getResult();
                        for (TopPick topPickItem : results.toObjects(TopPick.class)) {
                            topPickItems.add(topPickItem);
                        }

                        propagateAdaptor(topPickItems);
                        viewHolder.spinner.setVisibility(View.GONE);
                        viewHolder.topPicksView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getBaseContext(), "Loading top picks collection failed from Firestore!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * This method passes in the items from the TopPicks and sets the adapter to the view to show
     * the items.
     *
     * @param data
     */
    private void propagateAdaptor(List<ISpecialItem> data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        viewHolder.topPicksView.setLayoutManager(layoutManager);

        MainAdaptor itemAdaptor = new MainAdaptor(data);
        viewHolder.topPicksView.setAdapter(itemAdaptor);
    }

    /**
     * This method is responsible for setting the type of Category when selected within
     * the MainActivity screen
     *
     * @param category
     */
    private void handleCategories(String category) {
        Intent intent = new Intent(getBaseContext(), ListActivity.class);
        intent.putExtra("CATEGORY", category);
        startActivity(intent);
    }

    /**
     * This class is responsible for populating the elements within the MainActivity screen, namely
     * the TopPicks and Categories.
     */
    private class ViewHolder {
        CardView songsCard, albumsCard, podcastsCard;
        RecyclerView topPicksView;
        ProgressBar spinner;


        public ViewHolder() {
            songsCard = findViewById(R.id.songs_category);
            albumsCard = findViewById(R.id.albums_category);
            podcastsCard = findViewById(R.id.podcasts_category);
            topPicksView = findViewById(R.id.top_picks);
            spinner = findViewById(R.id.spinner);
        }
    }
}