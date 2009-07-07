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

import org.opensatnav.R;
import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 * 
 */
public class OpenStreetMapViewDirectedLocationOverlay extends OpenStreetMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Paint mPaint = new Paint();

	protected final Bitmap LOCATION_MOVING;
	protected final Bitmap LOCATION_STOPPED;

	protected GeoPoint mLocation;
	protected float mBearing;
	protected float speed = 0.0f;

	private final Matrix directionRotater = new Matrix();

	private final float DIRECTION_ARROW_CENTER_X;
	private final float DIRECTION_ARROW_CENTER_Y;
	private final int DIRECTION_ARROW_WIDTH;
	private final int DIRECTION_ARROW_HEIGHT;

	protected float accuracy = 0;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetMapViewDirectedLocationOverlay(final Context ctx) {
		this.LOCATION_MOVING = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.direction_arrow);
		this.LOCATION_STOPPED = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.location_marker);
		this.DIRECTION_ARROW_CENTER_X = this.LOCATION_MOVING.getWidth() / 2 - 0.5f;
		this.DIRECTION_ARROW_CENTER_Y = this.LOCATION_MOVING.getHeight() / 2 - 0.5f;
		this.DIRECTION_ARROW_HEIGHT = this.LOCATION_MOVING.getHeight();
		this.DIRECTION_ARROW_WIDTH = this.LOCATION_MOVING.getWidth();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setLocation(final GeoPoint mp) {
		this.mLocation = mp;
	}

	public void setBearing(final float aHeading) {
		this.mBearing = aHeading;
	}

	public void setSpeed(final float speed) {
		this.speed = speed;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
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
		if (this.mLocation != null) {
			final OpenStreetMapViewProjection pj = osmv.getProjection();
			final Point screenCoords = new Point();
			pj.toPixels(this.mLocation, screenCoords);
			// draw innacuracy circle
			// accuracy means the number of metres that we are accurate to
			float diameter = pj.metersToEquatorPixels(accuracy) * 100;
			//if it won't be hidden under the icon anyway
			if (diameter > DIRECTION_ARROW_WIDTH) {
				this.mPaint.setColor(Color.BLUE);
				this.mPaint.setAlpha(80);
				this.mPaint.setStrokeWidth(2);
				this.mPaint.setStyle(Style.STROKE);
				this.mPaint.setAntiAlias(true);
				c.drawCircle(screenCoords.x, screenCoords.y, diameter, this.mPaint);
				this.mPaint.setAlpha(20);
				this.mPaint.setStyle(Style.FILL);
				c.drawCircle(screenCoords.x, screenCoords.y, diameter, this.mPaint);
				//reset alpha
				this.mPaint.setAlpha(255);
			}
			// 0.5 m/s is a slow person walking
			if (speed > 0.5f) {
				/*
				 * Rotate the direction-Arrow according to the bearing we are
				 * moving. And draw it to the canvas.
				 */
				this.directionRotater.setRotate(this.mBearing, DIRECTION_ARROW_CENTER_X, DIRECTION_ARROW_CENTER_Y);
				Bitmap rotatedDirection = Bitmap.createBitmap(LOCATION_MOVING, 0, 0, DIRECTION_ARROW_WIDTH,
						DIRECTION_ARROW_HEIGHT, this.directionRotater, false);
				c.drawBitmap(rotatedDirection, screenCoords.x - rotatedDirection.getWidth() / 2, screenCoords.y
						- rotatedDirection.getHeight() / 2, this.mPaint);
			} else {
				// we're not moving, or we're moving too slowly for the bearing
				// to be accurate
				Bitmap locationStopped = Bitmap.createBitmap(LOCATION_STOPPED, 0, 0, DIRECTION_ARROW_WIDTH,
						DIRECTION_ARROW_HEIGHT);
				c.drawBitmap(locationStopped, screenCoords.x - locationStopped.getWidth() / 2, screenCoords.y
						- locationStopped.getHeight() / 2, this.mPaint);
			}
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
