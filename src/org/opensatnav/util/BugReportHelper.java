package org.opensatnav.util;

import org.opensatnav.OpenSatNavConstants;
import org.opensatnav.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class BugReportHelper {
	private Context context;

	public BugReportHelper(Context context) {
		this.context = context;
	}

	public String getRevision() {
		// Try meta data of package
		Bundle md = null;
		try {
			md = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).metaData;
		} catch (NameNotFoundException e) {
			Log.e(OpenSatNavConstants.LOG_TAG, "Package name not found", e);
		}

		if (md != null) {
			// if numeric revision
			int rev = md.getInt(OpenSatNavConstants.REVISION_METADATA);
			if (rev != 0)
				return "" + rev;
			else if (!TextUtils.isEmpty(md
					.getString(OpenSatNavConstants.REVISION_METADATA))) {
				// if string rev
				return md.getString(OpenSatNavConstants.REVISION_METADATA);
			}
		}
		return "Unknown";
	}

	public String getVersionName() {
		return context.getString(R.string.app_name) + " " + getVersionNumber();
	}

	public String getVersionNumber() {
		String version = "Unknown version";
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			version = pi.versionName;

		} catch (PackageManager.NameNotFoundException e) {
		}
		return version;
	}

}
