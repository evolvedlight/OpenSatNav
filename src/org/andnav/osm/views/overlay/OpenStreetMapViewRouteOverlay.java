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

package org.andnav.osm.views.overlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

/**
 * 
 * @author Kieran Fleming
 * 
 */
public class OpenStreetMapViewRouteOverlay extends OpenStreetMapViewOverlay implements Serializable {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	/**
	 * 
	 */
	private static final long serialVersionUID = 4352398623624395209L;
	protected final Paint mPaint = new Paint();
	protected List<GeoPoint> route;
	protected GeoPoint mLocation;
	protected ArrayList<GeoPoint> pointRoute;
	protected ArrayList<Point> pixelRoute;
	protected Point firstPixelPoint, oldFirstPoint, lastPixelPoint,
			oldLastPixelPoint;
	protected Path routePath;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetMapViewRouteOverlay(final Context ctx, List<GeoPoint> route) {
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(6);
		mPaint.setColor(Color.BLUE);
		mPaint.setAlpha(100);
		mPaint.setAntiAlias(true);
		this.route = route;
		pointRoute = new ArrayList<GeoPoint>();
		pixelRoute = new ArrayList<Point>();
		routePath = new Path();
		for (int i = 0; i < this.route.size(); i++) {
			GeoPoint nextPoint = this.route.get(i);
			nextPoint.setCoordsE6(nextPoint.getLatitudeE6(), nextPoint
					.getLongitudeE6());
			pointRoute.add(nextPoint);
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setLocation(final GeoPoint mp) {
		this.mLocation = mp;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onDrawFinished(Canvas c, OpenStreetMapView osmv) {
		return;
	}

	@Override
	public void onDraw(final Canvas c, final OpenStreetMapView osmv) {
		final OpenStreetMapViewProjection pj = osmv.getProjection();
		if (this.pointRoute.size() != 0) {
			if (firstPixelPoint != null)
				oldFirstPoint = firstPixelPoint;
			if (lastPixelPoint != null)
				oldLastPixelPoint = lastPixelPoint;
			//this is optimisation so the computation from lat/long to pixel coords
			//is only done if we need to, as this is expensive
			//right now this optimises map panning, but can optimise for zoom as well
			
			// get the points
			firstPixelPoint = pj.toPixels((GeoPoint) pointRoute.get(0), null);
			lastPixelPoint = pj.toPixels(this.pointRoute.get(this.pointRoute.size() - 1), null);
			
			//they panned, don't recompute from lat/long, just move the pixel route the appropriate amount
			if ((oldFirstPoint != null)
					&& (oldFirstPoint.x - firstPixelPoint.x) == (oldLastPixelPoint.x - lastPixelPoint.x)
					&& (oldFirstPoint.y - firstPixelPoint.y) == (oldLastPixelPoint.y - lastPixelPoint.y)) {
				for (int i = 0; i < this.pixelRoute.size(); i++) {
					this.pixelRoute.set(i, new Point(this.pixelRoute.get(i).x
							- (oldFirstPoint.x - firstPixelPoint.x),
							this.pixelRoute.get(i).y
									- (oldFirstPoint.y - firstPixelPoint.y)));
				}
			}
			// either there is no old point, ergo this is the first time we are
			// here,
			// or they have zoomed
			//if this is false it means the map is still and we don't need to do anything
			else if ((oldLastPixelPoint == null)
					|| (oldLastPixelPoint.x != lastPixelPoint.x)
					|| (oldLastPixelPoint.y != lastPixelPoint.y)) {
				if (pixelRoute != null)
					pixelRoute.clear();
				for (int i = 0; i < pointRoute.size(); i++) {
					// Point nextScreenCoords = new Point();
					pixelRoute.add(pj.toPixels((GeoPoint) pointRoute.get(i),
							null));
				}
			} 
			
			//draw the pixel route
			routePath.rewind();
			for (int i = 0; i < this.pixelRoute.size(); i++) {
				Point current = pixelRoute.get(i);
				if (i == 0)
					routePath.moveTo(current.x, current.y);
				else
					routePath.lineTo(current.x, current.y);
			}
			c.drawPath(routePath, this.mPaint);
		}
		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
