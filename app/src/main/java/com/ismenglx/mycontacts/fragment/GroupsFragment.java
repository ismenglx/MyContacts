package com.ismenglx.mycontacts.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ismenglx.mycontacts.R;
import com.ismenglx.mycontacts.bean.GroupEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/5/13.
 * 群组
 */
public class GroupsFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<GroupEntity> groupList = new ArrayList();

    public static GroupsFragment newInstance() {
        return new GroupsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_page, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        groupList.clear();
        getAllGroupInfo();
    }

    public void getAllGroupInfo() {
        Cursor cursor = null;
        try {
            String[] projection = {
                    ContactsContract.Data.DATA1,
                    ContactsContract.Data.CONTACT_ID
            };
            cursor = getContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    projection, ContactsContract.Data.MIMETYPE + "=?", new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, ContactsContract.Data.DATA1);
            int id=0,j=1;
            GroupEntity groupEntity = new GroupEntity();
            while (cursor.moveToNext()) {
                int groupId = cursor.getInt(0); // 组id
                if(groupId!=0){
                    if(id!=groupId){
                        if(groupEntity.getGroupId()!=0)
                            groupList.add(groupEntity);
                        groupEntity = new GroupEntity();
                        id=groupId;
                        groupEntity.setGroupId(groupId);
                        groupEntity.setNumber(1);
                    }else{
                        groupEntity.setNumber(++j);
                    }
                }
            }
            groupList.add(groupEntity);
            GroupAdapter groupAdapter = new GroupAdapter();
            groupAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(groupAdapter);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //----------------------RecordAdapter-------------------------------
    private class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_groups, parent, false);
            return new RecyclerView.ViewHolder(view) {};
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            View itemView = holder.itemView;
            TextView mName = itemView.findViewById(R.id.mName);
            TextView mNumber = itemView.findViewById(R.id.mNumber);
            LinearLayout mLayout = itemView.findViewById(R.id.mLayout);
            if (groupList.size()==0) {
                mLayout.setVisibility(View.GONE);
            } else {
                mLayout.setVisibility(View.VISIBLE);
                int id = groupList.get(position).getGroupId();
                int number = groupList.get(position).getNumber();
                mName.setText("群组："+id);
                mNumber.setText("人数："+number);
            }
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 群组界面
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupList.size();
        }

    }
}