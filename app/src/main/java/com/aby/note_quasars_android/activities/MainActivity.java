package com.aby.note_quasars_android.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aby.note_quasars_android.database.Folder;
import com.aby.note_quasars_android.database.LocalCacheManager;
import com.aby.note_quasars_android.interfaces.DeleteNoteInterface;
import com.aby.note_quasars_android.interfaces.MainViewInterface;
import com.aby.note_quasars_android.database.Note;
import com.aby.note_quasars_android.adapters.NotesAdapter;
import com.aby.note_quasars_android.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Fade;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidx.core.view.ViewCompat.*;

public class MainActivity extends AppCompatActivity implements MainViewInterface, DeleteNoteInterface {

    private String NOTE_OBJECT_NAME = "NoteOBJECT";
    @BindView(R.id.rvNotes)
    RecyclerView rvNotes;
    Folder folder;

    LocationManager locationManager;
    LocationListener locationListener;

    NotesAdapter adapter;
    List<Note> notesList;
    String sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        initViews();
        loadNotes();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                LocalCacheManager.getInstance(MainActivity.this).deleteNote(MainActivity.this,adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this,"Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(rvNotes);


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }



    private void initViews() {

        Intent intent = getIntent();
        folder = (Folder) intent.getSerializableExtra(FolderListerActivity.FOLDER_OBJ_NAME);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadNotes(){

        //Call Method to get Notes
        LocalCacheManager.getInstance(this).getNotesInFolder(this, folder.getId());


    }

    @OnClick(R.id.fabAddNote)
    public void addNote(){
        Intent i = new Intent(MainActivity.this,AddNoteActivity.class);
        i.putExtra(FolderListerActivity.FOLDER_OBJ_NAME, folder);
        startActivity(i);
    }

    @Override
    public void onNotesLoaded(List<Note> notes) {

        if(sortBy!= null){
            if(sortBy.equals("Title")){
                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return (lhs.getTitle().toLowerCase().compareTo(rhs.getTitle().toLowerCase()) > 0) ? -1 :  0;
                    }
                });
            }

            else{
                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        return lhs.getCreatedOn().after(rhs.getCreatedOn()) ? -1 :  0;
                    }
                });
            }
        }

        notesList = notes;

        if(notesList.size() == 0){
            onDataNotAvailable();
        }else {
            adapter = new NotesAdapter(this, notes);
            adapter.setOnItemClickListner(new NotesAdapter.OnItemClickListner() {
                @Override
                public void onItemClick(Note note, View view) {
                    Intent intent = new Intent(MainActivity.this,EditableDetailViewActivity.class);
                    intent.putExtra(NOTE_OBJECT_NAME,note);
                    intent.putExtra("transition_name", getTransitionName(view));

                    ActivityOptionsCompat options;
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            MainActivity.this,
                            view,
                            Objects.requireNonNull(getTransitionName(view)));

                    startActivity(intent, options.toBundle());
                }
            });
            rvNotes.setAdapter(adapter);
        }
    }

    @Override
    public void onNoteAdded() {
        Toast.makeText(this,"Note Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataNotAvailable() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        SearchView searchView = (SearchView) menu.findItem( R.id.action_search).getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }


    @Override
    public void onNoteDeleted() {
        loadNotes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_sort){
            // Creating alert Dialog with one Button
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

            //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("Sort By");

            // Setting Dialog Message
            alertDialog.setMessage("Select Sorting Method");
            final EditText input = new EditText(MainActivity.this);


            final RadioButton[] rb = new RadioButton[2];
            RadioGroup rg = new RadioGroup(this); //create the RadioGroup
            rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
            rb[0]  = new RadioButton(this);
            rb[0].setText("Title");
            rg.addView(rb[0]);

            rb[1]  = new RadioButton(this);
            rb[1].setText("Date");
            rg.addView(rb[1]);


            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            rg.setLayoutParams(lp);
            alertDialog.setView(rg);
            //alertDialog.setView(input);



            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Sort",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            // Write your code here to execute after dialog
                            if(rb[0].isChecked()){
                                sortBy = "Title";
                            }
                            else{
                                sortBy = "Date";
                            }
                            loadNotes();
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog
                            dialog.cancel();
                        }
                    });

            // closed

            // Showing Alert Message
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }


}