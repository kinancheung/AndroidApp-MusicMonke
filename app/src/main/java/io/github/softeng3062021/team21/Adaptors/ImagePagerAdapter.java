package io.github.softeng3062021.team21.Adaptors;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import io.github.softeng3062021.team21.R;

/**
 * This class adapts the images related to a specific item so that they can be displayed correctly
 *
 * @author  Kinan Cheung
 * @author  Flynn Fromont
 * @author  Maggie Pedersen
 */
public class ImagePagerAdapter extends PagerAdapter {
    private final Context mContext;
    private final List<String> imageFiles;

    public ImagePagerAdapter(Context context, List<String> images) {
        mContext = context;
        imageFiles = images;
    }

    /**
     * This will instantiate the the ViewGroup with the correct layout, displaying the correct image
     * that is scaled at the correct size.
     *
     * @param collection the view collection that is displayed
     * @param position the position of which image string from the list of image files
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.detail_image, collection, false);
        ImageView imageView1 = (ImageView) layout.findViewById(R.id.detail_image_view);
        int logoResource = mContext.getResources().getIdentifier(imageFiles.get(position), "drawable", mContext.getPackageName());
        imageView1.setImageResource(logoResource);
        imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        collection.addView(layout);
        return layout;
    }

    /**
     * Given the collection of views and a specific view, it will remove the view from the collection
     * @param collection the collection of views
     * @param position if it was a list of views, the position to remove
     * @param view the view to remove/destroy
     */
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    /**
     * Returns the number of images in the viewPager
     * @return
     */
    @Override
    public int getCount() {
        return 3;
    }

    /**
     * Checks that the passed in view is equal to the object passed in
     * @param view the view being checked against
     * @param object the object being checked
     * @return true if they are the same, false if they are not
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    


}
