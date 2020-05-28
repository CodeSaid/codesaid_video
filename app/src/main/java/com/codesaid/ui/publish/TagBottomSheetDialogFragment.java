package com.codesaid.ui.publish;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codesaid.R;
import com.codesaid.lib_base.util.PixUtils;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.callback.JsonCallback;
import com.codesaid.model.TagList;
import com.codesaid.ui.login.UserManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By codesaid
 * On :2020-05-28 15:17
 * Package Name: com.codesaid.ui.publish
 * desc:
 */
public class TagBottomSheetDialogFragment extends BottomSheetDialogFragment {


    private RecyclerView mRecyclerView;
    private TagsAdapter mAdapter;

    private List<TagList> mTagLists = new ArrayList<>();
    private onTagItemSelectedListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_tag_bottom_sheet_dialog, null, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new TagsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        dialog.setContentView(view);

        ViewGroup parent = (ViewGroup) view.getParent();
        BottomSheetBehavior<ViewGroup> behavior = BottomSheetBehavior.from(parent);
        // 默认展开高度
        behavior.setPeekHeight(PixUtils.getScreenHeight() / 3);
        // 手指往下滑动时 高度 只会收缩到 默认高度
        behavior.setHideable(false);

        // 设置 展开最大高度
        ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
        layoutParams.height = PixUtils.getScreenHeight() / 3 * 2;
        parent.setLayoutParams(layoutParams);

        queryTagList();

        return dialog;
    }

    /**
     * 查询标签数据
     */
    @SuppressWarnings("unchecked")
    private void queryTagList() {
        ApiService.get("/tag/queryTagList")
                .addParam("userId", UserManager.getInstance().getUserId())
                .addParam("pageCount", 100)
                .addParam("tagId", 0)
                .execute(new JsonCallback<List<TagList>>() {
                    @Override
                    public void onSuccess(ApiResponse<List<TagList>> response) {
                        if (response.body != null) {
                            List<TagList> body = response.body;
                            mTagLists.clear();
                            mTagLists.addAll(body);
                            ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ApiResponse<List<TagList>> response) {
                        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    class TagsAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView text = new TextView(parent.getContext());
            text.setTextSize(13);
            text.setTypeface(Typeface.DEFAULT_BOLD);
            text.setGravity(Gravity.CENTER_VERTICAL);
            text.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.color_000));
            text.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixUtils.dp2pix(45)));

            RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(text) {

            };
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(mTagLists.get(position).title);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onSelectedText(mTagLists.get(position));
                        dismiss();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTagLists.size();
        }
    }

    public void setOnTagSelectedListener(onTagItemSelectedListener listener) {
        mListener = listener;
    }

    public interface onTagItemSelectedListener {
        void onSelectedText(TagList tag);
    }
}
