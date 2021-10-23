package io.github.softeng3062021.team21.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.github.softeng3062021.team21.Adaptors.WishlistAdaptor;
import io.github.softeng3062021.team21.Models.Helper.ISpecialItem;
import io.github.softeng3062021.team21.Models.Helper.WishlistItem;
import io.github.softeng3062021.team21.R;

/**
 * This class encapsulates the logic for the WishlistActivity screen, which displays items in a users
 * wishlist.
 *
 * @author  Kinan Cheung
 * @author  Flynn Fromont
 * @author  Maggie Pedersen
 */
public class WishlistActivity extends AppCompatActivity {

    ViewHolder viewHolder;

    /**
     * Initialise the view.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        viewHolder = new ViewHolder();

        fetchWishlistData();
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
        MenuItem wishlistItem = menu.findItem(R.id.action_wishlist);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        wishlistItem.setVisible(false);
        searchItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Fetch data from the wishlist collection in Firebase.
     */
    private void fetchWishlistData() {
        List<ISpecialItem> wishlistItems = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("wishlists").document("wishlist1").collection("items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot results = task.getResult();

                for (WishlistItem item : results.toObjects(WishlistItem.class)) {
                    wishlistItems.add(item);
                }

                if (wishlistItems.size() == 0) {
                    Toast.makeText(getBaseContext(), "Wishlist collection was empty!", Toast.LENGTH_LONG).show();
                }

                // Propagate adaptor
                propagateAdaptor(wishlistItems);
                viewHolder.spinner.setVisibility(View.GONE);
            } else {
                Toast.makeText(getBaseContext(), "Loading wishlist collection failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Propagate wishlist adaptor.
     *
     * @param data the data to be adapted to a custom card view.
     */
    private void propagateAdaptor(List<ISpecialItem> data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewHolder.wishlistView.setLayoutManager(layoutManager);

        WishlistAdaptor wishlistAdaptor = new WishlistAdaptor(data);
        viewHolder.wishlistView.setAdapter(wishlistAdaptor);

        viewHolder.wishlistView.setVisibility(View.VISIBLE);
    }

    /**
     * Encapsulate the view of the activity.
     */
    private class ViewHolder {
        RecyclerView wishlistView;
        ProgressBar spinner;

        public ViewHolder() {
            wishlistView = findViewById(R.id.wishlist_list_view);
            spinner = findViewById(R.id.spinner);
        }
    }
}