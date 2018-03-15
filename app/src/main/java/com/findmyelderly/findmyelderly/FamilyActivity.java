package com.findmyelderly.findmyelderly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


public class FamilyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //private String account;
    private String password;
    private StorageReference riversRef;
    private StorageReference mStorageRef;
    private TextInputLayout accoutLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout familyaccountLayout;
    private EditText accountEdit;
    private EditText familyaccountEdit;
    private EditText passwordEdit;
    private Button enterBtn;
    private Button checkBtn;
    private TextView Id;
    private FirebaseUser user;
    private String userId = "123";
    private String temp_familyId;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mDatabaseEdit = ref.child("edit").push();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        initView();
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //user = mAuth.getCurrentUser();
        //userId = user.getUid();
        accountEdit = (EditText) findViewById(R.id.account_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        familyaccountEdit = (EditText) findViewById(R.id.familyaccount_edit);
        familyaccountLayout = (TextInputLayout) findViewById(R.id.familyaccount_layout);
        accoutLayout = (TextInputLayout) findViewById(R.id.account_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
        passwordLayout.setErrorEnabled(true);
        accoutLayout.setErrorEnabled(true);
        //familyaccountLayout.setErrorEnabled(true);
        enterBtn = (Button) findViewById(R.id.enter_button);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String familyac = familyaccountEdit.getText().toString();
                final String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    accoutLayout.setError("Please enter e-mail");
                    passwordLayout.setError("");
                    return;
                }
                /*if (TextUtils.isEmpty(familyac)) {
                    familyaccountLayout.setError("Please enter family e-mail");
                    accoutLayout.setError("");
                    return;
                }*/
                if (TextUtils.isEmpty(password)) {
                    accoutLayout.setError("");
                    passwordLayout.setError("Please enter password");
                    return;
                }
                accoutLayout.setError("");
                //familyaccountEdit.setError("");
                passwordLayout.setError("");

                mAuth.createUserWithEmailAndPassword(account, password)
                        .addOnCompleteListener(FamilyActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    writeNewUser(account);
                                    user = mAuth.getCurrentUser();
                                    userId = user.getUid();
                                    mDatabase.child("users").orderByChild("email").equalTo(familyac).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                userId = childSnapshot.getKey();
                                            }


                                            mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("familyId").setValue(userId);
                                            mDatabase.child("users").child(userId).child("elderlyId").push().setValue(user.getUid());

                                            //mDatabase.child("edit").orderByChild("elderlyId").equalTo(SignUpActivity_Elderly.temp_elderlyId).addValueEventListener(new ValueEventListener() {
                                            //@Override
                                            //public void onDataChange(DataSnapshot dataSnapshot) {
                                            //   for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            //childSnapshot.child("familyId").setValue(userId);
                                            //  }
                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }

                                    });

                                    Intent intent = new Intent();
                                    intent.setClass(FamilyActivity.this, HomeActivity.class); //jump back to home page
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(FamilyActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                });


            }
        });


    }

    public static class User {

        private String email;
        private String userName;
        private String type;
        private String familyId;
        private boolean Help;
        private float radius;    //default geofencing radius
        private boolean outGeo; //for family geofencing notification
        private double latitude;
        private double longitude;
        private String dateTime;
        private Integer batteryLV;
        private String address;
        private String editId;


        public User(String email) {
            this.email = email;
            this.userName = "";
            this.type = "elderly";
            this.familyId = "";
            this.Help = false;
            this.outGeo = false;
            this.radius = 1609; //1 miles
            this.latitude = 22.316362;
            this.longitude = 114.180287;
            this.dateTime = "N/A";
            this.batteryLV = 100;
            this.address = "The Open University of Hong Kong";
            this.editId = "";

        }

        public String getEmail() {
            return email;
        }

        public String getType() {
            return type;
        }

        public String getUserName() {
            return userName;
        }

        public String getFamilyId() {
            return familyId;
        }

        public float getRadius() {
            return radius;
        }

        public boolean getHelp() {
            return Help;
        }

        public boolean getoutGeo() {
            return outGeo;
        }

        public String getDateTime() {
            return dateTime;
        }

        public Integer getBatteryLV() {
            return batteryLV;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getAddress() {
            return address;
        }

        public String getEditId() {
            return editId;
        }
    }



    private void writeNewUser(String email) {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //final String pushkey = mDatabaseEdit.getKey();
        SignUpActivity_Elderly.User user = new SignUpActivity_Elderly.User(email);
        //SignUpActivity_Elderly.Edit edit = new SignUpActivity_Elderly.Edit(email);
        Log.d("id", userId);
        mDatabase.child("users").child(userId).setValue(user);
        //mDatabase.child("edit").child(pushkey).setValue(edit);
        //mDatabase.child("edit").child(pushkey).child("editId").setValue(pushkey);
    }
}