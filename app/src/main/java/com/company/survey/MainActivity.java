package com.company.survey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri mNewProfileImageUri;

    private StorageReference profilePictureFolderStorageReference;
    private DatabaseReference userDataFolderReference;


    private StorageTask mUploadTask;
    private ProgressBar mUpdateProfileDataProgressBar;
    ImageView mProfilePicturePreviewImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProfilePicturePreviewImageView = findViewById(R.id.imagev);
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
            final StorageReference ref = profilePictureFolderStorageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + getFileExtension(mNewProfileImageUri));

            mUploadTask = ref.putFile(mNewProfileImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mUpdateProfileDataProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            Log.d("UpdateProfile", "Picture uploaded to Firebase Storage");

                            String profilePictureUrlAtCloud;
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profilePictureUrlAtCloud = uri.toString();

                                    //String firstName = mFirstNameEditText.getText().toString().trim();
                                    //String lastName = mLastNameEditText.getText().toString().trim();

//                                    userDataFolderReference.child("firstName").setValue(firstName);
//                                    userDataFolderReference.child("lastName").setValue(lastName);
//                                    userDataFolderReference.child("profilePictureLink").setValue(profilePictureUrlAtCloud);
//                                    userDataFolderReference.child("profileStatus").setValue(3);
                                }
                            });
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mUpdateProfileDataProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }
}
