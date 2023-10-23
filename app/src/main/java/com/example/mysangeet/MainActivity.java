package com.example.mysangeet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    //String[] items;
//    EditText editText;
    ArrayList<File> mySongs;
//    ArrayList<Integer> index;
    customAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listView);
//        editText=findViewById(R.id.searchFilter);
        runtimePermission();


//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                customAdapter.getFilter().filter(s);
////              customAdapter.notifyDataSetChanged();
////              listView.setAdapter(customAdapter);
////                customAdapter=new customAdapter(duplicate,MainActivity.this);
////                listView.setAdapter(customAdapter);
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView =(SearchView)menuItem.getActionView();
        searchView.setQueryHint("Type to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                customAdapter.getFilter().filter(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void runtimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


    public ArrayList<File> findSong(File file){
        ArrayList arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if(files!=null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findSong(singleFile));
                } else {
                    if ((singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith((".wav"))&&(!singleFile.getName().startsWith(".")))) {
                        arrayList.add(singleFile);
                    }
                }
            }
        }
        return arrayList;
    }



    public void displaySong(){
        mySongs = findSong(Environment.getExternalStorageDirectory());
//        items = new String[mySongs.size()];
        ArrayList<String> items;
        items=new ArrayList<String>(mySongs.size());
        for(int i=0;i<mySongs.size();i++){
//            items[i]=mySongs.get(i).getName().replace(".mp3","").replace(".wav","");
            items.add(mySongs.get(i).getName().replace(".mp3","").replace(".wav",""));

        }

        customAdapter= new customAdapter(items,this);
        listView.setAdapter(customAdapter);
        listView.setTextFilterEnabled(true);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String songName=(String) listView.getItemAtPosition(position);
//                startActivity(new Intent(getApplicationContext(),PlayActivity.class)
//                        .putExtra("songs",mySongs)
//                        .putExtra("songname",songName)
//                        .putExtra("pos",position));
//            }
//        });
    }
    public class customAdapter extends BaseAdapter implements Filterable
    {
        private ArrayList<String> items;
        ArrayList<String> duplicate;
        private Context context;
        private LayoutInflater inflater;

        public customAdapter(ArrayList<String> items, Context context) {
            super();
            this.items = items;
            this.duplicate = items;
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            getFilter();
        }

        @Override


        public int getCount() {
            return duplicate.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_items,null);
            TextView txtsong=view.findViewById(R.id.txtSong);
            txtsong.setSelected(true);
            txtsong.setText(duplicate.get(position));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),PlayActivity.class)
                            .putExtra("songs",mySongs)
                            .putExtra("songname",txtsong.getText())
                            .putExtra("pos",items.indexOf(duplicate.get(position))));
//                            .putExtra("pos",index.get(position)));

                }
            });
            return view;
        }



        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    duplicate=(ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<String> FilteredList= new ArrayList<String>();
                    if (constraint == null || constraint.length() == 0) {
                        // No filter implemented we return all the list
                        results.values = items;
                        results.count = items.size();
                    }
                    else {
                        for (int i = 0; i < items.size(); i++) {
                            String data = items.get(i);
                            if (data.toLowerCase().contains(constraint.toString()))  {
                                FilteredList.add(data);
//                                index.add(i);
                            }
                        }
                        results.values = FilteredList;
                        results.count = FilteredList.size();
                    }
                    return results;
                }
            };
            return filter;
        }
    }

}
