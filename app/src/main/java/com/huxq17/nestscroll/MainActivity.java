package com.huxq17.nestscroll;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final ViewGroup toolbar = findViewById(R.id.toolbar);
        final View header = findViewById(R.id.home_head);
        final CollapsingTitleView collapsingTitleView = findViewById(R.id.home_app_bar);
        collapsingTitleView.setOnOffsetChangedListener(new CollapsingTitleView.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(CollapsingTitleView collapsingTitleView1, int verticalOffset) {
                int totalScrollRange = collapsingTitleView.getTotalScrollRange();
                int absOffset = Math.abs(verticalOffset);
                float alpha = absOffset * 1f / totalScrollRange;
                alpha = alpha > 1 ? 1 : alpha;
                toolbar.setAlpha(alpha);
                header.setAlpha(1 - alpha);
                toolbar.setVisibility(alpha == 0 ? View.GONE : View.VISIBLE);
//                if (alpha == 0) {
//                    setWindowStatusBarColor(Color.parseColor("#1DD07A"));
//                } else if (alpha == 1) {
//                    setWindowStatusBarColor(Color.parseColor("#1dbabf"));
//                }
            }
        });
    }

}
