package com.shine.nettyzk.zkopt.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shine.nettyzk.model.ServerInfo;
import com.shine.nettyzk.util.Constant;

public class OnlinePathCache extends PathChildrenCache {

	public OnlinePathCache(CuratorFramework client, ServerInfo serverInfo) {
		super(client, Constant.PathDirectory.CONFIG_NODES, true);
		this.getListenable().addListener(new OnlineListenable(serverInfo));
	}
}

class OnlineListenable implements PathChildrenCacheListener {

	private final Logger logger = LoggerFactory.getLogger(OnlineListenable.class);
	
	private ServerInfo serverInfo;
	
	public OnlineListenable(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
			throws Exception {
		String path = event.getData().getPath();
		int index = path.lastIndexOf("/");
		if (index > 0) {
			path = path.substring(index);
		}
		if (!this.serverInfo.getServerName().equals(path)) {
			logger.info("非当前系统{}", path);
			return;
		}
		switch (event.getType()) {
		case CHILD_ADDED:
			String cachePath = Constant.PathDirectory.NODES + path;
			if (null == client.checkExists().forPath(cachePath)) {
				client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(cachePath, "true".getBytes());
			} else {
				client.setData().forPath(cachePath, "true".getBytes());
			}
			break;
		case CHILD_REMOVED:
			client.delete().deletingChildrenIfNeeded().inBackground().forPath(path);
			break;
		default:
			logger.info("非CHILD_ADDED类型不做监控.{}", event.getType());
			break;
		}
	}
}
