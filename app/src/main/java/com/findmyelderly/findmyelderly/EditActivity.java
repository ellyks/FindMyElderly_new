package com.findmyelderly.findmyelderly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String elderlyId="";
    private String tempName ="";
    private String tempTel ="";
    private String tempAddress="";
    private String tempFName ="";
    private String tempFTel ="";
    private float tempRadius=0;
    private String currentUserId;
    private com.google.firebase.database.Query mQueryMF;
    private String eEmail = "";
    private static String realeEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button submit = (Button) findViewById(R.id.button1);
        final EditText tempEdit   = (EditText)findViewById(R.id.editName);
        final EditText tempEdit2   = (EditText)findViewById(R.id.editTel);
        final EditText tempEdit3   = (EditText)findViewById(R.id.editAddress);

        final EditText tempEdit4   = (EditText)findViewById(R.id.editFName);
        final EditText tempEdit5   = (EditText)findViewById(R.id.editFTel);
        final EditText tempEdit6   = (EditText)findViewById(R.id.editRadius);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempName =  tempEdit.getText().toString();
                tempTel = tempEdit2.getText().toString();
                tempAddress = tempEdit3.getText().toString();
                tempFName=tempEdit4.getText().toString();
                tempFTel=tempEdit5.getText().toString();
                if(!tempEdit6.getText().toString().equals("")){
                    tempRadius=Float.parseFloat(tempEdit6.getText().toString());}
                eEmail = getElderlyEmail();
                editInfo(eEmail,tempName,tempTel,tempAddress,tempFName,tempFTel,tempRadius);
                startActivity(new Intent(EditActivity.this, MainActivity_Family.class));
    }
});
    }

    public static void setElderlyEmail(String email){
        realeEmail = email;
    }

    public String getElderlyEmail(){
        return realeEmail;
    }

    public static void editInfo(String email,String name,String tel,String address, String fname,String ftel,float radius) {

        //get the current userid
        final String elderlyemail = email;
        final String Aname = name;
        final String Atel = tel;
        final String Aaddress = address;
        final String Afname = fname;
        final String Aftel = ftel;
        final float Aradius = radius;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("email").equalTo(elderlyemail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String elderlyId =childSnapshot.getKey();
                    //save text in edittext into the firebase
                    if (!Aname.equals(""))
                        mDatabase.child("users").child(elderlyId).child("userName").setValue(Aname);
                    if (!Atel.equals(""))
                        mDatabase.child("users").child(elderlyId).child("Tel").setValue(Atel);
                    if (!Aaddress.equals(""))
                        mDatabase.child("users").child(elderlyId).child("address").setValue(Aaddress);
                    if (Aradius != 0.0f)
                        mDatabase.child("users").child(elderlyId).child("radius").setValue(Aradius);

                    if (!Afname.equals(""))
                        mDatabase.child("users").child(user.getUid()).child("userName").setValue(Afname);
                    if (!Aftel.equals(""))
                        mDatabase.child("users").child(user.getUid()).child("Tel").setValue(Aftel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}



