package ch.ralena.movienight;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ch.ralena.movienight.adapters.ResultsAdapter;
import ch.ralena.movienight.search.SearchResult;
import ch.ralena.movienight.search.SearchResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = MainActivity.class.getSimpleName();

	private String mResults;
	private SearchResults mSearchResults;

	private RecyclerView mRecyclerView;

	// API stuff
	public static final String API_URL = "https://api.themoviedb.org/3/";
	public static final String API_KEY = "e924bfb7ddb531cb8116f491052edfdd";
	private static final OkHttpClient client = new OkHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mRecyclerView = (RecyclerView) findViewById(R.id.resultsRecyclerView);
		getMovie("550");
	}

	// toggle button state for the CheckedTextView buttons
	public void onClick(View view) {
		CheckedTextView button = (CheckedTextView) view;
		button.setChecked(!button.isChecked());
	}

	public void getMovie(String movieId) {
		if(isNetworkAvailable()) {
			Log.d(TAG, API_URL + "discover/movie/?api_key=" + API_KEY);
			Request request = new Request.Builder()
					.url(API_URL + "discover/movie/?api_key=" + API_KEY)
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
						try {
							mResults = response.body().string();
							Log.d(TAG, mResults);
							unpackResults(mResults);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						// update activity with data
						updateActivity();
					}
				}
			});
		}
	}

	private void updateActivity() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ResultsAdapter adapter = new ResultsAdapter(mSearchResults);
				mRecyclerView.setAdapter(adapter);
				RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
				mRecyclerView.setLayoutManager(layoutManager);
				mRecyclerView.setHasFixedSize(true);
			}
		});
	}

	private void unpackResults(String results) throws JSONException {
		mSearchResults = new SearchResults();
		JSONObject jsonObject = new JSONObject(results);
		JSONArray jsonArray = jsonObject.getJSONArray("results");

		int curPage = jsonObject.getInt("page");
		int totalResults = jsonObject.getInt("total_results");
		int totalPages = jsonObject.getInt("total_pages");
		mSearchResults.setCurPage(curPage);
		mSearchResults.setNumPages(totalPages);
		mSearchResults.setTotalResults(totalResults);

		for(int i = 0; i < jsonArray.length(); i++) {
			JSONObject movie = jsonArray.getJSONObject(i);
			String posterPath = movie.getString("poster_path");
			String title = movie.getString("title");
			String originalLanguage = movie.getString("original_language");
			String originalTitle = movie.getString("original_title");
			String releaseDate = movie.getString("release_date");
			boolean isAdult = movie.getBoolean("adult");
			int id = movie.getInt("id");
			int voteCount = movie.getInt("vote_count");
			// pull out the genre ids
			JSONArray genre_ids = movie.getJSONArray("genre_ids");
			int[] genreIds = new int[genre_ids.length()];
			for(int j = 0; j < genre_ids.length(); j++) {
				genreIds[j] = genre_ids.getInt(j);
			}
			// continue parsing rest of data
			double popularityRating = movie.getDouble("popularity");
			double voteAverage = movie.getDouble("vote_average");
			SearchResult result = new SearchResult(
					posterPath,
					title,
					originalLanguage,
					originalTitle,
					releaseDate,
					isAdult,
					id,
					voteCount,
					genreIds,
					popularityRating,
					voteAverage);

			mSearchResults.addSearchResult(result);
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
