package com.katae.pad.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.katae.pad.R;

import java.io.File;

public class SelectPicActivity extends AppCompatActivity implements View.OnClickListener {

    /***
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /***
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    /***
     * 从Intent获取图片路径的KEY
     */
    public static final String KEY_PHOTO_PATH = "photo_path";
    private static final String TAG = "SelectPicActivity";

    private LinearLayout dialogLayout;
    private Button takePhotoBtn, pickPhotoBtn, clearPhotoBtn;
    private Button confirmBtn, cancelBtn;
    private ImageView previewImage;

    /**获取到的图片路径*/
    private String picPath;
    private Intent lastIntent;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);
        initView();
    }

    /**
     * 初始化加载View
     */
    private void initView() {
        dialogLayout = findViewById(R.id.dialog_layout);
        dialogLayout.setOnClickListener(this);
        takePhotoBtn = findViewById(R.id.btn_take_photo);
        takePhotoBtn.setOnClickListener(this);
        pickPhotoBtn = findViewById(R.id.btn_pick_photo);
        pickPhotoBtn.setOnClickListener(this);
        clearPhotoBtn = findViewById(R.id.btn_clear_photo);
        clearPhotoBtn.setOnClickListener(this);
        confirmBtn = findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(this);
        cancelBtn = findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        previewImage = findViewById(R.id.preview_image);

        lastIntent = getIntent();

        if(lastIntent.hasExtra(KEY_PHOTO_PATH)) {
            picPath = lastIntent.getStringExtra(KEY_PHOTO_PATH);
            photoUri = Uri.fromFile(new File(picPath));
            previewImage.setImageURI(photoUri);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                takePhoto();
                break;
            case R.id.btn_pick_photo:
                pickPhoto();
                break;
            case R.id.btn_clear_photo:
                lastIntent.putExtra(KEY_PHOTO_PATH, "");
                setResult(Activity.RESULT_OK, lastIntent);
                finish();
                break;
            case R.id.btn_confirm:
                lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
                setResult(Activity.RESULT_OK, lastIntent);
                finish();
                break;
            case R.id.dialog_layout:
            case R.id.btn_cancel:
            default:
                setResult(Activity.RESULT_OK, lastIntent);
                finish();
                break;
        }
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            /**-----------------*/
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }else{
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
        {
            doPhoto(requestCode,data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode,Intent data) {
        //从相册取图片，有些手机有异常情况，请注意
        if(requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            if(data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }

            photoUri = data.getData();
            if(photoUri == null ) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String[] pojo = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(photoUri, pojo, null, null,null);
        if(cursor != null ) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            cursor.close();
        }

        Log.i(TAG, "imagePath = " + picPath);
        if(picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG")
                || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
            //lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
            //setResult(Activity.RESULT_OK, lastIntent);
            previewImage.setImageURI(photoUri);
            //finish();
        }else{
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }

}
