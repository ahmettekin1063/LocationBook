package com.example.myloc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.myloc.MapsActivity.database;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    static ArrayList<String> nameArray;
    static ArrayList<Integer> idArray;

    ArrayAdapter arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        nameArray = new ArrayList<String>();
        idArray = new ArrayList<Integer>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nameArray);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("locId",idArray.get(position));
                intent.putExtra("info","old");
                startActivity(intent);


            }
        });

        getData();

    }

    public void getData(){

        try {
            MapsActivity.database = this.openOrCreateDatabase("Locs", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM locs", null);

            int locNameIx = cursor.getColumnIndex( "locname" );
            int idIx = cursor.getColumnIndex( "id" );

            while (cursor.moveToNext()) {

                nameArray.add(cursor.getString(locNameIx));
                idArray.add(cursor.getInt(idIx));

            }
            arrayAdapter.notifyDataSetChanged();

            cursor.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflater
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_loc,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_loc_item) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
           intent.putExtra("info","new");

            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

}