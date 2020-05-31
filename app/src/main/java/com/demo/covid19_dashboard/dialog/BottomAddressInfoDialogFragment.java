package com.demo.covid19_dashboard.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;

import com.demo.covid19_dashboard.R;
import com.demo.covid19_dashboard.databinding.LayoutBottomDialogBinding;
import com.demo.covid19_dashboard.listeners.OnItemClickListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomAddressInfoDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String DATA = "DATA";
    private LayoutBottomDialogBinding binding;
    private OnItemClickListener listener;
    private String address;

    public static BottomAddressInfoDialogFragment newInstance(String address) {
        BottomAddressInfoDialogFragment fragment = new BottomAddressInfoDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATA, address);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            address = getArguments().getString(DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_bottom_dialog, container, false);
        binding.setClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(v, null, 0);
        dismiss();
    }

}