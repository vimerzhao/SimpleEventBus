package top.vimerzhao.simpleevnetbus;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import top.vimerzhao.simpleevnetbus.handler.AsyncEventHandler;
import top.vimerzhao.simpleevnetbus.handler.DefaultEventHandler;
import top.vimerzhao.simpleevnetbus.handler.IEventHandler;
import top.vimerzhao.simpleevnetbus.handler.MainEventHandler;

/**
 * Created by vimerzhao on 18-12-23
 */
public class EventBus {
    private static EventBus sInstance;
    private final Map<EventType, CopyOnWriteArrayList<Subscription>> mSubscriptionsByEventtype = new ConcurrentHashMap<>();
    private EventDispatcher mEventDispatcher = new EventDispatcher();
    private ThreadLocal<Queue<EventType>> mThreadLocalEvents = new ThreadLocal<Queue<EventType>>() {
        @Override
        protected Queue<EventType> initialValue() {
            return new ConcurrentLinkedQueue<>();
        }
    };


    public EventBus() {
    }

    public static EventBus getDefault() {
        if (sInstance == null) {
            synchronized (EventBus.class) {
                if (sInstance == null) {
                    sInstance = new EventBus();
                }
            }
        }
        return sInstance;
    }

    public void register(Object subscriber) {
        if (subscriber == null) {
            return;
        }
        synchronized (this) {
            subscribe(subscriber);
        }
    }

    public void unregister(Object subscriber) {
        if (subscriber == null) {
            return;
        }
        Iterator<CopyOnWriteArrayList<Subscription>> iterator = mSubscriptionsByEventtype.values().iterator();
        while (iterator.hasNext()) {
            CopyOnWriteArrayList<Subscription> subscriptions = iterator.next();
            if (subscriptions != null) {
                List<Subscription> foundSubscriptions = new LinkedList<>();
                for (Subscription subscription : subscriptions) {
                    Object cacheObject = subscription.subscriber.get();
                    if (cacheObject == null || cacheObject.equals(subscriber)) {
                        foundSubscriptions.add(subscription);
                    }
                }
                subscriptions.removeAll(foundSubscriptions);
            }
            // 如果针对某个Event的订阅者数量为空了,那么需要从map中清除
            if (subscriptions == null || subscriptions.size() == 0) {
                iterator.remove();
            }
        }
    }

    private void subscribe(Object subscriber) {
        if (subscriber == null) {
            return;
        }

        // TODO 巨踏马难看的缩进
        Class<?> clazz = subscriber.getClass();
        while (clazz != null && !isSystemClass(clazz.getName())) {
            final Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if (annotation != null) {
                    Class<?>[] paramClassArray = method.getParameterTypes();
                    if (paramClassArray != null && paramClassArray.length == 1) {
                        Class<?> paramType = convertType(paramClassArray[0]);
                        EventType eventType = new EventType(paramType);
                        SubscriberMethod subscriberMethod = new SubscriberMethod(method, annotation.threadMode(), paramType);
                        realSubscribe(subscriber, subscriberMethod, eventType);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private void realSubscribe(Object subscriber, SubscriberMethod method, EventType eventType) {
        CopyOnWriteArrayList<Subscription> subscriptions = mSubscriptionsByEventtype.get(subscriber);
        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<>();
        }
        Subscription subscription = new Subscription(subscriber, method);
        if (subscriptions.contains(subscription)) {
            return;
        }
        subscriptions.add(subscription);
        mSubscriptionsByEventtype.put(eventType, subscriptions);
    }


    public void post(Object message) {
        if (message == null) {
            return;
        }

        mThreadLocalEvents.get().offer(new EventType(message.getClass()));
        mEventDispatcher.dispatchEvents(message);
    }

    // TODO 通用方法迁移到工具类
    private boolean isSystemClass(String name) {
        return name.startsWith("java.")
                || name.startsWith("javax.")
                || name.startsWith("android.");
    }

    // TODO 这里为什么要转换
    private Class<?> convertType(Class<?> paramType) {
        Class<?> returnClass = paramType;
        if (paramType.equals(boolean.class)) {
            returnClass = Boolean.class;
        } else if (paramType.equals(int.class)) {
            returnClass = Integer.class;
        } else if (paramType.equals(float.class)) {
            returnClass = Float.class;
        } else if (paramType.equals(double.class)) {
            returnClass = Double.class;
        }
        return returnClass;
    }

    private class EventDispatcher {
        private IEventHandler mMainEventHandler = new MainEventHandler();
        private IEventHandler mPostEventHandler = new DefaultEventHandler();
        private IEventHandler mAsyncEventHandler = new AsyncEventHandler();

        void dispatchEvents(Object message) {
            Queue<EventType> eventQueue = mThreadLocalEvents.get();
            while (eventQueue.size() > 0) {
                handleEvent(eventQueue.poll(), message);
            }
        }

        private void handleEvent(EventType eventType, Object message) {
            List<Subscription> subscriptions = mSubscriptionsByEventtype.get(eventType);
            if (subscriptions == null) {
                return;
            }
            for (Subscription subscription : subscriptions) {
                IEventHandler eventHandler =  getEventHandler(subscription.subscriberMethod.threadMode);
                eventHandler.handleEvent(subscription, message);
            }
        }

        private IEventHandler getEventHandler(ThreadMode mode) {
            if (mode == ThreadMode.ASYNC) {
                return mAsyncEventHandler;
            }
            if (mode == ThreadMode.POST) {
                return mPostEventHandler;
            }
            return mMainEventHandler;
        }
    }// end of the class
}
