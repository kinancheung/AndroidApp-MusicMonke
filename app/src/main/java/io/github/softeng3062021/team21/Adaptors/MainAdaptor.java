package io.github.softeng3062021.team21.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.softeng3062021.team21.Activities.DetailsActivity;
import io.github.softeng3062021.team21.Models.Helper.ISpecialItem;
import io.github.softeng3062021.team21.R;

/**
 * This class manages the views shown within MainActivity. It extends a RecyclerView to show the
 * TopPicks dynamically as well as set the logic for ListActivity view when selected
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public class MainAdaptor extends RecyclerView.Adapter<MainAdaptor.ViewHolder> {

    private final List<ISpecialItem> mItems;
    private Context mContext;

    public MainAdaptor(List<ISpecialItem> items) {
        mItems = items;
    }

    /**
     * This method is overriden from the parent class RecyclerView.Adapter and sets the layout
     * for the TopPicks by returning it.
     *
     * @param parent
     * @param viewType
     * @return ViewHolder holder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View topPickView = inflater.inflate(R.layout.top_pick_card, parent, false);
        ViewHolder holder = new ViewHolder(topPickView);
        return holder;
    }


    /**
     * This method is overriden from the parent class RecyclerView.Adapter and sets the components
     * within each TopPick item while adding onClick properties to go to the DetailsActivity of
     * the item.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ISpecialItem currentItem = mItems.get(position);
        int logoResource = mContext.getResources().getIdentifier(currentItem.getLogoImage(),
                "drawable", mContext.getPackageName());
        holder.itemImage.setImageResource(logoResource);
        holder.itemTitle.setText(currentItem.getName());

        holder.thisView.setOnClickListener(task -> {
            loadDetailsActivity(currentItem.getCategory(), currentItem.getName());
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * This method loads the DetailsActivity info for the item selected in TopPicks
     *
     * @param category
     * @param itemName
     */
    private void loadDetailsActivity(String category, CharSequence itemName) {
        Intent detailIntent = new Intent(mContext, DetailsActivity.class);

        detailIntent.putExtra("CATEGORY", category);
        detailIntent.putExtra("ITEM_TITLE", itemName);

        mContext.startActivity(detailIntent);
    }

    /**
     * This class contains the items within the TopPicks item, used for populating the item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        View thisView;

        public ViewHolder(View currentView) {
            super(currentView);
            thisView = currentView;
            itemImage = currentView.findViewById(R.id.item_image);
            itemTitle = currentView.findViewById(R.id.item_title);
        }
    }
}
