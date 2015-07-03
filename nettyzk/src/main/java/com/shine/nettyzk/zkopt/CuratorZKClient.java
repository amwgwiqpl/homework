//package com.shine.nettyzk.zkopt;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.curator.RetryPolicy;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.framework.api.PathAndBytesable;
//import org.apache.curator.framework.recipes.cache.PathChildrenCache;
//import org.apache.curator.retry.ExponentialBackoffRetry;
//import org.apache.curator.utils.PathUtils;
//import org.apache.zookeeper.CreateMode;
//import org.apache.zookeeper.data.Stat;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.collect.Lists;
//import com.shine.nettyzk.model.ServerInfo;
//import com.shine.nettyzk.util.Constant;
//import com.shine.nettyzk.zkopt.listener.OnlinePathCache;
//import com.shine.nettyzk.zkopt.listener.PathCacheMap;
//
//public class CuratorZKClient {
//	private static Logger logger = LoggerFactory.getLogger(CuratorZKClient.class);
//	
//	private CuratorFramework zkCurator;
//	private static CuratorZKClient instance;
//	private volatile boolean isExec = false;
//	private AtomicInteger atomicInteger = new AtomicInteger(0);
//	private ServerInfo serverInfo = null;
//	private List<PathChildrenCache> pathChildrenCacheList = new ArrayList<PathChildrenCache>();
//	
//	public static CuratorZKClient getInstance() {
//		if (instance == null) {
//			synchronized (CuratorZKClient.class) {
//				if (instance == null) {
//					instance = new CuratorZKClient();
//				}
//			}
//			if (instance == null) {
//				instance = new CuratorZKClient();
//			}
//		}
//		return instance;
//	}
//	
//	private CuratorZKClient() {
//		try {
//			InputStream is = this.getClass().getClassLoader().getResourceAsStream("zk.properities");
//			if (is == null) {
//				throw new RuntimeException("zk config file not found. Please make sure 'zk.properties' is in in your classpath");
//			}
//			
//			Properties zkConfig = new Properties();
//			zkConfig.load(is);
//			
//			String connectString = zkConfig.getProperty("connectString");
//			int sessionTimeout = Integer.parseInt(zkConfig.getProperty("sessionTimeout"));
//			String namespace = zkConfig.getProperty("namespace");
//			
//			if (StringUtils.isEmpty(namespace) || namespace.startsWith("/")) {
//				throw new RuntimeException("zk命名空间配置异常，或以'/'开头。");
//			}
//			
//			RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//			this.zkCurator = CuratorFrameworkFactory.builder().connectString(connectString).namespace(namespace).sessionTimeoutMs(sessionTimeout).retryPolicy(retryPolicy).build();
//			this.zkCurator.start();
//		} catch (Exception e) {
//			logger.error("zk初始化异常：", e);
//		}
//		
//		if (!this.isExec) {
//			this.createZkDirectory(this.getDirectoryMap());
//			this.registerServer();
//			this.onlineServer();
//			this.isExec = true;
//		}
//	}
//
//	/**
//	 * 创建项目在zk中要使用的目录结构
//	 * 
//	 * @param directory {@value CuratorZKClient.getDirectoryMap()}
//	 */
//	public void createZkDirectory(List<String> directory){
//		try {
//			PathAndBytesable<String> createPath = this.zkCurator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT);
//				
//			for (String path : directory) {
//				Stat stat = this.zkCurator.checkExists().forPath(path);
//				if (stat == null) {
//					createPath.forPath(path, "".getBytes());
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			logger.error("在zk环境初始化目录结构时发生异常。", e);
//		}
//	}
//	
//	/**
//	 * 注册系统服务信息，并保存zk获取的内容
//	 */
//	private void registerServer() {
//		try {
//			InputStream is = this.getClass().getClassLoader().getResourceAsStream("node.properities");
//			if (is == null) {
//				throw new RuntimeException("node config file not found. Please make sure 'node.properties' is in in your classpath");
//			}
//			
//			Properties zkConfig = new Properties();
//			zkConfig.load(is);
//			is.close();
//			
//			String serverName = zkConfig.getProperty("servername");
//			String remoteIP = zkConfig.getProperty("remoteIP");
//			int remotePort = Integer.parseInt(zkConfig.getProperty("remotePort"));
//			// Server Name，全局唯一
//			String nodePath = Constant.PathDirectory.CONFIG_NODES + this.renameServerName(serverName, false);
//			if (null != this.zkCurator.checkExists().forPath(nodePath)) {
//				String name = "";
//				do {
//					name = this.renameServerName(serverName, true);
//				} while (null != this.zkCurator.checkExists().forPath(Constant.PathDirectory.CONFIG_NODES + name));
//				this.closeServer();
//				throw new RuntimeException("服务名已经存在，请修改。建议使用[" + name + "]做为服务名。");
//			}
//			
//			String server = String.format("%s:%d", remoteIP, remotePort);
//			logger.info("当前注册服务路径[{}]，所使用的数据[{}]", nodePath, server);
//			this.zkCurator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath, server.getBytes());
//			
//			serverName = nodePath.substring(nodePath.lastIndexOf("/"));
//			serverInfo = new ServerInfo(serverName, remoteIP, remotePort);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			this.closeServer();
//			throw new RuntimeException("注册服务时发生异常.", e);
//		}
//	}
//	
//	/**
//	 * 服务上线，等待工作
//	 */
//	private void onlineServer() {
//		try {
//			pathChildrenCacheList.add(new OnlinePathCache(zkCurator, serverInfo));
//			for (PathChildrenCache cache : pathChildrenCacheList) {
//				cache.start();
//			}
//			
//			Thread.sleep(100000);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * 目录结构
//	 * 
//	 * @return
//	 */
//	private List<String> getDirectoryMap() {
//		List<String> directoryMap = Lists.newArrayList(
//				Constant.PathDirectory.CONFIG_NODES,
//				Constant.PathDirectory.CONFIG_COMMONCFG,
//				Constant.PathDirectory.NODES,
//				Constant.PathDirectory.PERFORMANCE,
//				Constant.PathDirectory.COMMANDS);
//
//		return directoryMap;
//	}
//
//	/**
//	 * 关闭服务
//	 */
//	public void closeServer(){
//		try {
//			if (this.pathChildrenCacheList != null && this.pathChildrenCacheList.size() > 0) {
//				for(PathChildrenCache cache : this.pathChildrenCacheList){
//					cache.close();
//				}
//			}
//			this.zkCurator.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			logger.error("关闭服务时发生异常.", e);
//		}
//	}
//	
//	public CuratorFramework getZkCurator() {
//		return zkCurator;
//	}
//	
//	/**
//	 * 修改服务名
//	 * @param serverName
//	 * @return
//	 */
//	public String renameServerName(String serverName, boolean append){
//		if (!serverName.startsWith("/")) {
//			serverName = "/" + serverName;
//		}
//		if (append) {
//			int index = serverName.lastIndexOf("-");
//			if (index > 0) {
//				serverName = serverName.substring(0, index + 1).trim() + this.atomicInteger.incrementAndGet();
//			} else {
//				serverName += "-" + this.atomicInteger.incrementAndGet();
//			}
//		}
//		try {
//			PathUtils.validatePath(serverName);
//		} catch (IllegalArgumentException e) {
//			logger.error("修改服务名", e);
//		}
//		return serverName;
//	}
//}
