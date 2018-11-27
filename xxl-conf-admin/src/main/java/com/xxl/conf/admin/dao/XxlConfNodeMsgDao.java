package com.xxl.conf.admin.dao;

import com.xxl.conf.admin.core.model.XxlConfNodeMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xuxueli on 16/10/8.
 */
@Mapper
public interface XxlConfNodeMsgDao {

	public void add(XxlConfNodeMsg xxlConfNode);

	public List<XxlConfNodeMsg> findMsg(@Param("readedMsgIds") List<Integer> readedMsgIds);

	public int cleanMessage(@Param("messageTimeout") int messageTimeout);

}
