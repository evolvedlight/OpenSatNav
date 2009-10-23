package org.opensatnav;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.anddev.openstreetmap.contributor.util.OSMUploader;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ContributeActivity extends Activity {
	Bundle gpsTracks = new Bundle();
	
	private static final int UPLOAD_NOW = 10;
	private static final int TRACE_TOGGLE = UPLOAD_NOW + 1;
	private static final int DELETE_TRACKS = TRACE_TOGGLE + 1;
	private static final int NEW_WAYPOINT = DELETE_TRACKS + 1;
	private static final int CLEAR_OLD_TRACES = NEW_WAYPOINT + 1;
	
	private String editName = "";
	private String editDescription = "";
	
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
		Boolean tracing = Boolean.valueOf(getIntent().getDataString());
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
				// TODO Auto-generated method stub
				setResult(TRACE_TOGGLE);
				finish();
			}
		});



		deleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(DELETE_TRACKS);
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

		alert.setPositiveButton(getText(R.string.contribute_dialogue_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String description = input.getText().toString();
				Intent data = getIntent();
				data.putExtra("description", description);
				setResult(UPLOAD_NOW, data);
				finish();
			}
		});
		
		

		alert.setNegativeButton(getText(R.string.contribute_dialogue_cancel), new DialogInterface.OnClickListener() {
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

		alert.setPositiveButton(getText(R.string.contribute_dialogue_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = wayPointName.getText().toString();
				
				Intent data = getIntent();
				data.putExtra("wayPointName", name);
				setResult(NEW_WAYPOINT, data);
				finish();
			}
		});

		alert.setNegativeButton(getText(R.string.contribute_dialogue_cancel), new DialogInterface.OnClickListener() {
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
}