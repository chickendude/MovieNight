package ch.ralena.movienight.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import ch.ralena.movienight.fragments.MovieFragment;
import ch.ralena.movienight.search.SearchResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static ch.ralena.movienight.search.SearchResult.IMAGE_URL_BASE;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
	final static String TAG = ResultsAdapter.class.getSimpleName();
	private LruCache<String, Bitmap> mBitmapCache;
	private List<SearchResult> mSearchResults;

	private MainActivity mMainActivity;

	public ResultsAdapter(List<SearchResult> searchResults, MainActivity mainActivity) {
		mSearchResults = searchResults;
		mMainActivity = mainActivity;
		// set up our cache, first get max memory available
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 4;
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
		public SearchResult mResult;

		public ResultsViewHolder(View itemView) {
			super(itemView);
			mPosterImageView = (ImageView) itemView.findViewById(R.id.posterImageView);
			mPosterLoadingIcon = (ProgressBar) itemView.findViewById(R.id.posterLoadingIcon);
			mTitleLabel = (TextView) itemView.findViewById(R.id.titleLabel);
			itemView.setOnClickListener(this);
		}

		public void bindResults(SearchResult result, int position) {
			mTitleLabel.setText(result.getTitle());
			mResult = result;
			Log.d(TAG,"Loading movie position " + position + " " + mTitleLabel.getText());
			Bitmap bitmap = mBitmapCache.get(position + "");
			mPosition = position;
			if (bitmap != null) {
				mPosterImageView.setImageBitmap(bitmap);
				mPosterLoadingIcon.setVisibility(View.GONE);
				mPosterImageView.setVisibility(View.VISIBLE);
			} else {
				mPosterLoadingIcon.setVisibility(View.VISIBLE);
				mPosterImageView.setVisibility(View.GONE);
				downloadPoster(result);
			}
		}

		// download poster image, cache it, and display it
		private void downloadPoster(SearchResult result) {
			// save the initial position value, since it might be different when the poster finishes loading
			final int position = mPosition;
			String url = IMAGE_URL_BASE + "342" + result.getPosterPath();
			Request request = new Request.Builder()
					.url(url)
					.build();
			Call call = MainActivity.client.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					Log.d(TAG, "Download failed. Probably due to a timeout. " + mTitleLabel.getText());
					e.printStackTrace();
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if(response.isSuccessful()) {
						InputStream input = response.body().byteStream();
						final Bitmap poster = BitmapFactory.decodeStream(input);
						if (poster != null) {
							addBitmapToCache(position + "", poster);
							if (position == mPosition) {
								mMainActivity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// hide loading icon and show poster
										mPosterLoadingIcon.setVisibility(View.GONE);
										mPosterImageView.setVisibility(View.VISIBLE);
										mPosterImageView.setImageBitmap(poster);
									}
								});
							} // position
						} // poster
					} else {
						Log.e(TAG, "Response was not successful");
					} // response
				}
			});
		}

		@Override
		public void onClick(View v) {
			if(MainActivity.isFilterOpen) {
				mMainActivity.toggleFilters(v);
			} else {
				ImageView poster = (ImageView) v.findViewById(R.id.posterImageView);

				TextView title = (TextView) v.findViewById(R.id.titleLabel);
				Log.d(TAG, mPosition + " clicked.");

				Bundle bundle = new Bundle();
				bundle.putString(MovieFragment.TITLE, title.getText().toString());
				bundle.putString(MovieFragment.OVERVIEW, mResult.getOverview());
				bundle.putString(MovieFragment.POSTERURL, mResult.getPosterPath());
				bundle.putString(MovieFragment.RELEASE_DATE, mResult.getFormattedReleaseDate() + "");
				MovieFragment dialog = new MovieFragment();
				dialog.setArguments(bundle);
				dialog.show(mMainActivity.getFragmentManager(), "error_dialog");
			}
		}
	}

	private void addBitmapToCache(String key, Bitmap bitmap) {
		if (mBitmapCache.get(key) == null) {
			mBitmapCache.put(key, bitmap);
		}
	}
}
