package com.xxl.conf.admin.openapi.registry.thread;

import com.xxl.conf.admin.constant.enums.MessageTypeEnum;
import com.xxl.conf.admin.model.dto.MessageForConfDataDTO;
import com.xxl.conf.admin.model.dto.MessageForRegistryDTO;
import com.xxl.conf.admin.model.entity.Message;
import com.xxl.conf.admin.openapi.confdata.config.ConfDataBootstrap;
import com.xxl.conf.admin.openapi.registry.config.RegistryBootstrap;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.json.GsonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private static Logger logger = LoggerFactory.getLogger(MessageHelpler.class);

    /**
     * message filtering to avoid duplicate processing
     */
    private volatile List<Long> readedMessageIds = Collections.synchronizedList(new ArrayList());

    /**
     * msg clean Interval, by second
     */
    public static final int CLEAN_INTERVAL_TIME = 30;

    /**
     * thread stop variable
     */
    private volatile boolean toStop = false;

    /**
     * 实时广播消息
     */
    private Thread messageListenThread;

    /**
     * start
     */
    public void start(){

        // 3、messageListenThread
        messageListenThread = startThread(new Runnable() {
            @Override
            public void run() {
                logger.info(">>>>>>>>>>> xxl-conf, MessageHelpler-messageListenThread start");
                while (!toStop) {
                    try {
                        // a、detect real-time messages, Once per second
                        Date msgTimeValidStart = DateTool.addSeconds(new Date(), -1 * 10);
                        Date msgTimeValidEnd = DateTool.addMinutes(msgTimeValidStart, 5);
                        List<Long> excludeMsgIds = readedMessageIds.size()>50?readedMessageIds.subList(0, 50):readedMessageIds;

                        List<Message> messageList = RegistryBootstrap.getInstance().getMessageMapper().queryValidMessage(msgTimeValidStart, msgTimeValidEnd, excludeMsgIds);

                        // b、dispatch message
                        if (CollectionTool.isNotEmpty(messageList)) {

                            // 1、parse message by type
                            List<MessageForRegistryDTO> messageForRegistryDTOList = messageList.stream()
                                    .filter(item->item.getType()== MessageTypeEnum.REGISTRY.getValue())
                                    .map(item-> (GsonTool.fromJson(item.getData(), MessageForRegistryDTO.class)))
                                    .collect(Collectors.toList());
                            List<MessageForConfDataDTO> messageForConfDataDTOList = messageList.stream()
                                    .filter(item->item.getType()== MessageTypeEnum.CONFDATA.getValue())
                                    .map(item-> (GsonTool.fromJson(item.getData(), MessageForConfDataDTO.class)))
                                    .collect(Collectors.toList());

                            // 2、process registry message
                            RegistryBootstrap.getInstance().getRegistryCacheHelpler().checkUpdateAndPush(messageForRegistryDTOList);

                            // 3、process confdata message
                            ConfDataBootstrap.getInstance().getConfDataCacheHelpler().checkUpdateAndPush(messageForConfDataDTOList);
                        }

                        // c、store msgId, avoid repeat message
                        readedMessageIds.addAll(messageList.stream().map(Message::getId).collect(Collectors.toList()));

                        // d、clean old message， Avoid too often clean
                        if ( (System.currentTimeMillis()/1000) % CLEAN_INTERVAL_TIME ==0) {
                            RegistryBootstrap.getInstance().getMessageMapper().cleanMessageInValid(msgTimeValidStart, msgTimeValidEnd);
                            readedMessageIds.clear();
                        }

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, MessageHelpler-messageListenThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-conf, MessageHelpler-messageListenThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-conf, MessageHelpler-messageListenThread stop");
            }
        }, "xxl-conf, admin MessageHelpler-messageListenThread");

    }

    /**
     * stop
     */
    public void stop(){
        // mark stop
        toStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // stop thread
        stopThread(messageListenThread);
    }

    // ---------------------- util ----------------------

    /**
     * start thread
     *
     * @param runnable
     * @param name
     * @return
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
     * @param thread
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
     * @param messageTypeEnum
     * @param data
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
