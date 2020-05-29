package com.codesaid.ui.publish;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.codesaid.lib_base.util.FileUploadManager;

/**
 * Created By codesaid
 * On :2020-05-29 01:21
 * Package Name: com.codesaid.ui.publish
 * desc:
 */
public class UploadFileWorker extends Worker {

    public UploadFileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String filePath = inputData.getString("file");
        String fileUrl = FileUploadManager.upload(filePath);
        if (TextUtils.isEmpty(fileUrl)) {
            return Result.failure();
        } else {
            Data outputData = new Data.Builder()
                    .putString("fileUrl", fileUrl)
                    .build();
            return Result.success(outputData);
        }
    }
}
