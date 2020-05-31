package com.demo.covid19_dashboard.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import com.demo.covid19_dashboard.R;
import com.demo.covid19_dashboard.databinding.LayoutBottomDialogBinding;
import com.demo.covid19_dashboard.listeners.OnItemClickListener;

public class AddressInfoDialogFragment extends DialogFragment implements View.OnClickListener {

    private LayoutBottomDialogBinding binding;
    private OnItemClickListener<String> listener;
    public static final String DATA = "DATA";
    private String address;


    public static AddressInfoDialogFragment newInstance(String desc) {
        AddressInfoDialogFragment f = new AddressInfoDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(DATA, desc);
        f.setArguments(args);

        return f;
    }

    public void setListener(OnItemClickListener<String> listener) {
        this.listener = listener;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        address = getArguments().getString(DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_bottom_dialog, null, false);
        binding.setClickListener(this);
        binding.setAddress(address);
        setCancelable(false);
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        listener.onItemClick(view,address,0);
        dismiss();
    }
}