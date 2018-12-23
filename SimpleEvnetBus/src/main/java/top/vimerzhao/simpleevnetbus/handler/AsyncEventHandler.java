package top.vimerzhao.simpleevnetbus.handler;

import android.os.Handler;
import android.os.HandlerThread;

import top.vimerzhao.simpleevnetbus.Subscription;

/**
 * Created by vimerzhao on 18-12-23
 */
public class AsyncEventHandler implements IEventHandler {
    private DispatcherThread mDispatcherThread;
    private IEventHandler mEventHandler = new DefaultEventHandler();

    public AsyncEventHandler() {
        mDispatcherThread = new DispatcherThread(AsyncEventHandler.class.getSimpleName());
        mDispatcherThread.start();
    }

    @Override
    public void handleEvent(final Subscription subscription, final Object message) {
        mDispatcherThread.post(new Runnable() {
            @Override
            public void run() {
                mEventHandler.handleEvent(subscription, message);
            }
        });

    }

    private class DispatcherThread extends HandlerThread {

        // 关联了AsyncExecutor消息队列的Handler
        Handler mAsyncHandler;

        DispatcherThread(String name) {
            super(name);
        }

        public void post(Runnable runnable) {
            if (mAsyncHandler == null) {
                throw new NullPointerException("mAsyncHandler == null, please call start() first.");
            }
            mAsyncHandler.post(runnable);
        }

        @Override
        public synchronized void start() {
            super.start();
            mAsyncHandler = new Handler(this.getLooper());
        }

    }

}
