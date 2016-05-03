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
import org.codehaus.jettison.json.JSONException;
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
		if(0 != oneHopArray.length()){
			resultArray.put(oneHopArray);
		}
		if(0 != twoHopArray.length()){
			resultArray.put(twoHopArray);
		}
		if(0 != triHopArray.length()){
			resultArray.put(triHopArray);
		}
		
		System.out.println("cost :"+(System.currentTimeMillis()-startTime)+"ms");
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
			JSONObject ajson = httpService("Id="+a);
			JSONObject bjson = httpService("Id="+b);
			System.out.println("a:"+ajson.toString()+"\nb:"+bjson.toString());
			JSONArray aRidArray = new JSONArray();
			JSONArray bRidArray = new JSONArray();
			try {
				aRidArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				bRidArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				boolean pass = false;
				for(int i = 0;i < aRidArray.length();i++){
					for(int j = 0;j < bRidArray.length();j++){
						if(aRidArray.get(i) == bRidArray.get(j)){
							jsonArray.put(a);
							jsonArray.put(b);
							pass = true;
							break;
						}
					}
					if(pass){
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			JSONObject ajson = httpService("Id="+a);
			System.out.println("a:"+ajson.toString());
			JSONArray aAAArray = new JSONArray();
			try {
				aAAArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				for(int i = 0;i < aAAArray.length();i++){
					if(aAAArray.getJSONObject(i).getInt("AuId") == b){
						jsonArray.put(a);
						jsonArray.put(b);
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			JSONObject bjson = httpService("Id="+b);
			System.out.println("b:"+bjson.toString());
			JSONArray bAAArray = new JSONArray();
			try {
				bAAArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				for(int i = 0;i < bAAArray.length();i++){
					if(bAAArray.getJSONObject(i).getInt("AuId") == a){
						jsonArray.put(a);
						jsonArray.put(b);
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{                        //D case:[AA.AuId,AA.AuId]
			//no paths
		}
		return jsonArray;
	}

	public JSONObject httpService(String exp) {
		URIBuilder builder;
		try{
			builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
			builder.setParameter("expr", exp);
	        builder.setParameter("model", "latest");
	        builder.setParameter("count", "10000");
	        builder.setParameter("attributes", "Id,AA.AuId,AA.AfId,F.FId,J.JId,C.CId,RId");
	        builder.setParameter("subscription-key", Constant.subscribeKey);
	        URI uri = builder.build();
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
//		URIBuilder builder;
		try {
//			builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
//			builder.setParameter("expr", "Composite(AA.AuId="+id+")");
//	        builder.setParameter("model", "latest");
//	        builder.setParameter("count", "10000");
//	        builder.setParameter("attributes", "Id");
//	        builder.setParameter("subscription-key", Constant.subscribeKey);
//	        URI uri = builder.build();
			
			String expr = "Composite(AA.AuId="+id+")";
	        JSONObject responseJson = httpService(expr);
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
//		URIBuilder builder;
		try {
//			builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
//			builder.setParameter("expr", "Composite(AA.AuId="+id+")");
//	        builder.setParameter("model", "latest");
//	        builder.setParameter("count", "10000");
//	        builder.setParameter("attributes", "Id");
//	        builder.setParameter("subscription-key", Constant.subscribeKey);
//	        URI uri = builder.build();

	        String expr = "Composite(AA.AuId="+id+")";
	        JSONObject responseJson = httpService(expr);
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
