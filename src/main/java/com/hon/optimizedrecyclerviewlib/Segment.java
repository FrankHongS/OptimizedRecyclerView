package com.hon.optimizedrecyclerviewlib;

import java.util.List;

/**
 * Created by Frank on 2018/2/22.
 * E-mail:frank_hon@foxmail.com
 */

abstract class Segment<T> {
    abstract int size();

    abstract void clear();

    abstract void add(T item);

    abstract void addAll(List<? extends T> items);

    abstract List<T> getAll();

    abstract int positionImpl();

    abstract void insertImpl(int position,T item);

    abstract void insertAllImpl(int position,List<? extends T> items);

    abstract T getImpl(int position);

    abstract void setImpl(int position,T newItem);

    abstract void removeImpl(int position);

    abstract void removeImpl(T needRemove);

    // insert data

    final void insert(int adapterPosition, T item) {
        if (is(adapterPosition)) {
            insertImpl(adapterPosition - positionImpl(), item);
        } else {
            throw new IndexOutOfBoundsException("Insert error,  insert position");
        }
    }

    final void insertAll(int adapterPosition, List<? extends T> items) {
        if (is(adapterPosition)) {
            insertAllImpl(adapterPosition - positionImpl(), items);
        } else {
            throw new IndexOutOfBoundsException("Insert error, check your insert position");
        }
    }

    final void insertBack(int adapterPosition, T item) {
        if (is(adapterPosition)) {
            int insertPosition = adapterPosition - positionImpl() + 1;
            if (insertPosition == size()) {
                add(item);
            } else {
                insertImpl(insertPosition, item);
            }
        } else {
            throw new IndexOutOfBoundsException("Insert error, check your insert position");
        }
    }

    final boolean insertAllBack(int adapterPosition, List<? extends T> items) {
        if (is(adapterPosition)) {
            int insertPosition = adapterPosition - positionImpl() + 1;
            if (insertPosition == size()) {
                addAll(items);
            } else {
                insertAllImpl(insertPosition, items);
            }
            return true;
        } else {
            throw new IndexOutOfBoundsException("Insert error, check your insert position");
        }
    }

    final void removeAllBack(int adapterPosition, int removeSize) {
        if (is(adapterPosition)) {
            int removePosition = adapterPosition - positionImpl() + 1;
            for (int i = 0; i < removeSize; i++) {
                removeImpl(removePosition);
            }
        } else {
            throw new IndexOutOfBoundsException("Remove error, check your remove position");
        }
    }

    final T get(int adapterPosition) {
        if (is(adapterPosition)) {
            return getImpl(adapterPosition - positionImpl());
        } else {
            throw new NullPointerException();
        }
    }

    final int position() {
        if (size() == 0) return -1;
        return positionImpl();
    }

    final void set(int adapterPosition, T newItem) {
        if (is(adapterPosition)) {
            setImpl(adapterPosition - positionImpl(), newItem);
        } else {
            throw new IndexOutOfBoundsException("Set error, check your set position");
        }
    }

    final void remove(int adapterPosition) {
        if (is(adapterPosition)) {
            removeImpl(adapterPosition - positionImpl());
        } else {
            throw new IndexOutOfBoundsException("Remove error, check your remove position");
        }
    }

    final void remove(T needRemove) {
        if (size() != 0) {
            removeImpl(needRemove);
        } else {
            throw new IndexOutOfBoundsException("Remove error, check your remove position");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void swap(int fromAdapterPosition, int toAdapterPosition) {
        final List l = getAll();
        fromAdapterPosition = fromAdapterPosition - positionImpl();
        toAdapterPosition = toAdapterPosition - positionImpl();
        l.set(fromAdapterPosition, l.set(toAdapterPosition, l.get(fromAdapterPosition)));
    }

    final boolean is(int adapterPosition) {
        return adapterPosition >= 0 && size() > 0 && adapterPosition - positionImpl() < size()
                && adapterPosition - positionImpl() >= 0;
    }
}
