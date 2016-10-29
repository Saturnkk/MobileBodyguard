package com.le.safe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.dell.myapplication.R;

public class SetUp1Activity extends SetUpBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public boolean next_activity() {
		Intent intent = new Intent(this,SetUp2Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	public boolean pre_activity() {
		return false;
	}
	
}
