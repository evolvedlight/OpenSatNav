/* 
This file is part of OpenSatNav.

    OpenSatNav is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenSatNav is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenSatNav.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.opensatnav;

import java.util.ArrayList;

import org.andnav.osm.util.GeoPoint;
import org.opensatnav.services.GeoCoder;
import org.opensatnav.services.OSMGeoCoder;
import org.opensatnav.services.Router;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * @author Kieran Fleming
 * 
 */

public class GetDirectionsActivity extends Activity {
	protected static final int CHOOSE_LOCATION = 0;
	protected Intent data;
	// protected ProgressDialog progress;
	protected Bundle locations;
	protected ArrayList<String> route;
	protected GeoPoint from;
	protected String toText;
	protected EditText toField;
	protected Spinner vehicleSpinner;
	protected String vehicle;
	protected Boolean backgroundThreadComplete;

	public void onCreate(Bundle savedInstanceState) {
		data = new Intent();
		super.onCreate(savedInstanceState);
		from = GeoPoint.fromDoubleString(getIntent().getDataString(), ',');
		setTitle(R.string.get_directions);
		setContentView(R.layout.getdirections);

		Spinner s = (Spinner) findViewById(R.id.modeoftransport);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.mode_of_transport_types,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		s.setPrompt((CharSequence) findViewById(R.string.transport_type));

		vehicleSpinner = (Spinner) findViewById(R.id.modeoftransport);
		toField = (EditText) findViewById(R.id.to_text_field);
		toField.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on enter key press
					getLocations(toField.getText().toString());
					return true;
				}
				return false;
			}

		});
		Button goButton = (Button) findViewById(R.id.go_button);
		goButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getLocations(toField.getText().toString());
			}
		});
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED, data);
				finish();
			}
		});
		// TODO: save and restore vehicle type
	}

	public void getLocations(final String toText) {
		if (toText.length() != 0) {
			final ProgressDialog progress = ProgressDialog.show(GetDirectionsActivity.this, this.getResources()
					.getText(R.string.please_wait), this.getResources().getText(R.string.searching), true, true);
			final Handler handler = new Handler() {
				// threading stuff - this actually handles the stuff after the
				// thread has completed (code below)
				public void handleMessage(Message msg) {
					progress.dismiss();
					if (locations != null) {
						Intent intent = new Intent(GetDirectionsActivity.this,
								org.opensatnav.ChooseLocationActivity.class);
						intent.putExtra("fromLocation", from.toDoubleString());
						intent.putExtra("locations", locations);
						startActivityForResult(intent, CHOOSE_LOCATION);
					} else {
						Toast.makeText(
								GetDirectionsActivity.this,
								GetDirectionsActivity.this.getResources().getText(R.string.place_not_found) + " "
										+ toText, Toast.LENGTH_LONG).show();
					}
				}
			};
			new Thread(new Runnable() {
				public void run() {
					// put long running operations here
					GeoCoder geoCoder = new OSMGeoCoder();
					locations = geoCoder.getFromLocationName(toText, 15, GetDirectionsActivity.this);
					// ok, we are done
					handler.sendEmptyMessage(0);
				}
			}).start();

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_LOCATION) {
			if (resultCode == RESULT_OK) {
				String selectedVehicle = (String) vehicleSpinner.getSelectedItem();
				// TODO: make this less wasteful and dumb :)
				// the point is to support i18n by not hardcoding the text
				// selected
				String car = (String) this.getResources().getText(R.string.car);
				String bicycle = (String) this.getResources().getText(R.string.bicycle);
				String walking = (String) this.getResources().getText(R.string.walking);

				if (selectedVehicle.compareTo(car) == 0) {
					vehicle = Router.CAR;
				} else if (selectedVehicle.compareTo(bicycle) == 0) {
					vehicle = Router.BICYCLE;
				} else if (selectedVehicle.compareTo(walking) == 0) {
					vehicle = Router.WALKING;
				}
				Bundle bundle = new Bundle();
				bundle.putString("vehicle", vehicle);
				bundle.putString("to", data.getStringExtra("location"));
				data.putExtras(bundle);
				setResult(RESULT_OK, data);
				finish();

			}
		}

	}

}
