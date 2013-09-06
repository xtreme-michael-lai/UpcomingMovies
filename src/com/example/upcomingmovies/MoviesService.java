package com.example.upcomingmovies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class MoviesService extends IntentService {

	private ResultReceiver receiver;
	
	public MoviesService() {
		super("MoviesService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		receiver = intent.getParcelableExtra("Receiver");
		
		String moviesJson = pullMovies();
		if(moviesJson != null) {
			Bundle bundle = new Bundle();
			bundle.putString("Movies", moviesJson);
			receiver.send(200, bundle);
		}
	}
	
	private String pullMovies() {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?page_limit=10&page=1&country=us&apikey=jx8e9aaskadxwr3gx6g9b8wy");
		
		try {
			HttpResponse response = client.execute(request);
			if(response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream in = entity.getContent();
				BufferedReader bd = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				
				String line = null;
				while((line = bd.readLine()) != null) {
					sb.append(line + "\n");
				}
				in.close();
				
				return sb.toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();  
		}
		
		return null;
	}
}
