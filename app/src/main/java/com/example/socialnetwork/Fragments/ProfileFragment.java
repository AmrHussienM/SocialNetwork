package com.example.socialnetwork.Fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialnetwork.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    StorageReference storageReference;

    String storagePath="Users_Profile_Cover_Img/";


    ImageView coverPhoto;
    CircleImageView imageProfile;
    TextView nameTv,emailTv,phoneTv;
    TextView univTv,businessTv;
    FloatingActionButton fab;
    ProgressDialog pd;

    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int STORAGE_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    public static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    String cameraPermssions[];
    String storagePermission[];
    Uri image_uri;
    String profileOrCoverPhoto;


    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);


        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Users");
        storageReference=getInstance().getReference();

        cameraPermssions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};



        imageProfile= view.findViewById(R.id.image_profile);
        nameTv=view.findViewById(R.id.name_profile);
        emailTv=view.findViewById(R.id.email_profile);
        phoneTv=view.findViewById(R.id.Phone_profile);
        univTv=view.findViewById(R.id.univ_profile);
        businessTv=view.findViewById(R.id.business_profile);
        coverPhoto=view.findViewById(R.id.background);
        fab=view.findViewById(R.id.fab);
        pd=new ProgressDialog(getActivity());


        Query query=reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Check for required Data,,
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    //get Data
                    String name=""+ds.child("name").getValue();
                    String email=""+ds.child("email").getValue();
                    String phone=""+ds.child("phone").getValue();
                    String image=""+ds.child("image").getValue();
                    String univ=""+ds.child("univeristy").getValue();
                    String business=""+ds.child("business").getValue();
                    String cover=""+ds.child("cover").getValue();

                    //Set Data
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    univTv.setText(univ);
                    businessTv.setText(business);

                    try
                    {
                        //if image is recieved then Set,,
                        Picasso.get().load(image).into(imageProfile);
                    }
                    catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.profile).into(imageProfile);
                    }

                    try
                    {
                        //if image is recieved then Set,,
                        Picasso.get().load(cover).into(coverPhoto);
                    }
                    catch (Exception e)
                    {
                        Picasso.get().load(R.color.colorPrimaryDark).into(coverPhoto);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditProfile();
            }
        });


        return view;
    }

    private boolean checkStoragePermission()
    {
        //check if storage permission is enabled or not
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission()
    {
        requestPermissions(storagePermission,STORAGE_REQUEST_CODE );
    }


    private boolean checkCameraPermission()
    {
        //check if storage permission is enabled or not
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);


        return result && result1;
    }

    private void requestCameraPermission()
    {
        requestPermissions(cameraPermssions,CAMERA_REQUEST_CODE );
    }

    private void ShowEditProfile()
    {
        String options[]={"Edit Profile Picture","Edit Cover Photo","Edit Name","Edit Phone"
                ,"Edit Univeristy Graduated","Edit Business"};

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle("Options");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    pd.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto="image";
                    showImagePicDialog();

                }
                else if (which == 1)
                {
                    pd.setMessage("Updating Cover Photo");
                    profileOrCoverPhoto="cover";
                    showImagePicDialog();

                }

                else if (which == 2)
                {
                    pd.setMessage("Change Name");
                    showNamePhoneUpdatingDialog("name");

                }

                else if (which == 3)
                {
                    pd.setMessage("Updating Phone");
                    showNamePhoneUpdatingDialog("phone");

                }

                else if (which == 4)
                {
                    pd.setMessage("Updating Univeristy Graduated");
                    showNamePhoneUpdatingDialog("univeristy");

                }

                else if (which == 5)
                {
                    pd.setMessage("Updating Business career");
                    showNamePhoneUpdatingDialog("business");

                }

            }
        });

        builder.create().show();
    }

    private void showNamePhoneUpdatingDialog(final String key)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+key);

        ConstraintLayout constraintLayout=new ConstraintLayout(getActivity());
        constraintLayout.setPadding(10,10,10,10);

        final EditText editText=new EditText(getActivity());
        editText.setHint("Enter "+key);
        constraintLayout.addView(editText);

        builder.setView(constraintLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String value=editText.getText().toString().trim();

                if (!TextUtils.isEmpty(value))
                {
                    pd.show();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put(key,value);

                    reference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "" +e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }
                else
                    {
                        Toast.makeText(getActivity(), "Please enter "+key, Toast.LENGTH_SHORT).show();
                    }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        builder.create().show();

    }

    private void showImagePicDialog()
    {

        String options[]={"Camera","Gallery"};

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setTitle("Pick Image");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    if (!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                        {
                            pickFromCamera();
                        }


                }
                else if (which == 1)
                {
                    if (!checkStoragePermission())
                    {
                        requestStoragePermission();
                    }
                    else
                        {
                            pickFromGallery();
                        }

                }

            }
        });

        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:
                {
                    if (grantResults.length >0)
                    {
                        boolean cameraAccepted=grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;

                        if (cameraAccepted && writeStorageAccepted)
                        {
                            pickFromCamera();
                        }else
                            {
                                Toast.makeText(getActivity(), "please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                            }
                    }
                }

            break;
            case STORAGE_REQUEST_CODE:
                {

                    if (grantResults.length >0)
                    {
                        boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;

                        if (writeStorageAccepted)
                        {
                            pickFromGallery();
                        }else
                        {
                            Toast.makeText(getActivity(), "please enable storage permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                break;
        }


        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);//
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK)
        {
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE)
            {
                image_uri=data.getData();

                uploadProfileCoverPhoto(image_uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE)
            {
                uploadProfileCoverPhoto(image_uri);

            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri)
    {
        pd.show();
        String filePathAndName=storagePath+ ""+profileOrCoverPhoto+"_"+user.getUid();

        StorageReference storageReference2nd=storageReference.child(filePathAndName);

        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri=uriTask.getResult();

                        if (uriTask.isSuccessful())
                        {
                            HashMap<String,Object> results=new HashMap<>();
                            results.put(profileOrCoverPhoto,downloadUri.toString());

                            reference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Image Updated...", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Error Updating Image..", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                        else
                            {
                                pd.dismiss();
                                Toast.makeText(getActivity(), "some error occured", Toast.LENGTH_SHORT).show();
                            }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void pickFromCamera()
    {
        //Intent of picking image from device camera
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"temp description");

        //Put Image Uri
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //Intent to Start Camera
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);


    }


    private void pickFromGallery()
    {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);
    }
}
