package com.jt.order.job;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.jt.order.mapper.OrderMapper;

/**
 * 扫描超过2天未付款的订单关闭
 * 
 */
public class PaymentOrderJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        ApplicationContext applicationContext = (ApplicationContext) context.getJobDetail().getJobDataMap()
                .get("applicationContext");
        
        //joda库提供的日期处理方法，减去2天
        applicationContext.getBean(OrderMapper.class).paymentOrderScan(new DateTime().minusDays(2).toDate());
    }

}