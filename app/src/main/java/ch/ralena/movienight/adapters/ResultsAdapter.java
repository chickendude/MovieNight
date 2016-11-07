package ch.ralena.movienight.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

/**
 * Created by crater-windoze on 11/7/2016.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {
	// TODO: Implement caching

	final static String TAG = ResultsAdapter.class.getSimpleName();

	private SearchResults mSearchResults;

	public ResultsAdapter(SearchResults searchResults) {
		mSearchResults = searchResults;
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
		holder.bindResults(mSearchResults.getSearchResults().get(position));
	}

	@Override
	public int getItemCount() {
		int size = mSearchResults.getSearchResults().size();
		Log.d(TAG, size + " is the current size ======================");
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

		public void bindResults(SearchResult result) {
			if (mDownloadPosterTask != null) {
				mDownloadPosterTask.cancel(true);
			}
			mPosterImageView.setImageResource(android.R.drawable.presence_invisible);
			String url = IMAGE_URL_BASE + "185" + result.getPosterPath();
			mDownloadPosterTask = new DownloadPosterTask(mPosterImageView).execute(url);
			mTitleLabel.setText(result.getTitle());
		}

		@Override
		public void onClick(View v) {
			ImageView poster = (ImageView) v.findViewById(R.id.posterImageView);
			poster.setImageResource(android.R.drawable.btn_dialog);
			Log.d(TAG, "CLICKY CLICKY!");
		}
	}

	private class DownloadPosterTask extends AsyncTask<String, Void, Bitmap> {
		ImageView mPosterImageView;

		public DownloadPosterTask(ImageView posterImageView) {
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
				e.printStackTrace();
			}
			return poster;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			mPosterImageView.setImageBitmap(result);
		}
	}
}
