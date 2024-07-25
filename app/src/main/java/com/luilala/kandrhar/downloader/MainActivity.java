package com.luilala.kandrhar.downloader;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;

    boolean isDownload = false;

    int mRequestCode = 1;
//    private Completed completed;
    private long pressTime;
    Toolbar toolbar;
    int permissionTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_home);
        toolbar = findViewById(R.id.toolbar_home);

        // settoolbar
        setSupportActionBar(toolbar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // Check if the app has write and read permissions
        int readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED) {
            // Read permission is granted
            Toast.makeText(this, "granded", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "no granded", Toast.LENGTH_SHORT).show();
            // Read permission is not granted
            getPermissions();
        }

        Intent intent = getIntent();
        isDownload = intent.getBooleanExtra("isDownload", false);
        if (isDownload) {
            // download fragment
            getSupportFragmentManager().beginTransaction().add(new FileFragment(), "download").replace(R.id.MainFrameLayout, new FileFragment()).commit();
        } else {
            // Home Fragment
            getSupportFragmentManager().beginTransaction().add(new HomeFragment(), "youtube").replace(R.id.MainFrameLayout, new HomeFragment()).commit();

        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                if (menuItem.getItemId() == R.id.menu_bottom_youtube) {
                    fragment = new YoutubFragment();
                } else if (menuItem.getItemId() == R.id.menu_bottom_home) {
                    fragment = new HomeFragment();
                } else if (menuItem.getItemId() == R.id.menu_bottom_download) {
                    fragment = new FileFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.MainFrameLayout, fragment).commit();

                return true;
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();


        }
        else {
            if (permissionTime == 3){
                Toast.makeText(this, "App will be finish", Toast.LENGTH_SHORT).show();
                SystemClock.sleep(1000);
                finish();
                return;
            }
            getPermissions();

        }
    }

    private void getPermissions() {
        // Request read and write permissions
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        permissionTime++;
    }

    @Override
    public void onBackPressed() {
        if (pressTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "please click again", Toast.LENGTH_SHORT).show();
        }
        pressTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}