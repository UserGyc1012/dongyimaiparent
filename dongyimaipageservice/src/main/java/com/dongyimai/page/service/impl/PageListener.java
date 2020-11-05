package com.dongyimai.page.service.impl;

import com.dongyimai.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;

@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        try {
            TextMessage textMessage = (TextMessage) message;
            String id = textMessage.getText();
            itemPageService.genItemHtml(Long.parseLong(id));

            System.out.println("页面生成完毕...");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
