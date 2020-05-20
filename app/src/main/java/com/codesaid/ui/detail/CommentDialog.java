package com.codesaid.ui.detail;

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

import com.codesaid.R;
import com.codesaid.databinding.LayoutCommentDialogBinding;
import com.codesaid.lib_base.global.AppGlobals;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.callback.JsonCallback;
import com.codesaid.model.Comment;
import com.codesaid.ui.login.UserManager;

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

        } else if (v.getId() == R.id.comment_delete) {

        }
    }

    @SuppressWarnings("unchecked")
    private void postComment() {
        if (TextUtils.isEmpty(mBinding.inputView.getText())) {
            return;
        }

        String content = mBinding.inputView.getText().toString();
        ApiService.post(ADD_COMMENT)
                .addParam("userId", UserManager.getInstance().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", content)
                .addParam("image_url", null)
                .addParam("video_url", null)
                .addParam("width", 0)
                .addParam("height", 0)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommentSuccess(response.body);
                    }

                    @Override
                    public void onError(ApiResponse<Comment> response) {
                        showToast("评论失败:" + response.message);
                    }
                });
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
