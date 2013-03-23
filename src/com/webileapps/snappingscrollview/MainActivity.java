package com.webileapps.snappingscrollview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.webileapps.snappingscrollview.SnappingScrollView.HorizontalScrollViewAdapter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SnappingScrollView layout = new SnappingScrollView(this);
        layout.setBackgroundColor(Color.LTGRAY);
        layout.setAdapter(new HorizontalScrollViewAdapter() {
			
			@Override
			public View getViewAtPosition(int position) {
				TextView tv = new TextView(MainActivity.this);
				tv.setText("This is a very long text");
				tv.setGravity(Gravity.CENTER);
				tv.setBackgroundColor(position%2==0?Color.WHITE:Color.DKGRAY);
				tv.setTextColor(position%2==0?Color.BLACK:Color.WHITE);
				return tv;
			}
			
			@Override
			public int getCount() {
				return 4;
			}
		});
        setContentView(layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
