package com.ismenglx.mycontacts.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.Toast;
import com.ismenglx.mycontacts.R;
import com.ismenglx.mycontacts.bean.Contacts;

/**
 * Created by lenovo on 2018/5/16.
 */

public class EditContact {
    //联系人姓名
    private String name;
    //头像
    private String photo_uri;
    //手机号
    private String phone_number;
    private String other_phone;
    private String home_number;
    //公司
    private String company;
    //职位
    private String duty;
    //群组
    private String group;
    //邮箱
    private String email;
    //邮编
    private String zipCode;
    private String note;
    //即时通讯号码
    private String im;

    private Activity context;

    public EditContact(Activity context){
        this.context = context;
    }

    private Contacts getContactInfoByActivity(){
        EditText et_name = context.findViewById(R.id.et_name);
        EditText et_phonenumber = context.findViewById(R.id.et_phonenumber);
        EditText et_otherphone = context.findViewById(R.id.et_otherphone);
        EditText et_homenumber = context.findViewById(R.id.et_homenumber);
        EditText et_company = context.findViewById(R.id.et_company);
        EditText et_duty = context.findViewById(R.id.et_duty);
        EditText et_Email = context.findViewById(R.id.et_Email);
        EditText et_zipcode = context.findViewById(R.id.et_zipcode);
        EditText et_note = context.findViewById(R.id.et_note);
        EditText et_group = context.findViewById(R.id.et_group);
        EditText et_im = context.findViewById(R.id.et_im);
        Contacts contact = new Contacts();

        contact.setName(et_name.getText().toString());
        contact.setPhone_number(et_phonenumber.getText().toString());
        contact.setOther_phone(et_otherphone.getText().toString());
        contact.setHome_number(et_homenumber.getText().toString());
        contact.setCompany(et_company.getText().toString());
        contact.setDuty(et_duty.getText().toString());
        contact.setEmail(et_Email.getText().toString());
        contact.setZipCode(et_zipcode.getText().toString());
        contact.setNote(et_note.getText().toString());
        contact.setGroup(et_group.getText().toString());
        contact.setIm(et_im.getText().toString());

        return contact;
    }

    public int getAddContactId(){
        return addContact(getContactInfoByActivity());
    }

    // 添加联系人信息
    public int addContact(Contacts contact) {
        name = contact.getName();
        photo_uri = contact.getPhoto_uri();
        phone_number = contact.getPhone_number();
        other_phone = contact.getOther_phone();
        home_number = contact.getHome_number();
        company = contact.getCompany();
        duty = contact.getDuty();
        group = contact.getGroup();
        note = contact.getNote();
        email = contact.getEmail();
        zipCode = contact.getZipCode();
        im = contact.getIm();

        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();

        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        ContentResolver resolver = context.getContentResolver();

        // 向data表插入姓名数据
        if (name != ""||name!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }
        // 向data表插入手机号
        if (phone_number != ""||phone_number!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone_number);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }
        // 向data表插入其他号码
        if (other_phone != ""||other_phone!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, other_phone);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }
        // 向data表插入家庭电话
        if (home_number != ""||home_number!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, home_number);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }

        // 向data表插入Email数据
        if (email != ""||email!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Email.DATA, email);
            values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }

        // 向data表插入QQ数据
        if (im != ""||im!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Im.DATA, im);
            values.put(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }

        // 向data表插入公司,个人职位数据
        if (company != ""||company!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Organization.COMPANY, company);
            values.put(ContactsContract.CommonDataKinds.Organization.TITLE, duty);
            values.put(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }

        // 向data表插入邮编
        if (zipCode != ""||zipCode!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, zipCode);
            values.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }

        // 向data表插入备注
        if (note != ""||note!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }

        // 向data表插入群组
        if (group != ""||group!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, group);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }

        // TODO 向data表插入头像
        if (photo_uri != ""||photo_uri!=null)
        {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.Data.DATA1, photo_uri);
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }
        Toast.makeText(context, "保存联系人成功", Toast.LENGTH_SHORT).show();
        //获取联系人的contact_id
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Data.CONTACT_ID},
                ContactsContract.Data.RAW_CONTACT_ID + " = " + rawContactId, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }else{
            return -1;
        }
    }

    public void editContact(int id){
        Contacts contact = getContactInfoByActivity();
        name = contact.getName();
        photo_uri = contact.getPhoto_uri();
        phone_number = contact.getPhone_number();
        other_phone = contact.getOther_phone();
        home_number = contact.getHome_number();
        company = contact.getCompany();
        duty = contact.getDuty();
        group = contact.getGroup();
        note = contact.getNote();
        email = contact.getEmail();
        zipCode = contact.getZipCode();
        im = contact.getIm();

        ContentResolver resolver = context.getContentResolver();
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        if (name != ""||name!=null) {
            //更新联系人姓名
            values.clear();
            values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + "= ?", new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id + ""});
        }
        if (phone_number != ""||phone_number!=null) {
            //更新电话号码
            values.clear();
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone_number);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.CommonDataKinds.Phone.TYPE + "= ? and " + ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + "", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, id + ""});
        }
        if (home_number != ""||home_number!=null) {
            //更新家庭电话号码
            values.clear();
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, home_number);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.CommonDataKinds.Phone.TYPE + "= ? and " + ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Phone.TYPE_HOME + "", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, id + ""});
        }
        if (other_phone != ""||other_phone!=null) {
            //更新其他电话号码
            values.clear();
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, other_phone);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.CommonDataKinds.Phone.TYPE + "= ? and " + ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Phone.TYPE_OTHER + "", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, id + ""});
        }
        if (email != ""||email!=null) {
            //更新Email
            values.clear();
            values.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email);
            values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, id + ""});
        }
        if (im != ""||im!=null) {
            //更新Im
            values.clear();
            values.put(ContactsContract.CommonDataKinds.Im.DATA, im);
            values.put(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE, id + ""});
        }
        //更新company,职位
            values.clear();
            values.put(ContactsContract.CommonDataKinds.Organization.COMPANY, company);
            values.put(ContactsContract.CommonDataKinds.Organization.TITLE, duty);
            values.put(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE, id + ""});
        if (zipCode != ""||zipCode!=null) {
            //更新邮编
            values.clear();
            values.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, zipCode);
            values.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE, id + ""});
        }
        if (note != ""||note!=null) {
            //更新备注
            values.clear();
            values.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE, id + ""});
        }
        if (group != ""||group!=null) {
            //更新群组
            values.clear();
            values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, group);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE, id + ""});
        }
        if (photo_uri != ""||photo_uri!=null) {
            values.clear();
            values.put(ContactsContract.Data.DATA1, photo_uri);
            resolver.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE, id + ""});
        }
        Toast.makeText(context, "修改联系人成功", Toast.LENGTH_SHORT).show();
    }

    // 删除联系人
    public void deleteContact(int id) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,new String[]{ContactsContract.RawContacts._ID}, ContactsContract.Data.CONTACT_ID+"=?",new String[]{id+""}, null );
        if(cursor.moveToFirst()){
            int raw_id = cursor.getInt(0);
            context.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, "_id=?",new String[]{id+""});
            context.getContentResolver().delete(ContactsContract.Contacts.CONTENT_URI, "raw_contact_id=?",new String[]{raw_id+""});
            cursor.close();
        }
        Toast.makeText(context,"删除联系人成功",Toast.LENGTH_SHORT).show();
    }

}
