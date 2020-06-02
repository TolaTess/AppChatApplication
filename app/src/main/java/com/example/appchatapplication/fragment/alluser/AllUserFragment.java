package com.example.appchatapplication.fragment.alluser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatapplication.R;

public class AllUserFragment extends Fragment {
    private static final String TAG = "AllUserFragment";

    private RecyclerView mUserList;
    private AllUserPresenter allUserPresenter;

    public AllUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_user, container, false);
        // Inflate the layout for this fragment
        mUserList = view.findViewById(R.id.alluserfrag_recycler);
        mUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        allUserPresenter = new AllUserPresenterImpl(getContext(), mUserList);
        allUserPresenter.fetchAllUsers();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        allUserPresenter.getAdapter().startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        allUserPresenter.getAdapter().stopListening();
    }
}
