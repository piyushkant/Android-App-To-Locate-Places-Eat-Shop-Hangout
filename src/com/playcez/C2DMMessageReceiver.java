/*
 * 
 */
package com.playcez;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class C2DMMessageReceiver.
 */
public class C2DMMessageReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Message Receiver called");
		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");
			final String payload = intent.getStringExtra("payload");
			Log.d("C2DM", "dmControl: payload = " + payload);
			// TODO Send this to my application server to get the real data

			// createNotification(context, payload);
			notifyUser(context, payload);
		}
	}

	/**
	 * Notify user.
	 *
	 * @param context the context
	 * @param payload the payload
	 */
	public void notifyUser(Context context, String payload) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon,
				"New offers available", System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, NearOffers.class);
		intent.putExtra("payload", payload);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "PlayCez",
				"New Offers available", pendingIntent);
		notificationManager.notify(0, notification);

	}
	/*
	 * public void createNotification(Context context, String payload) {
	 * NotificationManager notificationManager = (NotificationManager) context
	 * .getSystemService(Context.NOTIFICATION_SERVICE); Notification
	 * notification = new Notification(R.drawable.icon, "Message received",
	 * System.currentTimeMillis()); // Hide the notification after its selected
	 * notification.flags |= Notification.FLAG_AUTO_CANCEL;
	 * 
	 * Intent intent = new Intent(context, MessageReceivedActivity.class);
	 * intent.putExtra("payload", payload); PendingIntent pendingIntent =
	 * PendingIntent.getActivity(context, 0, intent, 0);
	 * notification.setLatestEventInfo(context, "Message",
	 * "New message received", pendingIntent); notificationManager.notify(0,
	 * notification);
	 * 
	 * }
	 */
}