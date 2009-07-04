package org.opensatnav.services;

import android.content.Context;
import android.os.Bundle;

public interface GeoCoder {

	public abstract Bundle getFromLocationName(String locationName,
			int maxResults, Context context);

}