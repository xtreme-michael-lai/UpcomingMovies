package com.example.upcomingmovies;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MoviesAdapter extends ArrayAdapter<String> {
	
	private List<String> movies;
	private LayoutInflater inflater;
	private ThumbnailManager thumbnailManager;
	
	public MoviesAdapter(Context context, int resource, List<String> movies) {
		super(context, resource, movies);
		
		this.movies = movies;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.thumbnailManager = new ThumbnailManager();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.row, parent, false);
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.title);
		TextView rating = (TextView) convertView.findViewById(R.id.rating);
		TextView date = (TextView) convertView.findViewById(R.id.date);
		ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
		
		String movie = movies.get(position);
		try {
			JSONObject movieInfo = new JSONObject(movie);
			String movieTitle = movieInfo.getString("title");
			String movieRating = movieInfo.getString("mpaa_rating");
			String movieDate = movieInfo.getJSONObject("release_dates")
										.getString("theater");
			String thumbnailUrl = movieInfo.getJSONObject("posters")
										   .getString("thumbnail");
			
			title.setText(movieTitle);
			rating.setText(movieRating);
			date.setText(movieDate);
			thumbnailManager.getThumbnails(thumbnailUrl, thumbnail);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return convertView;
	}

	public class ThumbnailManager {
		private Map<String, Drawable> thumbnailMap;
		
		public ThumbnailManager() {
			thumbnailMap = new HashMap<String, Drawable>();
		}
		
		public class GetThumbnail extends AsyncTask<String, Void, Drawable> {
			private ImageView imageView;
			
			public GetThumbnail(ImageView imageView) {
				this.imageView = imageView;
			}
			
			@Override
			protected Drawable doInBackground(String... urls) {
				Drawable drawable = fetchThumbnail(urls[0]);
				return drawable;
			}
			
			@Override
			protected void onPostExecute(Drawable result) {
				imageView.setImageDrawable(result);
			}
		}
		
		public void getThumbnails(String url, ImageView imageView) {
			GetThumbnail getThumbnail = new GetThumbnail(imageView);
			getThumbnail.execute(new String[] { url });
		}
		
		public Drawable fetchThumbnail(String url) {
			if(thumbnailMap.containsKey(url)) {
				return thumbnailMap.get(url);
			}
			
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			try {
				InputStream in = client.execute(request).getEntity().getContent();
				Drawable drawable = Drawable.createFromStream(in, "src");
				
				if(drawable != null) {
					thumbnailMap.put(url, drawable);
				}
				
				return drawable;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return null;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
