package basic.com.vngtest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.text.DecimalFormat;
import java.util.List;

public class RecentPlaceInfoActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks{
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private boolean mResolvingError = false;
    private GoogleApiClient mGoogleApiClient;

    private TextView txtName;
    private TextView txtPhone;
    private TextView txtDistance;
    private TextView txtAddress;
    private TextView txtWebsite;
    private RecyclerView recyclerViewPhoto;
    private Toolbar toolbar;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_place_detail);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(RecentPlaceInfoActivity.this);
            }
        });
        setSupportActionBar(toolbar);

        //set up GoogleApiClient to use Google RecentPlace API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // find Id for ui component
        txtName = (TextView)findViewById(R.id.recent_place_txt_place_name_info);
        txtPhone = (TextView)findViewById(R.id.recent_place_txt_place_phone_info);
        txtAddress = (TextView)findViewById(R.id.recent_place_txt_place_address_info);
        txtDistance = (TextView)findViewById(R.id.recent_place_txt_place_distance_info);
        txtWebsite = (TextView)findViewById(R.id.recent_place_txt_place_website_info);
        recyclerViewPhoto = (RecyclerView)findViewById(R.id.recent_place_detail_list_photo);

        // set up for recycler view
        recyclerViewPhoto.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerViewPhoto.setLayoutManager(layoutManager);

        // get data from Intent and set for UI Component
        Intent receiver = getIntent();
        if(receiver.hasExtra("package")){
            Bundle bundle = receiver.getBundleExtra("package");
            id = bundle.getString("id");
            String name = bundle.getString("name");
            String phone = bundle.getString("phone");
            String address = bundle.getString("address");
            String website = bundle.getString("website");
            double distance = bundle.getDouble("distance");

            // set data for UI Component
            DecimalFormat df = new DecimalFormat("0.0");
            txtName.setText(name);
            txtPhone.setText(phone);
            txtDistance.setText(df.format(distance) + " m");
            txtAddress.setText(address);
            txtWebsite.setText(website);
        }

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
    public void onConnected(Bundle bundle) {
        PhotoTask photoTask = new PhotoTask(mGoogleApiClient){
            @Override
            protected void onPostExecute(List<Bitmap> bitmaps) {
                RecentPlaceInfoAdapter adapter = new RecentPlaceInfoAdapter(bitmaps);
                recyclerViewPhoto.setAdapter(adapter);
            }
        };
        photoTask.execute(id);
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
