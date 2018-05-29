package com.ismenglx.mycontacts;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ismenglx.mycontacts.bean.Contacts;
import com.ismenglx.mycontacts.util.EditContact;
import com.ismenglx.mycontacts.view.SelectPicPopupWindow;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by lenovo on 2018/5/13.
 */

public class ListItemActivity extends AppCompatActivity {
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    //自定义的弹出框类
    private SelectPicPopupWindow menuWindow;

    private Contacts contact = new Contacts();
    private ImageView iv_bg;
    private EditText et_zipcode;
    private EditText et_name;
    private EditText et_phonenumber;
    private EditText et_otherphone;
    private EditText et_homenumber;
    private EditText et_Email;
    private EditText et_company;
    private EditText et_duty;
    private EditText et_group;
    private EditText et_im;
    private EditText et_note;
    private ImageButton fab_photo;
    private FloatingActionButton fab_edit;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private LinearLayout layout_edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        initContactContent();
        initViewItem();
        setEditTextEditable(false);
        setContent();
    }

    private void initContactContent() {
        ContentResolver resolver = this.getContentResolver();
        int id = -1;
        try{
            id = this.getIntent().getExtras().getInt("id");
        }catch (NullPointerException e){
            Toast.makeText(this, "不存在此联系人,请刷新后重试", LENGTH_LONG).show();
            this.finish();
        }
        contact.setId(id);
        //从一个Cursor获取所有的信息
        Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.CONTACT_ID + " = " + id, null, null);
        int i_mimetype = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        int i_data1 = cursor.getColumnIndex(ContactsContract.Data.DATA1);
        int i_data2 = cursor.getColumnIndex(ContactsContract.Data.DATA2);
//        int i_data3 = cursor.getColumnIndex(ContactsContract.Data.DATA3);
        int i_data4 = cursor.getColumnIndex(ContactsContract.Data.DATA4);
