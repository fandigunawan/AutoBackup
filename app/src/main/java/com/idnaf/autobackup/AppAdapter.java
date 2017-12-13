package com.idnaf.autobackup;


/**
 * Created by Fandi on 12/7/2017.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter
{
    private Activity activity;
    private static LayoutInflater inflater = null;
    private ArrayList<HashMap<String, String>> data;

    public AppAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;

    private void updateExternalStorageState()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }
    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_app, null);

        TextView appName = (TextView)vi.findViewById(R.id.tvAppName);
        TextView appPkgName = (TextView)vi.findViewById(R.id.tvPkgName);
        TextView appVersion = (TextView)vi.findViewById(R.id.tvAppVersion);
        ImageView appIcon = (ImageView) vi.findViewById(R.id.ivIcon);

        HashMap<String, String> app = new HashMap<String, String>();
        app = data.get(position);

        // Setting all values in listview

        appName.setText(app.get(activity.getString(R.string.kv_app_name)));
        appPkgName.setText(app.get(activity.getString(R.string.kv_pkg_name)));

        updateExternalStorageState();
        if(mExternalStorageAvailable && mExternalStorageWriteable)
        {
            // external storage path e.g /sdcard/
            String externalStoragePath = Environment.getExternalStorageDirectory().toString();
            // root app path e.g /sdcard/AutoBackup/
            String rootAppPath = externalStoragePath + File.separator + activity.getString(R.string.app_name);
            // app path e.g. /sdcard/AutoBackup/com.example.myapp
            String appPath = rootAppPath + File.separator + appPkgName.getText();
            File fileAppPath = new File(appPath);
            if(fileAppPath.exists())
            {
                int calcApk = 0;
                String[] apks = fileAppPath.list();
                for (String s : apks)
                {
                    if(s.toUpperCase().endsWith(".APK"))
                    {
                        calcApk++;
                    }
                }
                if(calcApk > 0)
                    appName.setText(appName.getText() + " (" + Integer.toString(calcApk) + ")");
            }
        }
        try
        {
            PackageManager pm = activity.getPackageManager();

            PackageInfo pi = pm.getPackageInfo(app.get(activity.getString(R.string.kv_pkg_name)), PackageManager.GET_META_DATA);

            appIcon.setImageDrawable(pi.applicationInfo.loadIcon(pm));

            appVersion.setText(pi.versionName +"(" + Integer.toString(pi.versionCode) + ")");

        }
        catch(PackageManager.NameNotFoundException e)
        {
            Log.d(activity.getString(R.string.app_name), e.getMessage());
        }
        return vi;
    }
}

