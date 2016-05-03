package com.test.utils;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

public class HttpClientUtils {
	private int connTimeout = 5000;
	private int soTimeout = 5000;
	private int buffSize = 128*1024;
	private String agent = "";
	private int maxRedirect = 3;
	private int maxTotal = 500;
	private int maxPerRoute = 100;

	public HttpClient newClient() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, this.connTimeout);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, this.soTimeout);
		params.setParameter(CoreConnectionPNames.SO_REUSEADDR, true);
		params.setParameter(CoreConnectionPNames.TCP_NODELAY, false);
		params.setParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, this.buffSize);
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		params.setParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME, "org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager");
		params.setParameter(ClientPNames.MAX_REDIRECTS, this.maxRedirect);
		ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager();
		ccm.setMaxTotal(maxTotal);
		ccm.setDefaultMaxPerRoute(maxPerRoute);
		return new DefaultHttpClient(ccm, params);
	}
	public int getConnTimeout() {
		return connTimeout;
	}
	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
	}
	public int getSoTimeout() {
		return soTimeout;
	}
	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}
	public int getBuffSize() {
		return buffSize;
	}
	public void setBuffSize(int buffSize) {
		this.buffSize = buffSize;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public int getMaxRedirect() {
		return maxRedirect;
	}
	public void setMaxRedirect(int maxRedirect) {
		this.maxRedirect = maxRedirect;
	}
	public int getMaxPerRoute() {
		return maxPerRoute;
	}
	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}
}