/*
 *Copyright (c) 2015-2015. SJY.JIANGSU Corporation. All rights reserved
 */

package com.sjy.ttclub.framework;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public abstract class AbstractStartupManager {
    private static final String TAG = "StartupManager";
    private static final boolean DEBUG = false;
    private Step mStepsHead;

    private Handler mForegroundHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (DEBUG) {
                Log.d(TAG, "msg.what = " + msg.what);
            }

            Step step = (Step) msg.obj;
            int result = msg.arg1;
            int errorCode = msg.arg2;
            if (step != null) {
                if (DEBUG) {
                    Log.d(TAG, "begin step: " + step.getClass().getSimpleName());
                }

                if (result == Step.SUCCESS) {
                    step.doAction();
                    if (DEBUG) {
                        Log.d(TAG, "SUCCESS, finished step: "
                                + step.getClass().getSimpleName());
                    }
                } else if (result == Step.FAIL) {
                    if (DEBUG) {
                        Log.d(TAG, "FAIL, finished step: "
                                + step.getClass().getSimpleName()
                                + ", errorCode: " + errorCode);
                    }

                    if (step.mErrorHandler != null) {
                        step.mErrorHandler.onFail(step, errorCode);
                    }
                }
            }
        }
    };

    public AbstractStartupManager() {

    }

    public void start() {
        mStepsHead = buildSteps();

        // start from the first step, then wait next message
        if (mStepsHead != null) {
            mStepsHead.doAction();
        }
    }

    // please return the head of the step list
    public abstract Step buildSteps();

    public abstract class Step {
        public static final int FAIL = 0;
        public static final int SUCCESS = 1;
        private Step mNextStep;
        protected IStepErrorHandler mErrorHandler;

        public void setErrorHandler(IStepErrorHandler errorHandler) {
            this.mErrorHandler = errorHandler;
        }

        public Step getNextStep() {
            return mNextStep;
        }

        public Step setNextStep(Step nextStep) {
            this.mNextStep = nextStep;
            return this;
        }

        public abstract void doAction();

        public void gotoNext(int result, int errorCode) {
            Step step;
            int what;
            if (result == SUCCESS) {
                if (hasNextStep()) {
                    step = mNextStep;
                    what = mNextStep.getID();
                } else {
                    step = null;
                    what = 0;
                }
            } else {
                step = this;
                what = errorCode;
            }

            if (step != null) {
                Message msg = mForegroundHandler.obtainMessage(what, step);
                msg.arg1 = result;
                msg.arg2 = errorCode;
                mForegroundHandler.sendMessage(msg);
            }
        }

        private boolean hasNextStep() {
            return mNextStep != null;
        }

        public abstract int getID();

    }

    public interface IStepErrorHandler {
        public void onFail(Step curStep, int errorCode);
    }

}
