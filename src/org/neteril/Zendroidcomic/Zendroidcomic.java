package org.neteril.Zendroidcomic;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.Toast;

public class Zendroidcomic extends Activity {
	ComicService service;
	ImageCache cache;
	ImageAdaptater adapter;
	Gallery gallery;
	Toast currentToast;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        service = new ComicService();
        cache = new ImageCache(service, BitmapFactory.decodeResource(getResources(), R.drawable.empty));
        adapter = new ImageAdaptater(this, cache);
        gallery = (Gallery)findViewById(R.id.mainGallery);
        gallery.setAdapter(adapter);
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onNothingSelected(AdapterView<?> parent) {
        		if (currentToast != null) {
        			currentToast.cancel();
        			currentToast = null;
        		}
        	}
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onNothingSelected(parent);
				showComicInfos(cache.getInformations(position));
			}
		});
    }
    
    private void showComicInfos (ComicInformations is) {
		if (is == null)
			return;
		currentToast = Toast.makeText(this, String.format("%s from %s", is.getName(), is.getAuthor()), Toast.LENGTH_SHORT);
		currentToast.show ();
	}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
}