package cn.aircas.airproject.config.aop;

import cn.aircas.airproject.entity.domain.ProgressInfo;
import cn.aircas.airproject.entity.domain.ProgressResponseSingleTon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Set;

@Configuration
@EnableScheduling
@Slf4j
@PropertySource(value = "classpath:/application.yml")
public class SchedulingConfig {

    @Scheduled(cron = "${configtask.cron}")
    public void clearFinishProgress() {
        log.info("----------------------start:执行定时任务：清理已完成的上传文件进度-----------------------");
        Map<String, ProgressInfo> map = ProgressResponseSingleTon.getInstance();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            if (map.get(key).equals("100")) {
                map.remove(key);
            }
        }
        log.info("----------------------end:执行定时任务：清理已完成的上传文件进度-----------------------");
    }
}
