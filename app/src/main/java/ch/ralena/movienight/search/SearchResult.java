package ch.ralena.movienight.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchResult {
	public static final String IMAGE_URL_BASE = "http://image.tmdb.org/t/p/w"; // size: w185/
	private String mPosterPath;
	private String mTitle;
	private String mOriginalLanguage;
	private String mOriginalTitle;
	private String mOverview;
	private Date mReleaseDate;
	private boolean mIsAdult;
	private int mId;
	private int mVoteCount;
	private int[] mGenreIds;
	private double mPopularityRating;
	private double mVoteAverage;

	public SearchResult(String posterPath, String title, String originalLanguage, String originalTitle, String overview, String releaseDate, boolean isAdult, int id, int voteCount, int[] genreIds, double popularityRating, double voteAverage) {
		mPosterPath = posterPath;
		mTitle = title;
		mOriginalLanguage = originalLanguage;
		mOriginalTitle = originalTitle;
		mOverview = overview;
		mIsAdult = isAdult;
		mId = id;
		mVoteCount = voteCount;
		mGenreIds = genreIds;
		mPopularityRating = popularityRating;
		mVoteAverage = voteAverage;
		// convert String-formatted date to proper Date format
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = dateFormat.parse(releaseDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mReleaseDate = date;

	}

	public String getPosterPath() {
		return mPosterPath;
	}

	public void setPosterPath(String posterPath) {
		mPosterPath = posterPath;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getOriginalLanguage() {
		return mOriginalLanguage;
	}

	public void setOriginalLanguage(String originalLanguage) {
		mOriginalLanguage = originalLanguage;
	}

	public String getOriginalTitle() {
		return mOriginalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		mOriginalTitle = originalTitle;
	}

	public Date getReleaseDate() {
		return mReleaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		mReleaseDate = releaseDate;
	}

	public boolean isAdult() {
		return mIsAdult;
	}

	public void setAdult(boolean adult) {
		mIsAdult = adult;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public int getVoteCount() {
		return mVoteCount;
	}

	public void setVoteCount(int voteCount) {
		mVoteCount = voteCount;
	}

	public int[] getGenreIds() {
		return mGenreIds;
	}

	public void setGenreIds(int[] genreIds) {
		mGenreIds = genreIds;
	}

	public double getPopularityRating() {
		return mPopularityRating;
	}

	public void setPopularityRating(double popularityRating) {
		mPopularityRating = popularityRating;
	}

	public double getVoteAverage() {
		return mVoteAverage;
	}

	public void setVoteAverage(double voteAverage) {
		mVoteAverage = voteAverage;
	}

	public String getOverview() {
		return mOverview;
	}

	@Override
	public String toString() {
		return "SearchResult{" +
				"mTitle='" + mTitle + '\'' +
				", mReleaseDate=" + mReleaseDate +
				'}';
	}

	public String getFormattedReleaseDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(mReleaseDate);
	}
}
