package dev.haguel.moveotask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import dev.haguel.moveotask.DatabaseManager;
import dev.haguel.moveotask.LocationAndPermissionManager;
import dev.haguel.moveotask.R;
import dev.haguel.moveotask.fragments.list_frag.NoteListFragment;
import dev.haguel.moveotask.fragments.maps_frag.MapsFragment;


public class MainActivity extends AppCompatActivity implements OnSuccessListener<Location> {

    // UI
    private FloatingActionButton fabCreateNote;
    private TextView tvSignout, tvUserWelcomeMsg;
    private FrameLayout flLoader;
    // DATA
    private LatLng latLng = new LatLng(-1, -1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cannot Pass Without User Logged In
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        // Fetch Views
        flLoader = findViewById(R.id.flLoader);
        tvSignout = findViewById(R.id.tvSignout);
        tvUserWelcomeMsg = findViewById(R.id.tvUserWelcomeMsg);
        fabCreateNote = findViewById(R.id.fabCreateNote);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        // Hanlde Clicks
        fabCreateNote.setOnClickListener(v->{
            NoteActivity.startNotActivity(MainActivity.this, null);
        });
        tvSignout.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });


        // Request Location Permission To Detect Location
        LocationAndPermissionManager.instance().init(this);
        // Init Data
        loadNotesListFirstTime();
        DatabaseManager.instance().loadUserNameFromDBAsync(()->{
            tvUserWelcomeMsg.setText("Welcome, " + DatabaseManager.instance().getFullName());
        });


        // Register Fragment Changes
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.listMode){
                selectedFragment = NoteListFragment.newInstance();
            } else if (item.getItemId() == R.id.mapMode){
                selectedFragment = MapsFragment.newInstance(latLng.latitude, latLng.longitude);
            }
            // It will help to replace the
            // one fragment to other.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });
    }


    private void loadNotesListFirstTime() {
        toggleLoader(true);
        // Load Notes from db
        DatabaseManager.instance().loadNotesFromDBAsync(()->{
            try {
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, NoteListFragment.newInstance()).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            toggleLoader(false);
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        LocationAndPermissionManager.instance().initLocationWithPermission(this, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == DatabaseManager.GEO_LOCATION_REQUEST_CODE && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationAndPermissionManager.instance().refreshLastLocation(this);
        }
    }

    @Override
    public void onSuccess(Location location) {
        if (location == null)
            latLng = new LatLng(-1, -1);
        else
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // Check And Update Maps Frag
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof MapsFragment) {
            ((MapsFragment)frag).setNewPosition(latLng);
        }
    }


    public void toggleLoader(boolean visible){
        if (visible){
            flLoader.setVisibility(View.VISIBLE);
        } else {
            flLoader.setVisibility(View.GONE);
        }
    }
}

