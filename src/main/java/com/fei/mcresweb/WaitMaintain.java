package com.fei.mcresweb;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 等待任务的维护清理
 *
 * @author yuanlu
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WaitMaintain {

    /**
     * 延时监听元素
     *
     * @author yuanlu
     */
    @AllArgsConstructor
    private static abstract class Element implements Delayed {
        /**
         * 单位
         */
        private static final TimeUnit U = TimeUnit.MILLISECONDS;

        /**
         * 到期时间
         */
        long expire;
        /**
         * 清理监听
         */
        Runnable clearListener;

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(getDelay(U), o.getDelay(U));
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        /**
         * 处理
         */
        abstract void handle();

    }

    /**
     * 延时监听元素
     *
     * @author yuanlu
     */
    @Value
    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings("rawtypes")
    private static class MutiMapElement extends Element {

        /**
         * 图
         */
        Map map;

        /**
         * 键
         */
        Object k;

        /**
         * 值
         */
        Object old;

        /**
         * 读写锁
         */
        @NotNull Object locker;

        public MutiMapElement(long expire, Map map, Object k, Object old, @NotNull Object locker,
            Runnable clearListener) {
            super(expire, clearListener);
            this.map = map;
            this.k = k;
            this.old = old;
            this.locker = locker;
        }

        /**
         * 处理
         */
        @Override
        void handle() {
            boolean clear;
            synchronized (locker) {
                Collection c = (Collection)map.get(k);
                if (c == null)
                    return;
                clear = c.remove(old);
                if (c.isEmpty())
                    map.remove(k, c);
            }
            if (clear && clearListener != null)
                clearListener.run();
        }
    }

    /**
     * 队列
     */
    private static final DelayQueue<Element> QUEUE;

    static {
        QUEUE = new DelayQueue<>();
        new Thread("MRW-" + WaitMaintain.class) {
            @Override
            public void run() {
                while (true) {
                    try {
                        val ele = QUEUE.take();
                        if (ele != null)
                            ele.handle();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 将一对多键值对放入map,并设置最长超时时间, 超时后将被清理
     *
     * @param <K>           数据类型
     * @param <V>           数据类型
     * @param <L>           多值列表类型
     * @param map           图
     * @param k             键
     * @param v             值
     * @param maxTime       等待时长
     * @param builder       列表构造器
     * @param clearListener 清理监听
     * @return 如果此集合因调用而更改，则为true
     */
    public static <K, V, L extends Collection<V>> boolean add(@NonNull Map<K, L> map, @NonNull K k, @NonNull V v,
        long maxTime, @NonNull Supplier<L> builder, @NonNull Object locker, @Nullable Runnable clearListener) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (locker) {
            L c = map.get(k);
            if (c == null)
                map.put(k, c = builder.get());
            val r = c.add(v);
            QUEUE.add(new MutiMapElement(System.currentTimeMillis() + maxTime, map, k, v, locker, clearListener));
            return r;
        }
    }

}
