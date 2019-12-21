package com.company.survey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements MainContract.view
{
    MainContract.presenter presenter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSION_REQUEST_CODE = 1000;

    private Uri mNewProfileImageUri;

    Double latitude,longitude,altitude;

    private StorageReference profilePictureFolderStorageReference = FirebaseStorage.getInstance().getReference();

    private DatabaseReference userDataFolderReference = FirebaseDatabase.getInstance().getReference("Issues");
    private DatabaseReference userDataFolderReference2 = FirebaseDatabase.getInstance().getReference("Reward");

    FusedLocationProviderClient client;

    private StorageTask mUploadTask;
    ImageView mProfilePicturePreviewImageView;
    Button mUploadImageButton;
    EditText mEmailEditText;
    Button mGetLocationButton;
    String finalURL;
    Button mValidateDataButton;
    Button mSubmitButton;
    TextView mLatitudeTV,mLongitudeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProfilePicturePreviewImageView = findViewById(R.id.imagev);
        mUploadImageButton = findViewById(R.id.form_upload_image_button);
        mValidateDataButton = findViewById(R.id.validate_data);
        mEmailEditText = findViewById(R.id.email);
        mGetLocationButton = findViewById(R.id.get_location);
        mSubmitButton = findViewById(R.id.submit);
        mLatitudeTV = findViewById(R.id.latitude);
        mLongitudeTV = findViewById(R.id.longitude);
        client= LocationServices.getFusedLocationProviderClient(this );
        presenter = new MainPresenter(this);

        mUploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        mValidateDataButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Loading", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                //Intent intent = new Intent(SplashActivity.this, HomePageActivity.class);
                Toast.makeText(MainActivity.this, "It is a valid photo.. Kuudos", Toast.LENGTH_SHORT).show();
            },3000);
        });

        mGetLocationButton.setOnClickListener(v -> {
            checkPermissionForLocation();
            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {return;}

            client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        /**
                         * Converts recieved cordinates into latitude and longitude*/
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        mLatitudeTV.setText(latitude+"");
                        mLongitudeTV.setText(longitude+"");
                    }
                }
            });
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            mNewProfileImageUri = data.getData();
            Picasso.get().load(mNewProfileImageUri).into(mProfilePicturePreviewImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadData() {
        if (mNewProfileImageUri != null) {
            final StorageReference ref = profilePictureFolderStorageReference.child(FirebaseAuth.getInstance()+ "." + getFileExtension(mNewProfileImageUri));

            mUploadTask = ref.putFile(mNewProfileImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(MainActivity.this, "Uploaded the photo", Toast.LENGTH_SHORT).show();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profilePictureUrlAtCloud = uri.toString();
                                    finalURL = profilePictureUrlAtCloud;

                                    FormModel model = new FormModel("rishabh",profilePictureUrlAtCloud, String.valueOf(latitude), String.valueOf(longitude), "Yes");

                                    userDataFolderReference.setValue(model);
                                    userDataFolderReference2.child("rishabh").setValue("1000");

                                }
                            });
                            onBackPressed();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkPermissionForLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_CODE);
        }

    }
}
