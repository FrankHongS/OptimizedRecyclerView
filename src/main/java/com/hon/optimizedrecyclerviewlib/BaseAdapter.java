package com.hon.optimizedrecyclerviewlib;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by Frank on 2018/2/22.
 * E-mail:frank_hon@foxmail.com
 */

public abstract class BaseAdapter<T extends ItemType,VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH>{

    private DataSetObservable<T> mDataSet;
    private RecyclerView mRecyclerView;

    public BaseAdapter(){
        mDataSet=new DataSetObservable<>();
    }

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("onCreateViewHolder", "parent is RecyclerView : "+(parent instanceof RecyclerView));
        VH viewHolder = createHeaderFooterViewHolder(parent, viewType);
        if (viewHolder != null)
            return viewHolder;
        else
            return onNewCreateViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        if (mDataSet.header.is(position)) {
            mDataSet.header.get(position).onBind();
        } else if (mDataSet.data.is(position)) {
            onNewBindViewHolder(holder, position);
        } else if (mDataSet.footer.is(position)) {
            mDataSet.footer.get(position).onBind();
        } else {
            mDataSet.extra.get(position).onBind();
        }
    }

    @Override
    public final int getItemViewType(int position) {
        if (mDataSet.header.is(position)) {
            return mDataSet.header.get(position).hashCode();
        } else if (mDataSet.data.is(position)) {
            return mDataSet.data.get(position).itemType();
        } else if (mDataSet.footer.is(position)) {
            return mDataSet.footer.get(position).hashCode();
        } else {
            return mDataSet.extra.get(position).hashCode();
        }
    }

    @Override
    public final int getItemCount() {
       return mDataSet.totalSize();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView=recyclerView;
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);

        int position=holder.getAdapterPosition();
        if (mRecyclerView.getScrollState() != SCROLL_STATE_IDLE)
            return;

        if (mDataSet.extra.size() == 0) {
            if (position == mDataSet.totalSize() - 1) {
                loadMore();
            }
        } else {
            if (position == mDataSet.totalSize() - 1 - mDataSet.extra.size()) {
                loadMore();
            }
        }
    }

    protected abstract VH onNewCreateViewHolder(ViewGroup parent, int viewType);

    protected abstract void onNewBindViewHolder(VH holder, int position);

    public T get(int position) {
        return mDataSet.data.get(position);
    }

    public void add(T item) {
        mDataSet.data.add(item);
        mDataSet.notifyContent();
        notifyDataSetChanged();
    }

    public void addAll(List<? extends T> data) {
        mDataSet.data.addAll(data);
        mDataSet.extra.clear();
        notifyDataSetChanged();

        if (mDataSet.totalSize() == 0) {
            mDataSet.notifyEmpty();
        } else {
            mDataSet.notifyContent();
            if (data.size() == 0) {
                mDataSet.notifyNoMore();
            }
        }
    }

    public void clear(){
        mDataSet.clear();
        notifyDataSetChanged();
    }

    /**
     *  add or remove extra view
     * @param resId  extra view's resId
     * @param enabled  If adding,true.Otherwise false.
     */
    void showExtra(int resId, boolean enabled) {
        if (mDataSet.extra.size() == 0) {
            if (enabled) {
                mDataSet.extra.add(new SectionItemImpl(resId));
                notifyItemInserted(mDataSet.extra.position());
            }
        } else {
                if (enabled) {
                    mDataSet.extra.set(mDataSet.extra.position(), new SectionItemImpl(resId));
                    notifyItemChanged(mDataSet.extra.position());
                }else{
                    int position = mDataSet.extra.position();
                    mDataSet.extra.remove(position);
                    notifyItemRemoved(position);
                }
            }
    }

    void registerObserver(Observer observer) {
        mDataSet.addObserver(observer);
    }

    private void loadMore() {
        new Handler(Looper.getMainLooper()).post(()->mDataSet.notifyAutoLoadMore());
    }

    @SuppressWarnings("unchecked")
    private VH createHeaderFooterViewHolder(ViewGroup parent, int viewType) {
        List<SectionItem> tempContainer = new ArrayList<>();
        tempContainer.addAll(mDataSet.header.getAll());
        tempContainer.addAll(mDataSet.footer.getAll());
        tempContainer.addAll(mDataSet.extra.getAll());

        for (SectionItem each : tempContainer) {
            if (each.hashCode() == viewType) {
                View view = each.createView(parent);
                return (VH) new SectionItemViewHolder(view);
            }
        }
        return null;
    }

    private class SectionItemViewHolder extends BaseViewHolder<T> {

        SectionItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(ItemType data,int position) {

        }
    }
}
