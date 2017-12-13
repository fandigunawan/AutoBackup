package com.idnaf.autobackup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AppUpdateReceiver extends BroadcastReceiver
{
    public AppUpdateReceiver()
    {
    }

    /***
     * Implement file copy
     * @param src File of source file
     * @param dst File of destination file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void Copy(File src, File dst) throws FileNotFoundException, IOException
    {
        // If source file does not exists, return immediately
        if(src.exists() == false)
        {
            return;
        }
        // If both are directory type
        if(src.isDirectory() || dst.isDirectory())
        {
            return;
        }
        // If destination exists
        if(dst.exists() == false)
        {
            dst.delete();
            dst.createNewFile();
        }
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        byte[] buff = new byte[1024];
        int len;
        while((len = in.read(buff)) > 0)
        {
            out.write(buff,0 , len);
        }
        in.close();
        out.close();
        Log.d("FileCopy", "Copy " + src.toString() + " -> " + dst.toString());
    }
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }}


    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(context.getString(R.string.app_name), "onReceive() " + intent.toString());
        Bundle b = intent.getExtras();
        String[] packages = context.getPackageManager().getPackagesForUid(b.getInt("android.intent.extra.UID"));
        if(packages.length > 0)
        {
            try
            {
                // Check external storage condition
                updateExternalStorageState();
                // Make sure that the storage is available and is writable
                if(mExternalStorageAvailable && mExternalStorageWriteable)
                {
                    PackageInfo pi = context.getPackageManager().getPackageInfo(packages[0], PackageManager.GET_META_DATA);

                    // external storage path e.g /sdcard/
                    String externalStoragePath = Environment.getExternalStorageDirectory().toString();
                    // root app path e.g /sdcard/AutoBackup/
                    String rootAppPath = externalStoragePath + File.separator + context.getString(R.string.app_name);
                    // app path e.g. /sdcard/AutoBackup/com.example.myapp
                    String appPath = rootAppPath + File.separator + pi.packageName;

                    String sourceApk = pi.applicationInfo.sourceDir;
                    Log.d(context.getString(R.string.app_name), "APK=" + sourceApk);
                    // If root path of storage of AppName does not exist
                    if(new File(rootAppPath).exists() == false)
                    {
                        new File(rootAppPath).mkdir();
                    }
                    // If app path does not exists
                    if(new File(appPath).exists() == false)
                    {
                        new File(appPath).mkdir();
                    }
                    File fileSrcApk = new File(sourceApk);
                    if(fileSrcApk.exists())
                    {
                        File fileDstApk = new File(appPath + File.separator + pi.packageName +"_" + pi.versionName + "_" + Integer.toString(pi.versionCode) + ".apk");
                        if(fileDstApk.exists() == false)
                        {
                            Log.d(context.getString(R.string.app_name), "file does not exist create a new one " + fileDstApk);
                            fileDstApk.createNewFile();
                        }
                        // if found exact file then delete it
                        else
                        {
                            Log.d(context.getString(R.string.app_name), "file exists, delete and create new one " + fileDstApk);
                            fileDstApk.delete();
                            fileDstApk.createNewFile();

                        }
                        Copy(fileSrcApk, fileDstApk);
                    }
                }
                else
                {
                    Log.d(context.getString(R.string.app_name), "avail=" + mExternalStorageAvailable + " write=" + mExternalStorageWriteable);
                }


            }
            catch (PackageManager.NameNotFoundException e)
            {
                Log.d(context.getString(R.string.app_name), e.getMessage());

                e.printStackTrace();
            }
            catch(IOException e)
            {
                Log.d(context.getString(R.string.app_name), e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
