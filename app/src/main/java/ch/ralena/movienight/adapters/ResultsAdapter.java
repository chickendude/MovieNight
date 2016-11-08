package ch.ralena.movienight.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import ch.ralena.movienight.R;
import ch.ralena.movienight.search.SearchResult;
import ch.ralena.movienight.search.SearchResults;

import static ch.ralena.movienight.search.SearchResult.IMAGE_URL_BASE;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
	// TODO: Implement caching

	final static String TAG = ResultsAdapter.class.getSimpleName();
	private LruCache<String, Bitmap> mBitmapCache;
	private SearchResults mSearchResults;

	public ResultsAdapter(SearchResults searchResults) {
		mSearchResults = searchResults;
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
		holder.bindResults(mSearchResults.getSearchResults().get(position), position);
	}

	@Override
	public int getItemCount() {
		int size = mSearchResults.getSearchResults().size();
		return size;
	}

	public class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		public ImageView mPosterImageView;
		public TextView mTitleLabel;
		public AsyncTask mDownloadPosterTask;

		public ResultsViewHolder(View itemView) {
			super(itemView);
			mPosterImageView = (ImageView) itemView.findViewById(R.id.posterImageView);
			mTitleLabel = (TextView) itemView.findViewById(R.id.titleLabel);
			itemView.setOnClickListener(this);
		}

		public void bindResults(SearchResult result, int position) {
			Log.d(TAG,"Binding position "+position);
			if (mDownloadPosterTask != null) {
				mDownloadPosterTask.cancel(true);
			}
			Bitmap bitmap = mBitmapCache.get(position + "");
			Log.d(TAG,String.valueOf(bitmap));
			if (bitmap != null) {
				mPosterImageView.setImageBitmap(bitmap);
			} else {
				mPosterImageView.setImageResource(android.R.drawable.presence_invisible);
				String url = IMAGE_URL_BASE + "342" + result.getPosterPath();
				mDownloadPosterTask = new DownloadPosterTask(mPosterImageView, position).execute(url);
			}
			mTitleLabel.setText(result.getTitle());
		}

		@Override
		public void onClick(View v) {
			ImageView poster = (ImageView) v.findViewById(R.id.posterImageView);
			poster.setImageResource(android.R.drawable.btn_dialog);
			Log.d(TAG, "CLICKY CLICKY!");
		}
	}

	private void addBitmapToCache(String key, Bitmap bitmap) {
		if (mBitmapCache.get(key) == null) {
			mBitmapCache.put(key, bitmap);
		}
	}

	private class DownloadPosterTask extends AsyncTask<String, Void, Bitmap> {
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
	}
}
