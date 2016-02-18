package com.bnystudio.jellydirectionbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bnystudio.library.jellydirectionbutton.JellyDirectionButtonLayout;

public class TestActivity extends AppCompatActivity {

    private JellyDirectionButtonLayout mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mButton = (JellyDirectionButtonLayout)findViewById(R.id.btn_test);

        mButton.setOnJellyClickListener(new JellyDirectionButtonLayout.OnJellyClickListener() {

            @Override
            public void onReadyClick(JellyDirectionButtonLayout jellyDirectionButtonLayout, int direction, boolean isActiveState, float fraction) {
                Log.d("BB", "onClick :: direction :: " + direction + ", isActiveState :: " + isActiveState + ", fraction :: " + fraction);
            }

            @Override
            public void onClick(JellyDirectionButtonLayout jellyDirectionButtonLayout, int direction) {
                Log.d("BB", "onClick :: direction :: " + direction);
                mButton.setClickFinish();
            }
        });
    }
}
