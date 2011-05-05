package org.neteril.Zendroidcomic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.Toast;

public class Zendroidcomic extends Activity implements OnSharedPreferenceChangeListener {
	ComicService service;
	ImageCache cache;
	ImageAdaptater adapter;
	Gallery gallery;
	Toast currentToast;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);;
        setContentView(R.layout.main);
        setProgressBarIndeterminate(true);
        setProgressBarIndeterminateVisibility(false);
        
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
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	Log.w ("Preferences key", key);
    	if (key.equals(getString(R.string.comicCachePref))) {
    		// TODO
    	} else if (key.equals(getString(R.string.comicShowAd))) {
    		// TODO
    	} else {
    		// Comic list has changed
    		if (key.equals(getString(R.string.prefComicDilbert)))
    			service.toggleComicAvailability(ComicService.Available.Dilbert);
    		else if (key.equals(getString(R.string.prefComicGarfield)))
    			service.toggleComicAvailability(ComicService.Available.Garfield);
    		else if (key.equals(getString(R.string.prefComicLolcats)))
    			service.toggleComicAvailability(ComicService.Available.Lolcat);
    		else if (key.equals(getString(R.string.prefComicPhdcomic)))
    			service.toggleComicAvailability(ComicService.Available.PhdComic);
    		else if (key.equals(getString(R.string.prefComicXkcd)))
    			service.toggleComicAvailability(ComicService.Available.Xkcd);
    	}
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.options, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Only one button, launch setting activity
    	Intent intent = new Intent(this, ComicPreferences.class);
    	startActivity(intent);
    	return true;
    }
}