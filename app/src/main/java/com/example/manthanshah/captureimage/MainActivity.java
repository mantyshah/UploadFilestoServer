package com.example.manthanshah.captureimage;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    public static final int MY_REQUEST_CAMERA   = 10;
    public static final int MY_REQUEST_WRITE_CAMERA   = 11;
    public static final int CAPTURE_CAMERA   = 12;
    String imageName ;
    String type;
    public static final int MY_REQUEST_READ_GALLERY   = 13;
    public static final int MY_REQUEST_WRITE_GALLERY   = 14;
    public static final int MY_REQUEST_GALLERY   = 15;
    public static final int MY_REQUEST_PDF   = 16;
    byte[] imageInByte;
    ImageView viewImage;
    Button b, camera, gallery, selectpdf;
    RelativeLayout relativeLayout;
    Bitmap bitmap;
    public File filen = null;
    File mediaFile;
    TextView buttonselect;
    RelativeLayout loading;
    int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = (Button) findViewById(R.id.btnSelectPhoto);
        viewImage = (ImageView) findViewById(R.id.viewImage);
        camera = (Button) findViewById(R.id.CameraButton);
        gallery = (Button) findViewById(R.id.GalleryButton);
        selectpdf = (Button) findViewById(R.id.CancelButton);
        relativeLayout = (RelativeLayout) findViewById(R.id.Relative);
        buttonselect = (TextView) findViewById(R.id.buttonselect);
        loading = (RelativeLayout) findViewById(R.id.Loading);
        selectpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonselect.setText("0");
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_READ_GALLERY);
                } else {
                    checkPermissionRG();
                }
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_CAMERA);
                }
                else
                {
                    catchPhoto();
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonselect.setText("1");
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_READ_GALLERY);
                } else {
                    checkPermissionRG();
                }
            }
        });


    }
    private void checkPermissionWG(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_WRITE_GALLERY);
        } else {
            if(buttonselect.getText().equals("1")) {
                getPhotos();
            }
            else
            {
               getPDF();
            }
        }
    }
    private void getPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"),MY_REQUEST_PDF);
    }
    private void getPhotos() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),MY_REQUEST_GALLERY);
    }
    private void catchPhoto() {
        filen = getFile();
        if(filen!=null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                Uri photocUri = Uri.fromFile(filen);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photocUri);
                startActivityForResult(intent, CAPTURE_CAMERA);
            } catch (ActivityNotFoundException e) {

            }
        } else {
            Toast.makeText(MainActivity.this, "please check your sdcard status", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkPermissionCA(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CAMERA);
        } else {
            catchPhoto();
        }
    }
    private void checkPermissionRG(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_READ_GALLERY);
        } else {
            checkPermissionWG();
        }
    }

    public File getFile(){
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Pace/JPEG");
        imagesFolder.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //File fileDir = new File(Environment.getExternalStorageDirectory()
          //      + "/Android/data/"
            //    + getApplicationContext().getPackageName()
              //  + "/Files");

        if (!imagesFolder.exists()){
            if (!imagesFolder.mkdirs()){
                return null;
            }
        }

        mediaFile = new File(imagesFolder, "PACE_" + timeStamp + ".jpg");
        return mediaFile;
    }
    public File getPDFFile(){
        File PDFFolder = new File(Environment.getExternalStorageDirectory(), "Pace/PDF");
        PDFFolder.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //File fileDir = new File(Environment.getExternalStorageDirectory()
        //      + "/Android/data/"
        //    + getApplicationContext().getPackageName()
        //  + "/Files");

        if (!PDFFolder.exists()){
            if (!PDFFolder.mkdirs()){
                return null;
            }
        }

        mediaFile = new File(PDFFolder, "PACE_" + timeStamp + ".pdf");
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK){
            Log.e("msg", "photo not get");
            return;
        }

        switch (requestCode) {

            case CAPTURE_CAMERA:

                viewImage.setImageURI(Uri.parse("file:///" + mediaFile));
                relativeLayout.setVisibility(View.INVISIBLE);
                viewImage.setVisibility(View.VISIBLE);
                bitmap = ((BitmapDrawable) viewImage.getDrawable()).getBitmap();
                type = "Image";
                imageName = mediaFile.getName();
                loading.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), imageName , Toast.LENGTH_LONG).show();

                new MyTask().execute();

                break;


            case MY_REQUEST_GALLERY:
               try {

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    filen = getFile();

                    FileOutputStream fileOutputStream = new FileOutputStream(filen);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    }
                   fileOutputStream.close();
                   inputStream.close();
                   viewImage.setImageURI(Uri.parse("file:///" + filen));//fresco library
                   bitmap = ((BitmapDrawable) viewImage.getDrawable()).getBitmap();
                   imageName = filen.getName();
                   Toast.makeText(getApplicationContext(), imageName, Toast.LENGTH_LONG).show();
                   type = "Image";
                   loading.setVisibility(View.VISIBLE);
                   new MyTask().execute();

               } catch (Exception e) {

                    Log.e("", "Error while creating temp file", e);
                }
                break;
            case MY_REQUEST_PDF:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    filen = getPDFFile();

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    FileOutputStream fileOutputStream = new FileOutputStream(filen);
                    try {
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fileOutputStream.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        inputStream.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageName = filen.getName();
                type = "Pdf";
                loading.setVisibility(View.VISIBLE);
                new MyTask().execute();
                Toast.makeText(getApplicationContext(), "PDF selected", Toast.LENGTH_LONG).show();
                break;

        }
    }

    public void onRequestPermissionsResult (int requestCode, String[] permissions,  int[] grantResults)
    {

        switch (requestCode) {
            case MY_REQUEST_CAMERA:
                catchPhoto();
                break;
            case MY_REQUEST_WRITE_CAMERA:
                checkPermissionCA();
                break;
            case MY_REQUEST_READ_GALLERY:
                checkPermissionWG();
                break;
            case MY_REQUEST_WRITE_GALLERY:
                getPhotos();
                break;
            case MY_REQUEST_PDF:
                getPDF();
                break;
        }
    }

    class MyTask extends AsyncTask<String, String, MyTask.Wrapper> {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
        byte[] byteImagearray;
        Wrapper w = new Wrapper();
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Displays the progress bar for the first time.

        }




        @Override
        protected Wrapper doInBackground(String... strings) {
            //Create Webservice class object
            if(type.equals("Image")) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageInByte = baos.toByteArray();
                UploadWebservice com = new UploadWebservice();
                w = new Wrapper();
                w.result = com.UploadWebservice("ByteArrayToPicture", imageInByte, imageName,type);
                return w;
            }
            else
            {

                try {
                   byteImagearray =  loadFile(String.valueOf(filen));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                UploadWebservice com = new UploadWebservice();
                w = new Wrapper();
                w.result = com.UploadWebservice("ByteArrayToPicture", byteImagearray, imageName, type);
                return w;
            }

        }
        @Override
        protected void onPostExecute(Wrapper w) {

            super.onPostExecute(w);
            String discription = w.result;
            builder.setSmallIcon(R.mipmap.devlogo);
            builder.setContentTitle("UPLOAD STATUS");
            builder.setContentText(discription);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            Toast.makeText(getApplicationContext(), w.result, Toast.LENGTH_LONG).show();

            loading.setVisibility(View.INVISIBLE);



        }

        class Wrapper {
            String result;
        }
    }

    public static byte[] readFully(InputStream stream) throws IOException
    {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1)
        {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    public static byte[] loadFile(String sourcePath) throws IOException
    {
        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(sourcePath);
            return readFully(inputStream);
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
    }
}
