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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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
                    // 1、build filepath: /data/applogs/xxl-conf/{confdata}/{env}/{appname01}.properties
                    File filePathDir = new File(xxlConfBootstrap.getFilepath().trim(), "confdata");
                    FileTool.createDirectories(filePathDir);

                    // 2、load and write file-data
                    ConcurrentHashMap<String, ConcurrentHashMap<String, ConfDataCacheDTO>> confData = xxlConfBootstrap.getLocalCacheHelper().getAllConfData();
                    if (MapTool.isNotEmpty(xxlConfBootstrap.getLocalCacheHelper().getAllConfData())) {
                        for (String appname : confData.keySet()) {
                            // 3.1、file name
                            String fileName = buildFilePath(xxlConfBootstrap.getEnv(), appname);

                            // 3.2、file data
                            ConcurrentHashMap<String, ConfDataCacheDTO> keyMap = confData.get(appname);
                            String keyMapJson = GsonTool.toJson(keyMap);

                            // 3.3、write data
                            FileTool.writeString(fileName, keyMapJson);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("XxlConfFileHelper-confFileThread error: " + e.getMessage(), e);
                }
            }
        }, 5 * 60 * 1000, true);
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

    private String buildFilePath(String env, String appname) throws IOException {
        // build filepath: /data/applogs/xxl-conf/{confdata}/{env}/{appname01}.properties
        File filePathDir = new File(xxlConfBootstrap.getFilepath().trim(), "confdata");
        String fileName = filePathDir.getPath()
                .concat(File.separator).concat(xxlConfBootstrap.getEnv())
                .concat(File.separator).concat(appname)
                .concat(".txt");

        // create dir
        FileTool.createDirectories(new File(fileName));
        return fileName;
    }

    /**
     * query data
     *
     * @param env env
     * @param appname appname
     * @return confData
     */
    public HashMap<String, ConfDataCacheDTO> queryData(String env, String appname) throws IOException {
        // 1、build filepath: /data/applogs/xxl-conf/{confdata}/{env}/{appname01}.properties
        File filePathDir = new File(xxlConfBootstrap.getFilepath().trim(), "confdata");
        String fileName = filePathDir.getPath()
                .concat(File.separator).concat(xxlConfBootstrap.getEnv())
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
        return GsonTool.fromJsonMap(keyMapJson, String.class, ConfDataCacheDTO.class);
    }

}