package com.mishkin.tesseract;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class FileManagerActivity extends Activity {

    private ListView listView;
    private ArrayList<String> arrFiles;
    static ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fmanager);

        arrFiles = new ArrayList<String>();
        //   Log.e("FILES:", ""+"files");
        String path = Environment.getExternalStorageDirectory().toString();
        File dir = new File("/data/data/com.mishkin.tesseract/files/");
        File[] files = dir.listFiles();
        if (files != null)
            for (int i=0; i<files.length; ++i)
            {
                //   Log.e("FILE:", ""+files[i]);
                arrFiles.add( 0, files[i].getName() );
            }
        if ( files.length ==0 ) arrFiles.add( "Файлы не найдены!" );

        adapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_1, arrFiles);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                TextView textView = (TextView) itemClicked;
                String strText = textView.getText().toString(); // получаем текст нажатого элемента

                if(!strText.equalsIgnoreCase("Файлы не найдены!")) {
                    // Запускаем активность, связанную с определенным именем кота
                    Intent intent = new Intent(FileManagerActivity.this, MainActivity.class );
                    intent.putExtra("filename", strText );
                    startActivity(intent);
                }

            }
        });

    }


}


