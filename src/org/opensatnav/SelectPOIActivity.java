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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.andnav.osm.util.GeoPoint;
import org.opensatnav.services.GeoCoder;
import org.opensatnav.services.OSMGeoCoder;
import org.opensatnav.services.Router;
import org.opensatnav.services.YOURSRouter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Kieran Fleming
 * 
 */

public class SelectPOIActivity extends ListActivity {
	protected ArrayList<String> route;
	protected static final int CHOOSE_LOCATION = 0;
	protected Intent data;
	protected Bundle locations;
	protected GeoPoint from;

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = new Intent();
		from = GeoPoint.fromDoubleString(getIntent().getDataString(), ',');
		setTitle("Find nearest...");
		// Use an existing ListAdapter that will map an array
		// of strings to TextViews
		String[] poisUnsorted = this.getResources().getStringArray(R.array.poi_types);
		Arrays.sort(poisUnsorted);
		final String [] pois = poisUnsorted;
		setListAdapter(new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pois));
		getListView().setTextFilterEnabled(true);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {
				getLocations(pois[(int) position] + " near " + from.toDoubleString());

			}

		});
	}
	
	public void getLocations(final String toText) {
		if (toText.length() != 0) {
			final ProgressDialog progress = ProgressDialog.show(SelectPOIActivity.this, this.getResources().getText(
					R.string.please_wait), this.getResources().getText(R.string.searching), true, true);
			final Handler handler = new Handler() {
				// threading stuff - this actually handles the stuff after the
				// thread has completed (code below)
				public void handleMessage(Message msg) {
					progress.dismiss();
					if((locations != null) && (locations.getStringArray("names").length!=0)) {
						Intent intent = new Intent(SelectPOIActivity.this, org.opensatnav.ChooseLocationActivity.class);
						intent.putExtra("fromLocation", from.toDoubleString());
						intent.putExtra("locations", locations);
						startActivityForResult(intent, CHOOSE_LOCATION);
					} else
						Toast.makeText(
								SelectPOIActivity.this,
								SelectPOIActivity.this.getResources().getText(R.string.could_not_find_poi) + " "
										+ toText, Toast.LENGTH_LONG).show();
				}
			};
			new Thread(new Runnable() {
				public void run() {
					// put long running operations here
					GeoCoder geoCoder = new OSMGeoCoder();
					locations = geoCoder.getFromLocationName(toText, 15, SelectPOIActivity.this);
					// ok, we are done
					handler.sendEmptyMessage(0);
				}
			}).start();

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_LOCATION) {
			if (resultCode == RESULT_OK) {
				GeoPoint location = GeoPoint.fromIntString(data.getStringExtra("location"));
				getRoute(from, location);
				if (route != null) {
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("route", route);
					data.putExtras(bundle);
					setResult(RESULT_OK, data);
					finish();
				}
			}
		}

	}

	public void getRoute(final GeoPoint from, final GeoPoint to) {

		final ProgressDialog progress = ProgressDialog.show(SelectPOIActivity.this, this.getResources().getText(
				R.string.please_wait), this.getResources().getText(R.string.getting_route), true, true);
		final Handler handler = new Handler() {
			// threading stuff - this actually handles the stuff after the
			// thread has completed (code below)
			public void handleMessage(Message msg) {
				progress.dismiss();
				if (route != null) {
					data.putExtra("route", route);
					setResult(RESULT_OK, data);
					finish();
				} else
					Toast.makeText(SelectPOIActivity.this,
							SelectPOIActivity.this.getResources().getText(R.string.directions_not_found),
							Toast.LENGTH_LONG).show();
			}
		};
		new Thread(new Runnable() {
			public void run() {
				// put long running operations here
				//TODO: support non car routing for these as well
				Router router = new YOURSRouter();
				if (to != null)
					route = router.getRoute(from, to, Router.CAR, SelectPOIActivity.this);
				// ok, we are done
				handler.sendEmptyMessage(0);
			}
		}).start();

	}
}
