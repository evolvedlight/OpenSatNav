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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.andnav.osm.util.GeoPoint;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseLocationActivity extends ListActivity {
	DecimalFormat decimalFormat;

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getBundleExtra("locations");
		String[] locationInfo = b.getStringArray("info");
		String[] locationNames = b.getStringArray("names");
		final int[] locationLats = b.getIntArray("latitudes");
		final int[] locationLongs = b.getIntArray("longitudes");
		// currently hard coded to locale setting in phone, should be a setting
		// eventually
		boolean useImperial = false;
		String currentLocale = this.getResources().getConfiguration().locale.getCountry();
		if ((currentLocale.compareTo("GB") == 0) || (currentLocale.compareTo("US") == 0)) {
			useImperial = true;
		}
		GeoPoint from = GeoPoint.fromDoubleString(getIntent().getStringExtra("fromLocation"), ',');
		decimalFormat = new DecimalFormat("###,###.#");

		for (int i = 0; i < locationNames.length; i++) {
			// add unnamed text for places that need it
			if (locationNames[i].length() == 0)
				locationNames[i] = (String) ChooseLocationActivity.this.getResources().getText(R.string.unnamed_place);
			// add location type
			locationNames[i] = locationNames[i] + " (" + locationInfo[i] + ")";
			// add distance away
			locationNames[i] = locationNames[i] + " - "
					+ formatDistance(from.distanceTo(new GeoPoint(locationLats[i], locationLongs[i])), useImperial)
					+ " " + ChooseLocationActivity.this.getResources().getText(R.string.away);
		}
		setTitle("Choose location...");
		// Use an existing ListAdapter that will map an array
		// of strings to TextViews
		setListAdapter(new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationNames));
		getListView().setTextFilterEnabled(true);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {
				Intent data = getIntent();
				data.putExtra("location", locationLats[(int) position] + "," + locationLongs[(int) position]);
				setResult(RESULT_OK, data);
				finish();

			}

		});
	}

	public String formatDistance(int metres, boolean useImperial) {
		int rounded = 0;
		if (useImperial == false) {
			if (metres < 1000) {
				rounded = roundToNearest(metres, 50);
				return Integer.toString(rounded) + " m";
				// less than 10km
			} else if (metres < 10000) {
				rounded = roundToNearest(metres, 100);
				// round to 1 decimal point
				return decimalFormat.format(new Double(metres) / 1000) + " km";
			} else {
				// show only whole kms
				rounded = roundToNearest(metres, 1000);
				return decimalFormat.format(metres / 1000) + " km";
			}
		} else {
			int yards = (int) (metres * 1.0936133);
			if (yards < 1760) {
				rounded = roundToNearest(yards, 50);
				return Integer.toString(rounded) + " yd";
				// less than 10 miles
			} else if (yards < 17600) {
				rounded = roundToNearest(yards, 176);
				// round to 1 decimal point
				return decimalFormat.format(new Double(yards) / 1760) + " mi";
			} else {
				// show only whole miles
				rounded = roundToNearest(yards, 1760);
				return decimalFormat.format(yards / 1760) + " mi";
			}
		}

	}

	// round number to the nearest precision
	private int roundToNearest(int number, int precision) {
		return (number / precision) * precision;
	}
}
