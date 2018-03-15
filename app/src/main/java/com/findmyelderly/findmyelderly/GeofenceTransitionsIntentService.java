package com.findmyelderly.findmyelderly;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 2/3/2017.
 */

public class GeofenceTransitionsIntentService extends IntentService{
    protected static String name=GeofenceTransitionsIntentService.class.getSimpleName();
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mDatabaseGeo = ref.child("geo").push();

    private String familyid = "";
    private String usrname = "";
    private String currentUserId;




    public GeofenceTransitionsIntentService(){
        super(name);//Porque no hay un Constructor por default En IntentService
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        currentUserId = user.getUid();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent= GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String error= GeofenceErrorMessages.getErrorStr(this,geofencingEvent.getErrorCode());
            Log.e(name,error);
            return;
        }
        int geofenceTransition=geofencingEvent.getGeofenceTransition();

        if(geofenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER){
            //checkGeo();
        }
        if(geofenceTransition== Geofence.GEOFENCE_TRANSITION_EXIT) {

            checkGeo();
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences);
            sendNotification(geofenceTransitionDetails);
            Log.i(name, geofenceTransitionDetails);
        }else {
            Log.e(name,getString(R.string.unknown_geofence_error_transition_type)+geofenceTransition);


        }}


    public void checkGeo(){
        final String pushkey = mDatabaseGeo.getKey();
        HelpActivity.Geo geo = new HelpActivity.Geo();
        mDatabase.child("geo").child(pushkey).setValue(geo);
        mDatabase.child("users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                familyid = dataSnapshot.child("familyId").getValue(String.class);
                name = dataSnapshot.child("userName").getValue(String.class);

                mDatabase.child("geo").child(pushkey).child("familyId").setValue(familyid);
                mDatabase.child("geo").child(pushkey).child("username").setValue(name);
                mDatabase.child("geo").child(pushkey).child("outGeo").setValue(true);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private String getGeofenceTransitionDetails(Context context,int geofenceTransition,List<Geofence> triggeringGeofences){
        String geofenceTransitionString=getTransitionString(geofenceTransition);
        ArrayList triggeringGeofencesIdsList= new ArrayList();
        for (Geofence geofence:triggeringGeofences){//se define el tipo de objeto y para cada objeto en un Array se hace lo de aqui abajo  (objeto:array)
            triggeringGeofencesIdsList.add(geofence.getRequestId());

        }
        String triggeringGeofencesIdsStr= TextUtils.join(", ",triggeringGeofencesIdsList);
        return geofenceTransitionString+": "+triggeringGeofencesIdsStr;
    }
    private String getTransitionString(int transitionType){
        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "進入了圍欄";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "注意";
            default:
                return getString(R.string.unknown_geofence_error_transition);
        }
    }
    private void sendNotification(String notificationDetails){
        Intent notificationIntent=new Intent(getApplicationContext(),MainActivity.class);
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class).addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_clock).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_clock))
                .setColor(Color.RED).setContentTitle(notificationDetails).setContentText(getString((R.string.geofence_transition_notification))).setContentIntent(notificationPendingIntent);
        builder.setAutoCancel(true);
        NotificationManager mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,builder.build());

    }

}
