# JellyDirectionButton
---
This button is a button on the growing sense up / down.
By placing a middle toggle button it can be used to distinguish the toggle + up/down/right/left(4ways).
Use this button to create a fun UI ~

##ScreenShot

<img src="https://rawgit.com/bonghyun2/jelly-direction-button/master/screenshots/Screenshot_button.gif" width = 200/>

##sample application

    https://play.google.com/store/apps/details?id=com.bnystudio.jellydirectionbutton

##Usage
####build.gradle(Module:app)

	dependencies {
	    compile 'com.bnystudio:jelly-direction-button:0.1.1'
	}

<br/>
####Activity

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

####layout.xml
	<com.bnystudio.library.jellydirectionbutton.JellyDirectionButtonLayout
	        android:id="@+id/btn_test"
	        android:layout_width="50dp"
	        android:layout_height="150dp"
	        android:layout_centerInParent="true">
	
	        <ToggleButton
	            android:id="@+id/btn_play"
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            android:background="@drawable/btn_play_selector"
	            android:textOff=""
	            android:textOn="" />

	<com.bnystudio.library.jellydirectionbutton.JellyDirectionButtonLayout>

##License


	Copyright 2015 bonghyun choi
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.