# JellyDirectionButton
---
This button is a button on the growing sense up / down.
By placing a middle toggle button it can be used to distinguish the toggle + up / down.
Use this button to create a fun UI ~

##ScreenShot

<br/>

<img src="https://rawgit.com/bonghyun2/jelly-direction-button/master/screenshots/Screenshot_button.gif" width = 200/>


##Usage

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