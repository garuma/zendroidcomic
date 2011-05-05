package org.neteril.Zendroidcomic.ComicSource;

import java.util.regex.Pattern;

import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

public class LolcatComicSource implements IComicSource {
	private final static Pattern pattern = Pattern.compile("http://icanhascheezburger.files.wordpress.com/(\\d+)/(\\d+)/(\\w|-|_)+\\.\\w+");
	
	@Override
	public ComicInformations getNextComic() {
		return ComicSourceHelper.randomWithRegexFetcher(this,
				"http://icanhascheezburger.com/?random",
				pattern);
	}

	@Override
	public String getComicName() {
		return "Lolcats";
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
