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
package org.opensatnav.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class SDCardClearPreference extends DialogPreference {

	public SDCardClearPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			// setting a dummy preference here triggers the preference Listener
			// in the main process
			// better ideas will be cheerfully accepted :)
			getSharedPreferences().getString("clear_cache", "dummy");
			SharedPreferences.Editor editor = getSharedPreferences().edit();
			editor.putString("clear_cache", "dummy2");
			editor.commit();
		}
		super.onClick(dialog, which);
	}

}
