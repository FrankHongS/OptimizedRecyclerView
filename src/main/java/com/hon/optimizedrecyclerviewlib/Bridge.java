package com.hon.optimizedrecyclerviewlib;

/**
 * Created by Frank on 2018/2/22.
 * E-mail:frank_hon@foxmail.com
 */

public interface Bridge {
    void doSomething(OptimizedRecyclerView host);

    class Loading implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.displayLoadingAndResetStatus();
        }
    }

    class Empty implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.displayEmptyAndResetStatus();
        }
    }

    class Content implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.displayContentAndResetStatus();
        }
    }

    class Error implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.displayErrorAndResetStatus();
        }
    }

    class NoMore implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.showNoMoreIfEnabled();
        }
    }

    class LoadMoreFailed implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.showLoadMoreFailedIfEnabled();
        }
    }

    class ResumeLoadMore implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.showResumeLoadMoreIfEnabled();
        }
    }

    class AutoLoadMore implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.showAutoLoadMoreIfEnabled();
        }
    }

    class ManualLoadMore implements Bridge{
        @Override
        public void doSomething(OptimizedRecyclerView host) {
            host.showManualLoadMoreIfEnabled();
        }
    }

    class SwipeConflicts implements Bridge{

        private boolean enabled;

        SwipeConflicts(boolean enabled){
            this.enabled=enabled;
        }

        @Override
        public void doSomething(OptimizedRecyclerView host) {

        }
    }
}
