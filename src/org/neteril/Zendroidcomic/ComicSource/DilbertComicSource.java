package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DilbertComicSource implements IComicSource {
	final Pattern imageRegex = Pattern.compile("http://dilbert.com/dyn/str_strip/.+\\.strip\\.gif");

	@Override
	public ComicInformations getNextComic() {
		HttpEntity entity = null;
		try {
			final HttpClient client = new DefaultHttpClient();
			Matcher matcher = null;
			do {
				Calendar randomDate = ComicSourceHelper.getRandomDate(1995);
				String randomUrl = String.format ("http://dilbert.com/strips/comic/%d-%d-%d/",
						randomDate.get(Calendar.YEAR),
						randomDate.get(Calendar.MONTH),
						randomDate.get(Calendar.DATE));

				HttpResponse response = client.execute(new HttpGet(randomUrl));
				entity = response.getEntity();
				// Hurray for broken Dilbert encoding (was utf8-lias)
				String content = EncodingUtils.getString(EntityUtils.toByteArray(entity), "utf-8");
				ComicSourceHelper.consume(entity);
				matcher = imageRegex.matcher(content);
			} while (!matcher.find ());
			String lastUrl = matcher.group(0);
			HttpResponse response = client.execute(new HttpGet (lastUrl));
			
			entity = response.getEntity();
			byte[] imgData = EntityUtils.toByteArray(entity);
			ComicSourceHelper.consume(entity);
			BitmapFactory.Options config = new BitmapFactory.Options();
			Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, config);
			
			return new ComicInformations(getComicName(), getComicAuthor(), lastUrl, bmp);
		} catch (IOException e) {
			e.printStackTrace();
			try {
		        if (entity != null && entity.isStreaming()) {
		            InputStream instream = entity.getContent();
		            if (instream != null) {
		                instream.close();
		            }
		        }
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
