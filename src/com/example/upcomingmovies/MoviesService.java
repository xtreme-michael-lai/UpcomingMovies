package com.example.upcomingmovies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

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
	
	public class PullTask extends TimerTask {
		private int page;
		
		PullTask() {
			page = 1;
		}
		
		public void run() {
			pullMovies(page++);
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		receiver = intent.getParcelableExtra("Receiver");
		
		Timer timer = new Timer();
		TimerTask pullTask = new PullTask();
		timer.scheduleAtFixedRate(pullTask, 0, 30000);
	}
	
	private void pullMovies(int page) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?page_limit=5&page=" + Integer.toString(page) + "&country=us&apikey=jx8e9aaskadxwr3gx6g9b8wy");
		
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
				
				Bundle bundle = new Bundle();
				bundle.putString("Movies", sb.toString());
				receiver.send(200, bundle);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();  
		}
	}
}
