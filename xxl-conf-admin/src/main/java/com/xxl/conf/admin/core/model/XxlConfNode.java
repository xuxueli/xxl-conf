package com.xxl.conf.admin.core.model;

import com.xxl.conf.core.XxlConfZkClient;

/**
 * 配置节点
 * @author xuxueli 2015-9-4 15:26:01
 */
public class XxlConfNode {

	private String nodeGroup;		// group of prop
	private String nodeKey; 		// key of prop
	private String nodeValue; 		// value of prop
	private String nodeDesc;		// description of prop

	private String groupKey;		// key of prop [in zk]
	private String nodeValueReal; 	// value of prop [in zk]

	public String getNodeGroup() {
		return nodeGroup;
	}

	public void setNodeGroup(String nodeGroup) {
		this.nodeGroup = nodeGroup;
	}

	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	public String getNodeValue() {
		return nodeValue;
	}

	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}

	public String getNodeDesc() {
		return nodeDesc;
	}

	public void setNodeDesc(String nodeDesc) {
		this.nodeDesc = nodeDesc;
	}

	public String getGroupKey() {
		return XxlConfZkClient.generateGroupKey(nodeGroup, nodeKey);
	}

	public String getNodeValueReal() {
		return nodeValueReal;
	}

	public void setNodeValueReal(String nodeValueReal) {
		this.nodeValueReal = nodeValueReal;
	}

}
