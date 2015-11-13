package com.xxl.test;

import java.sql.*;

import com.xxl.core.util.PathUtil;

public class TestSqliteDb {

	public static void main(String[] args) {
		String znodePath = "/qqq";
		System.out.println("1:" + znodePath.lastIndexOf("/"));
		String parentPath = znodePath.substring(0, znodePath.lastIndexOf("/"));
		System.out.println("2:" + parentPath);
	}
	public static void main2(String[] args) {

		String driver = "org.sqlite.JDBC";
		// 红色部分路径要求全小写，大写会报错
		//String url = "jdbc:sqlite://E:/本地文件夹/UP/sqlite/cfg.db";
		String url = "jdbc:sqlite://" + PathUtil.classPath() +  "cfg.db";
		url = "jdbc:sqlite::resource:cfg.db";
		System.out.println(url);
		
		// String user="root";
		// String password="123456";
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			Class.forName(driver); // 加载驱动
			conn = DriverManager.getConnection(url);// 创建连接
			String sql = "SELECT * FROM znode_entity where znode_key = ?";
			psmt = conn.prepareStatement(sql);// 创建Statement用来发送语句
			psmt.setString(1, "key01");
			rs = psmt.executeQuery();// 返回结果集
			if (rs.next()) {
				String name = rs.getString("znode_value");
				System.out.println(name);
			}
		} catch (Exception e) {
		}
		try {
			rs.close();
			psmt.close();
			conn.close();
		} catch (Exception e) {
		}
	}
}