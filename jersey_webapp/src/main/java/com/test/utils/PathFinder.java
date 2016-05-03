package com.test.utils;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class PathFinder {
	
	public int a ;
	public int b ;
	public Boolean aIsID = false ;
	public Boolean bIsID = false ;
	public long startTime ;

	
	public PathFinder(int a, int b) {
		// TODO Auto-generated constructor stub
		this.setA(a);
		this.setB(b);
		this.setaIsID(a);
		this.setbIsID(b);
		this.setStartTime();
	}

	public JSONArray getPaths() {
		// TODO Auto-generated method stub
		JSONArray oneHopArray = get1HopPaths();
		JSONArray twoHopArray = get2HopPaths();
		JSONArray triHopArray = get3HopPaths();
		JSONArray resultArray = new JSONArray();
		if(null != oneHopArray){
			resultArray.put(oneHopArray);
		}
		if(null != twoHopArray){
			resultArray.put(twoHopArray);
		}
		if(null != triHopArray){
			resultArray.put(triHopArray);
		}
		return resultArray;
	}

	private JSONArray get3HopPaths() {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			
		}else{                        //D case:[AA.AuId,AA.AuId]
			
		}
		return jsonArray;
	}

	private JSONArray get2HopPaths() {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			
		}else{                        //D case:[AA.AuId,AA.AuId]
			
		}
		return jsonArray;
	}

	private JSONArray get1HopPaths() {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			
		}else{                        //D case:[AA.AuId,AA.AuId]
			//no paths
		}
		return jsonArray;
	}

	public JSONObject httpService(URI uri) {
		try{
			HttpClient httpclient = HttpClients.createDefault();
			
			HttpGet request = new HttpGet(uri);
	        HttpResponse response = httpclient.execute(request);
	        HttpEntity entity = response.getEntity();
	        String strEntity  = "[]";
	        strEntity = EntityUtils.toString(entity);
	        
	        JSONObject jsonobject = new JSONObject(strEntity);
			return jsonobject;
		}catch(Exception e){
			System.out.println("http Exception:"+e.getMessage());
		}
		return null;
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

//	        System.out.println("uri is: "+uri.toASCIIString());
	        
	        JSONObject responseJson = httpService(uri);
//	        System.out.println(responseJson.toString());
	        if(responseJson.getJSONArray("entities").length() == 0){
	        	this.aIsID = true;
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception:"+e.getMessage());
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
