package com.michaelmagdy.workmanagerexample;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import static com.michaelmagdy.workmanagerexample.DemoWorker.KEY_WORKER;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_COUNT_VALUE = "key_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Data data = new Data.Builder()
                .putInt(KEY_COUNT_VALUE, 18000)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        final OneTimeWorkRequest oneTimeWorkRequest =
                new OneTimeWorkRequest.Builder(DemoWorker.class)
                        .setInputData(data)
                        .setConstraints(constraints)
                .build();

        final TextView textView = findViewById(R.id.tv_status);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkManager.getInstance(MainActivity.this).enqueue(oneTimeWorkRequest);
                Toast.makeText(MainActivity.this,
                        "please plugin your charger to let the work done", Toast.LENGTH_SHORT).show();
            }
        });

        WorkManager.getInstance(MainActivity.this)
                .getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null){
                            textView.setText(workInfo.getState().name());

                            if (workInfo.getState().isFinished()){
                                Data data1 = workInfo.getOutputData();
                                String message = data1.getString(KEY_WORKER);
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
