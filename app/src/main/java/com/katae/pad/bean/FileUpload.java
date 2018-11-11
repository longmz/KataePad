package com.katae.pad.bean;
import android.content.Context;
import android.content.Intent;

import com.katae.pad.R;
import com.katae.pad.activity.MainTaskActivity;

public class FileUpload {
    public final String mSuccess;
    public final String mFileId;

    public FileUpload(String success, String fileId) {
        this.mSuccess = success;
        this.mFileId = fileId;
    }
}