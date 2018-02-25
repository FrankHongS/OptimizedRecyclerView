package com.hon.optimizedrecyclerviewlib;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Frank on 2018/2/22.
 * E-mail:frank_hon@foxmail.com
 */

public interface SectionItem {
    View createView(ViewGroup parent);

    void onBind();
}
