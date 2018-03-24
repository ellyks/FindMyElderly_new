package com.findmyelderly.findmyelderly;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.findmyelderly.findmyelderly.Constant.Edit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditList extends Fragment implements View.OnClickListener{
    private RecyclerView mPostList;
    EditListAdapter adapter;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;

    private Query mQueryFollowingPost;
    private LinearLayoutManager mLayoutManager;

    private boolean mProcessAdd=false;
    private boolean mProcessLike=false;
    private DatabaseReference mDatabaseAdd;
    private Button back;
    private DatabaseReference mDatabaseLike;
    private List<String> mDatabaseAddedPost;
    private Toolbar myToolbar;
    private FragmentManager fragmentManager;
    public FragmentEditList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_editlist, container, false);
        back = (Button) rootview.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getActivity(), MainActivity_Family.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0,0);
            }
        });
        return rootview;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getting view
        mPostList=(RecyclerView)getView().findViewById(R.id.post_list);
        mPostList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPostList.setLayoutManager(mLayoutManager);

        //get database draft
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
        mQueryFollowingPost=mDatabase.orderByChild("familyId").equalTo(user.getUid());
        //get shopping cart and like
//        mDatabaseAdd=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("shoppingCart");
//        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("likePost");

        //filter post first
        setUpAdapter();

    }

    //mPostList.setAdapter(firebaseRecyclerAdapter);

    @Override
    public void onClick(View v) {

    }


    //send draft's id and draft to adapter
    public void setUpAdapter(){
        final List<Edit> addedEdit=new ArrayList<>();
        final List<String> addEditId=new ArrayList<>();

        mQueryFollowingPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addEditId.clear();
                addedEdit.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final String editId = child.getKey();
                    final Edit edit = child.getValue(Edit.class);

                    addEditId.add(editId);
                    addedEdit.add(edit);
                }
                // Create adapter passing in the sample user data
                fragmentManager=getActivity().getSupportFragmentManager();
                adapter = new EditListAdapter(getActivity(), addEditId,addedEdit,fragmentManager);
                // Attach the adapter to the recyclerview to populate items
                mPostList.setAdapter(adapter);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}