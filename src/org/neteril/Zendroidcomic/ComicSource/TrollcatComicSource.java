package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

public class TrollcatComicSource implements IComicSource {
	private final Pattern pattern = Pattern.compile("http://trollcats.com/(\\d+)/(\\d+)/(\\w|-|_)+/");
	private final Pattern imgRegex = Pattern.compile("http://trollcats.com/wp-content/uploads/(\\d+)/(\\d+)/(\\w|-|_)+\\.\\w+");

	@Override
	public ComicInformations getNextComic() {
		String randomUrl = null;
		HttpClient client = ComicSourceHelper.obtainClient();
		
		do {
			try {
				Reader reader = ComicSourceHelper.fetchReader(client, "http://trollcats.com");
				randomUrl = ParserHelper.findTagAttribute(reader, "a", "href", pattern);
				reader.close();
				if (randomUrl == null)
					continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (randomUrl == null);
		
		return ComicSourceHelper.randomWithRegexFetcher(this, randomUrl, imgRegex);
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
