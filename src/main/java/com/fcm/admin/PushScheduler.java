package com.fcm.admin;

import com.fcm.admin.common.util.PropertyUtil;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.util.Date;

/**
 * 구글 FCM을 이용한 푸쉬전송 스케쥴러 프로그램
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
public class PushScheduler {
    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName("push scheduler");
        jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
        jobDetail.setJobClass(PushJob.class);

        SimpleTriggerImpl trigger = new SimpleTriggerImpl();
        trigger.setName("push trigger");
        trigger.setStartTime(new Date());
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setRepeatInterval(1000 * Integer.parseInt(PropertyUtil.getProperty("trigger.repeat.interval.seconds")));

        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }
}
