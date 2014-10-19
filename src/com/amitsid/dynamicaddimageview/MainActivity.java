package com.amitsid.dynamicaddimageview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private PhotoSortrView photoSorter;
	private Button addButton, removeButton;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		photoSorter = (PhotoSortrView) findViewById(R.id.photosortr);
		addButton = (Button)findViewById(R.id.btnAdd);
		removeButton = (Button)findViewById(R.id.btnRemove);
		
		addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				photoSorter.addImages(MainActivity.this, R.drawable.ic_launcher);
			}
		});
		
		removeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				photoSorter.removeImage();
			}
		});
	}
	
	

}
