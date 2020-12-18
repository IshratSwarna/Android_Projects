package com.example.passwordauthentication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;
import android.os.Handler;

public class LockScreen extends AppCompatActivity {
    String final_pattern;
    PatternLockView mPatternLockView;
    Password_Database pass_db = new Password_Database(this);
    String pass = "";

    String v1,v2;
    int Wc = 1;
    public LockScreen(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        //v2 get data from background services
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            v2 = extras.getString("pack");
        }
        Cursor res2 = this.pass_db.getAllData();
        while(res2.moveToNext()){
            this.pass = res2.getString(0);
        }
        v1 = pass; // v1 is the saved pattern in pass_dab
        mPatternLockView = (PatternLockView)findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                final_pattern = PatternLockUtils.patternToString(mPatternLockView,pattern);
                if(final_pattern.equals(v1)){
                    Wc = 1;
                    Toast.makeText(LockScreen.this, "Password is Correct.", Toast.LENGTH_LONG).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent launchIntentForPackage = LockScreen.this.getPackageManager().getLaunchIntentForPackage(LockScreen.this.v2);
                            launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LockScreen.this.startActivity(launchIntentForPackage);
                            LockScreen.this.finish();
                            return;
                        }
                    }, 1000);

                }
                else{
                    Wc = Wc + 1;
                    if(Wc == 4)
                        Toast.makeText(LockScreen.this, "Sorry, You are not permitted to access it.", Toast.LENGTH_LONG).show();
                    else{
                        Toast.makeText(LockScreen.this, "Password is Wrong.", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onCleared() {

            }
        });

    }
}