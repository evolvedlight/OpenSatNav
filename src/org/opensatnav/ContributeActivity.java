package org.opensatnav;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ContributeActivity extends Activity {
	Bundle gpsTracks = new Bundle();
	
	private static final int UPLOAD_NOW = 10;
	private static final int TRACE_TOGGLE = UPLOAD_NOW + 1;
	private static final int DELETE_TRACKS = TRACE_TOGGLE + 1;
	private static final int NEW_WAYPOINT = DELETE_TRACKS + 1;
	private static final int CLEAR_OLD_TRACES = NEW_WAYPOINT + 1;
	
	private Boolean inEditName = false;
	private Boolean inEditDescription = false;
	
	public void onCreate(Bundle onSavedInstance) {
		super.onCreate(onSavedInstance);
		setTitle(R.string.contribute_title);
		setContentView(R.layout.contribute);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (!(prefs.contains(String.valueOf(R.string.pref_username_key))) && prefs.contains(String.valueOf(R.string.pref_password_key))); 
		TextView textInfo = (TextView) findViewById(R.id.textInfo);
		textInfo.setText(getText(R.string.prefs_contribute_osm_username) + " : " + prefs.getString(getString(R.string.pref_username_key), getString(R.string.contribute_username_not_entered)));
		final Boolean tracing = TraceRecorderService.isTracing();
		Button startButton = (Button) findViewById(R.id.startRecord);
		if (tracing == true) {
			startButton.setText(this.getResources().getText(
					R.string.contribute_stop_recording));
		}
		Button deleteButton = (Button) findViewById(R.id.deleteTracks);

		Button deleteOldTracesButton = (Button) findViewById(R.id.clearOldTraces);
		Button addWayPointButton = (Button) findViewById(R.id.newWayPoint);
		if (tracing == false) {
			addWayPointButton.setVisibility(Button.INVISIBLE);
		} else {
			addWayPointButton.setVisibility(Button.VISIBLE);
		}
		

		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tracing == true) {
					TraceRecorderService.stop(ContributeActivity.this);
					displayToast(R.string.contribute_gps_off);
				} else {
					TraceRecorderService.start(ContributeActivity.this);
					displayToast(R.string.contribute_gps_on);
				}
				finish();
			}
		});



		deleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TraceRecorderService.resetTrace();
				displayToast(R.string.contribute_track_cleared);
				finish();
			}
		});
		
		deleteOldTracesButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(CLEAR_OLD_TRACES);
				finish();
			}
		});
		
		addWayPointButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				inEditName = true;
				getWayPointInfo();
			}
		});

		Button uploadButton = (Button) findViewById(R.id.uploadButton);
		uploadButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				inEditDescription = true;
				askForDescription();

			}
		});


	}
	public void askForDescription() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getText(R.string.contribute_dialogue_trace_description));
		alert.setMessage(getText(R.string.contribute_dialogue_trace_message));

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);

		alert.setView(input);

		alert.setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String description = input.getText().toString();
				Intent data = getIntent();
				data.putExtra("description", description);
				Log.v("OpenSatNav", "Setting Result");
				setResult(UPLOAD_NOW, data);
				Log.v("OpenSatNav", "Finishing");
				finish();
			}
		});
		
		

		alert.setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();

	}
	
	public void getWayPointInfo() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle(getText(R.string.contribute_dialogue_new_waypoint));
		alert.setMessage(getText(R.string.contribute_dialogue_new_waypoint_message));

		// Set an EditText view to get user input 
		final EditText wayPointName = new EditText(this);
		
		
		alert.setView(wayPointName);

		alert.setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = wayPointName.getText().toString();
				
				Intent data = getIntent();
				data.putExtra("wayPointName", name);
				setResult(NEW_WAYPOINT, data);
				finish();
			}
		});

		alert.setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}
	
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("inEditName", inEditName);
		savedInstanceState.putBoolean("inEditDescription", inEditDescription);
		super.onSaveInstanceState(savedInstanceState); // the UI component values are saved here.
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.getBoolean("inEditName")) {
			getWayPointInfo();
		}
		if (savedInstanceState.getBoolean("inEditDescription")) {
			askForDescription();
		}
	}

	private void displayToast(String msg) {
		Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
	}

	private void displayToast(int stringReference) {
		displayToast((String) getText(stringReference));
	}
}