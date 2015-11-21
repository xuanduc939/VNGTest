package basic.com.vngtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PERMISSION_CHECK_ACCESS_FINE_LOCATION = 1001;
    private Button btnDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDisplay = (Button)findViewById(R.id.main_btn_display);
        // click to show recent place
        btnDisplay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // check ACCESS_FINE_LOCATION permission
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    ,PERMISSION_CHECK_ACCESS_FINE_LOCATION);
        }
        else{
            Intent intent = new Intent(this,RecentPlaceListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CHECK_ACCESS_FINE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // call RecentPlaceListActivity to display recent place
                    Intent intent = new Intent(this,RecentPlaceListActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(this,"Permission denied!",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
