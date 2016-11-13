package ch.ralena.movienight.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ch.ralena.movienight.MainActivity;
import ch.ralena.movienight.R;
import ch.ralena.movienight.search.SearchResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static ch.ralena.movienight.search.SearchResult.IMAGE_URL_BASE;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
	private int mNumImagesLoading;

	final static String TAG = ResultsAdapter.class.getSimpleName();
	private LruCache<String, Bitmap> mBitmapCache;
	private List<SearchResult> mSearchResults;

	private MainActivity mMainActivity;

	public ResultsAdapter(List<SearchResult> searchResults, MainActivity mainActivity) {
		mSearchResults = searchResults;
		mMainActivity = mainActivity;
		mNumImagesLoading = 0;
		// set up our cache, first get max memory available
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		mBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
	}

	@Override
	public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// inflate view here
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.search_result_item, parent, false);
		ResultsViewHolder viewHolder = new ResultsViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ResultsViewHolder holder, int position) {
		holder.bindResults(mSearchResults.get(position), position);
	}

	@Override
	public int getItemCount() {
		int size = mSearchResults.size();
		return size;
	}

	public void addAll(List<SearchResult> newResults) {
		mSearchResults.addAll(newResults);
	}

	public class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		public ImageView mPosterImageView;
		public ProgressBar mPosterLoadingIcon;
		public TextView mTitleLabel;
		public int mPosition;

		public ResultsViewHolder(View itemView) {
			super(itemView);
			mPosterImageView = (ImageView) itemView.findViewById(R.id.posterImageView);
			mPosterLoadingIcon = (ProgressBar) itemView.findViewById(R.id.posterLoadingIcon);
			mTitleLabel = (TextView) itemView.findViewById(R.id.titleLabel);
			itemView.setOnClickListener(this);
		}

		public void bindResults(SearchResult result, int position) {
			Bitmap bitmap = mBitmapCache.get(position + "");
			if (bitmap != null) {
				mPosition = position;
				mPosterImageView.setImageBitmap(bitmap);
				mPosterLoadingIcon.setVisibility(View.GONE);
			} else {
				mPosterLoadingIcon.setVisibility(View.VISIBLE);
				mPosterImageView.setVisibility(View.GONE);
				mPosition = position;
				downloadPoster(result);
			}
			mTitleLabel.setText(position + " " + result.getTitle());
		}

		// download poster image, cache it, and display it
		private void downloadPoster(SearchResult result) {
			final int position = mPosition;
			Log.d(TAG, "Number of images loading: " + ++mNumImagesLoading);
			String url = IMAGE_URL_BASE + "342" + result.getPosterPath();
			Request request = new Request.Builder()
					.url(url)
					.build();
			MainActivity.client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					Log.d(TAG, "Download failed. Probably due to a timeout.");
					e.printStackTrace();
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if(response.isSuccessful()) {
						InputStream input = response.body().byteStream();
						final Bitmap poster = BitmapFactory.decodeStream(input);
						addBitmapToCache(mPosition + "", poster);
						if (position == mPosition) {
							mMainActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mPosterLoadingIcon.setVisibility(View.GONE);
									mPosterImageView.setVisibility(View.VISIBLE);
									mPosterImageView.setImageBitmap(poster);
									Log.d(TAG, String.format("Poster %d finished downloading.", mPosition));
									mNumImagesLoading--;
								}
							});
						}
					} else {
						Log.e(TAG, "Response was not successful");
					}
				}
			});
		}

		@Override
		public void onClick(View v) {
			ImageView poster = (ImageView) v.findViewById(R.id.posterImageView);
			poster.setImageResource(android.R.drawable.btn_dialog);
		}
	}

/*	private class DownloadPosterTask extends AsyncTask<String, Void, Bitmap> {
		ImageView mPosterImageView;
		int mPosition;

		public DownloadPosterTask(ImageView posterImageView, int position) {
			mPosition = position;
			mPosterImageView = posterImageView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			String url = urls[0];
			Bitmap poster = null;
			try {
				InputStream input = new java.net.URL(url).openStream();
				poster = BitmapFactory.decodeStream(input);
			} catch (IOException e) {
				Log.e(TAG, "THERE WAS AN ERROR WITH THE WEB STUFF");
				e.printStackTrace();
			}
			return poster;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			addBitmapToCache(mPosition + "", result);
			mPosterImageView.setImageBitmap(result);
		}
	}*/

	private void addBitmapToCache(String key, Bitmap bitmap) {
		if (mBitmapCache.get(key) == null) {
			mBitmapCache.put(key, bitmap);
		}
	}
}
