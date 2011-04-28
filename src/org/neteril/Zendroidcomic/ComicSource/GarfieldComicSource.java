package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GarfieldComicSource implements IComicSource {
	private final String baseUrl = "http://images.ucomics.com/comics/ga/%s/ga%s.gif";
	
	@Override
	public ComicInformations getNextComic() {
		final HttpClient client = ComicSourceHelper.obtainClient();
		Calendar rndDate = ComicSourceHelper.getRandomDate(1980);
		String finalUrl = String.format(baseUrl,
				rndDate.get(Calendar.YEAR),
				new SimpleDateFormat("yyMMdd").format(rndDate.getTime()));
		try {
			HttpResponse response = client.execute(new HttpGet(finalUrl));
			HttpEntity entity = response.getEntity();
			byte[] imgData = EntityUtils.toByteArray(entity);
			ComicSourceHelper.consume(entity);
			BitmapFactory.Options config = new BitmapFactory.Options();
			Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, config);
			
			return new ComicInformations(getComicName(), getComicAuthor(), finalUrl, bmp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
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
