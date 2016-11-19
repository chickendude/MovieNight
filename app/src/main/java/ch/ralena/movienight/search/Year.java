package ch.ralena.movienight.search;

/**
 * Created by crater-windoze on 11/19/2016.
 */

public class Year {
	private int mStartYear;
	private int mEndYear;
	private String mTitle;

	public Year(int startYear, int endYear, String title) {
		mStartYear = startYear;
		mEndYear = endYear;
		mTitle = title;
	}

	public Year(int startYear, int endYear) {
		mStartYear = startYear;
		mEndYear = endYear;
		if (endYear < 2000) {
			mTitle = endYear - 1900 + "s";
		} else {
			mTitle = startYear + "-" + endYear;
		}
	}

	public Year(int startYear) {
		mStartYear = startYear;
		mTitle = startYear + "";
	}

	public int getStartYear() {
		return mStartYear;
	}

	public int getEndYear() {
		return mEndYear;
	}

	public String getTitle() {
		return mTitle;
	}
}
