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

import java.io.File;

import android.os.Environment;


/**
 * @author Guillaume
 *
 */
public interface OpenSatNavConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================
	// TODO ZeroG remove cyclic dependency from org.andnav2 package on this Interface
	public static final File DATA_ROOT_DEVICE = Environment.getExternalStorageDirectory();
	public static final String DATA_PATH= "/org.opensatnav";
	
	public static final String TILE_CACHE_PATH = DATA_PATH + "/tiles";
	public static final String ERROR_PATH = DATA_PATH + "/errors";
	public static final String GPX_PATH = DATA_PATH + "/traces";
	
	public static final int TRACE_RECORDING_NOTIFICATION_ID = -1;
	
	// tag used to log any useful information Log.info(LOG_TAG, String msg)
	public static final String LOG_TAG = "OpenSatNav";
	public static final boolean DEBUGMODE = true;
	
	// metadata key used to retrieve SVN revision in Manifest.xml
	public static final String REVISION_METADATA = "org.opensatnav.metadata.REVISION";
	
	//When recording traces in the contribute activity, drop points if the GPS fix is worse than this value.
	public static final int GPS_TRACE_MIN_ACCURACY = 200;
	// ===========================================================
	// Methods
	// ===========================================================


}
