package com.xxl.conf.admin.model.adaptor;

import com.xxl.conf.admin.model.dto.ConfDataLogDTO;
import com.xxl.conf.admin.model.entity.ConfDataLog;
import com.xxl.tool.core.DateTool;

public class ConfDataLogAdaptor {

    public static ConfDataLogDTO adapt(ConfDataLog confDataLog) {

        ConfDataLogDTO confDataLogDTO = new ConfDataLogDTO();
        confDataLogDTO.setId(confDataLog.getId());
        confDataLogDTO.setDataId(confDataLog.getDataId());
        confDataLogDTO.setOldValue(confDataLog.getOldValue());
        confDataLogDTO.setValue(confDataLog.getValue());
        confDataLogDTO.setOptUsername(confDataLog.getOptUsername());
        confDataLogDTO.setAddTime(DateTool.formatDateTime(confDataLog.getAddTime()));
        confDataLogDTO.setUpdateTime(DateTool.formatDateTime(confDataLog.getUpdateTime()));
        return confDataLogDTO;
    }
}
