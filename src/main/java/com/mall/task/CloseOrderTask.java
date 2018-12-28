package com.mall.task;

import com.mall.common.Const;
import com.mall.service.IOrderService;
import com.mall.util.PropertiesUtil;
import com.mall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService  iOrderService;

//    @Scheduled(cron = "0 */1 * * * ?")
//    public void closeOrderTaskV1(){
//        log.info("关闭订单任务开始");
//        int hour = Integer.parseInt(PropertiesUtil.getPropery("close.order.task.time", "1"));
//        iOrderService.closeOrder(hour);
//        log.info("关闭订单任务结束");
//    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2() {
        log.info("关闭订单定时任务开始");
        long lockTimeout = Long.parseLong(PropertiesUtil.getPropery("lock.timeout", "5000"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.Redis_Lock.ORDER_CLOSE_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + lockTimeout));
        if (setnxResult!=null&&setnxResult.intValue()==1){
            closeOrder(Const.Redis_Lock.ORDER_CLOSE_TASK_LOCK);
        }else {
            log.error("获取锁失败:{}", Const.Redis_Lock.ORDER_CLOSE_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }

    public void closeOrder(String lockName){
        //s
        RedisShardedPoolUtil.expire(lockName, 50);
        log.info("获取锁:{},Thread:{}", lockName, Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getPropery("close.order.task.time", "1"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(lockName);
        log.info("释放锁:{},Thread:{}", lockName, Thread.currentThread().getName());
    }
}
