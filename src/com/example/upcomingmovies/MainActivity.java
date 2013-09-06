package com.example.upcomingmovies;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.example.upcomingmovies.MoviesReceiver.Receiver;

public class MainActivity extends ListActivity implements Receiver {

	private Intent service;
	private MoviesReceiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mReceiver = new MoviesReceiver(new Handler());
		mReceiver.setReceiver(this);
		
		service = new Intent(this, MoviesService.class);
		service.putExtra("Receiver", mReceiver);
		startService(service);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(service);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onReceiverResult(int resultCode, Bundle resultData) {
		parseMoviesJson(resultData.getString("Movies"));
	}

	private void parseMoviesJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray moviesArray = jsonObject.getJSONArray("movies");
			
			List<String> movies = new ArrayList<String>();
			for(int i = 0; i < moviesArray.length(); i++) {
				movies.add(moviesArray.getJSONObject(i).toString());
			}
			
			MoviesAdapter adapter = new MoviesAdapter(this, android.R.layout.simple_list_item_1, movies);
			setListAdapter(adapter);
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
}
