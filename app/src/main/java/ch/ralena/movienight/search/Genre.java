package ch.ralena.movienight.search;

/**
 * Created by crater-windoze on 11/18/2016.
 */

public class Genre {
	String name;
	int id;

	public Genre(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
}
