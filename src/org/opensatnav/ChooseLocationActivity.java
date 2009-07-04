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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseLocationActivity extends ListActivity{
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getBundleExtra("locations");
        String[] locationNames = b.getStringArray("names");
        final int[] locationLats = b.getIntArray("latitudes");
        final int[] locationLongs = b.getIntArray("longitudes");
//        
//        for(int i =0;i<locationNames.length;i++) {
//        	if (locationNames[i] == null)
//        		locationNames[i] = "Unnamed";
//        }
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
}
