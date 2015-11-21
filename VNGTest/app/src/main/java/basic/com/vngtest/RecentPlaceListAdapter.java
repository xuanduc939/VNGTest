package basic.com.vngtest;

import android.location.Location;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class RecentPlaceListAdapter extends RecyclerView.Adapter<RecentPlaceListAdapter.ViewHolder>  {
    private List<RecentPlace> places;
    private Location currentLocation;

    public RecentPlaceListAdapter(List<RecentPlace> places, Location currentLocation){
        this.places = places;
        this.currentLocation = currentLocation;

    }

    public List<RecentPlace> getPlaces(){
        return this.places;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_list_recent_place, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        double distance = places.get(position).getDistance(currentLocation);
        DecimalFormat df = new DecimalFormat("0.0");

        holder.txtName.setText(places.get(position).getName());
        holder.txtDistance.setText(df.format(distance) + " m");
        if(places.get(position).getPhotos() != null)
            holder.imgPhoto.setImageBitmap(places.get(position).getPhotos());
        else
            holder.imgPhoto.setImageResource(R.drawable.empty_photo);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        TextView txtDistance;
        ImageView imgPhoto;

        public ViewHolder(View v){
            super(v);
            txtName =(TextView)v.findViewById(R.id.row_item_list_recent_place_txt_place_name);
            txtDistance = (TextView)v.findViewById(R.id.row_item_list_recent_place_txt_place_distance);
            imgPhoto = (ImageView)v.findViewById(R.id.row_item_list_recent_place_img_place_photo);
        }
    }
}
