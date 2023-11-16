package cn.aircas.airproject.callback.impl;

import cn.aircas.airproject.callback.GrayConverCallback;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.entity.emun.TaskStatus;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.service.impl.ProgressServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import java.text.DecimalFormat;


@Slf4j
public class GrayConverCallbackImpl implements GrayConverCallback {

    private final ProgressService service = new ProgressServiceImpl();

    private final long[] callBackTime = {System.currentTimeMillis()};

    private long taskStartTime = callBackTime[0];

    private ProgressContr progress;

    public GrayConverCallbackImpl(ProgressContr progress) {
        this.progress = progress;
    }


    @Override
    public void run(double dfComplete) {
        long callBack = System.currentTimeMillis() - callBackTime[0];
        long consumTime = System.currentTimeMillis() - taskStartTime;
        if (callBack >= 1000) {
            ProgressContrDto pct = new ProgressContrDto();
            BeanUtils.copyProperties(progress, pct);
            pct.setConsumTime(consumTime);
            pct.setStatus(TaskStatus.WORKING);
            pct.setProgress(new DecimalFormat("##.##").format(dfComplete * 100) + "%");
            int i = service.updateProgress(pct);
            callBackTime[0] = System.currentTimeMillis();
            log.info("更新任务进度成功：status {} - taskId {} - 任务类型 {} - 文件 {}，进度 {}，耗时 {}",
                    i, pct.getTaskId(), pct.getTaskType(), pct.getFilePath(), pct.getProgress(), pct.getConsumTime());
        }
    }

}
