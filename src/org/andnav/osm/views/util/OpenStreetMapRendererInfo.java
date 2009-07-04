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
// Created by plusminus on 18:23:16 - 25.09.2008
package org.andnav.osm.views.util;

import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public enum OpenStreetMapRendererInfo {
	OSMARENDER("http://tah.openstreetmap.org/Tiles/tile/", "OsmaRender", ".png", 17, 256),
	MAPNIK("http://tile.openstreetmap.org/", "Mapnik", ".png", 18, 256),
	CYCLEMAP("http://b.andy.sandbox.cloudmade.com/tiles/cycle/", "Cycle Map", ".png", 17, 256),
//	OPENARIELMAP("http://tile.openaerialmap.org/tiles/1.0.0/openaerialmap-900913/", "OpenAerialMap (Satellite)", ".jpg", 13, 256),
//	CLOUDMADESMALLTILES("http://tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/2/64/", "Cloudmade (Small tiles)", ".jpg", 13, 64),
//	CLOUDMADESTANDARDTILES("http://tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/2/256/", "Cloudmade (Standard tiles)", ".jpg", 18, 256)
	;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	public final String BASEURL, NAME, IMAGE_FILENAMEENDING;
	public final int ZOOM_MAXLEVEL, MAPTILE_SIZEPX;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	private OpenStreetMapRendererInfo(final String aBaseUrl, final String aName, final String aImageFilenameEnding, final int aZoomMax, final int aTileSizePX){
		this.BASEURL = aBaseUrl;
		this.NAME = aName;
		this.ZOOM_MAXLEVEL = aZoomMax;
		this.IMAGE_FILENAMEENDING = aImageFilenameEnding;
		this.MAPTILE_SIZEPX = aTileSizePX;
	}
	
	public static OpenStreetMapRendererInfo getDefault() {
		return MAPNIK;
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	public String getTileURLString(final int[] tileID, final int zoomLevel){
		return new StringBuilder().append(this.BASEURL)
		.append(zoomLevel)
		.append("/")
		.append(tileID[OpenStreetMapViewConstants.MAPTILE_LONGITUDE_INDEX])
		.append("/")
		.append(tileID[OpenStreetMapViewConstants.MAPTILE_LATITUDE_INDEX])
		.append(this.IMAGE_FILENAMEENDING)
		.toString();
	}
}
