package org.neteril.Zendroidcomic;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.Toast;

public class Zendroidcomic extends Activity {
	private final int PREF_RETURN_CODE = 100;
	
	private ComicService service;
	private ImageCache cache;
	private ImageAdaptater adapter;
	private Gallery gallery;
	private Toast currentToast;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkInternetConnectivity();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);;
        setContentView(R.layout.main);
        setProgressBarIndeterminate(true);
        setProgressBarIndeterminateVisibility(false);

        boolean[] comicsEnabled = fetchComicPreferences ();
        while (comicsEnabled == null) {
        	askForNewPreferences();
        	comicsEnabled = fetchComicPreferences();
        }

        Log.w("initial settings", Arrays.toString(comicsEnabled));
        service = new ComicService(comicsEnabled);
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
        registerForContextMenu(gallery);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.context, menu);
    	ComicInformations comicInfos = cache.getInformations(((AdapterContextMenuInfo)menuInfo).position);
    	if (comicInfos != null)
    		menu.setHeaderTitle(comicInfos.getName());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	ComicInformations comicInformations = cache.getInformations(info.position);
    	if (comicInformations == null)
    		return false;
    	Intent intent = null;
    	switch (item.getItemId()) {
    	case R.id.contextBrowser:
    		intent = new Intent (Intent.ACTION_VIEW, Uri.parse(comicInformations.getUrl()));
    		break;
    	/*case R.id.contextFavorite:
    		break;*/
    	case R.id.contextShare:
    		intent = new Intent (Intent.ACTION_SEND);
    		intent.putExtra(Intent.EXTRA_SUBJECT, String.format("Sharing this %s strip", comicInformations.getName()));
    		intent.putExtra(Intent.EXTRA_TEXT, comicInformations.getUrl());
    		intent.setType("text/plain");
    		
    		break;
    	}
    	
    	if (intent == null)
    		return false;
    	
    	startActivity(Intent.createChooser(intent, "Share comic"));
    	
    	return true;
    }

    private boolean[] fetchComicPreferences () {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	boolean[] newAvailability = {
    		!prefs.getBoolean(getString(R.string.prefComicXkcd), true),
    		!prefs.getBoolean(getString(R.string.prefComicGarfield), true),
    		!prefs.getBoolean(getString(R.string.prefComicDilbert), true),
    		!prefs.getBoolean(getString(R.string.prefComicPhdcomic), true),
    		!prefs.getBoolean(getString(R.string.prefComicTrollcats), true)
    	};
    	
    	// Check that at least one of them is false
    	for (boolean b : newAvailability)
    		if (!b)
    			return newAvailability;
    	return null;
    }

    private void askForNewPreferences () {
    	AlertDialog.Builder builder = new AlertDialog.Builder (this);
    	builder.setTitle("Insufficient comic sources");
    	builder.setMessage("You have no enabled comic source, you should have at least one enabled");
    	builder.setCancelable(false);
    	builder.setPositiveButton("Open preferences", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Zendroidcomic.this, ComicPreferences.class);
				dialog.dismiss();
		    	startActivityForResult(intent, PREF_RETURN_CODE);
			}
		});
    	builder.create().show();
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
    	startActivityForResult(intent, PREF_RETURN_CODE);
    	return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == PREF_RETURN_CODE) {
    		boolean[] newAvailability = fetchComicPreferences();
    		if (newAvailability == null) {
    			askForNewPreferences();
    			return;
    		}
    		Log.w("Preferences", "updating preferences upon activity return");
    		service.updateComicAvailability(newAvailability);
    	}
    	
    	super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkInternetConnectivity () {
    	ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
    	// TODO: also monitor network changes
    	if (manager.getActiveNetworkInfo().isConnected())
    		return;
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder (this);
    	builder.setTitle("No Internet access");
    	builder.setMessage("You have to enable Internet access somehow before you can use this application");
    	builder.setCancelable(false);
    	builder.setPositiveButton("Close for now", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
    	builder.create().show();
    }
}