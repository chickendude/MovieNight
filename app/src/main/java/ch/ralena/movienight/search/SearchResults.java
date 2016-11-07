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

	public void addSearchResult(SearchResult searchResult) {
		mSearchResults.add(searchResult);
	}


	public int getCurPage() {
		return mCurPage;
	}

	public void setCurPage(int curPage) {
		mCurPage = curPage;
	}

	public int getNumPages() {
		return mNumPages;
	}

	public void setNumPages(int numPages) {
		mNumPages = numPages;
	}

	public int getTotalResults() {
		return mTotalResults;
	}

	public void setTotalResults(int totalResults) {
		mTotalResults = totalResults;
	}

	public List<SearchResult> getSearchResults() {
		return mSearchResults;
	}
}
