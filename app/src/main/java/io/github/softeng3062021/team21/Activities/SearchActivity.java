package io.github.softeng3062021.team21.Activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.github.softeng3062021.team21.Adaptors.SearchAdaptor;
import io.github.softeng3062021.team21.Models.Album;
import io.github.softeng3062021.team21.Models.IItem;
import io.github.softeng3062021.team21.Models.IncorrectSubTypeException;
import io.github.softeng3062021.team21.Models.Podcast;
import io.github.softeng3062021.team21.Models.Song;
import io.github.softeng3062021.team21.R;

/**
 * This class encapsulates the logic for the SearchActivity screen, which displays items from
 * based on a search string.
 *
 * @author  Kinan Cheung
 * @author  Flynn Fromont
 * @author  Maggie Pedersen
 */
public class SearchActivity extends AppCompatActivity {

    ViewHolder viewHolder;

    /**
     * Initialise the view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        viewHolder = new ViewHolder(this);

        handleIntent(getIntent());
    }

    /**
     * Handle new search query after view is created.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handle search query.
     *
     * @param intent the intent where the search query is made.
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            viewHolder.searchTitle.setText(viewHolder.searchTitle.getText() + " \"" + query + "\"");
            fetchAllCategoriesData(query.toLowerCase());
        }
    }

    /**
     * Fetch all category data from Firebase.
     *
     * @param query the search query string.
     */
    private void fetchAllCategoriesData(String query) {
        List<IItem> items = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create tasks for all three queries
        Task songsTask = db.collection("songs").get();
        Task albumsTask = db.collection("albums").get();
        Task podcastsTask = db.collection("podcasts").get();

        // Wait for all tasks to complete before doing anything
        Tasks.whenAllComplete(songsTask, albumsTask, podcastsTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Task<?>> tasks = task.getResult();
                for (Task queryTask : tasks) {
                    if (queryTask.isSuccessful()) {
                        for (QueryDocumentSnapshot document : (QuerySnapshot) queryTask.getResult()) {

                            // Map document to correct object of correct class
                            if (document.contains("songs")) {
                                items.add(document.toObject(Album.class));
                            } else if (document.contains("hosts")) {
                                items.add(document.toObject(Podcast.class));
                            } else {
                                items.add(document.toObject(Song.class));
                            }
                        }
                    }
                }
            }

            // Filter the list and propagate the adaptor
            List<IItem> filteredList = filterItems(query, items);
            propagateAdaptor(filteredList);

            viewHolder.spinner.setVisibility(View.GONE);
        });

    }

    /**
     * Filter all items from the database based on the users search query string.
     *
     * @param query The query to filter the search by.
     * @param items All items in the database.
     * @return A list of items that match the search.
     */
    private List<IItem> filterItems(String query, List<IItem> items) {
        List<IItem> filteredList = new ArrayList<>();

        for (IItem item : items) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();

            // If the item name or description contains the search string, add it to the list
            if (name.contains(query) || description.contains(query)) {
                filteredList.add(item);
            } else {
                // If the item artist/hosts contains the search string, add it to the list
                if (item instanceof Song) {
                    String artist = "";
                    try {
                        artist = item.getArtist().toLowerCase();
                    } catch (IncorrectSubTypeException e) {
                        e.printStackTrace();
                    }

                    if (artist.contains(query)) {
                        filteredList.add(item);
                    }
                } else if (item instanceof Album) {
                    String artist = "";
                    try {
                        artist = item.getArtist().toLowerCase();
                    } catch (IncorrectSubTypeException e) {
                        e.printStackTrace();
                    }

                    if (artist.contains(query)) {
                        filteredList.add(item);
                    } else {
                        List<String> songs = null;
                        try {
                            songs = item.getSongs();
                        } catch (IncorrectSubTypeException e) {
                            e.printStackTrace();
                        }

                        // If album songs contain the search string, add it to the list
                        for (String song : songs) {
                            if (song.toLowerCase().contains(query)) {
                                filteredList.add(item);
                                break;
                            }
                        }
                    }
                } else if (item instanceof Podcast) {
                    List<String> hosts = null;
                    try {
                        hosts = item.getHosts();
                    } catch (IncorrectSubTypeException e) {
                        e.printStackTrace();
                    }

                    for (String host : hosts) {
                        if (host.toLowerCase().contains(query)) {
                            filteredList.add(item);
                            break;
                        }
                    }
                }
            }
        }
        return filteredList;
    }

    /**
     * Propagate search adaptor, otherwise show the no results view.
     *
     * @param data the data to be adapted to a custom card view.
     */
    private void propagateAdaptor(List<IItem> data) {
        if (data.size() != 0) {
            SearchAdaptor searchAdaptor = new SearchAdaptor(data);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            viewHolder.listView.setLayoutManager(layoutManager);

            viewHolder.listView.setAdapter(searchAdaptor);
            viewHolder.listView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.noResultsText.setVisibility(View.VISIBLE);
            viewHolder.noResultsImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Encapsulate the view of the activity.
     */
    private class ViewHolder {
        RecyclerView listView;
        ProgressBar spinner;
        TextView searchTitle;
        TextView noResultsText;
        ImageView noResultsImage;
        ImageView backButton;

        public ViewHolder(Activity searchActivity) {
            listView = findViewById(R.id.list_view);
            spinner = findViewById(R.id.spinner);

            searchTitle = findViewById(R.id.search_title);
            noResultsText = findViewById(R.id.no_search_results_text);
            noResultsImage = findViewById(R.id.no_search_results_image);
            backButton = findViewById(R.id.back_button);


            backButton.setOnClickListener(view -> {
                Intent mainIntent = new Intent(searchActivity, MainActivity.class);
                startActivity(mainIntent);
                searchActivity.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        }
    }
}