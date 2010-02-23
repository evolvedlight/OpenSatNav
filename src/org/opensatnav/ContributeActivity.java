package org.opensatnav;

import org.anddev.openstreetmap.contributor.util.DatabaseAdapter;
import org.anddev.openstreetmap.contributor.util.constants.Constants;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContributeActivity extends ListActivity {
	Bundle gpsTracks = new Bundle();
	
	private static final int UPLOAD_NOW = 10;
	private static final int TRACE_TOGGLE = UPLOAD_NOW + 1;
	private static final int DELETE_TRACKS = TRACE_TOGGLE + 1;
	private static final int NEW_WAYPOINT = DELETE_TRACKS + 1;
	
	private Boolean inEditName = false;
	private Boolean inEditDescription = false;
	
	
	public void onCreate(Bundle onSavedInstance) {
		super.onCreate(onSavedInstance);
		setTitle(R.string.contribute_title);
		setContentView(R.layout.contribute);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (!(prefs.contains(String.valueOf(R.string.pref_username_key))) && prefs.contains(String.valueOf(R.string.pref_password_key))); 
		TextView textInfo = (TextView) findViewById(R.route_id.textInfo);
		textInfo.setText(getText(R.string.prefs_contribute_osm_username) + " : " + prefs.getString(getString(R.string.pref_username_key), getString(R.string.contribute_username_not_entered)));
		
		fillData();
		final Boolean tracing = TraceRecorderService.isTracing();
		Button startButton = (Button) findViewById(R.id.startRecord);
		if (tracing == true) {
			startButton.setText(this.getResources().getText(
					R.string.contribute_stop_recording));
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
	}
	
	private void fillData() {
        // Get all of the notes from the database and create the item list
		
		
		
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
		mDatabaseAdapter.open();
        Cursor c = mDatabaseAdapter.getJourneys();
        startManagingCursor(c);

        String[] from = new String[] { Constants.T_ROUTERECORDER_ID, Constants.T_ROUTERECORDER_JOURNEY_NAME };
        int[] to = new int[] { R.route_id.id, R.route_id.route_name };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
            new SimpleCursorAdapter(this, R.layout.journey_row, c, from, to);
        setListAdapter(notes);
        mDatabaseAdapter.close();
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