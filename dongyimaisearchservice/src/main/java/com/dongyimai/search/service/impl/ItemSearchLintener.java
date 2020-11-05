package com.dongyimai.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class ItemSearchLintener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            List<TbItem> itemList = JSON.parseArray(textMessage.getText(),TbItem.class);
            System.out.println("item List size : " + itemList.size());
            //每一个item 的spec 需要格式化
            for (TbItem tbItem : itemList) {
                Map map = JSON.parseObject(tbItem.getSpec());
                tbItem.setSpecMap(map);
            }

            itemSearchService.importList(itemList);
            System.out.println("solr导入成功...");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
