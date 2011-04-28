package org.neteril.Zendroidcomic.ComicSource;

import java.util.Random;
import java.util.regex.Pattern;

import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

public class PhdComicSource implements IComicSource {
	// Dirty hack
	private final int maxId = 1428;
	private Random rand = new Random ();
	private String baseUrl = "http://www.phdcomics.com/comics/archive.php?comicid=";

	@Override
	public ComicInformations getNextComic() {
		int id = -1;
		synchronized (rand) {
			id = rand.nextInt(maxId) + 1;
		}
		return ComicSourceHelper.randomWithRegexFetcher(this,
				baseUrl + id,
				Pattern.compile ("http://www.phdcomics.com/comics/archive/phd\\d{4,}s.gif"));
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
