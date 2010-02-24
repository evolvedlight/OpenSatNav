package org.anddev.openstreetmap.contributor.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.anddev.openstreetmap.contributor.util.constants.OSMConstants;
import org.andnav.osm.views.util.StreamUtils;
import org.opensatnav.OpenSatNavConstants;

import android.os.Environment;
import android.util.Log;

public class GPXToFileWriter implements OSMConstants {
    // ===========================================================
    // Constants
    // ===========================================================
	protected static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


public static void writeToFileAsync(final ArrayList<RecordedGeoPoint> recordedGeoPoints){
                new Thread(new Runnable(){
                        @Override
                        public void run() {
                                try {
	                                	
	                                    
                                        
                                        //FileWriter gpxwriter = new FileWriter(gpxfile);
                                        //BufferedWriter out = new BufferedWriter(gpxwriter);
                                        
	                                	String traceFolderPath = OpenSatNavConstants.DATA_ROOT_DEVICE
	                        			.getAbsolutePath()
	                        			+ "/" + OpenSatNavConstants.GPX_PATH;
	                                    
                                        // Ensure folder exists
                                        
                                        new File(traceFolderPath).mkdirs();

                                        // Create file and ensure that needed folders exist.
                                        final String filename = traceFolderPath + "/" + "test" + ".gpx";
                                        File dest = new File(filename);

                                        // Write Data
                                        final OutputStream out = new BufferedOutputStream(new FileOutputStream(dest),StreamUtils.IO_BUFFER_SIZE);
                                        final byte[] data = RecordedRouteGPXFormatter.create(recordedGeoPoints, "evolvedlight").getBytes();

                                        out.write(data);
                                        out.flush();
                                        out.close();
                                } catch (final Exception e) {
                                        Log.e("OSN", "File-Writing-Error", e);
                                }
                        }
                }, "GPXToFileSaver-Thread").start();
        }
}