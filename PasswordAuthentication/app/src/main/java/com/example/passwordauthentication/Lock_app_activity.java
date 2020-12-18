package com.example.passwordauthentication;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Lock_app_activity extends AppCompatActivity {

    public List<AppList>appList = new ArrayList();
    private RecyclerView recyclerView; //The RecyclerView class supports the display
    // of a collection of data. It is a modernized version of the ListView and the GridView classes provided by the Android framework.
    public ListofAppAdapter mAdapter; // sets the applist data.

    Apply_password_on_AppDatabase db = new Apply_password_on_AppDatabase(this);
    Password_Database pass_db = new Password_Database(this);

    List<String> lock = new ArrayList();
    String pass = "";

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_lockapp_activity);

        Cursor res = this.db.getAllData();
        Cursor res2 = this.pass_db.getAllData();

        while(res.moveToNext()){
            this.lock.add(res.getString(0));
        }
        while(res2.moveToNext()){
            this.pass = res2.getString(0);
        }

        this.recyclerView = (RecyclerView) findViewById(R.id.installed_app_list);

        this.mAdapter = new ListofAppAdapter(this.appList);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(this.mAdapter);

        getInstalledApps();
        this.mAdapter.notifyDataSetChanged();

        this.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),this.recyclerView,new RecyclerTouchListener.ClickListener(){
            public void onClick(View view, int position){
                AppList model = (AppList) Lock_app_activity.this.appList.get(position);
                if(model.getLocked() == R.drawable.ic_lock_black_24dp){
                    Lock_app_activity.this.db.deleteData(model.getPackage_name());
                    Toast.makeText(Lock_app_activity.this,"App Unlocked", Toast.LENGTH_LONG).show();
                    model.setLocked(R.drawable.ic_lock_open_black_24dp);
                }
                else{
                    System.out.println("Here is Output " + pass);
                    Lock_app_activity.this.db.insertData(model.getPackage_name());
                    Toast.makeText(Lock_app_activity.this,"App is Locked", Toast.LENGTH_LONG).show();
                    model.setLocked(R.drawable.ic_lock_black_24dp);
                }
                Lock_app_activity.this.mAdapter.notifyDataSetChanged();
            }
            public void onLongClick(View view, int position){
            }
        }));
    }

    private List<AppList> getInstalledApps(){
        ArrayList arrayList = new ArrayList();
        List installedPackages = getPackageManager().getInstalledPackages(0);

        for(int i=0; i<installedPackages.size(); i++){
            PackageInfo packageInfo = (PackageInfo) installedPackages.get(i);
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("size = ");
            sb.append(installedPackages.size());
            printStream.println(sb.toString());
            if(this.lock.contains(((PackageInfo) installedPackages.get(i)).packageName)){
                if(!isSystemPackage(packageInfo)){
                    this.appList.add(new AppList(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(),
                            packageInfo.applicationInfo.loadIcon(getPackageManager()),
                            R.drawable.ic_lock_black_24dp, ((PackageInfo) installedPackages.get(i)).packageName));

                }
            }
            else if(!isSystemPackage(packageInfo)){
                this.appList.add(new AppList(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(),
                        packageInfo.applicationInfo.loadIcon(getPackageManager()),
                        R.drawable.ic_lock_open_black_24dp, ((PackageInfo) installedPackages.get(i)).packageName));
            }
        }
        return  arrayList;
    }
    private boolean isSystemPackage(PackageInfo packageInfo){
        return (packageInfo.applicationInfo.flags & 1) != 0;
    }
    public void onBackPressed(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
