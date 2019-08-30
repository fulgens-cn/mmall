package cn.fulgens.mmall.task;

import cn.fulgens.mmall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String CLOSE_ORDER_TASK_LOCK = "close_order_task_lock";

    @Autowired
    private RedissonClient redissonClient;

    private final IOrderService orderService;

    public CloseOrderTask(IOrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrder() {
        log.info("定时关单任务开始...");
        Redisson redisson = (Redisson) redissonClient;
        RLock lock = redisson.getLock(CLOSE_ORDER_TASK_LOCK);
        boolean gotLock = false;
        try {
            // 尝试加锁，等待0秒，上锁以后30秒自动解锁
            gotLock = lock.tryLock(0, 30, TimeUnit.SECONDS);
            if (gotLock) {
                log.info("当前服务器节点获取到分布式锁: {} 开始执行定时任务...", CLOSE_ORDER_TASK_LOCK);
                orderService.closeOrder(1L, ChronoUnit.HOURS);
            } else {
                log.info("当前服务器节点未获取到分布式锁: {} 不执行定时任务...", CLOSE_ORDER_TASK_LOCK);
            }
        } catch (InterruptedException e) {
            log.info("redisson获取分布式锁异常", e);
        } finally {
            if (!gotLock) {
                return;
            }
            // 释放锁
            lock.unlock();
        }
        log.info("定时关单任务结束...");
    }
}
