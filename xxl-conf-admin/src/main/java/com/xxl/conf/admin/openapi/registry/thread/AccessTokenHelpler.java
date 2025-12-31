package com.xxl.conf.admin.openapi.registry.thread;

import com.xxl.conf.admin.constant.enums.AccessTokenStatuEnum;
import com.xxl.conf.admin.model.entity.AccessToken;
import com.xxl.conf.admin.openapi.registry.config.RegistryBootstrap;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.json.GsonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * AccessToken Helpler
 *
 * 功能：
 * 1、AccessToken 本地缓存，合法性校验
 *
 * @author xuxueli
 */
public class AccessTokenHelpler {
    private static final Logger logger = LoggerFactory.getLogger(AccessTokenHelpler.class);

    /**
     * 客户端监听器

     */
    private volatile Set<String> accessTokenStore = new ConcurrentSkipListSet<>();

    /**
     * registry monitor (will remove instance that expired more than 1 day)
     */
    private CyclicThread accessTokenThread;

    /**
     * start
     */
    public void start() {

        accessTokenThread = new CyclicThread("AccessTokenHelpler-accessTokenThread", true, new Runnable() {
            @Override
            public void run() {

                // 1、query valid accesstoken
                List<AccessToken> accessTokenList = RegistryBootstrap
                        .getInstance()
                        .getAccessTokenMapper()
                        .queryValidityAccessToken(AccessTokenStatuEnum.NORMAL.getValue());

                // 2、build new data
                ConcurrentSkipListSet<String> accessTokenStoreNew = new ConcurrentSkipListSet<>();
                if (CollectionTool.isNotEmpty(accessTokenList)) {
                    accessTokenStoreNew.addAll(accessTokenList.stream()
                            .map(AccessToken::getAccessToken)
                            .collect(Collectors.toSet()));
                }

                // 3、check and update
                String accessTokenStoreNewJson = GsonTool.toJson(accessTokenStoreNew);
                if (!accessTokenStoreNewJson.equals(GsonTool.toJson(accessTokenStore))) {
                    // do refresh
                    accessTokenStore = accessTokenStoreNew;
                    logger.info(">>>>>>>>>>> xxl-conf refresh accessToken success, accessTokenStoreNew: {}", accessTokenStoreNewJson);
                }

            }
        }, RegistryCacheHelpler.REGISTRY_BEAT_TIME * 1000, true);
        accessTokenThread.start();

    }

    public void stop() {
        if (accessTokenThread != null) {
            accessTokenThread.stop();
        }
    }

    // ---------------------- helper ----------------------


    /**
     * valid Request Token
     *
     * @param accessToken  accessToken
     * @return true means valid
     */
    public boolean validRequestToken(String accessToken) {
        return accessToken!=null
                && accessTokenStore.contains(accessToken);
    }

}
