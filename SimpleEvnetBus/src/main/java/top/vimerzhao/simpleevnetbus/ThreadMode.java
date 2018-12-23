package top.vimerzhao.simpleevnetbus;

public enum ThreadMode {
    MAIN, // 主线程
    POST, // 发送消息的线程
    ASYNC // 新开一个线程发送
}
