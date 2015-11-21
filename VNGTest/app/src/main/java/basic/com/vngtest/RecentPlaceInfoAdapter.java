package basic.com.vngtest;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class RecentPlaceInfoAdapter extends RecyclerView.Adapter<RecentPlaceInfoAdapter.ViewHolder> {

    private List<Bitmap> photos;

    public RecentPlaceInfoAdapter(List<Bitmap> photos){
        this.photos = photos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_recent_place_detail_list_photo,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder(View v){
            super(v);
            imageView = (ImageView)v.findViewById(R.id.row_item_recent_place_detail_list_photo_img_place_photo);
        }
    }
}
