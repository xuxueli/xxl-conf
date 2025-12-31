package com.xxl.conf.admin.openapi.registry.thread;

import com.xxl.conf.admin.constant.enums.MessageTypeEnum;
import com.xxl.conf.admin.model.dto.MessageForConfDataDTO;
import com.xxl.conf.admin.model.dto.MessageForRegistryDTO;
import com.xxl.conf.admin.model.entity.Message;
import com.xxl.conf.admin.openapi.confdata.config.ConfDataBootstrap;
import com.xxl.conf.admin.openapi.registry.config.RegistryBootstrap;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.json.GsonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * messsage /broadcast helper
 *
 * 功能：
 * 1、实时消息全局广播：秒级，广播全节点
 * 2、消费逻辑路由分发：dispatch 到 注册、配置 变更逻辑，识别 diff 并 push 客户端（DeferredResultHelpler）；
 *
 * @author xuxueli
 */
public class MessageHelpler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHelpler.class);

    /**
     * readed message id-list, avoid duplicate processing
     *
     *  1、数据结构：
     *  <pre>
     *      [
     *          1000,
     *          1001
     *      ]
     *  </pre>
     */
    private final List<Long> readedMessageIds = Collections.synchronizedList(new ArrayList<>());

    /**
     * msg clean Interval, by second
     */
    public static final int CLEAN_INTERVAL_TIME = 30;

    /**
     * 实时广播消息
     */
    private CyclicThread messageListenThread;

    /**
     * start
     */
    public void start(){

        messageListenThread = new CyclicThread("MessageHelpler-messageListenThread", true, new Runnable() {
            @Override
            public void run() {
                // 1、detect real-time messages, Once per second
                Date msgTimeValidStart = DateTool.addSeconds(new Date(), -1 * 10);
                Date msgTimeValidEnd = DateTool.addMinutes(msgTimeValidStart, 5);
                List<Long> excludeMsgIds = readedMessageIds.size()>50?readedMessageIds.subList(0, 50):readedMessageIds;

                List<Message> messageList = RegistryBootstrap.getInstance().getMessageMapper().queryValidMessage(msgTimeValidStart, msgTimeValidEnd, excludeMsgIds);

                // 2、dispatch message
                if (CollectionTool.isNotEmpty(messageList)) {

                    // 2.1、parse message by type
                    List<MessageForRegistryDTO> messageForRegistryDTOList = messageList.stream()
                            .filter(item->item.getType()== MessageTypeEnum.REGISTRY.getValue())
                            .map(item-> (GsonTool.fromJson(item.getData(), MessageForRegistryDTO.class)))
                            .collect(Collectors.toList());
                    List<MessageForConfDataDTO> messageForConfDataDTOList = messageList.stream()
                            .filter(item->item.getType()== MessageTypeEnum.CONFDATA.getValue())
                            .map(item-> (GsonTool.fromJson(item.getData(), MessageForConfDataDTO.class)))
                            .collect(Collectors.toList());

                    // 2.2、process registry message
                    RegistryBootstrap.getInstance().getRegistryCacheHelpler().checkUpdateAndPush(messageForRegistryDTOList);

                    // 2.3、process confdata message
                    ConfDataBootstrap.getInstance().getConfDataCacheHelpler().checkUpdateAndPush(messageForConfDataDTOList);
                }

                // 3、store msgId, avoid repeat message
                readedMessageIds.addAll(messageList.stream().map(Message::getId).toList());

                // 4、clean old message， Avoid too often clean
                if ( (System.currentTimeMillis()/1000) % CLEAN_INTERVAL_TIME ==0) {
                    RegistryBootstrap.getInstance().getMessageMapper().cleanMessageInValid(msgTimeValidStart, msgTimeValidEnd);
                    readedMessageIds.clear();
                }

            }
        }, 1 * 1000, true);
        messageListenThread.start();

    }

    /**
     * stop
     */
    public void stop(){
        if (messageListenThread != null) {
            messageListenThread.stop();
        }
    }

    // ---------------------- util ----------------------

    /**
     * start thread
     *
     * @param runnable runnable
     * @param name thread name
     * @return  thread
     */
    public static Thread startThread(Runnable runnable, String name) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName(name);
        thread.start();
        return thread;
    }

    /**
     * stop thread
     *
     * @param thread thread
     */
    public static void stopThread(Thread thread) {
        if (thread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            thread.interrupt();
            try {
                thread.join();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    // ---------------------- util ----------------------

    /**
     * broadcast message
     *
     * @param messageTypeEnum message type
     * @param data data
     */
    public static void broadcastMessage(MessageTypeEnum messageTypeEnum, String data){
        // message
        Message message = new Message();
        message.setType(messageTypeEnum.getValue());
        message.setData(data);      // convert
        message.setAddTime(new Date());
        message.setUpdateTime(new Date());
        RegistryBootstrap.getInstance().getMessageMapper().insert(message);
    }

}
