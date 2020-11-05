package com.dongyimai.search.service.impl;

import com.dongyimai.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] ids = (Long[])objectMessage.getObject();

            System.out.println("ids : " + ids);

            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));

            System.out.println("删除成功...");

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
