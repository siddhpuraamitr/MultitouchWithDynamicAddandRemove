package com.amitsid.dynamicaddimageview;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {

	// Change the URLs
	private static ProgressDialog pd;

	public static Boolean isOnline(Context mContext) {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		showAlert(mContext, "Alert", "Internet is not Available");
		return false;
	}

	public static boolean validateBlankField(EditText et, Context context,
			String msg) {
		if (et.getText().toString().equals("")) {
			Utils.showToast(context, msg);
			return false;
		}
		return true;
	}

	public static void showAlert(Context context, String title, String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public static void showProgress(Activity activity) {
		if (pd == null) {
			pd = ProgressDialog.show(activity, "Please wait", "Loading...");
			pd.setCancelable(false);
			pd.show();
		}
	}

	public static void closeProgress() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

	}

	// public static void checkUser(Context context) {
	//
	// showToast(context, "You need to Register");
	// Intent intent = new Intent(context, RegisterLogin.class);
	// context.startActivity(intent);
	// }

	public static void startBroadcast(Context context, String action) {
		Intent intent = new Intent(action);
		context.sendBroadcast(intent);
	}

	public static boolean emailValidator(String email, Context context) {
		Pattern pattern;
		Matcher matcher;
		final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			// showToast(context, "Enter valid email address");
			showAlert(context, "", "Enter valid email address");
		}
		return matcher.matches();

	}

	@SuppressLint("NewApi")
	public static int getDeviceWidth(Context context) {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			point.x = display.getWidth();
			// point.y = display.getHeight();
		}
		return point.x;
	}

	@SuppressLint("NewApi")
	public static int getDeviceHeight(Context context) {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			// point.x = display.getWidth();
			point.y = display.getHeight();
		}
		return point.y;
	}

	public static String encodeString(String name) {
		String afterEncode = "";
		try {
			afterEncode = URLEncoder.encode(name, "UTF-8");
			afterEncode = afterEncode.replaceAll(" ", "%20");
			// .replaceAll(
			// "+", "%20");
		} catch (UnsupportedEncodingException e) {
			afterEncode = name.replaceAll(" ", "%20");
			e.printStackTrace();
		}
		return afterEncode;
	}

}
