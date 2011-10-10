package org.neteril.Zendroidcomic.ComicSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.neteril.Zendroidcomic.ComicInformations;
import org.neteril.Zendroidcomic.IComicSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ComicSourceHelper {
	private final static HttpClient safeClient = initClient ();
	
	private static HttpClient initClient () {
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 100);
        ConnManagerParams.setTimeout(params, 2000);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpConnectionParams.setConnectionTimeout(params, 2000);
        HttpConnectionParams.setSoTimeout(params, 2000);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

        return new DefaultHttpClient(cm, params);
	}
	
	public static ComicInformations randomWithRegexFetcher (IComicSource source, String randomUrl, Pattern regex) {
		final HttpClient client = safeClient;
		
		byte[] imgData = null;
		String lastUrl = null, currentUrl = null;
		int tries = 3;
		
		do {
			Log.i("Fetcher", "Fetching " + randomUrl);
			try {
				HttpContext ctx = new BasicHttpContext();
				HttpResponse response = client.execute(new HttpGet(randomUrl), ctx);
				HttpEntity entity = response.getEntity();
				HttpUriRequest currentReq = (HttpUriRequest)ctx.getAttribute(ExecutionContext.HTTP_REQUEST);
		        HttpHost currentHost = (HttpHost)ctx.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		        // The real URL we got redirected to
		        currentUrl = currentHost.toURI() + currentReq.getURI();
				// Fuck off to Dilbert and its broken utf8 encoding
				Reader reader = new InputStreamReader(entity.getContent(), "utf-8");
				lastUrl = ParserHelper.findTagAttribute(reader, "img", "src", regex);
				consume(entity);
				if (lastUrl == null) {
					Thread.sleep(200);
					continue;
				}
				imgData = fetchByteArray(client, lastUrl);
			} catch (SocketException e) {
				// Socket exceptions are mostly triggered by timeout, go out early in that case
				Log.w("Comic src helper", "Socket exception");
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				Log.w("Comic src helper", "IO exception");
				e.printStackTrace();
				continue;
			} catch (InterruptedException e) {
				Log.w("Comic src helper", "Interrupted exception");
				e.printStackTrace();
				continue;
			} catch (Exception e) {
				Log.w("Comic src helper", "Generic exception");
				e.printStackTrace();
				continue;
			}
			
			BitmapFactory.Options config = new BitmapFactory.Options();
			Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, config);
			
			return new ComicInformations(source.getComicName(), source.getComicAuthor(), currentUrl, bmp);
		} while (--tries >= 0);
		
		return null;
	}
		
	public static Reader fetchReader (HttpClient client, String url) throws IOException {
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		// Fuck off to Dilbert and its broken utf8 encoding
		return new InputStreamReader(entity.getContent(), "utf-8");
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
	
	public static int getRandomIndex (int min, int max) {
		synchronized (rand) {
			return rand.nextInt(max - min) + min;			
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
