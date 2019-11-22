package yuelj.utils.dateTime;

import java.lang.Thread.State;
import java.util.Timer;
import java.util.TimerTask;

import yuelj.utils.logs.SystemLog;

/**
 * 计时标签
 * @author Jeky
 */
public class TimerUtil{

    private int maxTime;
    private int count;
    private static final int SECOND = 1000;
    private Thread thread;
    private boolean pause;
    private boolean start;

    
    public void timeVoid(){
        final Timer timer = new Timer();
        TimerTask tt=new TimerTask() { 
            @Override
            public void run() {
                SystemLog.printlog("到点啦！");
                timer.cancel();
            }
        };
        timer.schedule(tt, 3000);
    }
    
    
    public static void main(String[] args) {
    	SystemLog.printlog(1);
    	TimerUtil tu=new TimerUtil();
    	tu.timeVoid();
    	SystemLog.printlog(2);
	}

    /**
     * 修改倒计时起始时间
     * @param maxTime 新的起始时间
     */
    public void setMaxTime(int maxTime) {
        if (this.start) {
            return;
        }
        this.maxTime = maxTime;
        this.count = maxTime;
        initText();
        this.thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (count != 0) {
                    try {
                        if (!start) {
                            count = 0;
                            initText();
                            break;
                        }
                        if (!pause) {
                            Thread.sleep(SECOND);
                            count--;
                            initText();
                        }
                    } catch (InterruptedException ex) {
                        pause = true;
                    }
                }
                done();
            }
        });
        this.start = false;
    }

    /**
     * 倒计时完成后调用此方法
     */
    protected void done() {
    	//Time up
    }

    /**
     * 标签字符由此方法设置
     */
    protected void initText() {
        String min = String.valueOf(count / 60);
        String sec = String.valueOf(count % 60);
        while (min.length() < 2) {
            min = "0" + min;
        }
        while (sec.length() < 2) {
            sec = "0" + sec;
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (start) {
            thread.interrupt();
        }
    }

    /**
     * 检测标签倒计时是否开始
     * @return 如果开始返回true
     */
    public boolean isStart() {
        return start;
    }

    /**
     * 得到倒计时起始时间
     * @return 倒计时起始时间
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * 检测标签倒计时是否暂停
     * @return 倒计时暂停返回true
     */
    public boolean isPause() {
        return pause;
    }

    /**
     * 从暂停中恢复计时
     */
    public void continueDo() {
        if (this.pause) {
            this.pause = false;
        }
    }

    /**
     * 取消计时
     */
    public void stop() {
        if (start) {
            start = false;
        }
    }

    /**
     * 开始计时
     */
    public void start() {
        if (thread.getState().equals(State.NEW)) {
            start = true;
            thread.start();
        } else if (thread.getState().equals(State.TERMINATED)) {
            setMaxTime(maxTime);
            start = true;
            thread.start();
        }
    }
}
