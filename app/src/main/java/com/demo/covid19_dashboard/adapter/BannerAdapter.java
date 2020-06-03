package com.demo.covid19_dashboard.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.demo.covid19_dashboard.R;
import com.demo.covid19_dashboard.databinding.ListItemBannerBinding;

import java.util.ArrayList;
import java.util.List;


public class BannerAdapter extends PagerAdapter {

    private Activity mActivity;
    private ArrayList<Integer> mImageList;
    private LayoutInflater inflater;


    public BannerAdapter(Activity activity,ArrayList<Integer> mImageList) {
        mActivity = activity;
        this.mImageList = mImageList;
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ImageView ivBanner;

        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListItemBannerBinding binding = ListItemBannerBinding.inflate(inflater,container, false);

        container.addView(binding.getRoot());
        binding.imgDisplay.setImageResource(mImageList.get(position));
//        bindData(mImageList.get(position),binding);

        return binding.getRoot();
    }

    private void bindData(Integer model,ListItemBannerBinding binding){
        binding.setImage(model);
        binding.executePendingBindings();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    public void setAdvertisements(ArrayList<Integer> mImageList){
        this.mImageList = mImageList;
        notifyDataSetChanged();
    }
}
