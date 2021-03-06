package org.neteril.Zendroidcomic.ComicSource;

import java.util.regex.Pattern;

import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

public class PhdComicSource implements IComicSource {
	// Dirty hack
	private final int maxId = 1428;
	private static final String baseUrl = "http://www.phdcomics.com/comics/archive.php?comicid=";
	private static final Pattern pattern = Pattern.compile ("http://www.phdcomics.com/comics/archive/phd\\d{4,}s.gif");

	@Override
	public ComicInformations getNextComic() {
		int id = ComicSourceHelper.getRandomIndex(1, maxId);
		return ComicSourceHelper.randomWithRegexFetcher(this,
				baseUrl + id,
				pattern);
	}

	@Override
	public String getComicName() {
		return "PHDComic";
	}

	@Override
	public String getComicAuthor() {
		return "Jorge Cham";
	}

	@Override
	public boolean ShouldCachePicture() {
		return true;
	}

}
