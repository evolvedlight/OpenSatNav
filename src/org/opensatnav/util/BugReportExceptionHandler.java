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

import android.app.Activity;
import android.util.Log;

/**
 * Convenience class used to send Bug Report when Exceptions (caught or
 * uncaught) happen. May be used in a try / catch block to send Exception by
 * email or/and may be registered as a Thread.UncaughtExceptionHandler
 * 
 * @author zerog
 * 
 */
public class BugReportExceptionHandler {

	private static final String ERROR_PATH = OpenSatNavConstants.DATA_ROOT_DEVICE
			.getAbsolutePath()
			+ "/" + OpenSatNavConstants.ERROR_PATH;

	private static final class ExceptionHandler implements
			Thread.UncaughtExceptionHandler {

		private UncaughtExceptionHandler defaultExceptionHandler;

		/**
		 * Rely on defaultExceptionHandler after having done what we need with the caught exceptions.
		 * @param defaultExceptionHandler
		 */
		public ExceptionHandler(UncaughtExceptionHandler defaultExceptionHandler) {
			this.defaultExceptionHandler = defaultExceptionHandler;
		}

		@Override
		public void uncaughtException(final Thread thread, final Throwable ex) {

			saveBugReport(ex);

			// default action will close faulted Activity
			defaultExceptionHandler.uncaughtException(thread, ex);
		}

	}

	public static void register(final Activity parent) {
		parent.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				String[] stacktraceFiles = searchForStackTraces();
				if (stacktraceFiles != null)
					for (String stacktraceFile : stacktraceFiles) {
						Log.i(OpenSatNavConstants.LOG_TAG, "Error file found : " + stacktraceFile);
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
							Log.e(OpenSatNavConstants.LOG_TAG, e.getMessage(), e);
						}

						new BugReportSender(parent).sendBugReportAtRestart(contents.toString());
					}
			}
		});

		UncaughtExceptionHandler handler = Thread
				.getDefaultUncaughtExceptionHandler();
		// set this ExceptionHandler if not already done
		if (!(handler instanceof BugReportExceptionHandler.ExceptionHandler))
			Thread
					.setDefaultUncaughtExceptionHandler(new BugReportExceptionHandler.ExceptionHandler(
							handler));

	}

	/**
	 * Save the error as a file that will be sent to OSN at next start of the
	 * application
	 * 
	 * @param ex
	 */
	private static void saveBugReport(Throwable ex) {
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
			BufferedWriter bos = new BufferedWriter(new FileWriter(ERROR_PATH
					+ "/" + filename + ".stacktrace"));
			bos.write(buildStackTraceMessage(al));
			bos.flush();
			// Close up everything
			bos.close();
		} catch (Exception ebos) {
			// Nothing much we can do about this - the game is over
			Log.e(OpenSatNavConstants.LOG_TAG, ebos.getMessage(), ebos);			
		}

	}

	protected static String buildStackTraceMessage(List<Throwable> exceptions) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);

		for (Throwable e : exceptions) {
			e.printStackTrace(printWriter);
		}
		return result.toString();
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
