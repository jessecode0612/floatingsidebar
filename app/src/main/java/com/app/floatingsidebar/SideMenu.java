package com.app.floatingsidebar;

import static android.content.Context.WINDOW_SERVICE;

import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SideMenu {

    AppCompatActivity parentActivity;
    ArrayList<MenuItem> mList = new ArrayList<>();
    MenuDirections menuDirections;
    ScrollView contentContainer;
    FrameLayout indicatorContainer;

    public SideMenu(AppCompatActivity parentActivity, ArrayList<MenuItem> mList, MenuDirections menuDirections) {
        this.parentActivity = parentActivity;
        this.mList = mList;
        this.menuDirections = menuDirections;
        init();

    }


    private void init() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        WindowManager.LayoutParams p = new WindowManager.LayoutParams(width / 3, height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        WindowManager windowManager = (WindowManager) parentActivity.getSystemService(WINDOW_SERVICE);

        setupContentView();
        setupIndicator();


        SwipeLayout rootLayout = new SwipeLayout(parentActivity.getBaseContext(), menuDirections);
        if (menuDirections == MenuDirections.RIGHT) {
            rootLayout.addView(contentContainer);
            rootLayout.addView(indicatorContainer);
            p.gravity = Gravity.RIGHT;
        } else {
            rootLayout.addView(contentContainer);
            rootLayout.addView(indicatorContainer);
            p.gravity = Gravity.LEFT;
        }

        rootLayout.onFinishInflate();
        windowManager.addView(rootLayout, p);

    }

    private void setupContentView() {

        contentContainer = new ScrollView(parentActivity.getBaseContext());
        contentContainer.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.MATCH_PARENT
        ));
        contentContainer.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                contentContainer.post(() -> contentContainer.fullScroll(View.FOCUS_DOWN));
            }
            return false;
        });

        LinearLayout menuContainer = new LinearLayout(parentActivity.getBaseContext());

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
                Toast.makeText(parentActivity.getBaseContext(), mList.get(finalI).title + " Clicked!", Toast.LENGTH_SHORT).show();
            });

            menuContainer.addView(container);

        }
        contentContainer.setBackgroundResource(R.drawable.bg_menu);
        contentContainer.addView(menuContainer);


    }

    private void setupIndicator() {

        indicatorContainer = new FrameLayout(parentActivity);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params2.setMargins(0, dpToPx(200), 0, dpToPx(16));
        indicatorContainer.setLayoutParams(params2);

        View menuindicator = new View(parentActivity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpToPx(8), dpToPx(60), Gravity.BOTTOM);
        params.setMargins(0, dpToPx(200), 0, dpToPx(16));
        menuindicator.setLayoutParams(params);
        menuindicator.setBackgroundResource(R.drawable.bg_indicators);

        indicatorContainer.addView(menuindicator);
    }


    private LinearLayout itemView(MenuItem menuItem) {
        LinearLayout itemContainer = new LinearLayout(parentActivity.getBaseContext());
        itemContainer.setClickable(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        itemContainer.setLayoutParams(params);


        itemContainer.setOrientation(LinearLayout.VERTICAL);
        itemContainer.setGravity(Gravity.CENTER);

        ImageView ivIcon = new ImageView(parentActivity);
        ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(
                dpToPx(48),
                dpToPx(48)
        );

        Picasso.get().load(menuItem.urlIcon).into(ivIcon);

        ivIcon.setLayoutParams(imageParams);

        itemContainer.addView(ivIcon);


        TextView tvTitle = new TextView(parentActivity);
        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setText(menuItem.title);
        tvTitle.setTextSize(14);
        tvTitle.setHintTextColor(parentActivity.getColor(R.color.black));
        tvTitle.setLayoutParams(textParams);

        itemContainer.addView(tvTitle);

        return itemContainer;
    }


    private int dpToPx(int dp) {
        Resources resources = parentActivity.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
