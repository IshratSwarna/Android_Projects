package com.example.passwordauthentication;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
public class MainActivity extends AppCompatActivity {

    Button lock_app;
    Button update_pass;
    Password_Database db = new Password_Database(this);
    List<String> pass = new ArrayList();
    Context cm;


    //intent is used to call background services
    Intent mServiceIntent;
    //NotificationService is for background service class
    private BackgroundServices mYourService;


    String save_pattern_key = "pattern_code";
    String final_pattern;
    PatternLockView mPatternLockView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        lock_app = (Button)findViewById(R.id.lock_app_btn);
        update_pass = (Button)findViewById(R.id.update_pass_btn);

        this.cm = this;

        Cursor allData = this.db.getAllData();
        while (allData.moveToNext()) {
            Log.d("First","1");
            this.pass.add(allData.getString(0));
        }

        Paper.init(this);
        String save_pattern = Paper.book().read(save_pattern_key);

        lock_app.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // as at onstart the app is getting one password always so, this if part never gonna work actually, but just for incase this is written.
                if (save_pattern == null) {
                    Toast.makeText(MainActivity.this, "First Set Password", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity mainActivity = MainActivity.this;
                mainActivity.startActivity(new Intent(mainActivity, Lock_app_activity.class)); //goes to lockapp activity-> locks your apps on your phone.
                mainActivity.finish();
            }
        });
        update_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_set_pattern);

                //update previous password.

                mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
                mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onProgress(List<PatternLockView.Dot> progressPattern) {

                    }

                    @Override
                    public void onComplete(List<PatternLockView.Dot> pattern) {
                        final_pattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
                    }

                    @Override
                    public void onCleared() {

                    }
                });
                Button btnSetup = (Button) findViewById(R.id.btnSetPattern);
                btnSetup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(R.layout.activity_main);
                        if (final_pattern.equals(save_pattern)) {
                            Toast.makeText(MainActivity.this, "Pattern is Same.", Toast.LENGTH_SHORT).show();

                        } else {
                            MainActivity.this.db.delete();
                            MainActivity.this.db.insertData(final_pattern);
                            MainActivity.this.pass.add(final_pattern);
                            Paper.book().write(save_pattern_key, final_pattern);
                            Toast.makeText(MainActivity.this, "Pattern is Updated.", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });

            }

        });
    }

    //check the background services
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @RequiresApi(api = 19)

    protected void onStart() {
        super.onStart();

        Paper.init(this);
        String save_pattern = Paper.book().read(save_pattern_key);
        if(!isAccessGranted()) {
            new AlertDialog.Builder(this).setTitle("USAGE_STATS Permission").setMessage("Allow USAGE_STATS Permission in Setting").setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.startActivity(new Intent("android.settings.USAGE_ACCESS_SETTINGS"));
                }
            }).setNegativeButton("Abort", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).show();
        }
        else if (save_pattern == null) {
            this.update_pass.setText("Set Pattern");

            setContentView(R.layout.activity_set_pattern);
            mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView, pattern);

                }

                @Override
                public void onCleared() {

                }
            });
            Button btnSetup = (Button) findViewById(R.id.btnSetPattern);
            btnSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.activity_main);
                    MainActivity.this.db.insertData(final_pattern);
                    MainActivity.this.pass.add(final_pattern);
                    Paper.book().write(save_pattern_key, final_pattern);
                    Toast.makeText(MainActivity.this, "Pattern is Saved.", Toast.LENGTH_SHORT).show();
                    MainActivity.this.update_pass.setText("Update Password");
                    finish();
                }
            });
        }

        //run background services
        this.mYourService = new BackgroundServices();
        this.mServiceIntent = new Intent(this, this.mYourService.getClass());
        if (isMyServiceRunning(this.mYourService.getClass())) {
            return;
        }
        if (Build.VERSION.SDK_INT > 26) {
            startForegroundService(this.mServiceIntent);
        } else {
            startService(this.mServiceIntent);
        }

    }

    @RequiresApi(api = 19)
    private boolean isAccessGranted() {
        boolean z = false;
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
            if ((Build.VERSION.SDK_INT > 19 ? ((AppOpsManager) getSystemService(Context.APP_OPS_SERVICE)).checkOpNoThrow("android:get_usage_stats", applicationInfo.uid, applicationInfo.packageName) : 0) == 0) {
                z = true;
            }
            return z;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
