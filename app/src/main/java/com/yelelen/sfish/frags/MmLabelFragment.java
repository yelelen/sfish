package com.yelelen.sfish.frags;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yelelen.sfish.Model.MmLabelModel;
import com.yelelen.sfish.R;
import com.yelelen.sfish.adapter.MmLabelAdapter;
import com.yelelen.sfish.adapter.RecyclerAdapter;
import com.yelelen.sfish.contract.LoadContent;
import com.yelelen.sfish.helper.Contant;
import com.yelelen.sfish.helper.MMLabelDbHelper;
import com.yelelen.sfish.presenter.MmLabelPresenter;
import com.yelelen.sfish.utils.SnackbarUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MmLabelFragment extends BottomSheetDialogFragment
        implements RecyclerAdapter.AdapterListener<MmLabelModel>,
        LoadContent<MmLabelModel> {
    private static MmLabelFragment mInstance;
    private MmLabelAdapter mAdapter;
    private MmLabelPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private int mLastVisibleItem;
    private GridLayoutManager mLayoutManager;
    private static final int mCount = 24;
    private static final String LABEL_URL = Contant.ES_URL + "mm/mmlabel/_search";

    public MmLabelFragment() {
        mPresenter = new MmLabelPresenter(LABEL_URL, new MMLabelDbHelper(), this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusbarBottomSheetDialog(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mm_label, container, false);

        mRecyclerView = root.findViewById(R.id.mm_label_recycler);
        mAdapter = new MmLabelAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(getContext(), 4);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mLastVisibleItem == mAdapter.getItemCount() - 1) {
                        mPresenter.loadMoreData(mCount);
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadMoreData(mCount);
    }

    public static void show(FragmentManager fm) {
        if (mInstance != null)
            mInstance.dismiss();
        mInstance = new MmLabelFragment();
        mInstance.show(fm, MmLabelFragment.class.getName());
    }

    public class TransStatusbarBottomSheetDialog extends BottomSheetDialog {
        public TransStatusbarBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusbarBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
            super(context, theme);
        }

        protected TransStatusbarBottomSheetDialog(@NonNull Context context, boolean cancelable,
                                                  DialogInterface.OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onStart() {
            final BottomSheetBehavior behavior = BottomSheetBehavior.from(mRecyclerView);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }

    @Override
    public void onItemClick(RecyclerAdapter.BaseViewHolder holder, MmLabelModel data) {
        // TODO
        MmFragment.getInstance().loadDataByLabel(10, data.getLabel().toLowerCase());
        dismiss();
    }

    @Override
    public void onItemLongClick(RecyclerAdapter.BaseViewHolder holder, MmLabelModel data) {

    }

    @Override
    public void onLoadDone(final List<MmLabelModel> t) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (t != null && t.size() > 0) {
                    mAdapter.add(t);
                }
            }
        });
    }

    @Override
    public void onLoadFailed(String reason) {
        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SnackbarUtil.showNetPrompt(getActivity(), getString(R.string.network_unavailable));
                dismiss();
            }
        });
    }

}
