/*   
 * 	 Copyright (C) 2008-2009 pjv (and others, see About dialog)
 * 
 * 	 This file is part of OI About.
 *
 *   OI About is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   OI About is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with OI About.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   
 *   
 *   The idea, window layout and elements, and some of the comments below are based on GtkAboutDialog. See http://library.gnome.org/devel/gtk/stable/GtkAboutDialog.html and http://www.gtk.org.
 */

package org.openintents.about;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openintents.intents.AboutIntents;
import org.openintents.metadata.AboutMetaData;
import org.opensatnav.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.TabHost;
import android.widget.TextSwitcher;
import android.widget.TextView;

/**
 * Main About dialog activity.
 * 
 * @author pjv
 *
 */
public class About extends TabActivity {
	//TODO packaging
	//TODO BUG rotating screen broken due to TabHost?
	//TODO BUG OI Updater does not find OI About.
	
	private static final String TAG = "About";
	
	/**
	 * The views.
	 */
	protected ImageSwitcher mLogoImage;
	protected ImageSwitcher mEmailImage;
	protected TextSwitcher mProgramNameAndVersionText;
	protected TextSwitcher mCommentsText;
	protected TextSwitcher mCopyrightText;
	protected TextSwitcher mWebsiteText;
	protected TextSwitcher mEmailText;
	protected TextView mAuthorsLabel;
	protected TextView mAuthorsText;
	protected TextView mDocumentersLabel;
	protected TextView mDocumentersText;
	protected TextView mTranslatorsLabel;
	protected TextView mTranslatorsText;
	protected TextView mArtistsLabel;
	protected TextView mArtistsText;
	protected TextView mNoInformationText;
	protected TextView mLicenseText;

	protected TabHost tabHost;

	/**
	 * Menu item id's.
	 */
	public static final int MENU_ITEM_ABOUT = Menu.FIRST;


	/**
	 * Retrieve the package name to be used with this intent.
	 * 
	 * Package name is retrieved from EXTRA_PACKAGE or from
	 * getCallingPackage().
	 * 
	 * If none is supplied, it is set to this application.
	 */
	String getPackageNameFromIntent(Intent intent) {
		String packagename = null;
		
		if (intent.hasExtra(AboutIntents.EXTRA_PACKAGE_NAME)) {
			packagename = intent.getStringExtra(AboutIntents.EXTRA_PACKAGE_NAME);

			// Check whether packagename is valid:
			try {
	            getPackageManager().getApplicationInfo(
	            		packagename, 0);
		    } catch (NameNotFoundException e) {
		        Log.e(TAG, "Package name " + packagename + " is not valid.", e);
		        packagename = null;
		    }
		}
		
		// If no valid name has been found, we try to obtain it from
		// the calling activit.
		if (packagename == null) {
			// Retrieve from calling activity
			packagename = getCallingPackage();
		}
		
	    if (packagename == null) {
	    	// In the worst case, use our own name:
	    	packagename = getPackageName();
	    }
	    
	    return packagename;
	}
	
    /**
	 * Change the logo image using the resource in the string argument.
	 * 
	 * @param logoString
	 *            String of a content uri to an image resource
	 */
	protected void changeLogoImageUri(final String logoString) {
		Uri imageDescriptionUri = Uri.parse(logoString);
		if (imageDescriptionUri != null) {
			mLogoImage.setImageURI(imageDescriptionUri);
		} else { // Not a uri, so invalid.
			throw new IllegalArgumentException("Not a valid image.");
		}
	}

	/**
	 * Change the logo image using the resource name and package.
	 * 
	 * @param resourceFileName
	 *            String of the name of the image resource (as you would append
	 *            it after "R.drawable.").
	 * @param resourcePackageName
	 *            String of the name of the source package of the image resource
	 *            (the package name of the calling app).
	 */
	protected void changeLogoImageResource(final String resourceFileName,
			final String resourcePackageName) {
		try {
			Resources resources = getPackageManager()
					.getResourcesForApplication(resourcePackageName);
			final int id = resources
					.getIdentifier(resourceFileName, null, null);
			mLogoImage.setImageDrawable(resources.getDrawable(id));
		} catch (NumberFormatException e) { // Not a resource id
			throw new IllegalArgumentException("Not a valid image.");
		} catch (NotFoundException e) { // Resource not found
			throw new IllegalArgumentException("Not a valid image.");
		} catch (NameNotFoundException e) { //Not a package name
			throw new IllegalArgumentException(
					"Not a valid (image resource) package name.");
		}
		/*The idea for this came from:
			android.content.Intent.ShortcutIconResource and related contstants and intents, in android.content.Intent: http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/android/content/Intent.java;h=39888c1bc0f62effa788815e5b9376969d255766;hb=master
			what's done with this in com.android.launcher.Launcher: http://android.git.kernel.org/?p=platform/packages/apps/Launcher.git;a=blob;f=src/com/android/launcher/Launcher.java;h=928f4caecde593d0fb430718de28d5e52df989ad;hb=HEAD
				and in android.webkit.gears.DesktopAndroid: http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/android/webkit/gears/DesktopAndroid.java
		*/
	}

