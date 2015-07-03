package com.shine.nettyzk.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Address {
	private String name;
	private String host;
	private int port;

	public Address() {
		this.name = "Leader";
		this.host = "127.0.0.1";
		this.port = 8666;
	}

	public Address(String name, String host, int port) {
		this();
		this.name = name;
		this.host = host;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		return 31 * name.hashCode() + host.hashCode() + port;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Address)) {
			return false;
		}
		
		Address address = (Address) obj;
		return (address.getName().equals(this.getHost()) && address.getHost().equals(this.getHost()) && address.getPort() == this.port);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
