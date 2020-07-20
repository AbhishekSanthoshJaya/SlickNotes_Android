package com.aby.note_quasars_android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.aby.note_quasars_android.database.Folder;
import com.aby.note_quasars_android.database.LocalCacheManager;
import com.aby.note_quasars_android.database.Note;
import com.aby.note_quasars_android.interfaces.AddNoteViewInterface;
import com.aby.note_quasars_android.R;
import com.aby.note_quasars_android.interfaces.EditNoteViewInterface;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNoteActivity extends AppCompatActivity implements AddNoteViewInterface {


    EditText etTitle;


    File photoFile;
    Folder folder;

    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;


    @BindView(R.id.containerLinearLayout)
    LinearLayout containerLinearLayout;

    // variables for layout
    ArrayList<String> texts;
    ArrayList<String> viewOrder;
    ArrayList<String> imageURIs;
    ArrayList<String> soundURIs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);


        folder = (Folder) getIntent().getSerializableExtra(FolderListerActivity.FOLDER_OBJ_NAME);

        setUpViews();
    }



    private EditText getPreparedEditText(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        EditText editText = new EditText(this);
        editText.setLayoutParams(layoutParams);
        editText.setBackground(null);
        return editText;
    }

    private void setUpViews(){

        // Common layout params
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Title edit text
        etTitle = getPreparedEditText();
        etTitle.setHint("Title");
        etTitle.setId(R.id.etTitle);
        etTitle.setTextSize(30.0f);

        containerLinearLayout.addView(etTitle);

        // first edit text for note detail
        EditText editText1 = getPreparedEditText();
        containerLinearLayout.addView(editText1);


    }

    private ArrayList<String> getAllTexts(){
        ArrayList<String> texts = new ArrayList<>();
        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
            View v = containerLinearLayout.getChildAt(i);
            if (v instanceof EditText) {
                texts.add(((EditText) v).getText().toString());
            } else {
                // nothing
            }
        }
        return  texts;
    }


    private ArrayList<String> getAllViewOrder(){
        ArrayList<String> viewOrder = new ArrayList<>();
        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
            View v = containerLinearLayout.getChildAt(i);
            if (v instanceof EditText) {
                viewOrder.add("editText");
            } else if (v instanceof ImageView) {
                viewOrder.add("imageView");
            }
            else{
                viewOrder.add("soundView");
            }
        }
        return  viewOrder;
    }

    private ArrayList<String> getAllImageURIs(){
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
            View v = containerLinearLayout.getChildAt(i);
            if (v instanceof ImageView) {
                images.add(v.getTag().toString());
            }
        }
        return  images;
    }

    private ArrayList<String> getAllSoundURIs(){
        ArrayList<String> sounds = new ArrayList<>();
//        for (int i = 0; i < containerLinearLayout.getChildCount(); i++) {
//            View v = containerLinearLayout.getChildAt(i);
//            if (v instanceof ImageView) {
//                images.add(v.getTag().toString());
//            }
//        }
        return  sounds;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveNote(){

        // get all texts, viewOrder, all photos, all sounds
        texts = getAllTexts();

        viewOrder = getAllViewOrder();

        imageURIs = getAllImageURIs();

        soundURIs = getAllSoundURIs();

        String title = etTitle.getText().toString();
        String note_text = String.join(" ",texts);

        if(title.equals("") || note_text.equals("")){
            showToast("Please fill all the fields before saving");
        }else
        {
            //Call Method to add note
            Note note = new Note(title,note_text, folder.getId(),imageURIs,texts,viewOrder,soundURIs);
            LocalCacheManager.getInstance(this)
                    .addNotes(this, note);
        }
    }


    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note,menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.save_note){
            saveNote();
        }
        else if(id == R.id.take_photo){

            dispatchTakePictureIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteAdded() {
        Toast.makeText(this,"Note Added",Toast.LENGTH_SHORT).show();

        finish();

    }

    @Override
    public void onDataNotAvailable() {
        Toast.makeText(this,"Could not add note",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
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


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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


    private void addImageViewAt(int position, Uri photoURI){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageURI(photoURI);
        imageView.setTag(photoURI);
        containerLinearLayout.addView(imageView,position);



        // add a following editText below new imageView
        EditText editText = getPreparedEditText();
        containerLinearLayout.addView(editText,position+1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri photoURI = FileProvider.getUriForFile(this,
                "com.aby.note_quasars_android.fileprovider",
                photoFile);
        addImageViewAt(getCurrentChildPosition() + 1, photoURI);
    }
}