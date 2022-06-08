package com.app.floatingsidebar;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.Slide;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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
        installedApps();

        showWindowManager();
    }

    @SuppressLint("LongLogTag")
    private void installedApps() {
        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packageInfoList.size(); i++) {

            PackageInfo packageInfo = packageInfoList.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                Drawable applicationIcon;
                try {
                    applicationIcon = getApplicationContext().getPackageManager().getApplicationIcon(packageInfo.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    applicationIcon = getResources().getDrawable(R.mipmap.ic_launcher);
                }

                mList.add(new MenuItem(i,
                        packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(),
                        packageInfo.packageName,
                        applicationIcon

                ));


            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"ResourceAsColor", "SetTextI18n", "ClickableViewAccessibility", "WrongConstant"})
    public void showWindowManager() {
        if (requestPermission()) {
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        WindowManager.LayoutParams p = new WindowManager.LayoutParams(width / 3, height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.RIGHT;

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.MATCH_PARENT
        ));

        scrollView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
            return false;
        });


        LinearLayout menuContainer = new LinearLayout(this);
        menuContainer.setOrientation(LinearLayout.VERTICAL);
        menuContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));


        for (int i = 0; i < mList.size(); i++) {
            MenuItem menuItem = mList.get(i);
            View container = itemView(menuItem);
            int finalI = i;
            container.setOnClickListener(view -> {
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(mList.get(finalI).packageName);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ActivityOptions ao = ActivityOptions.makeBasic();
                    Rect rect = new Rect(0, 0, 0, 100);
                    ActivityOptions bounds = ao.setLaunchBounds(rect);
                    startActivity(intent, bounds.toBundle());
                    finish();
                } catch (Exception ex) {

                }
            });

            menuContainer.addView(container);

        }
        scrollView.setBackgroundResource(R.drawable.bg_menu);
        scrollView.addView(menuContainer);
        ///

        FrameLayout indicatorContiner = new FrameLayout(this);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params2.setMargins(0, dpToPx(16), 0, dpToPx(16));
        indicatorContiner.setLayoutParams(params2);

        Button sideMenu = new Button(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpToPx(8), dpToPx(60), Gravity.BOTTOM);
        sideMenu.setLayoutParams(params);
        sideMenu.setBackgroundResource(R.drawable.power);

        sideMenu.setOnClickListener(view -> {


            Transition transition = new Slide(Gravity.LEFT);
            transition.setDuration(600);
            transition.addTarget(scrollView);
            //  TransitionManager.beginDelayedTransition(rootLayout, transition);
            scrollView.setVisibility(show ? View.VISIBLE : View.GONE);

            show = !show;
        });

        ///
        indicatorContiner.addView(sideMenu);

        SwipeRevealLayout rootLayout = new SwipeRevealLayout(this);


        rootLayout.addView(scrollView);
        rootLayout.addView(indicatorContiner);


        rootLayout.onFinishInflate();
        windowManager.addView(rootLayout, p);


    }

    private LinearLayout itemView(MenuItem menuItem) {
        LinearLayout itemContainer = new LinearLayout(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        itemContainer.setLayoutParams(params);


        itemContainer.setOrientation(LinearLayout.VERTICAL);
        itemContainer.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(this);
        ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(
                dpToPx(48),
                dpToPx(48)
        );

        imageView.setImageDrawable(menuItem.icon);
        imageView.setLayoutParams(imageParams);

        itemContainer.addView(imageView);


        TextView textView = new TextView(this);
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textView.setGravity(Gravity.CENTER);
        textView.setText(menuItem.title);
        textView.setTextSize(14);
        textView.setHintTextColor(getColor(R.color.black));
        textView.setLayoutParams(textParams);

        itemContainer.addView(textView);

        return itemContainer;
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

    private int dpToPx(int dp) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}