//        int i_data5 = cursor.getColumnIndex(ContactsContract.Data.DATA5);
        int i_photo_uri = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
        while (cursor.moveToNext()) {
            String mimetype = cursor.getString(i_mimetype);
            String value = cursor.getString(i_data1);
            if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                contact.setName(value);//姓名
            } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                contact.setEmail(value);//邮箱
            } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                //当为号码时
                if (ContactsContract.CommonDataKinds.Phone.TYPE_HOME == cursor.getInt(i_data2)) {
                    contact.setHome_number(value);
                } else {
                    if (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE == cursor.getInt(i_data2)){
                        contact.setPhone_number(value);
                    }else {
                        contact.setOther_phone(value);
                    }
                }
            } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) {
                contact.setZipCode(value);//邮编
            } else if (ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE.equals(mimetype)) {
                //当为头像时
                contact.setPhoto_uri(cursor.getString(i_photo_uri));
            } else if (ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE.equals(mimetype)) {
                contact.setGroup(value);//群组
            } else if (ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE.equals(mimetype)) {
                //备注
                contact.setNote(value);
            } else if (ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE.equals(mimetype)) {
                //System.out.println("聊天(QQ)账号="+value);
                contact.setIm(value);
            } else if (ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE.equals(mimetype)) {
                //公司/职位
                if (value != null) {
                    contact.setCompany(value);
                }
                if (cursor.getString(i_data4) != null) {
                    contact.setDuty(cursor.getString(i_data4));
                }
            }
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                fab_photo.setImageBitmap(bitmap);
                //TODO 保存照片
                ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                ContentValues values = new ContentValues();
                values.put(ContactsContract.CommonDataKinds.Photo.PHOTO,baos.toByteArray());
                ListItemActivity.this.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE, contact.getId() + ""});
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PHOTO_REQUEST_GALLERY && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//        }
    }

    private void initViewItem() {
        fab_edit = findViewById(R.id.fab_editContact);
        fab_photo = findViewById(R.id.floatingActionButton);
        et_name = findViewById(R.id.et_name);
        et_phonenumber = findViewById(R.id.et_phonenumber);
        et_otherphone = findViewById(R.id.et_otherphone);
        et_homenumber = findViewById(R.id.et_homenumber);
        et_company = findViewById(R.id.et_company);
        et_duty = findViewById(R.id.et_duty);
        et_Email = findViewById(R.id.et_Email);
        et_zipcode = findViewById(R.id.et_zipcode);
        et_note = findViewById(R.id.et_note);
        et_group = findViewById(R.id.et_group);
        et_im = findViewById(R.id.et_im);
        iv_bg = findViewById(R.id.imageView_bg);
        mCollapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        layout_edit = findViewById(R.id.layout_edit);
        Button btn_save = findViewById(R.id.btn_save);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        Toolbar mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.GREEN);//设置收缩后Toolbar上字体的颜色
        //====================================监听事件==============================
        fab_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //实例化SelectPicPopupWindow
                menuWindow = new SelectPicPopupWindow(ListItemActivity.this, new ItemsOnClick());
                //显示窗口
                menuWindow.showAtLocation(fab_photo, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditTextEditable(true);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改联系人信息
                new EditContact(ListItemActivity.this).editContact(contact.getId());
                setEditTextEditable(false);
                initContactContent();
                setContent();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditTextEditable(false);
                setContent();
            }
        });

    }

    //为弹出窗口实现监听类
    private class ItemsOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo://从相机获取
                    // 激活相机
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
                    startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
                    break;
                case R.id.btn_pick_photo://从相册获取
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    break;
                default:
                    break;
            }
        }
    }

    private void setContent() {
        if (contact.getPhoto_uri() != null) {
            Uri photo_uri = Uri.parse(contact.getPhoto_uri());
            //设置页面显示内容
            Picasso.with(this)
                    .load(photo_uri)
                    .transform(new BlurTransformation())
                    .into(iv_bg);
            fab_photo.setImageURI(photo_uri);
        }
        et_name.setText(contact.getName());
        if(contact.getName()==null){
            mCollapsingToolbarLayout.setTitle(getResources().getString(R.string.unknown_name));
        }else{
            mCollapsingToolbarLayout.setTitle(contact.getName());//联系人详细界面图片上显示的字
        }
        et_phonenumber.setText(contact.getPhone_number());
        et_otherphone.setText(contact.getOther_phone());
        et_homenumber.setText(contact.getHome_number());
        et_company.setText(contact.getCompany());
        et_duty.setText(contact.getDuty());
        et_Email.setText(contact.getEmail());
        et_zipcode.setText(contact.getZipCode());
        et_im.setText(contact.getIm());
        et_note.setText(contact.getNote());
        et_group.setText(contact.getGroup());
    }

    /*
      * 剪切图片
    */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

