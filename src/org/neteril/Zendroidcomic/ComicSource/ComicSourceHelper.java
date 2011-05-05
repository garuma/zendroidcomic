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
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ComicSourceHelper {
	private final static HttpClient safeClient = initClient ();
	
	private static HttpClient initClient () {
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 100);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
         
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        
        return new DefaultHttpClient(cm, params);
	}
	
	public static ComicInformations randomWithRegexFetcher (IComicSource source, String randomUrl, Pattern regex) {
		final HttpClient client = safeClient;
		final Pattern imageRegex = regex;
		
		Matcher matcher = null;
		String content = null;
		byte[] imgData = null;
		
		do {
			do {
				try {
					content = fetchString(client, randomUrl);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				matcher = imageRegex.matcher(content);
			} while (content == null || matcher == null || !matcher.find ());
		
			String lastUrl = matcher.group(0);
			// Help GC a bit
			content = null;
			matcher = null;
			
			try {
				imgData = fetchByteArray(client, lastUrl);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			BitmapFactory.Options config = new BitmapFactory.Options();
			Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, config);
			
			return new ComicInformations(source.getComicName(), source.getComicAuthor(), lastUrl, bmp);
		} while (true);
	}
	
	public static String fetchString (HttpClient client, String url) throws IOException {
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		String content = EntityUtils.toString(entity);
		consume(entity);
		
		return content;
	}
	
	public static String fetchStringWithFixup (HttpClient client, String url) throws IOException {
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		String content = EncodingUtils.getString(EntityUtils.toByteArray(entity), "utf-8");
		ComicSourceHelper.consume(entity);
		
		return content;
	}
	
	public static byte[] fetchByteArray (HttpClient client, String url) throws IOException {
		HttpResponse response = client.execute(new HttpGet (url));
		HttpEntity entity = response.getEntity();
		byte[] data = EntityUtils.toByteArray(entity);
		consume(entity);
		
		return data;
	}
	
	public static HttpClient obtainClient () {
		return safeClient;
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
