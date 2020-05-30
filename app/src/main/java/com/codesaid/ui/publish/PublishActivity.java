package com.codesaid.ui.publish;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.alibaba.fastjson.JSONObject;
import com.codesaid.R;
import com.codesaid.databinding.ActivityPublishLayoutBinding;
import com.codesaid.lib_base.dialog.LoadingDialog;
import com.codesaid.lib_base.util.FileUtils;
import com.codesaid.lib_navannotation.ActivityDestination;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.callback.JsonCallback;
import com.codesaid.model.Feed;
import com.codesaid.model.TagList;
import com.codesaid.ui.login.UserManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created By codesaid
 * On :2020-05-28 14:45
 * Package Name: com.codesaid.ui.publish
 * desc:
 */

@ActivityDestination(pageUrl = "main/tabs/publish")
public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPublishLayoutBinding mBinding;

    private int width, height;
    private String filePath, coverFilePath;
    private boolean isVideo;

    private String mCoverPath;
    private UUID mFileUploadUUID;
    private UUID mCoverUUID;
    private String coverUploadUrl;
    private String fileUploadUrl;

    private TagList mTagLists;
    private LoadingDialog mDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish_layout);
        mBinding.actionClose.setOnClickListener(this);
        mBinding.actionPublish.setOnClickListener(this);
        mBinding.actionDeleteFile.setOnClickListener(this);
        mBinding.actionAddTag.setOnClickListener(this);
        mBinding.actionAddFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_close:
                showExitDialog();
                break;
            case R.id.action_publish:
                publish();
                break;
            case R.id.action_delete_file:
                mBinding.actionAddFile.setVisibility(View.VISIBLE);
                mBinding.fileContainer.setVisibility(View.GONE);
                mBinding.cover.setImageDrawable(null);
                filePath = null;
                width = 0;
                height = 0;
                isVideo = false;
                break;
            case R.id.action_add_tag:
                TagBottomSheetDialogFragment tagBottomSheetDialogFragment = new TagBottomSheetDialogFragment();
                tagBottomSheetDialogFragment.setOnTagSelectedListener(new TagBottomSheetDialogFragment.onTagItemSelectedListener() {

                    @Override
                    public void onSelectedText(TagList tag) {
                        mTagLists = tag;
                        mBinding.actionAddTag.setText(tag.title);
                    }
                });
                tagBottomSheetDialogFragment.show(getSupportFragmentManager(), "tag_dialog");
                break;
            case R.id.action_add_file:
                CaptureActivity.startActivity(this);
                break;
        }
    }

    private void publish() {
        showLoadingDialog();
        List<OneTimeWorkRequest> workRequestList = new ArrayList<>();
        if (!TextUtils.isEmpty(filePath)) {
            if (isVideo) {
                FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String coverPath) {
                        mCoverPath = coverPath;

                        OneTimeWorkRequest request = getOneTimeWorkRequest(coverPath);
                        mCoverUUID = request.getId();
                        workRequestList.add(request);

                        enqueue(workRequestList);

                    }
                });
            }

            OneTimeWorkRequest request = getOneTimeWorkRequest(filePath);
            mFileUploadUUID = request.getId();
            workRequestList.add(request);
            if (!isVideo) {
                enqueue(workRequestList);
            }
        } else { // 无 文件上传
            publishFeed();
        }
    }

    private void enqueue(List<OneTimeWorkRequest> workRequestList) {
        WorkContinuation workContinuation = WorkManager.getInstance(PublishActivity.this)
                .beginWith(workRequestList);
        workContinuation.enqueue();

        workContinuation.getWorkInfosLiveData().observe(PublishActivity.this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                int completeCount = 0;
                for (WorkInfo workInfo : workInfos) {
                    WorkInfo.State state = workInfo.getState();
                    Data outputData = workInfo.getOutputData();
                    UUID id = workInfo.getId();
                    if (state == WorkInfo.State.FAILED) {
                        if (id.equals(mCoverUUID)) {
                            showToast(getString(R.string.file_upload_cover_message));
                        } else if (id.equals(mFileUploadUUID)) {
                            showToast(getString(R.string.file_upload_original_message));
                        }
                    } else if (state == WorkInfo.State.SUCCEEDED) {
                        String fileUrl = outputData.getString("fileUrl");
                        if (id.equals(mCoverUUID)) {
                            coverUploadUrl = fileUrl;
                        } else if (id.equals(mFileUploadUUID)) {
                            fileUploadUrl = fileUrl;
                        }
                        completeCount++;
                    }
                }
                if (completeCount >= workInfos.size()) {
                    publishFeed();
                }
            }
        });
    }

    private void publishFeed() {
        ApiService.post("/feeds/publish")
                .addParam("coverUrl", coverUploadUrl)
                .addParam("fileUrl", fileUploadUrl)
                .addParam("fileWidth", width)
                .addParam("fileHeight", height)
                .addParam("userId", UserManager.getInstance().getUserId())
                .addParam("tagId", mTagLists == null ? 0 : mTagLists.tagId)
                .addParam("tagTitle", mTagLists == null ? "" : mTagLists.title)
                .addParam("feedText", mBinding.inputView.getText().toString())
                .addParam("feedType", isVideo ? Feed.TYPE_VIDEO : Feed.TYPE_IMAGE_TEXT)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        showToast(getString(R.string.feed_publisj_success));
                        PublishActivity.this.finish();
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                        dismissLoadingDialog();
                    }
                });
    }

    private void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new LoadingDialog(this);
            mDialog.setLoadingText(getString(R.string.feed_publish_ing));
        }
        mDialog.show();
    }

    private void dismissLoadingDialog() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                }
            });
        }
    }

    private void showToast(String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PublishActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressLint("RestrictedApi")
    @NotNull
    private OneTimeWorkRequest getOneTimeWorkRequest(String filePath) {
        Data inputData = new Data.Builder()
                .putString("file", filePath)
                .build();

        //        @SuppressLint("RestrictedApi") Constraints constraints = new Constraints();
        //        //设备存储空间充足的时候 才能执行 ,>15%
        //        constraints.setRequiresStorageNotLow(true);
        //        //必须在执行的网络条件下才能好执行,不计流量 ,wifi
        //        constraints.setRequiredNetworkType(NetworkType.UNMETERED);
        //        //设备的充电量充足的才能执行 >15%
        //        constraints.setRequiresBatteryNotLow(true);
        //        //只有设备在充电的情况下 才能允许执行
        //        constraints.setRequiresCharging(true);
        //        //只有设备在空闲的情况下才能被执行 比如息屏，cpu利用率不高
        //        constraints.setRequiresDeviceIdle(true);
        //        //workmanager利用contentObserver监控传递进来的这个uri对应的内容是否发生变化,当且仅当它发生变化了
        //        //我们的任务才会被触发执行，以下三个api是关联的
        //        constraints.setContentUriTriggers(null);
        //        //设置从content变化到被执行中间的延迟时间，如果在这期间。content发生了变化，延迟时间会被重新计算
        //这个content就是指 我们设置的setContentUriTriggers uri对应的内容
        //        constraints.setTriggerContentUpdateDelay(0);
        //        //设置从content变化到被执行中间的最大延迟时间
        //这个content就是指 我们设置的setContentUriTriggers uri对应的内容
        //        constraints.setTriggerMaxContentDelay(0);
        OneTimeWorkRequest request = new OneTimeWorkRequest
                .Builder(UploadFileWorker.class)
                .setInputData(inputData)
                //                .setConstraints(constraints)
                //                //设置一个拦截器，在任务执行之前 可以做一次拦截，去修改入参的数据然后返回新的数据交由worker使用
                //                .setInputMerger(null)
                //                //当一个任务被调度失败后，所要采取的重试策略，可以通过BackoffPolicy来执行具体的策略
                //                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                //                //任务被调度执行的延迟时间
                //                .setInitialDelay(10, TimeUnit.SECONDS)
                //                //设置该任务尝试执行的最大次数
                //                .setInitialRunAttemptCount(2)
                //                //设置这个任务开始执行的时间
                //                //System.currentTimeMillis()
                //                .setPeriodStartTime(0, TimeUnit.SECONDS)
                //                //指定该任务被调度的时间
                //                .setScheduleRequestedAt(0, TimeUnit.SECONDS)
                //                //当一个任务执行状态编程finish时，又没有后续的观察者来消费这个结果，难么workamnager会在
                //                //内存中保留一段时间的该任务的结果。超过这个时间，这个结果就会被存储到数据库中
                //                //下次想要查询该任务的结果时，会触发workmanager的数据库查询操作，可以通过uuid来查询任务的状态
                //                .keepResultsForAtLeast(10, TimeUnit.SECONDS)
                .build();
        return request;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CaptureActivity.REQUEST_CODE && data != null) {
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);

            showFileThumbnail();
        }
    }

    private void showFileThumbnail() {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        mBinding.actionAddFile.setVisibility(View.GONE);
        mBinding.fileContainer.setVisibility(View.VISIBLE);
        mBinding.cover.setImageUrl(filePath);
        mBinding.videoIcon.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        mBinding.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewActivity.startActivity(PublishActivity.this, filePath, isVideo, null);
            }
        });
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.publish_exit_message))
                .setNegativeButton(getString(R.string.publish_exit_action_cancel), null)
                .setPositiveButton(getString(R.string.publish_exit_action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PublishActivity.this.finish();
                    }
                })
                .create()
                .show();
    }
}
