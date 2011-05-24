package org.neteril.Zendroidcomic.ComicSource;

import java.util.Calendar;
import java.util.regex.Pattern;

import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

public class DilbertComicSource implements IComicSource {
	private static final Pattern imageRegex = Pattern.compile("http://dilbert.com/dyn/str_strip/.+\\.gif");

	@Override
	public ComicInformations getNextComic() {
		Calendar randomDate = ComicSourceHelper.getRandomDate(1995);
		String randomUrl = String.format ("http://dilbert.com/strips/comic/%d-%d-%d/",
				randomDate.get(Calendar.YEAR),
				randomDate.get(Calendar.MONTH),
				randomDate.get(Calendar.DATE));
		
		return ComicSourceHelper.randomWithRegexFetcher(this, randomUrl, imageRegex);
	}

	@Override
	public String getComicName() {
		return "Dilbert";
	}

	@Override
	public String getComicAuthor() {
		return "Scott Adams";
	}

	@Override
	public boolean ShouldCachePicture() {
		return true;
	}

}
