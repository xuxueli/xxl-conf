package com.xxl.conf.admin.core.model;

/**
 * 配置节点
 * @author xuxueli 2015-9-4 15:26:01
 */
public class XxlConfNode {

	private int id;
	private String nodeKey; 		// key of prop
	private String nodeValue; 		// value of prop [in sqlite]
	private String nodeDesc;		// description of prop
	
	private String nodeValueReal; 	// value of prop [in zk]

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public String getNodeValueReal() {
		return nodeValueReal;
	}

	public void setNodeValueReal(String nodeValueReal) {
		this.nodeValueReal = nodeValueReal;
	}

}
