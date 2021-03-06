package com.aau.ReportApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyActivity extends Activity {

    private String[] arraySpinner;
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;
    String timeStamp;
    Spinner spinner;
    EditText title;
    EditText description;
    CheckBox locationCheck;
    LocationManager locationManager;
    Location location;
    Issue newIssue;

    //Anders try
    URL connectURL;
    String responseString;
    String Title;
    String Description;
    byte[ ] dataToServer;
    FileInputStream fileInputStream = null;





    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.arraySpinner = new String[] {
                "Select subject", "Broken interior", "IT problem", "Vandalism", "Lighting", "Other"};
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        spinner.setAdapter(adapter);

        newIssue = new Issue();


        title = (EditText) findViewById(R.id.editText);
        description = (EditText) findViewById(R.id.editText2);
        locationCheck = (CheckBox) findViewById(R.id.checkBox);




    }
    public void pictureButton (View v){


        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
        }

        Uri outputFileUri = Uri.fromFile(photoFile);

            if (photoFile != null) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
            }

    }

    public void sendButton (View v){
        String locationString ="";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Issue created on "+timeStamp);
        if(locationCheck.isChecked()){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            }
            catch (Exception e){

            }
          //  newIssue.setLocation(location.toString());
          //  locationString = "\nLocation is: "+location.toString()+" with an accuracy of: "+location.getAccuracy();
        }

        if (spinner.getSelectedItem().equals("Select subject")){
            Toast.makeText(MyActivity.this, "You must select a subject", Toast.LENGTH_SHORT).show();
            return;
        }
        if(title.getText().length()<1){
            Toast.makeText(MyActivity.this, "You must write a title", Toast.LENGTH_SHORT).show();
            return;
        }
        if(description.getText().length()<5){
            Toast.makeText(MyActivity.this, "You must write a description of the issue", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            newIssue.setSubject(spinner.getSelectedItem().toString());
            newIssue.setDescription(description.getText().toString());
            newIssue.setTitle(title.getText().toString());
            newIssue.setIssuer("Test issuer");
            i.putExtra(Intent.EXTRA_TEXT   , "Subject: "+spinner.getSelectedItem().toString() +"\nTitle: "+title.getText().toString()
                    +"\nDescription: "+description.getText().toString()+locationString);
        }


        if(photoFile != null){
            i.putExtra(Intent.EXTRA_STREAM, mCurrentPhotoPath);
        }

        /*try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }*/
        UploadFile(photoFile);

    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        // photoFile.delete();


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file://" + image.getAbsolutePath();
        return image;
    }

    public void UploadFile(File fileToUpload){
        try {

            // Set your file path here
            FileInputStream fstrm = new FileInputStream(fileToUpload);

            // Set your server page url (and the file title/description)
            HttpFileUpload hfu = new HttpFileUpload("http://54.93.164.91/postNewIssue", newIssue.getTitle(),newIssue.getDescription(),"anders",newIssue.getSubject(),"location");

            hfu.Send_Now(fstrm);

        } catch (FileNotFoundException e) {
            // Error: File not found
        }
    }


}
