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

import org.andnav.osm.util.GeoPoint;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseLocationActivity extends ListActivity {
	DecimalFormat decimalFormat;

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// currently hard coded to locale setting in phone, should be a setting
		// eventually
		boolean showImperial = false;
		String currentLocale = this.getResources().getConfiguration().locale.getCountry();
		if ((currentLocale.compareTo("GB") == 0) || (currentLocale.compareTo("US") == 0)) {
			showImperial = true;
		}
		GeoPoint from = GeoPoint.fromDoubleString(getIntent().getStringExtra("fromLocation"), ',');
		decimalFormat = new DecimalFormat("###,###.#");
		setTitle(this.getResources().getText(R.string.choose_location));
		final LocationAdapter la = new LocationAdapter(from, showImperial);
		setListAdapter(la);
		getListView().setTextFilterEnabled(true);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {
				Intent data = getIntent();
				data.putExtra("location", la.getLocation((int) position).toString());
				setResult(RESULT_OK, data);
				finish();

			}

		});
	}

	protected class LocationAdapter extends BaseAdapter {

		GeoPoint from;
		boolean showImperial;
		Bundle b = getIntent().getBundleExtra("locations");
		String[] locationInfo = b.getStringArray("info");
		String[] locationNames = b.getStringArray("names");
		final int[] locationLats = b.getIntArray("latitudes");
		final int[] locationLongs = b.getIntArray("longitudes");

		public LocationAdapter(GeoPoint from, boolean showImperial) {
			this.from = from;
			this.showImperial = showImperial;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return locationNames.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout mainView = new LinearLayout(ChooseLocationActivity.this);
			mainView.setOrientation(LinearLayout.VERTICAL);
			
			TextView placeView = new TextView(ChooseLocationActivity.this);
			TextView infoView = new TextView(ChooseLocationActivity.this);
			TextView distanceView = new TextView(ChooseLocationActivity.this);
			//add name
			String place = locationNames[position];
			// add unnamed text for places that need it
			if (place.length() == 0)
				place = (String) ChooseLocationActivity.this.getResources().getText(R.string.unnamed_place);
			// add location type
			String info = locationInfo[position];
			info = info.substring(0,1).toUpperCase()+info.substring(1);
			// add distance away
			String distance = formatDistance(from.distanceTo(new GeoPoint(locationLats[position], locationLongs[position])),
					showImperial) + " " + ChooseLocationActivity.this.getResources().getText(R.string.away);
			
			placeView.setText(place);
			placeView.setTextSize(20);
			placeView.setTextColor(Color.WHITE);
			infoView.setText(info);
			distanceView.setText(distance);
			
			mainView.addView(placeView, 0);
			mainView.addView(infoView, 1);
			mainView.addView(distanceView, 2);
			
			return mainView;
		}

		public GeoPoint getLocation(int position) {
			return new GeoPoint(locationLats[position], locationLongs[position]);

		}

	}

	public String formatDistance(int metres, boolean showImperial) {
		int rounded = 0;
		if (showImperial == false) {
			if (metres < 1000) {
				rounded = roundToNearest(metres, 50);
				return Integer.toString(rounded) + this.getResources().getString(R.string.metres_abbreviation);
				// less than 10km
			} else if (metres < 10000) {
				rounded = roundToNearest(metres, 100);
				// round to 1 decimal point
				return decimalFormat.format(new Double(metres) / 1000)
						+ this.getResources().getString(R.string.kilometres_abbreviation);
			} else {
				// show only whole kms
				rounded = roundToNearest(metres, 1000);
				return decimalFormat.format(metres / 1000)
						+ this.getResources().getString(R.string.kilometres_abbreviation);
			}
		} else {
			int yards = (int) (metres * 1.0936133);
			if (yards < 1760) {
				rounded = roundToNearest(yards, 50);
				return Integer.toString(rounded) + this.getResources().getString(R.string.yards_abbreviation);
				// less than 10 miles
			} else if (yards < 17600) {
				rounded = roundToNearest(yards, 176);
				// round to 1 decimal point
				return decimalFormat.format(new Double(yards) / 1760)
						+ this.getResources().getString(R.string.miles_abbreviation);
			} else {
				// show only whole miles
				rounded = roundToNearest(yards, 1760);
				return decimalFormat.format(yards / 1760) + this.getResources().getString(R.string.miles_abbreviation);
			}
		}

	}

	// round number to the nearest precision
	private int roundToNearest(int number, int precision) {
		return (number / precision) * precision;
	}
}
