package com.aby.note_quasars_android.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aby.note_quasars_android.R;
import com.aby.note_quasars_android.Utils.UIHelper;
import com.aby.note_quasars_android.database.Folder;
import com.aby.note_quasars_android.database.LocalCacheManager;
import com.aby.note_quasars_android.database.Note;
import com.aby.note_quasars_android.interfaces.EditNoteViewInterface;
import com.aby.note_quasars_android.interfaces.FolderListerInterface;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditableDetailViewActivity extends AppCompatActivity implements EditNoteViewInterface, FolderListerInterface, AdapterView.OnItemSelectedListener {

    private String NOTE_OBJECT_NAME = "NoteOBJECT";
    private Menu menu;

    List<Folder> folders;
    File photoFile;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;

    private boolean isEditMode = false;
    Note note;

    @BindView(R.id.tvNoteTitleDetail)
    EditText tvNoteTitleDetail;

    @BindView(R.id.tvNoteCreatedOnDetail)
    TextView tvNoteCreatedOnDetail;

    @BindView(R.id.folders_spinner)
    Spinner folderSpinner;

    @BindView(R.id.containerLinearLayout)
    LinearLayout containerLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editable_detail_view);
        ButterKnife.bind(this);

        // setup all views
        initialSetupViewsForDetail();
    }

    private void initialSetupViewsForDetail(){

        Intent intent = getIntent();
        if(intent.getSerializableExtra(NOTE_OBJECT_NAME) != null){
            note = (Note) intent.getSerializableExtra(NOTE_OBJECT_NAME);
            String titleTransitionName = intent.getStringExtra("transition_name");
            tvNoteTitleDetail.setTransitionName(titleTransitionName);
            tvNoteTitleDetail.setText(note.getTitle());
            tvNoteTitleDetail.setEnabled(false);
            tvNoteTitleDetail.setBackground(null);

            tvNoteCreatedOnDetail.setText(note.getCreatedOn().toString());

            this.folderSpinner.setEnabled(false);


            // prepare all other views after the title
            ArrayList<String> viewOrder = note.getViewOrders();

            ArrayList<String> texts = note.getTexts();
            ArrayList<String> imageURIs = note.getPhotos();
            ArrayList<String> soundURIs = note.getSounds();


            int textPosition= 1, imagePosition= 0,  soundPosition= 0;
            int currentChildPosition = 3;
            for(String viewType: viewOrder.subList(1,viewOrder.size())){
                if(viewType.equals("editText")){
                    EditText newEditText = UIHelper.getPreparedEditText(this);
                    newEditText.setEnabled(false);
                    newEditText.setText(texts.get(textPosition));
                    containerLinearLayout.addView(newEditText);
                    textPosition ++;
                    currentChildPosition ++;
                }
                else if(viewType.equals("imageView")){
                    UIHelper.addImageViewAt(currentChildPosition,
                            Uri.parse(imageURIs.get(imagePosition)),
                            containerLinearLayout,this,false);
                    currentChildPosition++;
                    imagePosition ++;
                }

            }

        }

        LocalCacheManager.getInstance(this)
                .getFolders(this);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_note,menu);
        this.menu = menu;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(!isEditMode){
            if(id == R.id.edit_note){
                setupForEdit();

            }

        }
        else{

            if(id == R.id.edit_note){
                setupForDetail();
                // save to database here
                String title = tvNoteTitleDetail.getText().toString();


                // set the id  of selected folder
                String folderName = folderSpinner.getSelectedItem().toString();
                int folderId = 0;
                for(Folder folder:  this.folders){
                    if(folder.getName().equals(folderName)){
                        folderId = folder.getId();
                    }
                }
                note.setParentFolder(folderId);

                // set all other texts


                ArrayList<String> texts = UIHelper.getAllTexts(containerLinearLayout);
                String note_text = String.join(" ",texts);


                ArrayList<String> viewOrder = UIHelper.getAllViewOrder(containerLinearLayout);
                viewOrder.remove(1);// remove createdOn
                viewOrder.remove(1); //  remove  folder

                ArrayList<String> imageURIs = UIHelper.getAllImageURIs(containerLinearLayout);


                ArrayList<String> soundURIs = UIHelper.getAllSoundURIs(containerLinearLayout);

                Note note = new Note(title,note_text, folderId,imageURIs,texts,viewOrder,soundURIs);
                note.setId(this.note.getId());
                LocalCacheManager.getInstance(this).updateNote(this,note);
            }

            else if(id == R.id.take_photo){
                dispatchTakePictureIntent();
            }
            else{

            }

        }


        return super.onOptionsItemSelected(item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.aby.note_quasars_android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    private void setupForEdit(){

        // enable views
        tvNoteTitleDetail.setEnabled(true);
        this.folderSpinner.setEnabled(true);

        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
            View v = containerLinearLayout.getChildAt(i);
            if (v instanceof EditText) {
                v.setEnabled(true);
            }
        }

        isEditMode = true;

        MenuItem editSaveItem = menu.findItem(R.id.edit_note);
        // set your desired icon here based on a flag if you like
        editSaveItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_save_24
        ));


    }



    private void setupForDetail(){

        // disable views edit permission
        tvNoteTitleDetail.setEnabled(false);
        this.folderSpinner.setEnabled(false);

        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
            View v = containerLinearLayout.getChildAt(i);
            if (v instanceof EditText) {
                v.setEnabled(false);
            }
        }

        isEditMode = false;
        // change menu icon back to edit
        MenuItem editSaveItem = menu.findItem(R.id.edit_note);
        // set your desired icon here based on a flag if you like
        editSaveItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24
        ));

    }

    @Override
    public void onNoteUpdated() {
        Toast.makeText(this,"Note Updated",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFoldersLoaded(List<Folder> folders) {

        this.folders =  folders;
        ArrayList<String> folderNames = new ArrayList<>();
        int currentFolderPosition = 0;
        int counter = 0;
        for(Folder folder: folders){
            folderNames.add(folder.getName());
            if(folder.getId() == this.note.getParentFolder()){
                currentFolderPosition =  counter;
            }
            counter ++;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,folderNames);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        folderSpinner.setAdapter(arrayAdapter);
        folderSpinner.setOnItemSelectedListener(this);
        folderSpinner.setSelection(currentFolderPosition);
    }

    @Override
    public void onFolderAdded() {

    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String folderName = adapterView.getItemAtPosition(i).toString();
        System.out.println(folderName);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private int getCurrentChildPosition(){
        View view = containerLinearLayout.getFocusedChild();
        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
            View v = containerLinearLayout.getChildAt(i);
            if(v == view){
                return i;
            }
        }
        return containerLinearLayout.getChildCount()-1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri photoURI = FileProvider.getUriForFile(this,
                "com.aby.note_quasars_android.fileprovider",
                photoFile);
//        addImageViewAt(getCurrentChildPosition() + 1, photoURI);

        UIHelper.addImageViewAt(getCurrentChildPosition()+1,photoURI,containerLinearLayout,this);

    }
}