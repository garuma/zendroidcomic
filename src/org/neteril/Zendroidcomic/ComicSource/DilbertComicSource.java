package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DilbertComicSource implements IComicSource {
	private static final Pattern imageRegex = Pattern.compile("http://dilbert.com/dyn/str_strip/.+\\.strip\\.gif");

	@Override
	public ComicInformations getNextComic() {
		HttpEntity entity = null;
		try {
			final HttpClient client = ComicSourceHelper.obtainClient();
			Matcher matcher = null;
			do {
				Calendar randomDate = ComicSourceHelper.getRandomDate(1995);
				String randomUrl = String.format ("http://dilbert.com/strips/comic/%d-%d-%d/",
						randomDate.get(Calendar.YEAR),
						randomDate.get(Calendar.MONTH),
						randomDate.get(Calendar.DATE));

				String content = ComicSourceHelper.fetchStringWithFixup(client, randomUrl);
				matcher = imageRegex.matcher(content);
			} while (!matcher.find () && !matcher.find ());
			String lastUrl = matcher.group(0);
			byte[] imgData = ComicSourceHelper.fetchByteArray(client, lastUrl);
			BitmapFactory.Options config = new BitmapFactory.Options();
			Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, config);
			
			return new ComicInformations(getComicName(), getComicAuthor(), lastUrl, bmp);
		} catch (IOException e) {
			e.printStackTrace();
			try {
		        ComicSourceHelper.consume(entity);
			} catch (IOException ex) {}
			return getNextComic();
		}
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
