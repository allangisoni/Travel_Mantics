package com.example.android.travelmantics;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

import static android.provider.Settings.System.DATE_FORMAT;

public class TravelEntry extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnUpload)
    Button btnUpload;
    @BindView(R.id.ivtravel)
    ImageView ivtravel;
    @BindView(R.id.etcity)
    EditText etCity;
    @BindView(R.id.etamount)
    EditText etAmount;
    @BindView(R.id.etHotel)
    EditText etHotel;

    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    public static final String DATABASE_UPLOADS = "Travel Entries";
    private static final int MY_WRITESTORAGE_REQUEST_CODE = 100;
    public static final String STORAGE_PATH_UPLOADS = "imageuploads/";
    public static final int REQUEST_CODE_GALLERY = 0013;
    public static final String IMAGE_DIRECTORY = "Travel Mantics";
    private SimpleDateFormat dateFormatter;

    Uri travelImageUri;
    File file;

    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_entry);

        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Travel Mantics");
            ((TextView) toolbar.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.white));
        }

        FirebaseApp.initializeApp(getApplicationContext());



  //        mAuth = FirebaseAuth.getInstance();

  //      String currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child("Mantics");



        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }


        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
    }



    public void getImage(){

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TravelEntry.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITESTORAGE_REQUEST_CODE);

        } else {
            Matisse.from(TravelEntry.this)
                    .choose(MimeType.ofImage())
                    .countable(true)
                    .maxSelectable(1)
                    .theme(R.style.Matisse_Dracula)
                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new PicassoEngine())
                    .forResult(REQUEST_CODE_GALLERY);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_WRITESTORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Matisse.from(TravelEntry.this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(1)
                        .theme(R.style.Matisse_Dracula)
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new PicassoEngine())
                        .forResult(REQUEST_CODE_GALLERY);

            } else {

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {

            List<Uri> mSelected;
            mSelected = Matisse.obtainResult(data);

            for (Uri uri : mSelected) {

                File file = new File(Environment.getExternalStorageDirectory()
                        + "/" + IMAGE_DIRECTORY);
                if (!file.exists()) {
                    file.mkdirs();
                }
                travelImageUri = null;
                travelImageUri = uri;
                //fileImages.add(uriFile);
                Log.d("Matisse", "selected file: " + uri.getPath());
                File litfile;
                //litfile =file;

                File sourceFile = new File(getPathFromGooglePhotosUri(uri));
                Random random = new Random();
                int a = random.nextInt(100);
                File destFile = new File(file, "img_" + a + dateFormatter.format(new Date()).toString() + ".png");

                // fileImages.add(destFile);



                Log.d("TAG", "Source File Path :" + sourceFile);
                Log.d("TAG", "Destination File Path :" + destFile);

                try {
                    copyFile(sourceFile, destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }

                destFile.delete();

            }
            Picasso.with(getApplicationContext()).load(travelImageUri).placeholder(R.drawable.background_toast).error(R.drawable.error_toast).into(ivtravel);

        }
    }


    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(this);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        File imgcompressed = sourceFile;
        try {
            File compressedImageFile = new Compressor(this).compressToFile(sourceFile);
            imgcompressed = compressedImageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        source = new FileInputStream(imgcompressed).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }

        if (source.size() != 0) {
            source.close();
        }
        if (destination.size() != 0) {
            destination.close();
        }

    }


    public  void saveEntry() {
        if (TextUtils.isEmpty(etCity.getText().toString())) {
            //Toast.makeText(AddJournalEntry.this, "Journal title is empty", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(getApplicationContext(), "City name is empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
        } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
            // Toast.makeText(AddJournalEntry.this, "Journal message is empty", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(getApplicationContext(), "Amount is empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
        } else if (TextUtils.isEmpty(etHotel.getText().toString())) {
            // Toast.makeText(AddJournalEntry.this, "Journal message is empty", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(getApplicationContext(), "Hotel name is empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
        } else if (ivtravel.getDrawable() == null) {
            // Toast.makeText(AddJournalEntry.this, "Journal message is empty", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(getApplicationContext(), "Image is empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            //  progressDialog.setTitle("Saving Data");
            // progressDialog.show();

            progressDialog.setMessage("Uploading Data");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgress(0);
            progressDialog.show();

            //adding the file to reference
            DatabaseReference databaseReference = mDatabase.push();
            String refKey = databaseReference.push().getKey();

            File sourceFile = new File(getPathFromGooglePhotosUri(travelImageUri));
            Random random = new Random();
            int a = random.nextInt(100);
            File destFile = new File(file, "img_" + a + dateFormatter.format(new Date()).toString() + ".png");

            try {
                copyFile(sourceFile, destFile);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }

            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(travelImageUri));


            mUploadTask = sRef.putFile(Uri.fromFile(destFile));


            mUploadTask.addOnFailureListener(exception -> Log.i("It didn't work", "double check"))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Uri downloadUrl = uri;

                                    TravelManticsModel travelManticsModel = new TravelManticsModel();
                                    travelManticsModel.setCity(etCity.getText().toString().trim());
                                    travelManticsModel.setAmount(etAmount.getText().toString().trim());
                                    travelManticsModel.setHotel(etHotel.getText().toString().trim());
                                    travelManticsModel.setImageView(downloadUrl.toString());

                                    databaseReference.setValue(travelManticsModel);

                                    progressDialog.dismiss();
                                    TastyToast.makeText(getApplicationContext(), "Saved successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                    startActivity(new Intent(TravelEntry.this, MainActivity.class));
                                    finish();

                                    //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    TastyToast.makeText(getApplicationContext(), "Failed to save entry", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                }
                            });


                        }


                    });
        }
    }



    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_travel, menu);
        MenuItem menuItem = menu.findItem(R.id.item_save);

        if (menuItem != null) {
            tintMenuIcon(TravelEntry.this, menuItem);
        }

        return true;

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.item_save){
          saveEntry();
          return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  void tintMenuIcon(Context context, MenuItem item) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);

            DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(android.R.color.white));
            item.setIcon(wrapDrawable);

    }

}
