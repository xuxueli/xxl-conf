package com.xxl.conf.core.confdata;

import com.xxl.conf.core.bootstrap.XxlConfBootstrap;
import com.xxl.conf.core.openapi.confdata.model.ConfDataCacheDTO;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.io.FileTool;
import com.xxl.tool.json.GsonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * file conf
 *
 * @author xuxueli 2018-02-01 19:11:25
 */
public class XxlConfFileHelper {
    private static final Logger logger = LoggerFactory.getLogger(XxlConfFileHelper.class);

    // ---------------------- init ----------------------

    private volatile XxlConfBootstrap xxlConfBootstrap;
    public XxlConfFileHelper(XxlConfBootstrap xxlConfBootstrap) {
        this.xxlConfBootstrap = xxlConfBootstrap;
    }

    // ---------------------- start / stop ----------------------

    private CyclicThread confFileThread;

    /**
     * start
     */
    public void start(){

        // valid filepath
        if (StringTool.isBlank(xxlConfBootstrap.getFilepath())) {
            logger.warn(">>>>>>>>>>> xxl-conf XxlConfFileHelper-confFileThread not enabled, filepath not found.");
            return;
        }

        // init confFileThread
        confFileThread = new CyclicThread("XxlConfFileHelper-confFileThread", true, new Runnable() {
            @Override
            public void run() {
                try {
                    // pass until localCacheHelper ready, wait other module init
                    if (xxlConfBootstrap.getLocalCacheHelper() == null) {
                        TimeUnit.SECONDS.sleep(5);
                        if (xxlConfBootstrap.getLocalCacheHelper() == null) {
                            return;
                        }
                    }

                    // load and write file-data
                    ConcurrentHashMap<String, ConcurrentHashMap<String, ConfDataCacheDTO>> confData = xxlConfBootstrap.getLocalCacheHelper().getAllConfData();
                    if (MapTool.isNotEmpty(xxlConfBootstrap.getLocalCacheHelper().getAllConfData())) {
                        for (String appname : confData.keySet()) {
                            // 1、build file-name
                            String fileName = buildFilePath(appname);

                            // 2、generate file-data
                            ConcurrentHashMap<String, ConfDataCacheDTO> keyMap = confData.get(appname);
                            String keyMapJson_new = GsonTool.toJson(new TreeMap<>(keyMap));

                            // 3、write data
                            String keyMapJson_old = FileTool.exists(fileName)?FileTool.readString(fileName): null;
                            if (!keyMapJson_new.equals(keyMapJson_old)) {
                                FileTool.writeString(fileName, keyMapJson_new);
                                logger.info(">>>>>>>>>>> xxl-conf XxlConfFileHelper-confFileThread found conf changed, overwrite finish: fileName {}, appname: {}", fileName, appname);
                            }
                        }
                    }
                } catch (Throwable e) {
                    throw new RuntimeException("XxlConfFileHelper-confFileThread error: " + e.getMessage(), e);
                }
            }
        }, 3 * 60 * 1000, true);
        confFileThread.start();
    }

    /**
     * stop
     */
    public void stop(){
        if (confFileThread != null) {
            confFileThread.stop();
        }
    }

    // ---------------------- tool ----------------------

    private String buildFilePath(String appname) throws IOException {
        // build filepath: /data/applogs/xxl-conf/{confdata}/{env}/{appname01}.txt
        File filePathDir = new File(xxlConfBootstrap.getFilepath().trim(), "confdata");

        // build filename
        return filePathDir.getPath()
                .concat(File.separator).concat(xxlConfBootstrap.getEnv())
                .concat(File.separator).concat(appname)
                .concat(".txt");
    }

    /**
     * query data
     *
     * @param env env
     * @param appname appname
     * @return confData
     */
    public ConcurrentHashMap<String, ConfDataCacheDTO> queryData(String env, String appname) throws IOException {
        // 1、build filepath: /data/applogs/xxl-conf/{confdata}/{env}/{appname01}.properties
        File filePathDir = new File(xxlConfBootstrap.getFilepath().trim(), "confdata");
        String fileName = filePathDir.getPath()
                .concat(File.separator).concat(env)
                .concat(File.separator).concat(appname)
                .concat(".txt");

        // valid
        if (!FileTool.exists(fileName)) {
            return null;
        }

        // 2、read data
        String keyMapJson = FileTool.readString(fileName);
        if (StringTool.isBlank(keyMapJson)) {
            return null;
        }
        Map<String, ConfDataCacheDTO> keyMap = GsonTool.fromJsonMap(keyMapJson, String.class, ConfDataCacheDTO.class);
        return new ConcurrentHashMap<>(keyMap);
    }

}