package com.hon.optimizedrecyclerviewlib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Frank on 2018/2/23.
 * E-mail:frank_hon@foxmail.com
 */

public class SectionItemImpl implements SectionItem{

    private int mResId;

    public SectionItemImpl() {
    }

    public SectionItemImpl(int resId) {
        this.mResId = resId;
    }

    @Override
    public View createView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(mResId,parent,false);
    }

    @Override
    public void onBind() {

    }
}
