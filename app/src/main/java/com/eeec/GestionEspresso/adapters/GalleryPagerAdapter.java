package com.eeec.GestionEspresso.adapters;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.eeec.GestionEspresso.manager.GestionRequestManager;
import com.eeec.GestionEspresso.model.KGalleryItem;
import com.eeec.GestionEspresso.views.TouchImageView;

import java.util.ArrayList;

public class GalleryPagerAdapter extends PagerAdapter {

    public ArrayList<KGalleryItem> gallery;

    @Override
    public int getCount() {
        return this.gallery.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final TouchImageView image = new TouchImageView(container.getContext());
        KGalleryItem item = this.gallery.get(position);
        ImageLoader imageLoader = GestionRequestManager.getInstance(container.getContext()).getImageLoader();
        imageLoader.get(item.url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    image.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        container.addView(image, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}