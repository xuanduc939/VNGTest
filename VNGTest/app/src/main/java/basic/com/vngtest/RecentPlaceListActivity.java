package basic.com.vngtest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

public class RecentPlaceListActivity extends AppCompatActivity implements View.OnClickListener
        ,ConnectionCallbacks,OnConnectionFailedListener{
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private boolean mResolvingError = false;
    private GoogleApiClient mGoogleApiClient;

    private Toolbar toolbar;
    private RecyclerView recyclerViewPlace;
    private RecentPlaceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_recent_place);
        // get savedInstanceState to set mResolvingError
        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        //set up toolbar for activity
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(this);
        setSupportActionBar(toolbar);

        //set up GoogleApiClient to use Google RecentPlace API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // set up for recycler view
        recyclerViewPlace = (RecyclerView)findViewById(R.id.list_recent_place_rec_list);
        recyclerViewPlace.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewPlace.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public void onClick(View v) {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        new ListRecentPlaceTask(mGoogleApiClient){
            @Override
            protected void onPreExecute() {
                final GestureDetector mGestureDetector = new GestureDetector(RecentPlaceListActivity.this, new GestureDetector.SimpleOnGestureListener() {

                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                });

                recyclerViewPlace.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                        View child = recyclerViewPlace.findChildViewUnder(e.getX(), e.getY());
                        int pos = recyclerViewPlace.getChildAdapterPosition(child);
                        RecentPlaceListAdapter currentAdapter = (RecentPlaceListAdapter)recyclerViewPlace.getAdapter();
                        ArrayList<RecentPlace> recentPlaces = (ArrayList<RecentPlace>) currentAdapter.getPlaces();

                        if (child != null && mGestureDetector.onTouchEvent(e)) {
                            Intent intent = new Intent(RecentPlaceListActivity.this, RecentPlaceInfoActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", recentPlaces.get(pos).getId());
                            bundle.putString("name", recentPlaces.get(pos).getName());
                            bundle.putString("phone", recentPlaces.get(pos).getPhone());
                            bundle.putString("address", recentPlaces.get(pos).getAddress());
                            bundle.putDouble("distance", recentPlaces.get(pos).getDistance(location));
                            bundle.putString("website",recentPlaces.get(pos).getWebsite());
                            intent.putExtra("package", bundle);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });
            }

            @Override
            protected void onPostExecute(List<RecentPlace> places) {
                adapter = new RecentPlaceListAdapter(places,location);
                recyclerViewPlace.setAdapter(adapter);
            }
        }.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }
    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((RecentPlaceListActivity) getActivity()).onDialogDismissed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

}
