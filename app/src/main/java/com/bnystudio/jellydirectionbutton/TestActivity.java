package com.bnystudio.jellydirectionbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
                String debugStr = "onClick :: direction :: " + direction + ", isActiveState :: " + isActiveState + ", fraction :: " + fraction;
                Log.d("BB", debugStr);
//                Toast.makeText(TestActivity.this, debugStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick(JellyDirectionButtonLayout jellyDirectionButtonLayout, int direction) {
                String debugStr = "onClick :: direction :: " + direction;
                Log.d("BB", debugStr);
                Toast.makeText(TestActivity.this, debugStr, Toast.LENGTH_SHORT).show();
                mButton.setClickFinish();
            }
        });
    }
}
