package com.app.floatingsidebar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    ArrayList<MenuItem> mList = new ArrayList<>();
    boolean show = true;

    @SuppressLint("CommitPrefEdits")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        fillMenuList();
        showWindowManager();
    }

    @SuppressLint("LongLogTag")
    private void fillMenuList() {
        for (int i = 0; i < 10; i++) {
            mList.add(new MenuItem(i,
                    "Item " + i,
                    "https://www.clipartmax.com/png/full/4-41237_free-icons-png-new-home-icon.png"

            ));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"ResourceAsColor", "SetTextI18n", "ClickableViewAccessibility", "WrongConstant"})
    public void showWindowManager() {
        if (requestPermission()) {
            return;
        }

        //For Directions MenuDirections.LEFT || MenuDirections.RIGHT
        new SideMenu(this, mList, MenuDirections.LEFT);


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                showWindowManager();
            }
        }
    }

    public boolean requestPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            return true;
        }
        return false;
    }


}






