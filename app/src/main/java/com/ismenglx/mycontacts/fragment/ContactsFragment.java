package com.ismenglx.mycontacts.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.ismenglx.mycontacts.util.DividerDecoration;
import com.ismenglx.mycontacts.util.HanziToPinyin;
import com.ismenglx.mycontacts.ListItemActivity;
import com.ismenglx.mycontacts.R;
import com.ismenglx.mycontacts.adapter.ContactsListAdapter;
import com.ismenglx.mycontacts.bean.Contacts;
import com.thinkcool.circletextimageview.CircleTextImageView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2018/5/13.
 */
public class ContactsFragment extends Fragment implements OnQuickSideBarTouchListener {
    private static final int CONTACTS_PERMISSIONS_REQUEST = 1;
    private RecyclerView recyclerView;
    private QuickSideBarTipsView quickSideBarTipsView;
    private QuickSideBarView quickSideBarView;
    private ContactsListWithHeadersAdapter adapter;
    private HashMap<String, Integer> letters = new HashMap<>();
    private List<Contacts> contacts = new ArrayList<>();

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        quickSideBarTipsView = view.findViewById(R.id.quickSideBarTipsView);
        quickSideBarView = view.findViewById(R.id.quickSideBarView);
        //设置监听
        quickSideBarView.setOnQuickSideBarTouchListener(this);

