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
// Created by plusminus on 22:01:11 - 29.09.2008
package org.andnav.osm.views.overlay;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OpenStreetMapViewSimpleLocationOverlay extends OpenStreetMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	protected final Paint mPaint = new Paint();
	
	protected final Bitmap ARROW_ICON;
	/** Coordinates the middle of the arrow is located. */
	protected final android.graphics.Point ARROW_HOTSPOT = new android.graphics.Point(20,20);
	
	protected GeoPoint mLocation;
	protected int accuracy = 75;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public OpenStreetMapViewSimpleLocationOverlay(final Context ctx){
		this.ARROW_ICON = BitmapFactory.decodeResource(ctx.getResources(), android.R.drawable.presence_online);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public void setLocation(final GeoPoint mp){
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
		if(this.mLocation != null){
			final OpenStreetMapViewProjection pj = osmv.getProjection();
			final Point screenCoords = new Point();
			pj.toPixels(this.mLocation, screenCoords);
			c.drawBitmap(ARROW_ICON, screenCoords.x-10, screenCoords.y-10, this.mPaint);
			this.mPaint.setAlpha(127);
			this.mPaint.setColor(Color.BLUE);
			this.mPaint.setStrokeWidth(2);
			this.mPaint.setStyle(Style.STROKE);
			this.mPaint.setAntiAlias(true);
			//c.drawCircle(screenCoords.x, screenCoords.y, pj.metersToEquatorPixels(accuracy)*100, this.mPaint);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
