package com.idnaf.autobackup;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity
{
    private ArrayList<HashMap<String, String>> listApp = new ArrayList<HashMap<String, String>>();
    private AppAdapter adapter;
    private ListView lvApp;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listApp.clear();

        lvApp = (ListView) findViewById(R.id.lvApp);
        PackageManager pm = getPackageManager();
        List<PackageInfo> listPkg = pm.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo pkg: listPkg)
        {
            HashMap<String, String> app = new HashMap<>();
            app.put(getString(R.string.kv_app_name), pkg.applicationInfo.loadLabel(pm).toString());
            app.put(getString(R.string.kv_pkg_name), pkg.packageName);
            listApp.add(app);
        }

        Collections.sort(listApp, new Comparator<HashMap<String, String>>()
        {
            @Override
            public int compare(HashMap<String, String> stringStringHashMap, HashMap<String, String> t1)
            {
                return stringStringHashMap.get(getString(R.string.kv_app_name)).toUpperCase().compareTo(t1.get(getString(R.string.kv_app_name)).toUpperCase());
            }
        });

        adapter = new AppAdapter(this, listApp);

        lvApp.setAdapter(adapter);

        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

            }
        });
    }
}
