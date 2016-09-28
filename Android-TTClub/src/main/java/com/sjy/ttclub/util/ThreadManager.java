/*
 * Copyright (c) 2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    // SAFE_STATIC_VAR
    static HandlerThread sBackgroundThread;
    // SAFE_STATIC_VAR
    static Handler sBackgroundHandler;
    // SAFE_STATIC_VAR
    static HandlerThread sWorkThread;
    // SAFE_STATIC_VAR
    static Handler sWorkHandler;
    // SAFE_STATIC_VAR
    static HandlerThread sNormalThread;
    // SAFE_STATIC_VAR
    static Handler sNormalHandler;
    // SAFE_STATIC_VAR
    final static int mThreadPoolSize = HardwareUtil.getCpuCoreCount() * 3 + 2;
    // SAFE_STATIC_VAR
    static ExecutorService mThreadPool = Executors
            .newFixedThreadPool(mThreadPoolSize);
    // SAFE_STATIC_VAR
    static Handler mMainThreadHandler;
    // SAFE_STATIC_VAR
    static HandlerThread sSharedPreferencesThread;
    // SAFE_STATIC_VAR
    static Handler sSharedPreferencesHandler;
    // SAFE_STATIC_VAR
    static Handler sMonitorHandler;

    static HashMap<Object, RunnableMap> mRunnableCache = new HashMap<Object, RunnableMap>();

    static final long RUNNABLE_TIME_OUT_TIME = 10 * 1000;

    // 后台线程优先级的线程
    public static final int THREAD_BACKGROUND = 0;
    // 介于后台和默认优先级之间的线程
    public static final int THREAD_WORK = 1;
    // 主线程
    public static final int THREAD_UI = 2;
    // 和主线程同优先级的线程
    public static final int THREAD_NORMAL = 3;
    // 只用于写SharedPreferences,不用它用
    public static final int THREAD_SHAREDPREFERENCES = 4;

    private ThreadManager() {
    }

    /**
     * 将Runnable放入线程池执行 默认为后台优先级执行，如果需要调优先级请使用execute(runnable, callback,
     * priority)调用
     * 
     * @param runnable
     */
    public static void execute(final Runnable runnable) {
        execute(runnable, null, android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    /**
     * 将Runnable放入线程池执行 默认为后台优先级执行，如果需要调优先级请使用execute(runnable, callback,
     * priority)调用
     * 
     * @param runnable
     * @param callback
     *            回调到execute函数所运行的线程中
     */
    public static void execute(final Runnable runnable, final Runnable callback) {
        execute(runnable, callback,
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    /**
     * 将Runnable放入线程池执行
     * 
     * @param runnable
     * @param callback
     *            回调到execute函数所运行的线程中
     * @param priority
     *            android.os.Process中指定的线程优先级
     */
    public static void execute(final Runnable runnable,
            final Runnable callback, final int priority) {
        try {
            if (!mThreadPool.isShutdown()) {
                Handler handler = null;
                if (callback != null) {
                    handler = new Handler(Looper.myLooper());
                }

                final Handler finalHandler = handler;
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(priority);
                        try {
                            runnable.run();
                            if (finalHandler != null && callback != null) {
                                finalHandler.post(callback);
                            }
                        } catch (final Throwable t) {
                            t.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param threadType
     *            ThreadManager.THREAD_BACKGROUND or ThreadManager.THREAD_WORK
     *            or ThreadManager.THREAD_UI or ThreadManager.THREAD_NORMAL
     * @param callbackToMainThread
     *            if true, preCallback and postCallback will run in UI thread;
     *            if false, run in calling thread
     * @param preCallback
     *            run before execute task runnable, control by
     *            callbackToMainThread argument, can be null
     * @param postCallback
     *            run after execute task runnable, control by
     *            callbackToMainThread argument, can be null
     */
    public static void post(final int threadType, final Runnable preCallback,
            final Runnable task, final Runnable postCallback,
            final boolean callbackToMainThread, long delayMillis) {
        if (task == null) {
            return;
        }

        if (mMainThreadHandler == null) {
            createMainThread();
        }

        Handler handler = null;
        switch (threadType) {
        case THREAD_BACKGROUND:
            if (sBackgroundThread == null) {
                createBackgroundThread();
            }
            handler = sBackgroundHandler;
            break;
        case THREAD_WORK:
            if (sWorkThread == null) {
                createWorkerThread();
            }
            handler = sWorkHandler;
            break;
        case THREAD_UI:
            handler = mMainThreadHandler;
            break;
        case THREAD_NORMAL:
            if (sNormalThread == null) {
                createNormalThread();
            }

            handler = sNormalHandler;
            break;
        case THREAD_SHAREDPREFERENCES:
            if (sSharedPreferencesThread == null) {
                createSharedPreferencesThread();
            }

            handler = sSharedPreferencesHandler;
            break;
        default:
            handler = mMainThreadHandler;
            break;
        }

        if (handler == null)
            return;
        final Handler finalHandler = handler;

        Looper myLooper = null;
        if (callbackToMainThread == false) {
            myLooper = Looper.myLooper();
            if (myLooper == null) {
                myLooper = mMainThreadHandler.getLooper();
            }
        }
        final Looper looper = myLooper;

        final Runnable postRunnable = new Runnable() {
            @Override
            public void run() {

                Runnable monitorRunnable = null;
                if (sMonitorHandler != null) {
                    monitorRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mMainThreadHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    RuntimeException re = new RuntimeException(
                                            "这里使用了ThreadManager.post函数运行了一个超过30s的任务，"
                                                    + "请查看这个任务是否是非常耗时的任务，或者存在死循环，或者存在死锁，或者存在一直卡住线程的情况，"
                                                    + "如果存在上述情况请解决或者使用ThreadManager.execute函数放入线程池执行该任务。",
                                            new Throwable(task.toString()));
                                    throw re;
                                }
                            });
                        }
                    };
                }

                if (sMonitorHandler != null) {
                    sMonitorHandler.postDelayed(monitorRunnable, 30 * 1000);
                }

                try {
                    task.run();
                } catch (final Throwable t) {
                    t.printStackTrace();
                }

                if (sMonitorHandler != null) {
                    sMonitorHandler.removeCallbacks(monitorRunnable);
                }

                if (postCallback != null) {
                    if (callbackToMainThread
                            || (looper == mMainThreadHandler.getLooper())) {
                        mMainThreadHandler.post(postCallback);
                    } else {
                        new Handler(looper).post(postCallback);
                    }
                }

                try {
                    mRunnableCache.remove(task);
                } catch (Throwable t) {
                }
            }
        };

        Runnable realRunnable = new Runnable() {
            @Override
            public void run() {
                if (preCallback != null) {
                    if (callbackToMainThread
                            || (looper == mMainThreadHandler.getLooper())) {
                        mMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                preCallback.run();
                                finalHandler.post(postRunnable);
                            }
                        });
                    } else {
                        new Handler(looper).post(new Runnable() {
                            @Override
                            public void run() {
                                preCallback.run();
                                finalHandler.post(postRunnable);
                            }
                        });
                    }
                } else {
                    postRunnable.run();
                }
            }
        };

        finalHandler.postDelayed(realRunnable, delayMillis);

        synchronized (mRunnableCache) {

            if (preCallback == null) {
                mRunnableCache.put(task, new RunnableMap(realRunnable,
                        threadType));
            } else {
                mRunnableCache.put(task, new RunnableMap(postRunnable,
                        threadType));
            }
        }
    }

    /**
     * 
     * @param threadType
     *            ThreadManager.THREAD_BACKGROUND or ThreadManager.THREAD_WORK
     *            or ThreadManager.THREAD_UI or ThreadManager.THREAD_NORMAL
     * @param callbackToMainThread
     *            if true, preCallback and postCallback will run in UI thread;
     *            if false, run in calling thread
     * @param preCallback
     *            run before execute task runnable, control by
     *            callbackToMainThread argument, can be null
     * @param postCallback
     *            run after execute task runnable, control by
     *            callbackToMainThread argument, can be null
     */
    public static void post(int threadType, final Runnable preCallback,
            final Runnable task, final Runnable postCallback,
            boolean callbackToMainThread) {
        post(threadType, preCallback, task, postCallback, callbackToMainThread,
                0);
    }

    /**
     * 
     * @param threadType
     *            ThreadManager.THREAD_BACKGROUND or ThreadManager.THREAD_WORK
     *            or ThreadManager.THREAD_UI or ThreadManager.THREAD_NORMAL
     * @param postCallbackRunnable
     *            run after execute task runnable, control by
     *            callbackToMainThread argument, can be null
     */
    public static void post(int threadType, final Runnable task,
            final Runnable postCallbackRunnable) {
        post(threadType, null, task, postCallbackRunnable, false, 0);
    }

    /**
     * 
     * @param threadType
     *            ThreadManager.THREAD_BACKGROUND or ThreadManager.THREAD_WORK
     *            or ThreadManager.THREAD_UI or ThreadManager.THREAD_NORMAL
     * @param preCallback
     *            run before execute task runnable, control by
     *            callbackToMainThread argument, can be null
     * @param postCallback
     *            run after execute task runnable, control by
     *            callbackToMainThread argument, can be null
     */
    public static void post(int threadType, final Runnable preCallback,
            final Runnable task, final Runnable postCallback) {
        post(threadType, preCallback, task, postCallback, false, 0);
    }

    /**
     * @note 谨慎使用:post到消息队列，顺序执行事件,如需要实时性强的操作请注意，可考虑使用本类的execute()或其他
     * @param threadType
     *            ThreadManager.THREAD_BACKGROUND or ThreadManager.THREAD_WORK
     *            or ThreadManager.THREAD_UI or ThreadManager.THREAD_NORMAL
     * @param task
     */
    public static void post(int threadType, Runnable task) {
        post(threadType, null, task, null, false, 0);
    }

    /**
     * @note 谨慎使用:post到消息队列，顺序执行事件,如需要实时性强的操作请注意，可考虑使用本类的execute()或其他
     * @param threadType
     *            ThreadManager.THREAD_BACKGROUND or ThreadManager.THREAD_WORK
     *            or ThreadManager.THREAD_UI or ThreadManager.THREAD_NORMAL
     * @param task
     */
    public static void postDelayed(int threadType, Runnable task,
            long delayMillis) {
        post(threadType, null, task, null, false, delayMillis);
    }

    public static void postDelayed(int threadType, Runnable task,Runnable postCallback,
                                   long delayMillis) {
        post(threadType, null, task, postCallback, false, delayMillis);
    }

    /**
     * 可以直接移除所有使用ThreadManager post出去的task，不用指定是线程类型threadType.
     */
    public static void removeRunnable(final Runnable task) {
        if (task == null) {
            return;
        }

        RunnableMap map = (RunnableMap) mRunnableCache.get(task);
        if (map == null) {
            return;
        }

        Runnable realRunnable = map.getRunnable();
        if (realRunnable != null) {
            switch (map.getType()) {
            case THREAD_BACKGROUND:
                if (sBackgroundHandler != null) {
                    sBackgroundHandler.removeCallbacks(realRunnable);
                }
                break;
            case THREAD_WORK:
                if (sWorkHandler != null) {
                    sWorkHandler.removeCallbacks(realRunnable);
                }
                break;
            case THREAD_UI:
                if (mMainThreadHandler != null) {
                    mMainThreadHandler.removeCallbacks(realRunnable);
                }
                break;
            case THREAD_NORMAL:
                if (sNormalHandler != null) {
                    sNormalHandler.removeCallbacks(realRunnable);
                }
                break;
            case THREAD_SHAREDPREFERENCES:
                if (sSharedPreferencesHandler != null) {
                    sSharedPreferencesHandler.removeCallbacks(realRunnable);
                }
                break;
            default:
                break;
            }

            try {
                mRunnableCache.remove(task);
            } catch (Throwable t) {
            }
        }
    }

    private static synchronized void createBackgroundThread() {
        if (sBackgroundThread == null) {
            sBackgroundThread = new HandlerThread("BackgroundHandler",
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
            sBackgroundThread.start();
            sBackgroundHandler = new Handler(sBackgroundThread.getLooper());
        }
    }

    private static synchronized void createWorkerThread() {
        if (sWorkThread == null) {
            sWorkThread = new HandlerThread(
                    "WorkHandler",
                    (android.os.Process.THREAD_PRIORITY_DEFAULT + android.os.Process.THREAD_PRIORITY_BACKGROUND) / 2);
            sWorkThread.start();
            sWorkHandler = new Handler(sWorkThread.getLooper());
        }
    }

    private static synchronized void createNormalThread() {
        if (sNormalThread == null) {
            sNormalThread = new HandlerThread("sNormalHandler",
                    android.os.Process.THREAD_PRIORITY_DEFAULT);
            sNormalThread.start();
            sNormalHandler = new Handler(sNormalThread.getLooper());
        }
    }

    private static synchronized void createMainThread() {
        if (mMainThreadHandler == null) {
            mMainThreadHandler = new Handler(Looper.getMainLooper());
        }
    }

    private static synchronized void createSharedPreferencesThread() {
        if (sSharedPreferencesThread == null) {
            sSharedPreferencesThread = new HandlerThread(
                    "sSharedPreferencesHandler",
                    android.os.Process.THREAD_PRIORITY_DEFAULT);
            sSharedPreferencesThread.start();
            sSharedPreferencesHandler = new Handler(
                    sSharedPreferencesThread.getLooper());
        }
    }

    public static void doSomthingBeforDestroy() {
        if (sBackgroundThread != null) {
            sBackgroundThread.setPriority(Thread.MAX_PRIORITY);
        }
        if (sWorkThread != null) {
            sWorkThread.setPriority(Thread.MAX_PRIORITY);
        }
        if (sSharedPreferencesThread != null) {
            sSharedPreferencesThread.setPriority(Thread.MAX_PRIORITY);
        }
    }

    public static synchronized void destroy() {
        if (sBackgroundThread != null) {
            sBackgroundThread.quit();
            try {
                sBackgroundThread.interrupt();
            } catch (Throwable t) {
            }
            sBackgroundThread = null;
        }

        if (sWorkThread != null) {
            sWorkThread.quit();
            try {
                sWorkThread.interrupt();
            } catch (Throwable t) {
            }
            sWorkThread = null;
        }

        if (sNormalThread != null) {
            sNormalThread.quit();
            try {
                sNormalThread.interrupt();
            } catch (Throwable t) {
            }
            sNormalThread = null;
        }

        if (sSharedPreferencesThread != null) {
            sSharedPreferencesThread.quit();
            try {
                sSharedPreferencesThread.interrupt();
            } catch (Throwable t) {
            }
            sSharedPreferencesThread = null;
        }

        if (mThreadPool != null) {
            try {
                mThreadPool.shutdown();
            } catch (Throwable t) {
            }
            mThreadPool = null;
        }
    }

    public static Looper getBackgroundLooper() {
        createBackgroundThread();
        return sBackgroundThread.getLooper();
    }

    public static Looper getWorkLooper() {
        createWorkerThread();
        return sWorkThread.getLooper();
    }

    private static class RunnableMap {
        private Runnable mRunnable;
        private Integer mType;

        public RunnableMap(Runnable runnable, Integer type) {
            mRunnable = runnable;
            mType = type;
        }

        public Runnable getRunnable() {
            return mRunnable;
        }

        public int getType() {
            return mType;
        }
    }

    private static class CustomIdelHandler implements IdleHandler {
        private static final MessageQueue mMainThreadQueue = (MessageQueue) ReflectionHelper
                .getFieldValue(Looper.getMainLooper(), "mQueue");
        private static final Handler mHandler = new Handler(
                Looper.getMainLooper());
        private static final long mRunnableDelayTime = 10 * 1000;
        private Runnable mRunnable;

        public CustomIdelHandler(final Runnable runnable) {
            mRunnable = runnable;
        }

        private final Runnable mPostRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMainThreadQueue != null) {
                    mMainThreadQueue.removeIdleHandler(CustomIdelHandler.this);
                }
                mRunnable.run();
            }
        };

        @Override
        public boolean queueIdle() {
            mHandler.removeCallbacks(mPostRunnable);
            mRunnable.run();
            return false;
        }

        public void post() {
            if (mMainThreadQueue != null) {
                mHandler.postDelayed(mPostRunnable, mRunnableDelayTime);
                mMainThreadQueue.addIdleHandler(this);
            } else {
                throw new Error("CustomIdelHandler main thread queue is null!");
            }
        }
    };

    /**
     * 向主线程发送一个闲时处理的Runnable,这个Runnable会在主线程空闲的时候进行处理，也就是主线程没有消息要处理的时候进行处理
     * 
     * @param runnable
     */
    public static void postIdleRunnable(final Runnable runnable) {
        new CustomIdelHandler(runnable).post();
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static abstract class RunnableEx implements Runnable {
        private Object mArg;

        public void setArg(Object arg) {
            mArg = arg;
        }

        public Object getArg() {
            return mArg;
        }
    }
}
