package top.vimerzhao.simpleevnetbus.handler;

import top.vimerzhao.simpleevnetbus.Subscription;

/**
 * Created by vimerzhao on 18-12-23
 */
public interface IEventHandler {
    void handleEvent(Subscription subscription, Object message);
}
