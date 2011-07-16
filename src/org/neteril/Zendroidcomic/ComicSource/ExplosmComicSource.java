package org.neteril.Zendroidcomic.ComicSource;

import java.util.regex.Pattern;

import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

public class ExplosmComicSource implements IComicSource {
	private final static Pattern pattern = Pattern.compile("http://www.explosm.net/db/files/Comics/(\\w|_|-|/)+\\.\\w{3,}");

	@Override
	public ComicInformations getNextComic() {
		return ComicSourceHelper.randomWithRegexFetcher(this,
			"http://www.explosm.net/comics/" + ComicSourceHelper.getRandomIndex(50, 2486),
			pattern);
	}

	@Override
	public String getComicName() {
		return "Cyanide & Happiness";
	}

	@Override
	public String getComicAuthor() {
		return "Various";
	}

	@Override
	public boolean ShouldCachePicture() {
		return false;
	}

}
