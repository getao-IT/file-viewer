package cn.aircas.airproject.config.aop;


import cn.aircas.airproject.entity.domain.ProgressInfo;
import cn.aircas.airproject.entity.domain.ProgressResponseSingleTon;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Data
@NoArgsConstructor
@Slf4j
public class FileTransTimerTask extends TimerTask {

    private ProgressInfo progress;
    // 文件大小
    private long fileSize;

    // 已传输大小
    private long transLength;

    // 传输速度
    private String transVelocity = "";

    // 传输耗时 字符串表示
    private String consumTime = "";

    // 传输时长 单位秒
    private int second = 1;

    // 间隔时长
    private long interval = 1000;

    // 是否传输结束
    private boolean isEnd = false;

    // 定时器对象
    private Timer timer;

    // 是否启动了计时器
    private boolean isScheduled = false;

    // 获取响应单例
    private Map<String, ProgressInfo> progressResponse = ProgressResponseSingleTon.getInstance();

    public FileTransTimerTask(ProgressInfo progressInfo) {
        this.progress = progressResponse.get(progressInfo.getProgressId());
    }

    /**
     * 定时任务
     */
    @Override
    public void run() {
        this.progress = progressResponse.get(progress.getProgressId());
        if (!progress.isNormal()) { // 当传输出现问题时，停止该传输并清除
            setEnd(true);
            progressResponse.remove(progress.getProgressId());
        }
        if (!isEnd()) {
            transVelocity = getTransVelocity();// 设置传输速度
            progress.setTransVelocity(transVelocity);

            second = progress.getSecond(); // 传输耗时
            consumTime = secToTime(second);
            progress.setSecond(second+=1);
            progress.setConsumTime(consumTime);

            // 剩余时间
            int remainTime = getRemainTime();
            progress.setRemainTime(secToTime(remainTime));

            // 设置传输进度
            transLength = progress.getTransLength();
            //progress.setTransLength(transLength);
            //long transLength = progress.getTransLength();
            double percent = (double) (transLength*100)/(double) progress.getFileSize();
            DecimalFormat format = new DecimalFormat("#.##"); // 格式化输出进度
            progress.setPlan(format.format(percent));
            progress.setLastTransLength(progress.getTransLength());

            if (transLength < progress.getFileSize()) { // 没有传输完成
                // 更新进度
                progressResponse.put(progress.getProgressId(), progress);

                // 打印传输进度
                if (progress.getFileSize() == 0) {
                    progress.setPlan(String.valueOf(getTransLength())+"byte");
                }
                log.info("--------------------- 上传文件 {} 进度：总传输大小 {}， 已传输大小 {}， 传输速度 {}， 耗时 {} ，预估剩余时间 {}， " +
                                "传输进度 {} %", progress.getProgressId(), progress.getFileSize(), progress.getTransLength()
                        , progress.getTransVelocity(), progress.getConsumTime(), progress.getRemainTime(), progress.getPlan());
            } else {
                log.info("--------------------- 上传文件 {} 进度：总传输大小 {}， 已传输大小 {}， 传输速度 {}， 耗时 {} ，预估剩余时间 {}， " +
                                "传输进度 {} %", progress.getProgressId(), progress.getFileSize(), progress.getTransLength()
                        , progress.getTransVelocity(), progress.getConsumTime(), progress.getRemainTime(), progress.getPlan());

                log.info("--------------------- 远程上传文件 {} 成功 end ---------------  ", progress.getProgressId());
                setEnd(true);
                stop(); // 关闭计时器
                progressSuccess(); // 传输完成
                //initProgress(); // 上传完成后，初始化进度对象
            }
        } else {
            stop(); // 关闭计时器
        }
    }


    /**
     * 关闭定时器对象
     */
    private void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            setTimer(null);
            setScheduled(false);
        }
    }

    /**
     * 上传完成构造单例对象
     */
    private void progressSuccess() {
        progress.setFileSize(fileSize);
        progress.setTransLength(fileSize);
        progress.setPlan("100");
        //progress.setConsumTime("0");
        progress.setRemainTime("0");
        //progress.setTransVelocity("0");
        progress.setDone(true);
    }

    /**
     * 获取传输速度
     * @return
     */
    private String getTransVelocity() {
        String temp = "";
        String transVel = "0MB/s";
        DecimalFormat format = new DecimalFormat("#.##");
        double transByte = (double)(progress.getTransLength() - progress.getLastTransLength()) / 1;
        if (transByte < 1024) {
            temp = format.format(transByte);
            transVel = temp + "Byte/s";
            return transVel;
        }
        if (transByte < 1024 * 1024) {
            temp = format.format(Math.ceil(transByte / 1024));
            transVel = temp + "KB/s";
            return transVel;
        }
        double transMb = transByte / 1024 /1024;
        String s = format.format(transMb);
        transVel = s + "MB/s";

        return transVel;
    }

    /**
     * 将秒转换为时间字符串
     * @param second
     * @return
     */
    private String secToTime(int second) {
        StringBuffer strTime = new StringBuffer();
        int min;
        int hour;
        if (second < 60) {
            strTime.append(second + "s").toString();
        }
        if (second >= 60) {
            min = second / 60;
            if (min < 60) {
                strTime.append(min+"m"+(second%60)+"s").toString();
            }
            if (min >= 60) {
                hour = min / 60;
                strTime.append(hour+"h"+(min%60)+"m"+(second%3600)+"s").toString();
            }
        }
        return strTime.toString();
    }

    /**
     * 获取剩余预估时间 单位秒
     * @return
     */
    public int getRemainTime() {
        long remainSize = progress.getFileSize() - progress.getTransLength();
        double remainTime = 0.0;
        String transVelocity = progress.getTransVelocity();
        double velocity = 0.0;
        if (transVelocity.contains("MB/s")) {
            velocity = Double.parseDouble(transVelocity.replace("MB/s", ""));
            remainTime = (double) remainSize / 1024 / 1024 / velocity;
        }
        if (transVelocity.contains("KB/s")) {
            velocity = Double.parseDouble(transVelocity.replace("KB/s", ""));
            remainTime = (double) remainSize / 1024 / velocity;
        }
        if (transVelocity.contains("Byte/s")) {
            velocity = Double.parseDouble(transVelocity.replace("Byte/s", ""));
            remainTime = (double) remainSize / velocity;
        }

        return (int) Math.ceil(remainTime);
    }

    /*public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new FileTransTimerTask(), 1000, 1000);
    }*/
}
