package com.test.utils;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class PathFinder {
	
	public int a ;
	public int b ;
	public Boolean aIsID = false ;
	public Boolean bIsID = false ;
	public long startTime ;

	public HttpClientUtils httpClientUtils;
	
	public PathFinder(int a, int b) {
		// TODO Auto-generated constructor stub
		this.setA(a);
		this.setB(b);
		this.setaIsID(a);
		this.setbIsID(b);
		this.setStartTime();
		this.httpClientUtils = new HttpClientUtils();
	}

	public JSONArray getPaths() {
		// TODO Auto-generated method stub
		JSONArray oneHopArray = get1HopPaths();
		JSONArray twoHopArray = get2HopPaths();
		JSONArray triHopArray = get3HopPaths();
		JSONArray resultArray = new JSONArray();
		resultArray.put(oneHopArray);
		resultArray.put(twoHopArray);
		resultArray.put(triHopArray);
		return resultArray;
	}

	private JSONArray get3HopPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	private JSONArray get2HopPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	private JSONArray get1HopPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject httpService(URI uri) throws JSONException, ClientProtocolException, IOException{
		HttpClient httpclient = this.httpClientUtils.newClient();
		HttpGet request = new HttpGet(uri);
        HttpResponse response = httpclient.execute(request);
        HttpEntity entity = response.getEntity();
        String strEntity  = "[]";
        strEntity = EntityUtils.toString(entity);
        JSONObject jsonobject = new JSONObject(strEntity);
		return jsonobject;
	}
	
	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public Boolean getaIsID() {
		return aIsID;
	}

	public void setaIsID(int id){
		URIBuilder builder;
		try {
			builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
			builder.setParameter("expr", "Composite(AA.AuId="+id+")");
	        builder.setParameter("model", "latest");
	        builder.setParameter("count", "10000");
	        builder.setParameter("attributes", "Id");
	        builder.setParameter("subscription-key", Constant.subscribeKey);
	        URI uri = builder.build();

	        JSONObject responseJson = httpService(uri);
	        if(responseJson.getJSONArray("entities").length() == 0){
	        	this.aIsID = true;
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	public Boolean getbIsID() {
		return bIsID;
	}

	public void setbIsID(int id) {
		URIBuilder builder;
		try {
			builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
			builder.setParameter("expr", "Composite(AA.AuId="+id+")");
	        builder.setParameter("model", "latest");
	        builder.setParameter("count", "10000");
	        builder.setParameter("attributes", "Id");
	        builder.setParameter("subscription-key", Constant.subscribeKey);
	        URI uri = builder.build();

	        JSONObject responseJson = httpService(uri);
	        if(responseJson.getJSONArray("entities").length() == 0){
	        	this.bIsID = true;
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime() {
		this.startTime = System.currentTimeMillis();
	}
}
