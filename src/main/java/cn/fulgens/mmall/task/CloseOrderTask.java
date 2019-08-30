package cn.fulgens.mmall.task;

import cn.fulgens.mmall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 定时关单任务
 *
 * @author fulgens
 */
@Slf4j
@Component
public class CloseOrderTask {

    private final IOrderService orderService;

    public CloseOrderTask(IOrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrder() throws InterruptedException {
        log.info("定时关单任务开始...");
        orderService.closeOrder(1L, ChronoUnit.HOURS);
        log.info("定时关单任务结束...");
    }
}
