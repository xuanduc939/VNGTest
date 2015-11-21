package basic.com.vngtest;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Duc on 11/20/2015.
 */
public class PhotoTask extends AsyncTask<String, Void, List<Bitmap>> {

    private GoogleApiClient mGoogleApiClient;

    public PhotoTask(GoogleApiClient mGoogleApiClient){
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    protected List<Bitmap> doInBackground(String... params) {
        if(params.length != 1)
            return null;
        String placeId = params[0];
        List<Bitmap> photos = null;

        PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient,placeId).await();
        if(result.getStatus().isSuccess()){
            photos = new ArrayList<Bitmap>();
            PlacePhotoMetadataBuffer placePhotoMetadataBuffer = result.getPhotoMetadata();
            if(placePhotoMetadataBuffer.getCount() > 0&& !isCancelled()){
                for (PlacePhotoMetadata photoMetadata : placePhotoMetadataBuffer) {
                    Bitmap photo = photoMetadata.getScaledPhoto(mGoogleApiClient, 200, 300).await().getBitmap();
                    photos.add(photo);
                }
            }
            placePhotoMetadataBuffer.release();
        }
        return photos;
    }
}

