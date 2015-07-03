package com.shine.nettyzk;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;


/**
 * Unit test for simple App.
 */
public class AppTest {
	
	@Test
	public void testCreateZKClient(){
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//		CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(
//				"127.0.0.1:2181", retryPolicy);
		CuratorFramework curatorClient = CuratorFrameworkFactory.builder().namespace("myapp")
							.connectString("127.0.0.1:2181").retryPolicy(retryPolicy).build();
		curatorClient.start();
		try {
			System.out.println("curatorClient.getNamespace(); "
					+ curatorClient.getNamespace());
			Stat stat = curatorClient.checkExists().forPath("/bbb");
			if (stat == null) {
				curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/bbb", "test".getBytes());
			} else {
				System.out.println("stat: " + stat);
			}
			
			byte[] b = curatorClient.getData().forPath("/bbb");
			System.out.println(new String(b, Charset.defaultCharset()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		curatorClient.close();
	}
	
	@Test
	public void testWriteProperties() {
		try {

			Properties props = new Properties();
			InputStream in = new BufferedInputStream(new FileInputStream("node.properities"));
			props.load(in);
			in.close();

			// 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			OutputStream fos = new FileOutputStream("node.properities");
			props.setProperty("servername", "servername");
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			props.store(fos, "Update servername value servername");
			fos.flush();
			fos.close();
			
			System.out.println(props.getProperty("servername"));
			
			Thread.sleep(10000);
		} catch (Exception e) {
			System.err.println("属性文件更新错误");
		}
	}
}
