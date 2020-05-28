package com.codesaid.ui.publish;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codesaid.R;
import com.codesaid.databinding.ActivityPublishLayoutBinding;
import com.codesaid.lib_navannotation.ActivityDestination;
import com.codesaid.model.TagList;

/**
 * Created By codesaid
 * On :2020-05-28 14:45
 * Package Name: com.codesaid.ui.publish
 * desc:
 */

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPublishLayoutBinding mBinding;

    private int width, height;
    private String filePath, coverFilePath;
    private boolean isVideo;

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
