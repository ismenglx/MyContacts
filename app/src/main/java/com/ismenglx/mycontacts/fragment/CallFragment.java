package com.ismenglx.mycontacts.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ismenglx.mycontacts.util.DividerDecoration;
import com.ismenglx.mycontacts.R;
import com.ismenglx.mycontacts.bean.RecordEntity;
import com.thinkcool.circletextimageview.CircleTextImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2018/5/13.
 */
public class CallFragment extends Fragment {
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private RecyclerView recyclerView;

    private List<RecordEntity> recordEntityList = new ArrayList<>();

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
//        CallLog.Calls.INCOMING_TYPE
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CallLogObserver callLogObserver = new CallLogObserver(new Handler(), getContext());
        getContext().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, callLogObserver);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recordEntityList.size() > 0)
            return;
        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initRecord();
            } else {
                Toast.makeText(getActivity(), "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initRecord() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CALL_LOG}, READ_CONTACTS_PERMISSIONS_REQUEST);
            return;
        }
        String[] projection = new String[]{
                CallLog.Calls.NUMBER, // 号码
                CallLog.Calls.CACHED_NAME,//姓名
                CallLog.Calls.DATE,   // 日期
                CallLog.Calls.TYPE,// 类型：来电、去电、未接
                CallLog.Calls.DURATION //呼叫时间
        };
        Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE + " desc");
        if(cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    RecordEntity recordEntity = new RecordEntity();
                    //号码
                    recordEntity.setNumber(cursor.getString(0));
                    //联系人
                    recordEntity.setName(cursor.getString(1));
                    //呼叫时间
                    SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(Long.parseLong(cursor.getString(2)));
                    recordEntity.setlDate(sfd.format(date));
                    //呼叫类型
                    recordEntity.setType(Integer.parseInt(cursor.getString(3)));
                    //通话时间,单位:s
                    recordEntity.setDuration(cursor.getString(4));
                    recordEntityList.add(recordEntity);
                } while (cursor.moveToNext());
            }
            cursor.close();
//        getActivity().startManagingCursor(cursor);//cursor托管给activity
        }
        //设置列表数据和浮动header
        final LinearLayoutManager layoutManager = new
                LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecordAdapter recordAdapter = new RecordAdapter();
        recyclerView.setAdapter(recordAdapter);
        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
    }

    /**
     * 拨号记录的内容观察者。
     */
    private class CallLogObserver extends ContentObserver {
        /**观察到记录改变后的处理方式*/
        /**查询某一个联系人最近的通话记录*/
        private ContentResolver resolver;
        CallLogObserver(Handler handler, Context context) {
            super(handler);
            resolver = context.getContentResolver();
        }
        @Override
        public void onChange(boolean selfChange) {
            String[] projection = new String[]{
                    CallLog.Calls.NUMBER, // 号码
                    CallLog.Calls.CACHED_NAME,//姓名
                    CallLog.Calls.DATE,   // 日期
                    CallLog.Calls.TYPE,// 类型：来电、去电、未接
                    CallLog.Calls.DURATION //呼叫时间
            };
            @SuppressLint("MissingPermission") Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE + " desc limit 1");
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    RecordEntity recordEntity = new RecordEntity();
                    //号码
                    recordEntity.setNumber(cursor.getString(0));
                    //联系人
                    recordEntity.setName(cursor.getString(1));
                    //呼叫时间
                    SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(Long.parseLong(cursor.getString(2)));
                    recordEntity.setlDate(sfd.format(date));
                    //呼叫类型
                    recordEntity.setType(Integer.parseInt(cursor.getString(3)));
                    //通话时间,单位:s
                    recordEntity.setDuration(cursor.getString(4));
                    recordEntityList.add(0,recordEntity);
                }
                cursor.close();
            }
            RecordAdapter recordAdapter = new RecordAdapter();
            recyclerView.setAdapter(recordAdapter);
//                    recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
        }
    }


    //----------------------RecordAdapter-------------------------------
    class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.adapter_call, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                if (recordEntityList.get(position).getName().isEmpty()) {
                    holder.mName.setVisibility(View.GONE);
                } else {
                    holder.mName.setVisibility(View.VISIBLE);
                    holder.mName.setText(recordEntityList.get(position).getName());
                }
            } catch (Exception e) {
                holder.mName.setVisibility(View.VISIBLE);
                holder.mName.setText(getResources().getString(R.string.unknown_call_log_name));
            }
            holder.mNumber.setText(recordEntityList.get(position).getNumber());

            if (recordEntityList.get(position).getType() == CallLog.Calls.INCOMING_TYPE) {
                //来电
                holder.mTime.setText(recordEntityList.get(position).getlDate()
                        + " 呼入" + recordEntityList.get(position).getDuration() + "秒");
                holder.mName.setTextColor(Color.BLUE);
                holder.mNumber.setTextColor(Color.BLUE);
            } else if (recordEntityList.get(position).getType() == CallLog.Calls.OUTGOING_TYPE) {
                //去电
                holder.mTime.setText(recordEntityList.get(position).getlDate()
                        + " 呼出" + recordEntityList.get(position).getDuration() + "秒");
                holder.mName.setTextColor(Color.GREEN);
                holder.mNumber.setTextColor(Color.GREEN);
            } else if (recordEntityList.get(position).getType() == CallLog.Calls.MISSED_TYPE) {
                //未接
                holder.mTime.setText(recordEntityList.get(position).getlDate());
                holder.mName.setTextColor(Color.RED);
                holder.mNumber.setTextColor(Color.RED);
            } else if (recordEntityList.get(position).getType() == CallLog.Calls.VOICEMAIL_TYPE) {
                //语音信箱
                holder.mTime.setText(recordEntityList.get(position).getlDate());
                holder.mNumber.setTextColor(Color.GRAY);
                holder.mName.setTextColor(Color.GRAY);
            }
            //点击通话记录，拨打电话
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("tel:" + recordEntityList.get(position).getNumber());
                    Intent intent = new Intent(Intent.ACTION_CALL, uri);
                    startActivity(intent);
                }
            });
            try {
                if (recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("(") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals(")") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("[") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("]") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("（") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("）") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("【") ||
                        recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1).equals("】")) {
                    holder.mUserPhoto.setText(recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 2, recordEntityList.get(position).getName().length() - 1));
                } else {
                    holder.mUserPhoto.setText(recordEntityList.get(position).getName().substring(recordEntityList.get(position).getName().length() - 1));
                }
            } catch (Exception e) {
                holder.mUserPhoto.setText(R.string.unknown_contact_icon);
            }

        }

        @Override
        public int getItemCount() {
            return recordEntityList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            LinearLayout mLayout;
            CircleTextImageView mUserPhoto;
            TextView mName;
            TextView mNumber;
            TextView mTime;
            private MyViewHolder(View view) {
                super(view);
                mLayout = view.findViewById(R.id.mLayout);
                mUserPhoto = view.findViewById(R.id.mUserPhoto);
                mName = view.findViewById(R.id.mName);
                mNumber = view.findViewById(R.id.mNumber);
                mTime = view.findViewById(R.id.mTime);
            }
        }
    }

    private void checkPermission() {
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CALL_LOG);
            int checkWriteLogPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_CALL_LOG);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED||checkWriteLogPermission!= PackageManager.PERMISSION_GRANTED) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG}, READ_CONTACTS_PERMISSIONS_REQUEST);
            } else {
                initRecord();
            }
        } else {
            initRecord();
        }
    }
}