	/**
	 * Fetch and display artists information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayArtists(final String packagename, final Intent intent) {
		String[] textarray = AboutUtils.getStringArrayExtraOrMetadata(this, packagename, intent, AboutIntents.EXTRA_ARTISTS, AboutMetaData.METADATA_ARTISTS);

		String text = AboutUtils.getTextFromArray(textarray);
		
		if (!TextUtils.isEmpty(text)) {
			mArtistsText.setText(text);
			mArtistsLabel.setVisibility(View.VISIBLE);
			mArtistsText.setVisibility(View.VISIBLE);
		} else {
			mArtistsLabel.setVisibility(View.GONE);
			mArtistsText.setVisibility(View.GONE);
		}
	}

	/**
	 * Fetch and display authors information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	private void displayAuthors(final String packagename, final Intent intent) {
		String[] textarray = AboutUtils.getStringArrayExtraOrMetadata(this, packagename, intent, AboutIntents.EXTRA_AUTHORS, AboutMetaData.METADATA_AUTHORS);
		
		String text = AboutUtils.getTextFromArray(textarray);
		
		if (!TextUtils.isEmpty(text)) {
			mAuthorsText.setText(text);
			mAuthorsLabel.setVisibility(View.VISIBLE);
			mAuthorsText.setVisibility(View.VISIBLE);
		} else {
			mAuthorsLabel.setVisibility(View.GONE);
			mAuthorsText.setVisibility(View.GONE);
		}
	}

	/**
	 * Fetch and display comments information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayComments(final String packagename, final Intent intent) {
		String text = AboutUtils.getStringExtraOrMetadata(this, packagename, intent, 
				AboutIntents.EXTRA_COMMENTS, AboutMetaData.METADATA_COMMENTS);
		
		if (!TextUtils.isEmpty(text)) {
			mCommentsText.setText(text);
			mCommentsText.setVisibility(View.VISIBLE);
		} else {
			mCommentsText.setVisibility(View.GONE);
		}
	}

	/**
	 * Fetch and display copyright information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayCopyright(final String packagename, final Intent intent) {
		String text = AboutUtils.getStringExtraOrMetadata(this, packagename, intent, 
				AboutIntents.EXTRA_COPYRIGHT, AboutMetaData.METADATA_COPYRIGHT);
		
		if (!TextUtils.isEmpty(text)) {
			mCopyrightText.setText(text);
			mCopyrightText.setVisibility(View.VISIBLE);
		} else {
			mCopyrightText.setVisibility(View.GONE);
		}
	}

	/**
	 * Fetch and display documenters information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayDocumenters(final String packagename, final Intent intent) {
		String[] textarray = AboutUtils.getStringArrayExtraOrMetadata(this, packagename, intent, 
				AboutIntents.EXTRA_DOCUMENTERS, AboutMetaData.METADATA_DOCUMENTERS);
		String text = AboutUtils.getTextFromArray(textarray);
		
		if (!TextUtils.isEmpty(text)) {
			mDocumentersText.setText(text);
			mDocumentersLabel.setVisibility(View.VISIBLE);
			mDocumentersText.setVisibility(View.VISIBLE);
		} else {
			mDocumentersLabel.setVisibility(View.GONE);
			mDocumentersText.setVisibility(View.GONE);
		}
	}


	/**
	 * Fetch and display license information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayLicense(final String packagename, final Intent intent) {
		
		int resourceid = AboutUtils.getResourceIdExtraOrMetadata(this, packagename, intent, 
				AboutIntents.EXTRA_LICENSE_RESOURCE, AboutMetaData.METADATA_LICENSE);
		
		if (resourceid == 0) {
			mLicenseText.setText(R.string.no_information_available);
			return;
		}
		
		
		// Retrieve license from resource:
		String license = "";
		try {
    		Resources resources = getPackageManager()
				.getResourcesForApplication(packagename);
    		
    		//Read in the license file as a big String
    		BufferedReader in
    		   = new BufferedReader(new InputStreamReader(
    				resources.openRawResource(resourceid)));
    		String line;
    		StringBuilder sb = new StringBuilder();
    		try {
    			while ((line = in.readLine()) != null) { // Read line per line.
    				if (TextUtils.isEmpty(line)) {
    					// Empty line: Leave line break
    					sb.append("\n\n");
    				} else {
    					sb.append(line);
    					sb.append(" ");
    				}
    			}
    			license = sb.toString();
    		} catch (IOException e) {
    			//Should not happen.
    			e.printStackTrace();
    		}
    		
    	} catch (NameNotFoundException e) {
            Log.e(TAG, "Package name not found", e);
    	}
    	
    	mLicenseText.setText(license);
		/*
		mLicenseText.setHorizontallyScrolling(!intent.getBooleanExtra(
				AboutIntents.EXTRA_WRAP_LICENSE, false));
		mLicenseText.setHorizontalScrollBarEnabled(!intent.getBooleanExtra(
				AboutIntents.EXTRA_WRAP_LICENSE, false));
		if (intent.hasExtra(AboutIntents.EXTRA_LICENSE)
				&& intent.getStringExtra(AboutIntents.EXTRA_LICENSE) != null) {
			mLicenseText.setText(intent
					.getStringExtra(AboutIntents.EXTRA_LICENSE));
		} else {
    		mLicenseText.setText("");
    	}
    	*/
	}

	/**
	 * Fetch and display logo information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayLogo(final String packagename, final Intent intent) {
		if (intent.hasExtra(AboutIntents.EXTRA_ICON_RESOURCE)
				&& intent.getStringExtra(AboutIntents.EXTRA_ICON_RESOURCE) != null) {
    		try {
    			changeLogoImageResource(intent.getStringExtra(AboutIntents.EXTRA_ICON_RESOURCE),
						packagename);
    		} catch (IllegalArgumentException e) {
    			mLogoImage.setImageResource(android.R.drawable.ic_menu_info_details);
    			//mLogoImage.setImageURI(Uri.EMPTY);
    		}
    	} else if (intent.hasExtra(AboutIntents.EXTRA_ICON_URI)
				&& intent.getStringExtra(AboutIntents.EXTRA_ICON_URI) != null) {
    		try {
    			changeLogoImageUri(intent.getStringExtra(AboutIntents.EXTRA_ICON_URI));
    		} catch (IllegalArgumentException e) {
    			mLogoImage.setImageResource(android.R.drawable.ic_menu_info_details);
    			//mLogoImage.setImageURI(Uri.EMPTY);
    		}
    	} else {
    		try {
                PackageInfo pi = getPackageManager().getPackageInfo(
						packagename, 0);
    			Resources resources = getPackageManager()
    					.getResourcesForApplication(packagename);
    			String resourcename = resources.getResourceName(pi.applicationInfo.icon);
                changeLogoImageResource(resourcename, packagename);
    		} catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Package name not found", e);
    			mLogoImage.setImageResource(android.R.drawable.ic_menu_info_details);
        		//mLogoImage.setImageURI(Uri.EMPTY);
    		} catch (IllegalArgumentException e) {
    			mLogoImage.setImageResource(android.R.drawable.ic_menu_info_details);
    			//mLogoImage.setImageURI(Uri.EMPTY);
    		}
    	}
	}

	/**
	 * Fetch and display program name and version information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayProgramNameAndVersion(final String packagename, final Intent intent) {
		String applicationlabel = getApplicationLabel(packagename, intent);
		String versionname = getVersionName(packagename, intent);
		
		String combined = applicationlabel;
		if (!TextUtils.isEmpty(versionname)) {
			combined += " " + versionname;
		}
		
        mProgramNameAndVersionText.setText(combined);
	}

	/**
	 * Get application label.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected String getApplicationLabel(final String packagename, final Intent intent) {
		String applicationlabel = null;
		if (intent.hasExtra(AboutIntents.EXTRA_APPLICATION_LABEL)
				&& intent.getStringExtra(AboutIntents.EXTRA_APPLICATION_LABEL) 
					!= null) {
			applicationlabel = intent.getStringExtra(AboutIntents.EXTRA_APPLICATION_LABEL);
		} else {
            try {
                    PackageInfo pi = getPackageManager().getPackageInfo(
                    		packagename, 0);
                    int labelid = pi.applicationInfo.labelRes;
         			Resources resources = getPackageManager()
         					.getResourcesForApplication(packagename);
         			applicationlabel = resources.getString(labelid);
            } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "Package name not found", e);
            }
		}
		return applicationlabel;
	}
	
	/**
	 * Get version information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected String getVersionName(final String packagename, final Intent intent) {
		String versionname = null;
		if (intent.hasExtra(AboutIntents.EXTRA_VERSION_NAME)
				&& intent.getStringExtra(AboutIntents.EXTRA_VERSION_NAME) 
					!= null) {
			versionname = intent.getStringExtra(AboutIntents.EXTRA_VERSION_NAME);
		} else {
            try {
                    PackageInfo pi = getPackageManager().getPackageInfo(
                    		packagename, 0);
                    versionname = pi.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "Package name not found", e);
            }
		}
		return versionname;
	}

	/**
	 * Fetch and display translators information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayTranslators(final String packagename, final Intent intent) {

		String[] textarray = AboutUtils.getStringArrayExtraOrMetadata(this, packagename, intent, AboutIntents.EXTRA_TRANSLATORS, AboutMetaData.METADATA_TRANSLATORS);
		String text = AboutUtils.getTextFromArray(textarray);

		if (!TextUtils.isEmpty(text)) {
			mTranslatorsText.setText(text);
			mTranslatorsLabel.setVisibility(View.VISIBLE);
			mTranslatorsText.setVisibility(View.VISIBLE);
		} else {
			mTranslatorsLabel.setVisibility(View.GONE);
			mTranslatorsText.setVisibility(View.GONE);
		}
		
	}

	/**
	 * Fetch and display website link information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayWebsiteLink(final String packagename, final Intent intent) {
		String websitelabel = AboutUtils.getStringExtraOrMetadata(this, packagename,
			intent, AboutIntents.EXTRA_WEBSITE_LABEL, AboutMetaData.METADATA_WEBSITE_LABEL);
		String websiteurl = AboutUtils.getStringExtraOrMetadata(this, packagename,
				intent, AboutIntents.EXTRA_WEBSITE_URL, AboutMetaData.METADATA_WEBSITE_URL);
		
		setAndLinkifyWebsiteLink(websitelabel, websiteurl);
	}
	
	/**
	 * Set the website link TextView and linkify.
	 * 
	 * @param websiteLabel The label to set.
	 * @param websiteUrl The URL that the label links to.
	 */
	protected void setAndLinkifyWebsiteLink(final String websiteLabel, final String websiteUrl) {
		if (!TextUtils.isEmpty(websiteUrl)) {
			if (TextUtils.isEmpty(websiteLabel)) {
				mWebsiteText.setText(websiteUrl);
			} else {
				mWebsiteText.setText(websiteLabel);
			}
			mWebsiteText.setVisibility(View.VISIBLE);
			
			//Create TransformFilter
			TransformFilter tf = new TransformFilter() {
	
				public String transformUrl(final Matcher matcher,
						final String url) {
					return websiteUrl;
				}
				
			};
			
			//Allow a label and url through Linkify
			Linkify.addLinks((TextView) mWebsiteText.getChildAt(0), Pattern
					.compile(".*"), "", null, tf);
			Linkify.addLinks((TextView) mWebsiteText.getChildAt(1), Pattern
					.compile(".*"), "", null, tf);
		} else {
			mWebsiteText.setVisibility(View.GONE);
		}
	}

	/**
	 * Fetch and display website link information.
	 * 
	 * @param intent The intent from which to fetch the information.
	 */
	protected void displayEmail(final String packagename, final Intent intent) {
		String email = AboutUtils.getStringExtraOrMetadata(this, packagename,
			intent, AboutIntents.EXTRA_EMAIL, AboutMetaData.METADATA_EMAIL);
		
		if (!TextUtils.isEmpty(email)) {
			mEmailImage.setImageResource(android.R.drawable.ic_dialog_email);
			mEmailText.setText(email);
		} else {
			mEmailImage.setImageURI(null);
		}
	}

	/**
	 * Check whether any credits are available.
	 * If not, display "no information available".
	 */
	void checkCreditsAvailable() {
		if (mAuthorsLabel.getVisibility() == View.GONE
				&& mAuthorsLabel.getVisibility() == View.GONE
				&& mAuthorsLabel.getVisibility() == View.GONE
				&& mAuthorsLabel.getVisibility() == View.GONE ) {
			mNoInformationText.setVisibility(View.VISIBLE);
		} else {
			mNoInformationText.setVisibility(View.GONE);
		}
				
	}
	
	/* (non-Javadoc)
     * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	//Set up the layout with the TabHost
    	tabHost = getTabHost();
        
        LayoutInflater.from(this).inflate(R.layout.about,
				tabHost.getTabContentView(), true);
        
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.l_info))
                .setIndicator(getString(R.string.l_info))
                .setContent(R.id.sv_info));
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.l_credits))
                .setIndicator(getString(R.string.l_credits))
                .setContent(R.id.sv_credits));
        tabHost.addTab(tabHost.newTabSpec(getString(R.string.l_license))
                .setIndicator(getString(R.string.l_license))
                .setContent(R.id.sv_license));
        
        //Set the animations for the switchers
        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        
        //Find the views
        mLogoImage = (ImageSwitcher) findViewById(R.id.i_logo);
        mLogoImage.setInAnimation(in);
        mLogoImage.setOutAnimation(out);

        mEmailImage = (ImageSwitcher) findViewById(R.id.i_email);
        mEmailImage.setInAnimation(in);
        //mEmailImage.setOutAnimation(out);
            // Strange bug: setting the out animation results in the envelope image
        	// appearing and disappearing if one clicks on the email link repeatedly.
		
        mProgramNameAndVersionText = (TextSwitcher) 
        		findViewById(R.id.t_program_name_and_version);
		mProgramNameAndVersionText.setInAnimation(in);
		mProgramNameAndVersionText.setOutAnimation(out);
        
		mCommentsText = (TextSwitcher) findViewById(R.id.t_comments);
		mCommentsText.setInAnimation(in);
		mCommentsText.setOutAnimation(out);
		
		mCopyrightText = (TextSwitcher) findViewById(R.id.t_copyright);
		mCopyrightText.setInAnimation(in);
		mCopyrightText.setOutAnimation(out);
		
		mWebsiteText = (TextSwitcher) findViewById(R.id.t_website);
		mWebsiteText.setInAnimation(in);
		mWebsiteText.setOutAnimation(out);

        mEmailImage = (ImageSwitcher) findViewById(R.id.i_email);
        mEmailImage.setInAnimation(in);
        mEmailImage.setOutAnimation(out);
		
		mEmailText = (TextSwitcher) findViewById(R.id.t_email);
		mEmailText.setInAnimation(in);
		mEmailText.setOutAnimation(out);

		mAuthorsLabel = (TextView) findViewById(R.id.l_authors);
		mAuthorsText = (TextView) findViewById(R.id.et_authors);

		mDocumentersLabel = (TextView) findViewById(R.id.l_documenters);
		mDocumentersText = (TextView) findViewById(R.id.et_documenters);

		mTranslatorsLabel = (TextView) findViewById(R.id.l_translators);
		mTranslatorsText = (TextView) findViewById(R.id.et_translators);

		mArtistsLabel = (TextView) findViewById(R.id.l_artists);
		mArtistsText = (TextView) findViewById(R.id.et_artists);
		
		mNoInformationText = (TextView) findViewById(R.id.tv_no_information);

		mLicenseText = (TextView) findViewById(R.id.et_license);
    }


	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ITEM_ABOUT:
				// Show the about dialog for this app.
				showAboutDialog();
				return true;
			
			default:
				// Whoops, unknown menu item.
				Log.e(TAG, "Unknown menu item");
		}
		return super.onOptionsItemSelected(item);
	}
	

	/* (non-Javadoc)
	 * @see android.app.ActivityGroup#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
        // tabHost.setCurrentTabByTag(getString(R.string.l_info));
		
		//Decode the intent, if any
		final Intent intent = getIntent();
		/*
        if (intent == null) {
        	refuseToShow();
        	return;
        }
        */
		if (intent == null) {
			setIntent(new Intent());
		}
		
		String packagename = getPackageNameFromIntent(intent);
		
		Log.i(TAG, "Showing About dialog for package " + packagename);
    	
    	displayLogo(packagename, intent);
        displayProgramNameAndVersion(packagename, intent);
    	displayComments(packagename, intent);
    	displayCopyright(packagename, intent);
    	displayWebsiteLink(packagename, intent);
    	displayAuthors(packagename, intent);
    	displayDocumenters(packagename, intent);
    	displayTranslators(packagename, intent);
    	displayArtists(packagename, intent);
    	displayLicense(packagename, intent);
    	displayEmail(packagename, intent);
    	
    	checkCreditsAvailable();
    	
    	setResult(RESULT_OK);
	}

	/**
	 * Show an about dialog for this application.
	 */
	protected void showAboutDialog() {
		Intent intent = new Intent(AboutIntents.ACTION_SHOW_ABOUT_DIALOG);
		
		// Start about activity. Needs to be "forResult" with requestCode>=0
		// so that the package name is passed properly.
		//
		// The details are obtained from the Manifest through
		// default tags and metadata.
		startActivityForResult(intent, 0);
	}
}
