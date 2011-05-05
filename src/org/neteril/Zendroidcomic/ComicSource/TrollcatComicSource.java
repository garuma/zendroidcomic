package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

public class TrollcatComicSource implements IComicSource {
	private final Pattern pattern = Pattern.compile("http://trollcats.com/(\\d+)/(\\d+)/(\\w|-|_)+/");
	private final Pattern imgRegex = Pattern.compile("http://trollcats.com/wp-content/uploads/(\\d+)/(\\d+)/(\\w|-|_)+\\.\\w+");

	@Override
	public ComicInformations getNextComic() {
		String page = null;
		do {
			try {
				page = ComicSourceHelper.fetchString(ComicSourceHelper.obtainClient(), "http://trollcats.com");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (page == null);
		Matcher matcher = null;
		do {
			matcher = pattern.matcher(page);
		} while (!matcher.find());
		
		return ComicSourceHelper.randomWithRegexFetcher(this, matcher.group(0), imgRegex);
	}

	@Override
	public String getComicName() {
		return "Trollcats";
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
