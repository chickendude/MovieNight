package ch.ralena.movienight.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResults {
	private int mCurPage;
	private int mNumPages;
	private int mTotalResults;
	private List<SearchResult> mSearchResults;

	public SearchResults() {
		mSearchResults = new ArrayList<>();
	}
}