        //设置列表数据和浮动header
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (contacts.size() > 0)
            return;
        checkPermission();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getContext().getContentResolver().registerContentObserver(ContactsContract.Data.CONTENT_URI,
                false, new ContactsObserver(getActivity(),new Handler()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initContent();
            } else {
                Toast.makeText(getActivity(), "Write Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkPermission() {
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkWriteContactPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_CONTACTS);
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CALL_PHONE);
            int checkSendSmsPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.SEND_SMS);
            int checkInstallShortcutPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.INSTALL_SHORTCUT);
            if (checkSendSmsPermission != PackageManager.PERMISSION_GRANTED ||
                    checkWriteContactPermission != PackageManager.PERMISSION_GRANTED ||
                    checkCallPhonePermission != PackageManager.PERMISSION_GRANTED ||
                    checkInstallShortcutPermission != PackageManager.PERMISSION_GRANTED) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.INSTALL_SHORTCUT},
                        CONTACTS_PERMISSIONS_REQUEST);
            } else {
                initContent();
            }
        } else {
            initContent();
        }
    }

    private void initContent() {
        //联系人主界面列表查询
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.SORT_KEY_PRIMARY,
                ContactsContract.Data.CONTACT_ID
        };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection,
                null, null, ContactsContract.Data.SORT_KEY_PRIMARY);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(0);
                    String sortKey = getPinYin(cursor.getString(1)).substring(0, 1);
                    int id = cursor.getInt(2);
                    Contacts contact = new Contacts();
                    contact.setName(name);
                    contact.setSortKey(sortKey);
                    contact.setId(id);
                    contacts.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        // Add the sticky headers decoration
        adapter = new ContactsListWithHeadersAdapter();
        ArrayList<String> customLetters = new ArrayList<>();
        int position = 0;
        for (Contacts contact : contacts) {
            String letter = contact.getSortKey();
            //如果没有这个key则加入并把位置也加入
            if (!letters.containsKey(letter)) {
                letters.put(letter, position);
                customLetters.add(letter);
            }
            position++;
        }
        //不自定义则默认26个字母
        quickSideBarView.setLetters(customLetters);
        adapter.addAll(contacts);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));
    }

    /**
     * 输入汉字返回拼音的通用方法函数
     *
     * @param hanzi 数据库中读取出的sort key
     * @return 拼音
     */
    private static String getPinYin(String hanzi) {
        if (hanzi == null || hanzi.matches("\\d*")) {
            return "#";
        }
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(hanzi);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (HanziToPinyin.Token.PINYIN == token.type) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        return sb.toString().toUpperCase();
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            recyclerView.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }

    public void filter(String newText) {
        Filter filter = adapter.getFilter();
        if (newText == null || newText.length() == 0) {
            filter.filter(null);
        } else {
            filter.filter(newText);
        }
    }

    /**
     * 实现联系人列表适配器----------------------------
     */
    private class ContactsListWithHeadersAdapter extends ContactsListAdapter<RecyclerView.ViewHolder>
            implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>, Filterable {
        private List<Map<String, String>> values = null;
        private MyFilter mFilter;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            View itemView = holder.itemView;
            TextView mName = itemView.findViewById(R.id.mName);
            CircleTextImageView mUserPhoto = itemView.findViewById(R.id.mUserPhoto);
            LinearLayout mBottomLayout = itemView.findViewById(R.id.mBottomLayout);
            //这里实现主界面点击联系人
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = getItem(position).getId();
                    Intent intent = new Intent(getActivity(), ListItemActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
            if (position < contacts.size() - 1) {
                if (getItem(position).getSortKey().equals(getItem(position + 1).getSortKey())) {
                    mBottomLayout.setVisibility(View.GONE);
                } else {
                    mBottomLayout.setVisibility(View.VISIBLE);
                }
            } else {
                mBottomLayout.setVisibility(View.GONE);
            }
            String name = getItem(position).getName();
            try {
                mName.setText(name);
                if (name.substring(name.length() - 1).equals("(") ||
                        name.substring(name.length() - 1).equals(")") ||
                        name.substring(name.length() - 1).equals("[") ||
                        name.substring(name.length() - 1).equals("]") ||
                        name.substring(name.length() - 1).equals("（") ||
                        name.substring(name.length() - 1).equals("）") ||
                        name.substring(name.length() - 1).equals("【") ||
                        name.substring(name.length() - 1).equals("】")) {
                    mUserPhoto.setText(name.substring(name.length() - 2, name.length() - 1));
                } else {
                    mUserPhoto.setText(name.substring(name.length() - 1));
                }
            } catch (Exception e) {
                mName.setText(R.string.unknown_name);
                mName.setTextColor(getResources().getColor(R.color.gray));
                mUserPhoto.setText(R.string.unknown_contact_icon);
            }
        }

        @Override
        public long getHeaderId(int position) {
            return getItem(position).getSortKey().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts_head, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            View itemView = holder.itemView;

            TextView mHead = itemView.findViewById(R.id.mHead);
            mHead.setText(String.valueOf(getItem(position).getSortKey()));

//            holder.itemView.setBackgroundColor(getRandomColor());
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new MyFilter();
            }
            return mFilter;
        }

        private class MyFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                if (prefix == null || prefix.length() == 0) {
                    List<Map<String, String>> list1 = new ArrayList<>(values);
                    results.values = list1;
                    results.count = list1.size();
                } else {
                    String prefixString = prefix.toString().toLowerCase();
                    List<Map<String, String>> values1 = values;
                    int count = values1.size();
                    List<Map<String, String>> newValues = new ArrayList<>(count);
                    for (Map<String, String> value : values1) {
                        String title = value.get("title").toLowerCase();
                        if (title.contains(prefixString)) {
                            newValues.add(value);
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
            }
        }
    }

    /**
     * 联系人的内容观察者。
     */
    private class ContactsObserver extends ContentObserver {
        private Context context;
        public ContactsObserver(Context context,Handler handler) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            contacts.clear();
            adapter.clear();
            letters.clear();
            //联系人主界面列表查询
            String[] projection = new String[]{
                    ContactsContract.Data.DISPLAY_NAME,
                    ContactsContract.Data.SORT_KEY_PRIMARY,
                    ContactsContract.Data.CONTACT_ID
            };
            Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection,
                    null, null, ContactsContract.Data.SORT_KEY_PRIMARY);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(0);
                        String sortKey = getPinYin(cursor.getString(1)).substring(0, 1);
                        int id = cursor.getInt(2);
                        Contacts contact = new Contacts();
                        contact.setName(name);
                        contact.setSortKey(sortKey);
                        contact.setId(id);
                        contacts.add(contact);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ArrayList<String> customLetters = new ArrayList<>();
            int position = 0;
            for (Contacts contact : contacts) {
                String letter = contact.getSortKey();
                //如果没有这个key则加入并把位置也加入
                if (!letters.containsKey(letter)) {
                    letters.put(letter, position);
                    customLetters.add(letter);
                }
                position++;
            }
            //不自定义则默认26个字母
            quickSideBarView.setLetters(customLetters);
            adapter.addAll(contacts);
        }
    }
}