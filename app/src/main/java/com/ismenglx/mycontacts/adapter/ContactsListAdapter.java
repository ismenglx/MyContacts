package com.ismenglx.mycontacts.adapter;

/**
 * Created by lenovo on 2018/5/13.
 */

import android.support.v7.widget.RecyclerView;

import com.ismenglx.mycontacts.bean.Contacts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class ContactsListAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<Contacts> items = new ArrayList<>();

    public ContactsListAdapter() {
        setHasStableIds(true);
    }

    public void add(Contacts object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, Contacts object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Contacts> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(Contacts... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(String object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public Contacts getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
