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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseLocationActivity extends ListActivity{
	protected DecimalFormat oneDecimalPoint; 
	
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getBundleExtra("locations");
        String[] locationTypes = b.getStringArray("types");
        String[] locationNames = b.getStringArray("names");
        final int[] locationLats = b.getIntArray("latitudes");
        final int[] locationLongs = b.getIntArray("longitudes");
        GeoPoint from = GeoPoint.fromDoubleString(getIntent().getStringExtra("fromLocation"), ',');
        oneDecimalPoint = new DecimalFormat("#,###.#");//format to 1 decimal place
        
        for(int i =0;i<locationNames.length;i++) {
        	//add unnamed text for places that need it
        	if (locationNames[i].length() == 0)
        		locationNames[i] = (String) ChooseLocationActivity.this.getResources().getText(R.string.unnamed_place);
        	//add location type
        	locationNames[i] = locationNames[i]+" ("+locationTypes[i]+")";
        	//add distance away
        	locationNames[i] = locationNames[i]+" - "+formatDistance(from.distanceTo(new GeoPoint(locationLats[i],locationLongs[i])),false)+" "+ChooseLocationActivity.this.getResources().getText(R.string.away);
        }
        setTitle("Choose location...");
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(new android.widget.ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, locationNames));
        getListView().setTextFilterEnabled(true);
        getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {
				Intent data = getIntent();
				data.putExtra("location",locationLats[(int) position]+","+locationLongs[(int) position]);
				setResult(RESULT_OK, data);
				finish();
				
			}
        	
        });
    }
    
    public String formatDistance(int metres, boolean wantImperial) {
    	int rounded = 0;
    	if (metres < 1000) {
    		rounded = roundToNearest(metres, 50);
    		return Integer.toString(rounded)+"m";
    	}
    	else if (metres < 10000) {
    		rounded = roundToNearest(metres, 100);
    		//round to 1 decimal point
    		return oneDecimalPoint.format(new Double(metres)/1000)+"km";
    	}
    	else {
    		//show only whole kms
    		rounded = roundToNearest(metres, 1000);
    		return Integer.toString(rounded/1000)+"km";
    	}
    	
    }
    //round number to the nearest precision
    private int roundToNearest(int number, int precision) {
		return (number/precision)*precision;
    }
}
