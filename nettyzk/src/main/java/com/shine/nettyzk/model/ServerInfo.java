package com.shine.nettyzk.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ServerInfo {
	private String serverName;
	private String serverIp;
	private int serverPort;

	public ServerInfo() {
		this.serverName = "Leader";
		this.serverIp = "127.0.0.1";
		this.serverPort = 8666;
	}

	public ServerInfo(String serverName, String serverIp, int serverPort) {
		this();
		this.serverName = serverName;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
