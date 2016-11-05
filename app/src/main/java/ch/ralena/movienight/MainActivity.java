package ch.ralena.movienight;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = MainActivity.class.getSimpleName();

	private String mResults;

	// API stuff
	private static final String API_URL = "https://api.themoviedb.org/3/";
	private static final String API_KEY = "e924bfb7ddb531cb8116f491052edfdd";
	private static final OkHttpClient client = new OkHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getMovie("550");
	}

	// toggle button state for the CheckedTextView buttons
	public void onClick(View view) {
		CheckedTextView button = (CheckedTextView) view;
		button.setChecked(!button.isChecked());
	}

	public void getMovie(String movieId) {
		if(isNetworkAvailable()) {
			Request request = new Request.Builder()
					.url(API_URL + "movie/" + movieId + "?api_key=" + API_KEY)
					.build();
			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					Log.d(TAG, "Seems there was an error with the URL.");
					e.printStackTrace();
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (!response.isSuccessful()) {
						throw new IOException("Error: " + response);
					} else {
						mResults = response.body().string();
						Log.d(TAG, mResults);
					}
				}
			});
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}
}
