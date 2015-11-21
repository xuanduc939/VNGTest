package basic.com.vngtest;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

public class ListRecentPlaceTask extends AsyncTask<Void,Void,List<RecentPlace>> {
    private GoogleApiClient mGoogleApiClient;

    public ListRecentPlaceTask(GoogleApiClient mGoogleApiClient){
        this.mGoogleApiClient = mGoogleApiClient;
    }
    @Override
    protected List<RecentPlace> doInBackground(Void... params) {

        List<RecentPlace> recentPlaces = null;
        PlaceLikelihoodBuffer placeLikelihoodBuffer = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient,null).await();
        if(placeLikelihoodBuffer.getStatus().isSuccess()) {
            if (placeLikelihoodBuffer.getCount() > 0 && !isCancelled()) {
                recentPlaces = new ArrayList<RecentPlace>();
                for (PlaceLikelihood item : placeLikelihoodBuffer) {
                    RecentPlace place = new RecentPlace();
                    String placeId = item.getPlace().getId();
                    place.setName(item.getPlace().getName().toString());
                    place.setId(item.getPlace().getId());
                    place.setAddress(item.getPlace().getAddress().toString());
                    place.setPhone(item.getPlace().getPhoneNumber().toString());
                    place.setLatitude(item.getPlace().getLatLng().latitude);
                    place.setLongitude(item.getPlace().getLatLng().longitude);
                    place.setWebsite(item.getPlace().getWebsiteUri());
                    // get Photo
                    PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient,placeId).await();
                    if(result.getStatus().isSuccess()){
                        PlacePhotoMetadataBuffer placePhotoMetadataBuffer = result.getPhotoMetadata();
                        if(placePhotoMetadataBuffer.getCount() > 0&& !isCancelled()){
                            PlacePhotoMetadata photoMetadata = placePhotoMetadataBuffer.get(0);
                            Bitmap photo = photoMetadata.getScaledPhoto(mGoogleApiClient,200, 300).await().getBitmap();
                            place.setPhotos(photo);
                        }
                        placePhotoMetadataBuffer.release();
                    }
                    recentPlaces.add(place);
                }
            }
            placeLikelihoodBuffer.release();
        }
        return recentPlaces;
    }
}
