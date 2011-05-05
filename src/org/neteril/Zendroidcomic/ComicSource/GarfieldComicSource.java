package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.client.HttpClient;
import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GarfieldComicSource implements IComicSource {
	private final String baseUrl = "http://images.ucomics.com/comics/ga/%s/ga%s.gif";
	
	@Override
	public ComicInformations getNextComic() {
		final HttpClient client = ComicSourceHelper.obtainClient();
		do {
			Calendar rndDate = ComicSourceHelper.getRandomDate(1980);
			String finalUrl = String.format(baseUrl,
					rndDate.get(Calendar.YEAR),
					new SimpleDateFormat("yyMMdd").format(rndDate.getTime()));
			try {
				byte[] imgData = ComicSourceHelper.fetchByteArray(client, finalUrl);
				BitmapFactory.Options config = new BitmapFactory.Options();
				Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, config);
				
				return new ComicInformations(getComicName(), getComicAuthor(), finalUrl, bmp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	@Override
	public String getComicName() {
		return "Garfield";
	}

	@Override
	public String getComicAuthor() {
		return "Jim Davis";
	}

	@Override
	public boolean ShouldCachePicture() {
		return true;
	}

}
