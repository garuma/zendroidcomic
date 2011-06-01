package org.neteril.Zendroidcomic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager extends BroadcastReceiver {
	static ConnectivityManager manager;
	static Context context;
	
	public static void initialize (Context ctx) {
		context = ctx;
		manager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public static boolean getConnectedStatus () {
		NetworkInfo infos = manager.getActiveNetworkInfo(); 
		return infos != null && infos.isConnected();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION))
			return;
		
		if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false))
			showNoConnectivityDialog(null);
	}
	
	public static void showNoConnectivityDialog(final Activity target) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle("No Internet access");
    	builder.setMessage("You have to enable Internet access somehow to use this application");
    	builder.setCancelable(false);
    	builder.setPositiveButton(target == null ? "Alright" : "Close for now", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (target != null)
					target.finish();
			}
		});
    	builder.create().show();
	}
}
