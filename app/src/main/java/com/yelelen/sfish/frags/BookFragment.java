package com.yelelen.sfish.frags;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.yelelen.sfish.R;
import com.yelelen.sfish.utils.Utils;
import com.yelelen.sfish.view.SearchBar;


public class BookFragment extends BaseFragment {
    private SearchBar searchBar;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_book;
    }

    @Override
    protected void initView(View root) {
        super.initView(root);
        searchBar = root.findViewById(R.id.searchbar);
        searchBar.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.showToast(getContext(), s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button expand = root.findViewById(R.id.btn_expand);
        Button collapse = root.findViewById(R.id.btn_collapse);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.expand(30, 200);
            }
        });

        collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.collapse(30, 200);
            }
        });
    }
}
