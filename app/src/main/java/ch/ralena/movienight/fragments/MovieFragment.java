package ch.ralena.movienight.fragments;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import ch.ralena.movienight.MainActivity;
import ch.ralena.movienight.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static ch.ralena.movienight.search.SearchResult.IMAGE_URL_BASE;

public class MovieFragment extends DialogFragment {
	public final static String TAG = MovieFragment.class.getSimpleName();
	public static final String TITLE = "title";
	public static final String OVERVIEW = "overview";
	public static final String POSTERURL = "posterurl";
	public static final String RELEASE_DATE = "releasedate";
	public static final String RATING = "rating";

	private ImageView mPosterImageView;

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = layoutInflater.inflate(R.layout.movie_fragment, container);
		TextView titleTextView = (TextView) rootView.findViewById(R.id.movieTitleLabel);
		TextView summaryTextView = (TextView) rootView.findViewById(R.id.movieSummaryLabel);
		TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDateLabel);
		RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
		mPosterImageView = (ImageView) rootView.findViewById(R.id.movieImageView);

		Bundle bundle = getArguments();
		String title = bundle.getString(TITLE);
		String overview = bundle.getString(OVERVIEW);
		String posterUrl = bundle.getString(POSTERURL);
		String releaseDate = bundle.getString(RELEASE_DATE);
		double rating = bundle.getDouble(RATING);

		String url = IMAGE_URL_BASE + "500" + posterUrl;
		Log.d(TAG,url);

		titleTextView.setText(title);
		summaryTextView.setText(overview);
		releaseDateTextView.setText(releaseDate);
		ratingBar.setRating((float)rating);

		Request request = new Request.Builder()
				.url(url)
				.build();
		Call call = MainActivity.client.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d(TAG,"Failed loading fragment poster");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if(response.isSuccessful()) {
					Log.d(TAG,"Success loading fragment poster");
					InputStream input = response.body().byteStream();
					final Bitmap poster = BitmapFactory.decodeStream(input);
					if (poster != null) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mPosterImageView.setImageBitmap(poster);
							}
						});
					}
				}
			}
		});


		return rootView;
	}
}
