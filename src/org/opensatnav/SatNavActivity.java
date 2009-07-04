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
import org.andnav.osm.util.TypeConverter;
import org.andnav.osm.util.constants.OpenStreetMapConstants;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.overlay.OpenStreetMapViewDirectedLocationOverlay;
import org.andnav.osm.views.overlay.OpenStreetMapViewRouteOverlay;
import org.andnav.osm.views.util.OpenStreetMapRendererInfo;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 
 * @author Kieran Fleming
 * 
 */

public class SatNavActivity extends OpenStreetMapActivity implements OpenStreetMapConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int MENU_ZOOMIN_ID = Menu.FIRST;
	private static final int MENU_ZOOMOUT_ID = MENU_ZOOMIN_ID + 1;
	private static final int MENU_RENDERER_ID = MENU_ZOOMOUT_ID + 1;
	private static final int MENU_TOGGLE_AUTOFOLLOW = MENU_RENDERER_ID + 1;
	private static final int MENU_FIND_POIS = MENU_TOGGLE_AUTOFOLLOW + 1;
	private static final int MENU_GET_DIRECTIONS = MENU_FIND_POIS + 1;
	private static final int DIRECTIONS_OPTIONS = MENU_GET_DIRECTIONS + 1;
	private static final int SELECT_POI = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	private OpenStreetMapView mOsmv;
	private OpenStreetMapViewDirectedLocationOverlay mMyLocationOverlay;
	protected OpenStreetMapViewRouteOverlay routeOverlay;
	protected boolean autoFollowing = true;
	protected Location currentLocation;

	protected ArrayList<String> route = new ArrayList<String>();

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState, false); // Pass true here to actually
		// contribute to OSM!

		final RelativeLayout rl = new RelativeLayout(this);

		this.mOsmv = new OpenStreetMapView(this, OpenStreetMapRendererInfo.MAPNIK);
		rl.addView(this.mOsmv, new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		this.mOsmv.setZoomLevel(19);

		if (temporaryLocation != null)
			this.mOsmv.setMapCenter(TypeConverter.locationToGeoPoint(temporaryLocation));

		/* SingleLocation-Overlay */
		{
			/*
			 * Create a static Overlay showing a single location. (Gets updated
			 * in onLocationChanged(Location loc)!
			 */
			this.mMyLocationOverlay = new OpenStreetMapViewDirectedLocationOverlay(this);
			this.mOsmv.getOverlays().add(mMyLocationOverlay);

		}

		/* ZoomControls */
		{
			final ZoomControls zoomControls = new ZoomControls(this);
			// by default we are zoomed in to the max
			zoomControls.setIsZoomInEnabled(false);
			zoomControls.setOnZoomOutClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SatNavActivity.this.mOsmv.zoomOut();
					if (!SatNavActivity.this.mOsmv.canZoomOut())
						zoomControls.setIsZoomOutEnabled(false);
					zoomControls.setIsZoomInEnabled(true);
				}
			});
			zoomControls.setOnZoomInClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SatNavActivity.this.mOsmv.zoomIn();
					if (!SatNavActivity.this.mOsmv.canZoomIn())
						zoomControls.setIsZoomInEnabled(false);
					zoomControls.setIsZoomOutEnabled(true);
				}
			});

			final RelativeLayout.LayoutParams zoomParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			zoomParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			zoomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			rl.addView(zoomControls, zoomParams);

		}

		this.setContentView(rl);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onLocationChanged(Location newLocation) {
		if ((newLocation != null) && ((currentLocation == null) || (currentLocation.distanceTo(newLocation) < 50))) {
			this.mMyLocationOverlay.setLocation(TypeConverter.locationToGeoPoint(newLocation));
			this.mMyLocationOverlay.setBearing(newLocation.getBearing());
			this.mMyLocationOverlay.setSpeed(newLocation.getSpeed());
			if (this.autoFollowing)
				this.mOsmv.setMapCenter(TypeConverter.locationToGeoPoint(newLocation));
			currentLocation = newLocation;
		}
	}

	@Override
	public void onLocationLost() {
		// We'll do nothing here.
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu pMenu) {
		MenuItem directionsMenuItem = pMenu.add(0, MENU_GET_DIRECTIONS, Menu.NONE, R.string.get_directions);
		directionsMenuItem.setIcon(android.R.drawable.ic_menu_directions);
		MenuItem poisMenuItem = pMenu.add(0, MENU_FIND_POIS, Menu.NONE, "Find nearest...");
		poisMenuItem.setIcon(android.R.drawable.ic_menu_search);
		MenuItem toggleAutoFollowMenuItem = pMenu.add(0, MENU_TOGGLE_AUTOFOLLOW, Menu.NONE, R.string.toggle_autofollow);
		toggleAutoFollowMenuItem.setIcon(android.R.drawable.ic_menu_mylocation);
		// uncomment if you want to enable map mode switching
		// SubMenu mapModeMenuItem = pMenu.addSubMenu(0, MENU_RENDERER_ID,
		// Menu.NONE, "Map mode");
		// {
		// for (int i = 0; i < OpenStreetMapRendererInfo.values().length; i++)
		// mapModeMenuItem.add(0, 1000 + i, Menu.NONE,
		// OpenStreetMapRendererInfo.values()[i].NAME);
		// }
		// mapModeMenuItem.setIcon(android.R.drawable.ic_menu_mapmode);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_GET_DIRECTIONS:
			if (currentLocation != null) {
				Intent intent = new Intent(this, org.opensatnav.GetDirectionsActivity.class);
				intent.setData(Uri.parse(currentLocation.getLatitude() + "," + currentLocation.getLongitude()));
				startActivityForResult(intent, DIRECTIONS_OPTIONS);
			} else
				Toast.makeText(this, R.string.start_directions_failed, Toast.LENGTH_LONG).show();
			return true;
		case MENU_FIND_POIS:
			if (currentLocation != null) {
				Intent intent1 = new Intent(this, org.opensatnav.SelectPOIActivity.class);
				intent1.setData(Uri.parse(currentLocation.getLatitude() + "," + currentLocation.getLongitude()));
				startActivityForResult(intent1, SELECT_POI);
			} else
				Toast.makeText(this, R.string.start_directions_failed, Toast.LENGTH_LONG).show();
			return true;
		case MENU_RENDERER_ID:
			this.mOsmv.invalidate();
			return true;
		case MENU_TOGGLE_AUTOFOLLOW:
			if (this.autoFollowing) {
				this.autoFollowing = false;
				Toast.makeText(this, R.string.autofollow_stopped, Toast.LENGTH_SHORT).show();
			} else {
				this.autoFollowing = true;
				Toast.makeText(this, R.string.autofollow_started, Toast.LENGTH_SHORT).show();
			}
			return true;

		default:
			this.mOsmv.setRenderer(OpenStreetMapRendererInfo.values()[item.getItemId() - 1000]);
		}
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == DIRECTIONS_OPTIONS) || (requestCode == SELECT_POI)) {
			if (resultCode == RESULT_OK) {
				route = data.getStringArrayListExtra("route");
				if (route != null) {
					// this.mOsmv.invalidate();
					ArrayList<GeoPoint> niceRoute = new ArrayList<GeoPoint>();
					for (int i = 0; i < route.size(); i++) {
						int lat = Integer.parseInt(this.route.get(i).substring(0, this.route.get(i).indexOf(',')));
						int lon = Integer.parseInt(this.route.get(i).substring(this.route.get(i).indexOf(',') + 1));
						GeoPoint nextPoint = new GeoPoint(lat, lon);
						niceRoute.add(nextPoint);
					}

					if (this.mOsmv.getOverlays().contains(this.routeOverlay)) {
						this.mOsmv.getOverlays().remove(this.routeOverlay);
					}
					this.routeOverlay = new OpenStreetMapViewRouteOverlay(this, niceRoute);
					this.mOsmv.getOverlays().add(routeOverlay);
				} else
					Toast.makeText(this, R.string.unspecified_directions_failed, Toast.LENGTH_SHORT).show();
			}
		}

	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		// savedInstanceState.putDouble("currentLocationLat",
		// currentLocation.getLatitude());
		// savedInstanceState.putDouble("currentLocationLon",
		// currentLocation.getLongitude());
		savedInstanceState.putStringArrayList("route", route);
		super.onSaveInstanceState(savedInstanceState);
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		route = savedInstanceState.getStringArrayList("route");
		ArrayList<GeoPoint> niceRoute = new ArrayList<GeoPoint>();
		for (int i = 0; i < route.size(); i++) {
			int lat = Integer.parseInt(this.route.get(i).substring(0, this.route.get(i).indexOf(',')));
			int lon = Integer.parseInt(this.route.get(i).substring(this.route.get(i).indexOf(',') + 1));
			GeoPoint nextPoint = new GeoPoint(lat, lon);
			niceRoute.add(nextPoint);
		}
		this.routeOverlay = new OpenStreetMapViewRouteOverlay(this, niceRoute);
		this.mOsmv.getOverlays().add(this.routeOverlay);
		// currentLocation.setLatitude(savedInstanceState.getDouble("currentLocationLat"));
		// currentLocation.setLongitude(savedInstanceState.getDouble("currentLocationLon"));
		// onLocationChanged(currentLocation);
	}
}
