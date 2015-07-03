package com.shine.nettyzk;

import com.shine.nettyzk.zkopt.CuratorZKClient;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		 CuratorZKClient.getInstance().closeServer();
	}
}
