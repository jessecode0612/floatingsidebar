package com.app.floatingsidebar;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;

import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.SystemClock;
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

    private static final String TAG = "SideMenu";
    AppCompatActivity parentActivity;
    ArrayList<MenuItem> mList = new ArrayList<>();
    MenuDirections menuDirections;
    ScrollView contentContainer;
    FrameLayout indicatorContainer;
    FrameLayout indicatorContainer2;
    WindowManager.LayoutParams windowParams;
    int threshold = 0;
    View menuindicator;
    SwipeLayout rootLayout;
    WindowManager windowManager;

    public SideMenu(AppCompatActivity parentActivity, ArrayList<MenuItem> mList, MenuDirections menuDirections) {
        this.parentActivity = parentActivity;
        this.mList = mList;
        this.menuDirections = menuDirections;
        init();

    }

    public void close() {
        try {
            windowManager.removeView(rootLayout);
        } catch (Exception exception) {

        }
        try {
            windowManager.removeView(indicatorContainer);
        } catch (Exception exception) {

        }
    }

    private void init() {

        windowParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY |
                        WindowManager.LayoutParams.TYPE_PHONE, // Type Phone, These are non-application windows providing user interaction with the phone
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | FLAG_SPLIT_TOUCH | // This window would never get key input focus.
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, // This window will get outside touch.
                PixelFormat.TRANSPARENT // The view will be transparent
        );
        DisplayMetrics displayMetrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        int height = displayMetrics.heightPixels;
        threshold = displayMetrics.widthPixels;
        windowParams.width = WRAP_CONTENT;
        windowParams.height = height;
        windowManager = (WindowManager) parentActivity.getSystemService(WINDOW_SERVICE);

        setupContentView();
        setupIndicator();
        setupIndicator2();


        rootLayout = new SwipeLayout(parentActivity.getBaseContext(), menuDirections);
        if (menuDirections == MenuDirections.RIGHT) {
            rootLayout.addView(contentContainer);
            rootLayout.addView(indicatorContainer2);
            windowParams.gravity = Gravity.RIGHT;
            threshold = threshold - dpToPx(60);
        } else {
            rootLayout.addView(contentContainer);
            rootLayout.addView(indicatorContainer2);
            windowParams.gravity = Gravity.LEFT;
            threshold = dpToPx(60);
        }

        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    rootLayout.close(true);

                }
                return false;
            }
        });
        rootLayout.setSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeLayout view) {

                windowManager.removeView(rootLayout);
                windowManager.addView(indicatorContainer, windowParams);
            }

            @Override
            public void onOpened(SwipeLayout view) {

            }

            @Override
            public void onSlide(SwipeLayout view, float slideOffset) {

            }
        });

      /*  indicatorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
*/
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private long downTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downTime = SystemClock.elapsedRealtime();
                        initialX = windowParams.x;
                        initialY = windowParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        long currentTime = SystemClock.elapsedRealtime();
                        if (currentTime - downTime < 200) {
                            v.performClick();
                        } else {
                            updateViewLocation();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:


                        if (menuDirections == MenuDirections.RIGHT && event.getRawX() < threshold ||
                                menuDirections == MenuDirections.LEFT && event.getRawX() > threshold
                        ) {
                            windowParams.x = 0;
                            windowParams.y = 0;
                            try {
                                windowManager.removeView(indicatorContainer);
                                windowManager.addView(rootLayout, windowParams);
                            } catch (Exception exception) {

                            }
                            rootLayout.open(true);
                        } else {
                            if (menuDirections == MenuDirections.RIGHT) {
                                windowParams.x = initialX + (int) (initialTouchX - event.getRawX());
                                windowParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            } else {
                                windowParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                                windowParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            }

                            windowManager.updateViewLayout(indicatorContainer, windowParams);
                        }


                        return true;
                }
                return false;
            }

            private void updateViewLocation() {

                DisplayMetrics displayMetrics = new DisplayMetrics();
                parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


                int width = displayMetrics.widthPixels / 2;
                if (windowParams.x >= width)
                    windowParams.x = (width * 2) - 10;
                else if (windowParams.x <= width)
                    windowParams.x = 10;
                windowManager.updateViewLayout(indicatorContainer, windowParams);
            }
        };

        menuindicator.setOnTouchListener(touchListener);
        rootLayout.onFinishInflate();
        windowManager.addView(indicatorContainer, windowParams);


    }


    private void setupContentView() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        parentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        contentContainer = new ScrollView(parentActivity.getBaseContext());
        contentContainer.setLayoutParams(new ScrollView.LayoutParams(
                displayMetrics.widthPixels / 3,
                displayMetrics.heightPixels
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
        indicatorContainer.setLayoutParams(params2);

        menuindicator = new View(parentActivity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpToPx(8), dpToPx(100), Gravity.BOTTOM);
        int left = 0, right = 0;
        if (menuDirections == MenuDirections.RIGHT) {
            left = dpToPx(16);

        } else {
            right = dpToPx(16);
        }
        params2.setMargins(left, dpToPx(200), right, dpToPx(16));


        params.setMargins(left, dpToPx(200), right, dpToPx(16));
        menuindicator.setLayoutParams(params);
        menuindicator.setBackgroundResource(R.drawable.bg_indicators);

        indicatorContainer.addView(menuindicator);
    }

    private void setupIndicator2() {

        indicatorContainer2 = new FrameLayout(parentActivity);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        indicatorContainer2.setLayoutParams(params2);

        View menuindicator = new View(parentActivity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dpToPx(8), dpToPx(100), Gravity.BOTTOM);
        int left = 0, right = 0;
        if (menuDirections == MenuDirections.RIGHT) {
            left = dpToPx(16);

        } else {
            right = dpToPx(16);
        }
        params2.setMargins(left, dpToPx(200), right, dpToPx(16));


        params.setMargins(left, dpToPx(200), right, dpToPx(16));
        menuindicator.setLayoutParams(params);
        menuindicator.setBackgroundResource(R.drawable.bg_indicators);

        indicatorContainer2.addView(menuindicator);
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
