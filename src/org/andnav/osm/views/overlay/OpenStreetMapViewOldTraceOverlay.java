package org.andnav.osm.views.overlay;

import org.anddev.openstreetmap.contributor.util.RouteRecorder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

public class OpenStreetMapViewOldTraceOverlay extends OpenStreetMapViewTraceOverlay {
	public OpenStreetMapViewOldTraceOverlay(final Context ctx, RouteRecorder mRouteRecorder) {
		super(ctx, mRouteRecorder);
		this.mPaint.setColor(Color.MAGENTA);
		this.mPaint.setAlpha(120);
		this.wayPointPaint.setAlpha(80);
	}
}