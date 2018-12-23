package top.vimerzhao.simpleevnetbus;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by vimerzhao on 18-12-23
 */
public class Subscription {
    public final Reference<Object> subscriber;
    public final SubscriberMethod subscriberMethod;

    public Subscription(Object subscriber,
                        SubscriberMethod subscriberMethod) {
        this.subscriber = new WeakReference<>(subscriber);// EventBus3 没用弱引用?
        this.subscriberMethod = subscriberMethod;
    }

    @Override
    public int hashCode() {
        return subscriber.hashCode() + subscriberMethod.methodString.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Subscription) {
            Subscription other = (Subscription) obj;
            return subscriber == other.subscriber
                    && subscriberMethod.equals(other.subscriberMethod);
        } else {
            return false;
        }
    }
}
