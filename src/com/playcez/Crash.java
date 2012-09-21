/*
 * 
 */
package com.playcez;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.os.Build;

/**
 * The Class Crash.
 */
@ReportsCrashes(formKey = "dHgwWGRheG52Q3FnenVhU1Z0Zl80YkE6MQ", includeDropBoxSystemTags = true, additionalDropBoxTags = {
		"TAG", "TAG" }, dropboxCollectionMinutes = 10)
public class Crash extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		ACRA.init(this);

		ErrorReporter.getInstance()
				.putCustomData("PACKAGE_NAME", "com.playcez");
		ErrorReporter.getInstance().putCustomData("PHONE_MODEL", Build.MODEL);
		ErrorReporter.getInstance().putCustomData("PRODUCT", Build.PRODUCT);
		ErrorReporter.getInstance().putCustomData("ANDROID_VERSION",
				Build.VERSION.RELEASE);

		super.onCreate();
	}
}