//    private Bitmap drawCircleView(Bitmap bitmap){
//        //前面同上，绘制图像分别需要bitmap，canvas，paint对象
//        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
//        Bitmap bm = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bm);
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        //这里需要先画出一个圆
//        canvas.drawCircle(100, 100, 100, paint);
//        //圆画好之后将画笔重置一下
//        paint.reset();
//        //设置图像合成模式，该模式为只在源图像和目标图像相交的地方绘制源图像
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, 0, 0, paint);
//        return bm;
//    }

    /**
     * 模糊转换
     */
    public class BlurTransformation implements Transformation {
        BlurTransformation() {
            super();
        }
        @Override
        public Bitmap transform(Bitmap bitmap) {
            return ListItemActivity.blurBitmap(getApplicationContext(), bitmap, 15f);
        }
        @Override
        public String key() {
            return "blur";
        }
    }

    /**
     * 图片模糊
     */
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float blur) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context.getApplicationContext());
        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        //Set the radius of the blur
        blurScript.setRadius(blur);
        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        //recycle the original bitmap
        bitmap.recycle();
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }

    private void setEditTextEditable(boolean flag) {
        et_zipcode.setEnabled(flag);
        et_name.setEnabled(flag);
        et_phonenumber.setEnabled(flag);
        et_otherphone.setEnabled(flag);
        et_homenumber.setEnabled(flag);
        et_Email.setEnabled(flag);
        et_company.setEnabled(flag);
        et_duty.setEnabled(flag);
        et_group.setEnabled(flag);
        et_note.setEnabled(flag);
        et_im.setEnabled(flag);

        if (flag) {
            layout_edit.setVisibility(View.VISIBLE);
            fab_edit.setVisibility(View.INVISIBLE);
        } else {
            layout_edit.setVisibility(View.INVISIBLE);
            fab_edit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_item, menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_send_email:
                Intent intent;
                if(!contact.getEmail().isEmpty()){
                    Uri uri = Uri.parse("mailto:"+contact.getEmail());
                    intent = new Intent(Intent.ACTION_SENDTO, uri);
                    startActivity(intent);
                }
                break;
            case R.id.action_send_desk:
                addShortcut();
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.action_delete_contact)
                        .setMessage("将要删除联系人 \"" + contact.getName() + "\"")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new EditContact(ListItemActivity.this).deleteContact(contact.getId());
                                ListItemActivity.this.finish();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                break;
            case R.id.action_call_phone:
                if(!contact.getOther_phone().isEmpty()&&!contact.getPhone_number().isEmpty()){
                    new AlertDialog.Builder(this)
                            .setMessage("选择向 \"" + contact.getName() + "\"拨打电话")
                            .setPositiveButton("手机号码", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse("tel:" + contact.getPhone_number());
                                    Intent intent = new Intent(Intent.ACTION_CALL,uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("其他号码",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse("tel:" + contact.getOther_phone());
                                    Intent intent = new Intent(Intent.ACTION_CALL,uri);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }else if(!contact.getPhone_number().isEmpty()){
                    Uri uri = Uri.parse("tel:" + contact.getPhone_number());
                    intent = new Intent(Intent.ACTION_CALL,uri);
                    startActivity(intent);
                }else if(!contact.getOther_phone().isEmpty()){
                    Uri uri = Uri.parse("tel:" + contact.getOther_phone());
                    intent = new Intent(Intent.ACTION_CALL,uri);
                    startActivity(intent);
                }
                break;
            case R.id.action_send_sms:
                if(!contact.getOther_phone().isEmpty()&&!contact.getPhone_number().isEmpty()){
                    new AlertDialog.Builder(this)
                            .setMessage("选择向 \"" + contact.getName() + "\"发送短信")
                            .setPositiveButton("手机号码", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("smsto:"+contact.getPhone_number()));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("其他号码",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("smsto:"+contact.getOther_phone()));
                                    startActivity(intent);
                                }
                            })
                            .show();
                }else if(!contact.getPhone_number().isEmpty()){
                    intent = new Intent(Intent.ACTION_VIEW,Uri.parse("smsto:"+contact.getPhone_number()));
                    startActivity(intent);
                }else if(!contact.getOther_phone().isEmpty()){
                    intent = new Intent(Intent.ACTION_VIEW,Uri.parse("smsto:"+contact.getOther_phone()));
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //发送快捷方式到桌面
    private void addShortcut() {
        //创建一个加入快捷方式的Intent
        Intent addSC = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷键的标题
        String title = contact.getName();
        //快捷键的图标
        Parcelable icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.default_con_pic);
        //创建单击快捷键启动本程序的Intent
        Intent launcherIntent = new Intent(this, ListItemActivity.class);
        launcherIntent.putExtra("id",contact.getId());
        addSC.putExtra("duplicate", false);
        //设置快捷键的标题
        addSC.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        //设置快捷键的图标
        addSC.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        //设置单击此快捷键启动的程序
        addSC.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
        //向系统发送加入快捷键的广播
        sendBroadcast(addSC);
        Toast.makeText(this,"添加快捷方式成功",Toast.LENGTH_SHORT).show();
    }
}
