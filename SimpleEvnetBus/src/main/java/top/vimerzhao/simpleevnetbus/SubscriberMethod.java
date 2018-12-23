package top.vimerzhao.simpleevnetbus;

import java.lang.reflect.Method;

/**
 * Created by vimerzhao on 18-12-23
 */
public class SubscriberMethod {
    public final Method method;
    final ThreadMode threadMode;
    final Class<?> paramType;

    // for effective
    String methodString;

    public SubscriberMethod(Method method, ThreadMode threadMode, Class<?> paramType) {
        this.method = method;
        this.threadMode = threadMode;
        this.paramType = paramType;
    }
}
