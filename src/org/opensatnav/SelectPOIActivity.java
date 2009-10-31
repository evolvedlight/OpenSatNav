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
import java.util.Arrays;
import java.util.Comparator;

import org.andnav.osm.util.GeoPoint;
import org.opensatnav.services.GeoCoder;
import org.opensatnav.services.OSMGeoCoder;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
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
		setTitle(this.getResources().getText(R.string.find_nearest));
		final POIAdapter pa = new POIAdapter();
		setListAdapter(pa);
		getListView().setTextFilterEnabled(true);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {
				getLocations(pa.getItem((int) position) + " near " + from.toDoubleString());

			}

		});
	}

	/**
	 * The an extension of the listadapter that needs to translate what was
	 * clicked on back to English so the English only server can understand it.
	 * 
	 * @author kieran
	 * 
	 */
	protected class POIAdapter extends BaseAdapter {

		public String[] poisUnsorted;
		String[] [] pois;
		int poiIds;

		public POIAdapter() {
			//[0] is the string that goes into the query string, [1] is the string according to current locale
			pois = new String[] [] {
					{"atm",SelectPOIActivity.this.getResources().getString(R.string.atm)},
					{"cafe",SelectPOIActivity.this.getResources().getString(R.string.cafe)},
					{"cinema",SelectPOIActivity.this.getResources().getString(R.string.cinema)},
					{"fuel",SelectPOIActivity.this.getResources().getString(R.string.fuel)},
					{"hospital",SelectPOIActivity.this.getResources().getString(R.string.hospital)},
					{"hotel",SelectPOIActivity.this.getResources().getString(R.string.hotel)},
					{"parking",SelectPOIActivity.this.getResources().getString(R.string.parking)},	
					{"police",SelectPOIActivity.this.getResources().getString(R.string.police)},
					{"pub",SelectPOIActivity.this.getResources().getString(R.string.pub)},
					{"restaurant",SelectPOIActivity.this.getResources().getString(R.string.restaurant)}
			};
			Arrays.sort(pois, new PoiComparator());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pois.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return (pois[position][0]);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView poiView = new TextView(SelectPOIActivity.this);
			poiView.setMinLines(2);
			poiView.setGravity(Gravity.CENTER_VERTICAL);
			poiView.setPadding(5, 5, 5, 5);
			poiView.setTextAppearance(SelectPOIActivity.this, android.R.style.TextAppearance_Large);
			poiView.setText(pois[position][1]);
			return poiView;
		}

	}

	public void getLocations(final String toText) {
		if (toText.length() != 0) {
			final ProgressDialog progress = ProgressDialog.show(SelectPOIActivity.this, this.getResources().getText(
					R.string.please_wait), this.getResources().getText(R.string.searching), true, true);
			final Handler handler = new Handler() {
				// threading stuff - this is run after the
				// thread has completed (code below)
				public void handleMessage(Message msg) {
					if (progress.isShowing())
						progress.dismiss();
					if ((locations != null) && (locations.getStringArray("names").length != 0)) {
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
					Bundle bundle = new Bundle();
				bundle.putString("to", data.getStringExtra("location"));
					data.putExtras(bundle);
					setResult(RESULT_OK, data);
					finish();

	}
			}

	}
	
	private class PoiComparator implements Comparator<String []> {
		/**
		 * special comparator that is used for sorting on the second element in a 2D array 
		 * (in this case we are sorting on the translated string)
		 */
		@Override
		public int compare(String[] object1, String[] object2) {
			// TODO Auto-generated method stub
			return object1[1].compareTo(object2[1]);
		}
		
	}
}
