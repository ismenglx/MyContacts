package com.ismenglx.mycontacts;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ismenglx.mycontacts.bean.Contacts;
import com.ismenglx.mycontacts.util.EditContact;
import com.ismenglx.mycontacts.view.SelectPicPopupWindow;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;

/**
 * Created by lenovo on 2018/5/13.
 */

public class AddNewActivity extends AppCompatActivity {
    private byte[] photobyte;
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    //自定义的弹出框类
    private SelectPicPopupWindow menuWindow;

    private Contacts contact = new Contacts();
    //    private FloatingActionButton fab_photo;
    private ImageButton fab_photo;
    private ImageView iv_bg;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        initViewItem();
        setContent();
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
                ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                photobyte = baos.toByteArray();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViewItem() {
        Button btn_save = findViewById(R.id.btn_save);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        iv_bg = findViewById(R.id.imageView_bg);
        fab_photo = findViewById(R.id.floatingActionButton);
        mCollapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
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
                menuWindow = new SelectPicPopupWindow(AddNewActivity.this, new ItemsOnClick());
                //显示窗口
                menuWindow.showAtLocation(fab_photo, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = new EditContact(AddNewActivity.this).getAddContactId();
                if (id != -1) {
                    if(photobyte!=null){
                        ContentValues values = new ContentValues();
                        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO,photobyte);
                        AddNewActivity.this.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.CONTACT_ID + " = ?", new String[]{ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE, id + ""});
                    }
                    Intent intent = new Intent(AddNewActivity.this, ListItemActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                } else {
                    Toast.makeText(AddNewActivity.this, "保存联系人失败", Toast.LENGTH_SHORT).show();
                }
                AddNewActivity.this.finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewActivity.this.finish();
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
        mCollapsingToolbarLayout.setTitle("联系人");//联系人添加图片上显示的字
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.about_us:
                Intent intent = new Intent(this, AboutSoftwareActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                //TODO 设置
                break;
            case R.id.action_delete_all:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.action_deleteAll)
                        .setMessage("将要清空所有联系人信息！！！确定吗？？？")
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null)
                        .setIcon(R.mipmap.ic_danger)
                        .show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
