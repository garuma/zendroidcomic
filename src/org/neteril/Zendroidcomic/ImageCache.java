package org.neteril.Zendroidcomic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.neteril.Zendroidcomic.ComicService.ComicCallback;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class ImageCache {
	final int MAX_PICTURE_FETCH = 3;
	final Bitmap emptyBitmap;
	
	ComicService service;
	List<ComicInformations> bitmapCache = new ArrayList<ComicInformations> ();
	
	public ImageCache (ComicService service, Bitmap defaultImage) {
		this.emptyBitmap = defaultImage;
		this.service = service;
	}
	
	public void getImage (int position, final Handler handler) {
		if (position >= bitmapCache.size()) {
			final AtomicInteger count = new AtomicInteger(MAX_PICTURE_FETCH);
			for (int i = 0; i < MAX_PICTURE_FETCH; i++) {
				ComicCallback callback = new ComicService.ComicCallback() {
					@Override
					public void run(ComicInformations bitmap) {
						if (bitmap != null) {
							synchronized (bitmapCache) {
								bitmapCache.add(bitmap);
							}
						}
						if (count.decrementAndGet() == 0) {
							Message msg = handler.obtainMessage(ImageAdaptater.MESSAGE_NEXT_COMPLETE);
							handler.sendMessage(msg);
						}
					}
				};
				service.getNextComic(callback);
			}
		} else {
			Message msg = handler.obtainMessage(ImageAdaptater.MESSAGE_NEXT_COMPLETE);
			handler.sendMessage(msg);
		}
	}
	
	public void initializeBaseCache (final Handler handler) {
		final AtomicInteger count = new AtomicInteger(MAX_PICTURE_FETCH);
		for (int i = 0; i < MAX_PICTURE_FETCH; i++) {
			ComicCallback callback = new ComicService.ComicCallback() {
				@Override
				public void run(ComicInformations bitmap) {
					synchronized (bitmapCache) {
						bitmapCache.add(bitmap);
					}
					if (count.decrementAndGet() == 0) {
						Message msg = handler.obtainMessage(ImageAdaptater.MESSAGE_PRELOAD_COMPLETE);
						handler.sendMessage(msg);
					}
				}
			};
			service.getNextComic(callback);
		}
	}
	
	public Bitmap getDefaultBitmap () {
		return emptyBitmap;
	}
	
	public Bitmap unsafeGetBitmap (int position) {
		synchronized (bitmapCache) {
			if (position >= bitmapCache.size())
				return emptyBitmap;
			ComicInformations infos = bitmapCache.get(position);
			return infos == null ? emptyBitmap : infos.getComic();	
		}
	}
	
	public boolean canReturnBitmap (int position) {
		return position >= 0 && position < bitmapCache.size();
	}
	
	public ComicInformations getInformations (int position) {
		synchronized (bitmapCache) {
			if (position < 0 || position >= bitmapCache.size())
				return null;
			return bitmapCache.get(position);	
		}
	}
	
	public int getCacheCount () {
		return bitmapCache.size();
	}
}
