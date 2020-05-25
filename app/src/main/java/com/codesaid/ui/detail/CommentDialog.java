package com.codesaid.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.codesaid.R;
import com.codesaid.databinding.LayoutCommentDialogBinding;
import com.codesaid.lib_base.dialog.LoadingDialog;
import com.codesaid.lib_base.global.AppGlobals;
import com.codesaid.lib_base.util.FileUploadManager;
import com.codesaid.lib_base.util.FileUtils;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.callback.JsonCallback;
import com.codesaid.model.Comment;
import com.codesaid.ui.login.UserManager;
import com.codesaid.ui.publish.CaptureActivity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created By codesaid
 * On :2020-05-20 15:05
 * Package Name: com.codesaid.ui.detail
 * desc:
 */
public class CommentDialog extends DialogFragment implements View.OnClickListener {

    private static final String ADD_COMMENT = "/comment/addComment";

    private static final String KET_ITEM_ID = "itemId";

    private LayoutCommentDialogBinding mBinding;

    private long itemId;

    private CommentAddListener mCommentAddListener = null;
    private String mFilePath;
    private int mWidth;
    private int mHeight;
    private boolean isVideo;
    private LoadingDialog mLoadingDialog;
    private String fileUrl;
    private String coverUrl;

    public static CommentDialog newInstance(long itemId) {
        Bundle args = new Bundle();

        args.putLong(KET_ITEM_ID, itemId);

        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = LayoutCommentDialogBinding.inflate(inflater, container, false);

        mBinding.commentVideo.setOnClickListener(this);
        mBinding.commentDelete.setOnClickListener(this);
        mBinding.commentSend.setOnClickListener(this);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        itemId = getArguments().getLong(KET_ITEM_ID);

        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.comment_send) {

            postComment();

        } else if (v.getId() == R.id.comment_video) {
            CaptureActivity.startActivity(getActivity());
        } else if (v.getId() == R.id.comment_delete) {
            mFilePath = null;
            isVideo = false;
            mWidth = 0;
            mHeight = 0;
            mBinding.commentVideo.setImageDrawable(null);
            mBinding.commentExtLayout.setVisibility(View.GONE);

            mBinding.commentVideo.setEnabled(true);
            mBinding.commentVideo.setAlpha(100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mFilePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            mWidth = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            mHeight = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);

            if (!TextUtils.isEmpty(mFilePath)) {
                mBinding.commentExtLayout.setVisibility(View.VISIBLE);
                mBinding.commentCover.setImageUrl(mFilePath);

                if (isVideo) {
                    mBinding.commentIconVideo.setVisibility(View.VISIBLE);
                }
            }
            mBinding.commentVideo.setEnabled(false);
            mBinding.commentVideo.setAlpha(50);
        }
    }

    @SuppressWarnings("unchecked")
    private void postComment() {
        if (TextUtils.isEmpty(mBinding.inputView.getText())) {
            return;
        }

        if (isVideo && !TextUtils.isEmpty(mFilePath)) {
            FileUtils.generateVideoCover(mFilePath).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String coverPath) {
                    uploadFile(coverPath, mFilePath);
                }
            });
        } else if (!TextUtils.isEmpty(mFilePath)) {
            uploadFile(null, mFilePath);
        } else {
            publish();
        }
    }

    private void uploadFile(String coverPath, String filePath) {
        showLoadingDialog();
        AtomicInteger count = new AtomicInteger(1);
        if (!TextUtils.isEmpty(coverPath)) {
            count.set(2);
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    int remain = count.decrementAndGet();
                    coverUrl = FileUploadManager.upload(coverPath);
                    if (remain <= 0) {
                        if (!TextUtils.isEmpty(fileUrl) && !TextUtils.isEmpty(coverUrl)) {
                            publish();
                        } else {
                            dismissLoadingDialog();
                            showToast(getString(R.string.file_upload_failed));
                        }
                    }
                }
            });
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int remain = count.decrementAndGet();
                fileUrl = FileUploadManager.upload(filePath);
                if (remain <= 0) {
                    if (!TextUtils.isEmpty(fileUrl)
                            || !TextUtils.isEmpty(coverPath)
                            && !TextUtils.isEmpty(coverUrl)) {
                        publish();
                    }
                } else {
                    dismissLoadingDialog();
                    showToast(getString(R.string.file_upload_failed));
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void publish() {
        String content = mBinding.inputView.getText().toString();
        ApiService.post(ADD_COMMENT)
                .addParam("userId", UserManager.getInstance().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", content)
                .addParam("image_url", isVideo ? coverUrl : fileUrl)
                .addParam("video_url", fileUrl)
                .addParam("width", mWidth)
                .addParam("height", mHeight)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommentSuccess(response.body);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(ApiResponse<Comment> response) {
                        showToast("评论失败:" + response.message);
                        dismissLoadingDialog();
                    }
                });
    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(getContext());
        }
        mLoadingDialog.setLoadingText(getString(R.string.upload_text));
        mLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    private void onCommentSuccess(Comment comment) {
        showToast("评论添加成功");
        if (mCommentAddListener != null) {
            mCommentAddListener.onAddComment(comment);
        }
    }

    public interface CommentAddListener {
        void onAddComment(Comment comment);
    }

    public void setCommentAddListener(CommentAddListener commentAddListener) {
        this.mCommentAddListener = commentAddListener;
    }

    private void showToast(String s) {
        //showToast 几个可能会出现在异步线程调用
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(AppGlobals.getApplication(), s, Toast.LENGTH_SHORT).show();
        } else {
            ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AppGlobals.getApplication(), s, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
