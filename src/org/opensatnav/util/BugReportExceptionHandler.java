package org.opensatnav.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opensatnav.OpenSatNavConstants;
import org.opensatnav.R;

import android.app.Activity;
import android.app.AlertDialog;
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

	private Activity parent;

	private static final String ERROR_PATH=OpenSatNavConstants.DATA_ROOT_DEVICE.getAbsolutePath() + "/" + OpenSatNavConstants.ERROR_PATH;
	
	public BugReportExceptionHandler(final Activity parent) {
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.parent = parent;

		String[] stacktraceFiles = searchForStackTraces();
		if (stacktraceFiles != null)
			for (String stacktraceFile : stacktraceFiles) {
				// Read contents of stacktrace
				StringBuilder contents = new StringBuilder();

				String line;
				BufferedReader input;
				try {
					input = new BufferedReader(new FileReader(ERROR_PATH
							+ "/" + stacktraceFile));

					while ((line = input.readLine()) != null) {
						contents.append(line);
						contents.append(System.getProperty("line.separator"));
					}
					input.close();

					new File(ERROR_PATH + "/" +stacktraceFile).delete();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sendBugReportAtRestart(contents.toString());
			}

	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {

		saveBugReport(ex);

		// default action will close faulted Activity
		defaultExceptionHandler.uncaughtException(thread, ex);
	}

	/**
	 * Save the error as a file that will be sent to OSN at next start of the
	 * application
	 * 
	 * @param ex
	 */
	private void saveBugReport(Throwable ex) {
		ArrayList<Throwable> al = new ArrayList<Throwable>();
		al.add(ex);

		try {
			// Random number to avoid duplicate files
			Random generator = new Random();
			int random = generator.nextInt(99999);

			File dir = new File(ERROR_PATH + "/");
			// Try to create the files folder if it doesn't exist
			if (!dir.exists())
				dir.mkdirs();

			String filename = "OpenSatNav-errors-" + Integer.toString(random);
			BufferedWriter bos = new BufferedWriter(new FileWriter(
					ERROR_PATH + "/" + filename + ".stacktrace"));
			bos.write(buildStackTraceMessage(al));
			bos.flush();
			// Close up everything
			bos.close();
		} catch (Exception ebos) {
			// Nothing much we can do about this - the game is over
			ebos.printStackTrace();
		}

	}

	public void sendBugReport(final Throwable e) {

		AlertDialog dialog = new AlertDialog.Builder(parent).setPositiveButton(
				android.R.string.yes, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<Throwable> al = new ArrayList<Throwable>();
						al.add(e);
						Intent intent = send(buildStackTraceMessage(al));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						parent.startActivity(intent);
					}
				}).setNegativeButton(android.R.string.no, null).create();
		dialog.setMessage(parent.getString(R.string.bug_report_ask_user));
		dialog.setTitle(R.string.bug_report_ask_user_title);
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.show();
	}

	public void sendBugReportAtRestart(final String stacktrace) {

		AlertDialog dialog = new AlertDialog.Builder(parent).setPositiveButton(
				android.R.string.yes, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = send(stacktrace);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						parent.startActivity(intent);
					}
				}).setNegativeButton(android.R.string.no, null).create();
		dialog.setMessage(parent.getString(R.string.bug_report_not_sent_ask_user));
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
	private Intent send(final String stackTrace) {
		String[] addressee = { parent
				.getString(R.string.bug_report_mail_address) };

		String subject = new StringBuilder(getVersionName()).append(
				" Error Report (Android: ").append(Build.VERSION.RELEASE)
				.append(" - model: ").append(Build.MODEL).append(")")
				.toString();
		String message = parent.getString(R.string.error_email_message);

		message += stackTrace;

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, addressee);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, message);
		intent.setType(MIME_TYPE);
		return Intent.createChooser(intent, parent
				.getString(R.string.error_email_chooser_title));
	}

	private String buildStackTraceMessage(List<Throwable> exceptions) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);

		for (Throwable e : exceptions) {
			e.printStackTrace(printWriter);
		}
		return result.toString();
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

	/**
	 * Search for stack trace files.
	 * 
	 * @return
	 */
	private static String[] searchForStackTraces() {
		File dir = new File(ERROR_PATH + "/");
		// Try to create the files folder if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();

		// Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".stacktrace");
			}
		};
		return dir.list(filter);
	}

}
