package com.shine.nettyzk.zkopt.listener;

import java.util.HashMap;

import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import com.shine.nettyzk.util.Constant;
import com.shine.nettyzk.zkopt.CuratorZKClient;

public class PathCacheMap extends HashMap<String, PathChildrenCache> {

	private static final long serialVersionUID = 1L;

	private static PathCacheMap instance;
	public PathCacheMap() {
		this.put(Constant.PathDirectory.CONFIG_NODES, new OnlinePathCache(null, null));
	}
	
	public static PathCacheMap getInstance() {
		if (instance == null) {
			synchronized (CuratorZKClient.class) {
				if (instance == null) {
					instance = new PathCacheMap();
				}
			}
			if (instance == null) {
				instance = new PathCacheMap();
			}
		}
		return instance;
	}
}
