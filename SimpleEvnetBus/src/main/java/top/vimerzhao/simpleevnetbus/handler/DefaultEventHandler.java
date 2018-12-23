package top.vimerzhao.simpleevnetbus.handler;

import java.lang.reflect.InvocationTargetException;

import top.vimerzhao.simpleevnetbus.Subscription;

/**
 * Created by vimerzhao on 18-12-23
 */
public class DefaultEventHandler implements IEventHandler {
    @Override
    public void handleEvent(Subscription subscription, Object message) {
        if (subscription == null || subscription.subscriber.get() == null) {
            return;
        }
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber.get(), message);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
