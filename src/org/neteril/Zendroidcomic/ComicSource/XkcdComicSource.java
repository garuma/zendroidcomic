package org.neteril.Zendroidcomic.ComicSource;

import java.util.regex.Pattern;

import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

public class XkcdComicSource implements IComicSource {
	private final static Pattern pattern = Pattern.compile("http://imgs.xkcd.com/comics/\\w+\\.\\w+");

	@Override
	public ComicInformations getNextComic() {
		return ComicSourceHelper.randomWithRegexFetcher(this,
				"http://dynamic.xkcd.com/comic/random/",
				pattern);
	}

	@Override
	public String getComicName() {
		return "XKCD";
	}

	@Override
	public String getComicAuthor() {
		return "Randall Munroe";
	}

	@Override
	public boolean ShouldCachePicture() {
		return true;
	}

}
