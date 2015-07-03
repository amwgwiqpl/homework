package com.shine.nettyzk.util;

public final class Constant {

	/**
	 * 目录结构
	 * 
	 * @author wowplus
	 *
	 */
	public static final class PathDirectory {
		/**
		 * 存入每个Server的访问Ip，端口等信息
		 */
		public static final String CONFIG_NODES = "/config/nodes";
		/**
		 * 通讯过程所用到的参数，如bufferSize等
		 */
		public static final String CONFIG_COMMONCFG = "/config/commoncfg";
		/**
		 * 节点上下线状态
		 */
		public static final String NODES = "/nodes";
		
		public static final String COMMANDS = "/commands";
		public static final String PERFORMANCE = "/performance";
		
	}
}
