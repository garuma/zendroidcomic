package org.neteril.Zendroidcomic;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdaptater extends BaseAdapter {
	public static final int MESSAGE_PRELOAD_COMPLETE = 1;
	public static final int MESSAGE_NEXT_COMPLETE = 2;

	private Context context;
	private ImageCache cache;
	private int count = 0;
	private ImageView currentImageView;
	private ProgressDialog dialog;

	public ImageAdaptater (Context context, ImageCache cache) {
		this.context = context;
		this.cache = cache;
        dialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
        cache.initializeBaseCache(handler);
	}
	
	private final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_PRELOAD_COMPLETE:
					if (dialog == null)
						return;
					dialog.dismiss();
					count = cache.getCacheCount ();
					dialog = null;
					dataChanged(true);
					break;
				case MESSAGE_NEXT_COMPLETE:
					if (currentImageView == null)
						return;
					currentImageView = null;
					count = cache.getCacheCount ();
					dataChanged(false);
					break;
				}
			}
	};

	@Override
	public int getCount() {
		return count + 1;
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view = new ImageView (context);
		
		if (count == 0) {
			view.setImageBitmap(cache.getDefaultBitmap());
		} else if (cache.canReturnBitmap(position)) {
			view.setImageBitmap(cache.unsafeGetBitmap (position));
		} else {
			// There is already pending request to show something, cancel
			if (currentImageView != null)
				return currentImageView;
			
			view.setImageBitmap(cache.getDefaultBitmap());
			currentImageView = view;
			cache.getImage(position, handler);
		}
		
        view.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT, Gallery.LayoutParams.MATCH_PARENT));
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        view.setBackgroundColor(Color.BLACK);
		
		return view;
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	private void dataChanged (boolean firstTime) {
		this.notifyDataSetChanged();
	}
	
	
}
