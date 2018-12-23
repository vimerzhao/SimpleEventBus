package top.vimerzhao.simpleevnetbus.handler;

import android.os.Handler;
import android.os.Looper;

import top.vimerzhao.simpleevnetbus.Subscription;

/**
 * Created by vimerzhao on 18-12-23
 */
public class MainEventHandler implements IEventHandler {
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    DefaultEventHandler hanlder = new DefaultEventHandler();

    @Override
    public void handleEvent(final Subscription subscription, final Object message) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                hanlder.handleEvent(subscription, message);
            }
        });

    }
}
