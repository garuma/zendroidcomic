package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class HttpClientPool extends ThreadLocal<HttpClient> {
	@Override
	protected HttpClient initialValue() {
		return new DefaultHttpClient();
	}
}

public class ComicSourceHelper {
	private static final HttpClientPool clientPool = new HttpClientPool();
	
	public static ComicInformations randomWithRegexFetcher (IComicSource source, String randomUrl, Pattern regex) {
		final HttpClient client = clientPool.get();
		final Pattern imageRegex = regex;
		HttpEntity entity = null;
		
		try {
			Matcher matcher = null;
			do {
				HttpResponse response = client.execute(new HttpGet(randomUrl));
				entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				consume(entity);
				matcher = imageRegex.matcher(content);
			} while (!matcher.find ());
			String lastUrl = matcher.group(0);
			HttpResponse response = client.execute(new HttpGet (lastUrl));
			entity = response.getEntity();
			byte[] imgData = EntityUtils.toByteArray(entity);
			consume(entity);
			BitmapFactory.Options config = new BitmapFactory.Options();
			Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, config);
			
			return new ComicInformations(source.getComicName(), source.getComicAuthor(), lastUrl, bmp);
		} catch (IOException e) {
			e.printStackTrace();			
			return randomWithRegexFetcher(source, randomUrl, regex);
		}
	}
	
	public static HttpClient obtainClient () {
		return clientPool.get ();
	}
	
	private static Random rand = new Random ();

	public static Calendar getRandomDate (int baseYear) {
		synchronized (rand) {
			Calendar today = Calendar.getInstance();
			int year = rand.nextInt (today.get(Calendar.YEAR) - baseYear + 1) + baseYear;
			int month = year == today.get(Calendar.YEAR) ? rand.nextInt (today.get(Calendar.MONTH) - 1) + 1 : rand.nextInt (12) + 1;
			int day = year == today.get(Calendar.YEAR) ? rand.nextInt (today.get(Calendar.DAY_OF_WEEK)) + 1 : rand.nextInt (29) + 1;
			
			return new GregorianCalendar(year, month, day);	
		}
	}
	
	public static void consume(final HttpEntity entity) throws IOException {
        if (entity == null) {
            return;
        }
        if (entity.isStreaming()) {
            InputStream instream = entity.getContent();
            if (instream != null) {
                instream.close();
            }
        }
    }
}
