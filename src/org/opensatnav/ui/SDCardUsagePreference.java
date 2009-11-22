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

import org.opensatnav.OpenSatNavConstants;

import android.content.Context;
import android.os.StatFs;
import android.util.AttributeSet;

import com.hlidskialf.android.preference.SeekBarPreference;

public class SDCardUsagePreference extends SeekBarPreference {

	public SDCardUsagePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (OpenSatNavConstants.DATA_ROOT_DEVICE.canWrite()) {
			StatFs stat = new StatFs(android.os.Environment
					.getExternalStorageDirectory().getAbsolutePath());
			long blockSize = stat.getBlockSize();
			int bytesInMb = 1048576;
			long max = (blockSize * stat.getAvailableBlocks()) / bytesInMb;
			setMax((int) max);
		}
		else
			setEnabled(false);
	}

}
