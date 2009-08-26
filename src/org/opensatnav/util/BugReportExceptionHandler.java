package org.opensatnav.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.opensatnav.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;

/**
 * Convenience class used to send Bug Report when Exceptions (caught or
 * uncaught) happen. May be used in a try / catch block to send Exception by
 * email or/and may be registered as a Thread.UncaughtExceptionHandler
 * 
 * @author zerog
 * 
 */
public class BugReportExceptionHandler implements
		Thread.UncaughtExceptionHandler {

	private static final String MIME_TYPE = "message/rfc822";

	private final UncaughtExceptionHandler defaultExceptionHandler;

	private Context parent;

	public BugReportExceptionHandler(Context parent) {
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.parent = parent;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		// doesn't work when crashing...
		sendBugReport(ex);
		// default action will close faulted Activity
		defaultExceptionHandler.uncaughtException(thread, ex);
	}

	public void sendBugReport(final Throwable e) {

		AlertDialog dialog = new AlertDialog.Builder(parent).setPositiveButton(
				android.R.string.yes, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<Throwable> al = new ArrayList<Throwable>();
						al.add(e);
						Intent intent = send(al);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						parent.startActivity(intent);
					}
				}).setNegativeButton(android.R.string.no, null).create();
		dialog.setMessage(parent.getString(R.string.bug_report_ask_user));
		dialog.setTitle(R.string.bug_report_ask_user_title);
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.show();
	}

	/**
	 * Sends an email to the project with the given exceptions
	 * 
	 * @param exceptions
	 * @param resources
	 *            {@link Resources} for getting some Strings.
	 * @return
	 * @return Intent ready to start.
	 */
	private Intent send(final List<Throwable> exceptions) {
		String[] addressee = { parent
				.getString(R.string.bug_report_mail_address) };

		String subject = new StringBuilder(getVersionName()).append(" Error Report (Android ").append(Build.VERSION.RELEASE).append(")").toString();
		String message = parent.getString(R.string.error_email_message);

		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);

		for (Throwable e : exceptions) {
			e.printStackTrace(printWriter);
		}
		message += result.toString();

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, addressee);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		intent.setType(MIME_TYPE);
		return Intent.createChooser(intent, parent
				.getString(R.string.error_email_chooser_title));
	}

	private String getVersionName() {
		return parent.getString(R.string.app_name) + " " + getVersionNumber();
	}

	private String getVersionNumber() {
		String versionname = "Unknown version";
		try {
			PackageInfo pi = parent.getPackageManager().getPackageInfo(
					parent.getPackageName(), 0);
			versionname = pi.versionName;
		} catch (PackageManager.NameNotFoundException e) {
		}
		return versionname;
	}
}
