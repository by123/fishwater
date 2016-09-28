package com.sjy.ttclub;

import android.os.Message;

import com.sjy.ttclub.account.Constant;
import com.sjy.ttclub.account.model.AccountInfo;
import com.sjy.ttclub.account.model.AccountManager;
import com.sjy.ttclub.common.CommonConst;
import com.sjy.ttclub.framework.AbstractStartupManager;
import com.sjy.ttclub.framework.INotify;
import com.sjy.ttclub.framework.MsgDispatcher;
import com.sjy.ttclub.framework.Notification;
import com.sjy.ttclub.framework.NotificationCenter;
import com.sjy.ttclub.framework.adapter.MsgDef;
import com.sjy.ttclub.framework.adapter.NotificationDef;
import com.sjy.ttclub.util.PrivacyProtectionHelper;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.util.ThreadManager;

/**
 * Created by linhz on 2015/11/4.
 * Email: linhaizhong@ta2she.com
 */
public class TTClubStartupManager extends AbstractStartupManager {

    public TTClubStartupManager() {

    }

    @Override
    public Step buildSteps() {
        Step firstStep = new StepAppSecurity();
        Step preStep = firstStep;

        Step nextStep = new StepShowSplashWindow();
        preStep.setNextStep(nextStep);
        preStep = nextStep;

        nextStep = new StepEnsureSplashFinished();
        preStep.setNextStep(nextStep);
        preStep = nextStep;

        nextStep = new StepInitEnv();
        preStep.setNextStep(nextStep);
        preStep = nextStep;

        nextStep = new StepShowMainWindow();
        preStep.setNextStep(nextStep);
        preStep = nextStep;

        nextStep = new StepShowPrivacyWindow();
        preStep.setNextStep(nextStep);
        preStep = nextStep;

        nextStep = new StepShowGuideLoginWindow();
        preStep.setNextStep(nextStep);
        preStep = nextStep;

        return firstStep;
    }

    private class StepAppSecurity extends Step {

        @Override
        public void doAction() {
            gotoNext(SUCCESS, 0);
        }

        @Override
        public int getID() {
            return StepMsgDef.MSG_STEP_SECURITY;
        }

    }

    private class StepShowMainWindow extends Step {

        @Override
        public void doAction() {
            MsgDispatcher.getInstance().sendMessageSync(MsgDef.MSG_SHOW_MAIN_WINDOW);
            gotoNext(SUCCESS, 0);
        }

        @Override
        public int getID() {
            return StepMsgDef.MSG_STEP_MAIN_WINDOW;
        }
    }

    private class StepShowSplashWindow extends Step {

        @Override
        public void doAction() {
            Message msg = Message.obtain();
            msg.what = MsgDef.MSG_SHOW_SPLASH_WINDOW;
            MsgDispatcher.getInstance().sendMessageSync(msg);
            gotoNext(SUCCESS, 0);
        }

        @Override
        public int getID() {
            return StepMsgDef.MSG_STEP_SPLASH_WINDOW;
        }
    }

    private class StepInitEnv extends Step {
        @Override
        public void doAction() {
            Message msg = Message.obtain();
            msg.what = MsgDef.MSG_INIT;
            MsgDispatcher.getInstance().sendMessageSync(msg);
            gotoNext(SUCCESS, 0);
        }

        @Override
        public int getID() {
            return StepMsgDef.MSG_STEP_INIT;
        }
    }

    private class StepEnsureSplashFinished extends Step implements INotify {
        private static final int DELAY_TIME = 5 * 1000;
        private Runnable mPendingTask = new Runnable() {

            @Override
            public void run() {
                MsgDispatcher.getInstance().sendMessage(
                        MsgDef.MSG_CLOSE_SPLASH_WINDOW);
            }
        };

        public StepEnsureSplashFinished() {
            NotificationCenter.getInstance().register(this,
                    NotificationDef.N_SPLASH_FINISHED);
        }

        @Override
        public void doAction() {
            ThreadManager.postDelayed(ThreadManager.THREAD_UI, mPendingTask,
                    DELAY_TIME);
            gotoNext(SUCCESS, 0);
        }

        @Override
        public int getID() {
            return StepMsgDef.MSG_STEP_SPLASH_FINISHED;
        }

        @Override
        public void notify(Notification notification) {
            if (notification.id == NotificationDef.N_SPLASH_FINISHED) {
                ThreadManager.removeRunnable(mPendingTask);
                NotificationCenter.getInstance().unregister(this,
                        NotificationDef.N_SPLASH_FINISHED);

            }
        }
    }

    private class StepShowGuideLoginWindow extends Step {

        @Override
        public void doAction() {
            if (!AccountManager.getInstance().isLogin()) {
                AccountManager.getInstance().tryOpenGuideLoginWindow();
            } else {
                AccountInfo info = AccountManager.getInstance().getAccountInfo();
                if (info != null && StringUtils.parseInt(info.getIfHasUploadSex(), -1) == CommonConst.UPDATE_SEX_NO_COMPLETE) {
                    MsgDispatcher.getInstance().sendMessage(MsgDef.MSG_SHOW_GUIDE_NEW_USER_WINDOW);
                }
            }
            gotoNext(SUCCESS, 0);
        }

        @Override
        public int getID() {
            return StepMsgDef.MSG_STEP_GUIDE_LOGIN_WINDOW;
        }
    }

    private class StepShowPrivacyWindow extends Step {

        @Override
        public void doAction() {
            if (PrivacyProtectionHelper.isOpenPrivacyProtection()) {
                Message msg = Message.obtain();
                msg.what = MsgDef.MSG_SHOW_PRIVACY_PROTECTION_WINDOW;
                msg.arg1 = Constant.PRIVACY_INTO_APPLICATION;
                MsgDispatcher.getInstance().sendMessageSync(msg);
            }
            gotoNext(SUCCESS, 0);
        }

        @Override
        public int getID() {
            return StepMsgDef.MSG_STEP_PRIVACY_PROTECTION;
        }
    }

    private static class StepMsgDef {
        private static int sIdBase = 1000;

        private final static int genMsgId() {
            sIdBase += 1;
            return sIdBase;
        }

        public static final int MSG_STEP_SECURITY = genMsgId();

        public static final int MSG_STEP_MAIN_WINDOW = genMsgId();

        public static final int MSG_STEP_SPLASH_WINDOW = genMsgId();

        public static final int MSG_STEP_SPLASH_FINISHED = genMsgId();

        public static final int MSG_STEP_INIT = genMsgId();

        public static final int MSG_STEP_PRIVACY_PROTECTION = genMsgId();

        public static final int MSG_STEP_GUIDE_LOGIN_WINDOW = genMsgId();

    }
}
