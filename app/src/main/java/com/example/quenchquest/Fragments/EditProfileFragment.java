package com.example.quenchquest.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quenchquest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditProfileFragment extends Fragment {
    TextView editProfilePic;
    ImageView profilePictureImageView;
    BottomSheetDialog dialog;
    EditText displayNameET;
    String currentDisplayName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        editProfilePic = view.findViewById(R.id.txtViewEditPicture);
        profilePictureImageView = view.findViewById(R.id.imgProfilePic);
        displayNameET = view.findViewById(R.id.editTextName);

        if (user != null && user.getPhotoUrl() != null) {
            // Convert Uri to Bitmap and set it to ImageView
            Bitmap bitmap = getBitmapFromUri(user.getPhotoUrl());
            if (bitmap != null) {
                profilePictureImageView.setImageBitmap(bitmap);
            } else {
                // Set a default image if unable to load profile picture
                profilePictureImageView.setImageResource(R.drawable.prince);
            }
        } else {
            // Set a default image if no profile picture URL is available
            profilePictureImageView.setImageResource(R.drawable.prince);
        }

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        currentDisplayName = documentSnapshot.getString("displayName");
                        Log.d("TAG", "Retrieved display name from Firestore: " + currentDisplayName);
                        if (currentDisplayName != null && !currentDisplayName.isEmpty()) {
                            displayNameET.setText(currentDisplayName);
                        } else {
                            displayNameET.setText("Set display name");
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "Error retrieving display name from Firestore", e);
                }
            });
        }

        dialog = new BottomSheetDialog(this.getContext());
        createDialog();
        editProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        displayNameET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    String newDisplayName = displayNameET.getText().toString().trim();
                    if (!newDisplayName.isEmpty()) {
                        updateDisplayNameInAuth(newDisplayName);
                        currentDisplayName = newDisplayName;
                        displayNameET.setText(currentDisplayName);
                    }
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(displayNameET.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        return view;
    }
    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.profile_picturedialog, null, false);
        ImageView img1 = view.findViewById(R.id.img1);
        ImageView img2 = view.findViewById(R.id.img2);
        ImageView img3 = view.findViewById(R.id.img3);
        ImageView img4 = view.findViewById(R.id.img4);

        img1.setImageResource(R.drawable.cinderella);
        img2.setImageResource(R.drawable.prince);
        img3.setImageResource(R.drawable.spiderman);
        img4.setImageResource(R.drawable.gwen);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePictureImageView.setImageResource(R.drawable.cinderella);
                updateProfilePictureInAuth(R.drawable.cinderella);
                dialog.dismiss();
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePictureImageView.setImageResource(R.drawable.prince);
                updateProfilePictureInAuth(R.drawable.prince);
                dialog.dismiss();
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePictureImageView.setImageResource(R.drawable.spiderman);
                updateProfilePictureInAuth(R.drawable.spiderman);
                dialog.dismiss();
            }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePictureImageView.setImageResource(R.drawable.gwen);
                updateProfilePictureInAuth(R.drawable.gwen);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void updateProfilePictureInAuth(int drawableId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + drawableId);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "User profile picture updated successfully");
                        // Update the profile picture URL in Firestore
                        updateProfilePictureUrlInFirestore(user.getUid(), photoUri.toString());
                    } else {
                        Log.e("TAG", "Error updating user profile picture", task.getException());
                        // Handle the error
                    }
                });
    }
    private void updateProfilePictureUrlInFirestore(String userId, String photoUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .update("profilePictureUrl", photoUrl)
                .addOnSuccessListener(aVoid -> Log.d("TAG", "Profile picture URL updated in Firestore"))
                .addOnFailureListener(e -> Log.e("TAG", "Error updating profile picture URL in Firestore", e));
    }
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void updateDisplayNameInAuth(String newDisplayName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newDisplayName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User profile updated.");
                                // Update the display name in Firestore
                                updateDisplayNameInFirestore(newDisplayName);
                            }
                        }
                    });
        }
    }
    private void updateDisplayNameInFirestore(String newDisplayName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.update("displayName", newDisplayName)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully updated!");
                            displayNameET.setText(newDisplayName);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error updating document", e);
                        }
                    });
        }
    }

}
