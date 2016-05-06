package com.test.utils;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.enterprise.config.serverbeans.HttpService;


public class PathFinder {
	
	public long a ;
	public long b ;
	public Boolean aIsID = false ;
	public Boolean bIsID = false ;
	public long startTime ;
	
	private Logger logger ;

	
	public PathFinder(long a, long b) {
		// TODO Auto-generated constructor stub
		this.setA(a);
		this.setB(b);
		this.setaIsID(a);
		this.setbIsID(b);
		this.setStartTime();
		logger = LoggerFactory.getLogger(this.getClass());
	}

	public JSONArray getPaths() {
		// TODO Auto-generated method stub
		JSONArray oneHopArray = get1HopPaths();
		logger.info("find 1-hop cost :"+(System.currentTimeMillis()-startTime)+"ms");
		JSONArray twoHopArray = get2HopPaths();
		logger.info("find 2-hop cost :"+(System.currentTimeMillis()-startTime)+"ms");
		JSONArray triHopArray = get3HopPaths();
		logger.info("find 3-hop cost :"+(System.currentTimeMillis()-startTime)+"ms");
		JSONArray resultArray = new JSONArray();
		if(0 != oneHopArray.length()){
			resultArray.put(oneHopArray);
		}
		int i = 0;
		for(i = 0;i < twoHopArray.length();i++){
			try {
				resultArray.put(twoHopArray.getJSONArray(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.info("add twoHopArray error,exception:"+e.getMessage());
			}	
		}
		for(i = 0;i < triHopArray.length();i++){
			try {
				resultArray.put(triHopArray.getJSONArray(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.info("add twoHopArray error,exception:"+e.getMessage());
			}	
		}
//		System.out.println("cost :"+(System.currentTimeMillis()-startTime)+"ms");
		return resultArray;
	}

	private JSONArray get3HopPaths() {
		logger.info("start with 3-hops paths /////////////////////////");
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			
		}else{                        //D case:[AA.AuId,AA.AuId]
			
		}
		logger.info("end with 3-hops paths /////////////////////////");
		return jsonArray;
	}

	private JSONArray get2HopPaths() {
		logger.info("start with 2-hops paths...................");
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			JSONObject ajson = httpService("Id="+a);
			JSONObject bjson = httpService("Id="+b);      
			/*
			 * instance (1,1)
			 * todo: find RIds both in ajson and bjson then from them get different Ids
			 * */
			JSONArray aRIdArray = new JSONArray();
			JSONArray bRIdArray = new JSONArray();
			try {
				aRIdArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				bRIdArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				/*
				 * find RIds both in ajson and bjson
				 * */
				HashSet<Long> idsHS = new HashSet<Long>();
				for(int i = 0;i < aRIdArray.length();i++){
					for(int j = 0;j < bRIdArray.length();j++){
						/*
						 * process every found RId
						 * */
						if(aRIdArray.getLong(i) == bRIdArray.getLong(j)){
							JSONObject temp = httpService("RId="+aRIdArray.getInt(i));
							JSONArray idsArray = temp.getJSONArray("entities");
							for(int k = 0;k < idsArray.length(); k++){
								idsHS.add(idsArray.getJSONObject(k).getLong("Id"));
							}
						}
					}
				}
				//gen array from hashset
				Iterator<Long> it = idsHS.iterator();
				while(it.hasNext()){
					JSONArray tempArray = new JSONArray();
					tempArray.put(a);tempArray.put(it.next());tempArray.put(b);
					jsonArray.put(tempArray);
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (1,1) exception:"+e.getMessage());
			}
			
			/*
			 * instance (5,11)
			 * todo: find AuIds both in ajson and bjson
			 * */
			JSONArray aAAArray = new JSONArray();
			JSONArray bAAArray = new JSONArray();
			try {
				aAAArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				bAAArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				for(int i = 0;i < aAAArray.length();i++){
					for(int j = 0;j < bAAArray.length();j++){
						if(aAAArray.getJSONObject(i).getLong("AuId") == bAAArray.getJSONObject(j).getLong("AuId")){
							JSONArray temp = new JSONArray();
							temp.put(a);
							temp.put(aAAArray.getJSONObject(i).getLong("AuId"));
							temp.put(b);
							jsonArray.put(temp);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (5,11) exception:"+e.getMessage());
			}
			
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			JSONObject ajson = httpService("Id="+a);  
			/*
			 * instance (1,5)
			 * todo: find Ids both in (Ids from RIds in ajson) and (Ids from bjson)
			 * */
			JSONArray aRIdArray = new JSONArray();
			HashSet<Long> idsHS = new HashSet<Long>();
			try {
				aRIdArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				/*
				 * find Ids in aRIdArray
				 * */
				for(int i = 0;i < aRIdArray.length();i++){
					//get Ids where Rid = i and AuId = b
					JSONObject tempjson = httpService("And(RId="+aRIdArray.getLong(i)+",Composite(AA.AuId="+b+"))");
					JSONArray idsArray = tempjson.getJSONArray("entities");
					for(int j = 0;j < idsArray.length();j++){
						idsHS.add(idsArray.getJSONObject(j).getLong("Id"));
					}
				}
				//gen array from hashset
				Iterator<Long> it = idsHS.iterator();
				while(it.hasNext()){
					JSONArray tempArray = new JSONArray();
					tempArray.put(a);tempArray.put(it.next());tempArray.put(b);
					jsonArray.put(tempArray);
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (1,1) exception:"+e.getMessage());
			}
			
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			JSONObject ajson = httpService("Composite(AA.AuId = "+a+")");
			JSONObject bjson = httpService("Id="+b);
			/*
			 * instance (11,1)
			 * todo: get RIds from a
			 *       foreach Id in a get whoes Rids has common with Rids in b
			 * */
			JSONArray aEntitiesArray = new JSONArray();
			JSONArray bRIdArray = new JSONArray();
			try {
				aEntitiesArray = ajson.getJSONArray("entities");
				bRIdArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				for(int i = 0;i < aEntitiesArray.length(); i++){
					boolean pass = false;
					JSONArray aRidArray = aEntitiesArray.getJSONObject(i).getJSONArray("RId");
					for(int k = 0;k < aRidArray.length();k++){
						for(int j = 0;j < bRIdArray.length();j++){
							if(aRidArray.getLong(k) == bRIdArray.getLong(j)){
								JSONArray temp = new JSONArray();
								temp.put(a);temp.put(aEntitiesArray.getJSONObject(i).getLong("Id"));temp.put(b);
								jsonArray.put(temp);
								pass = true;
								break;
							}
						}
						if(pass){
							break;
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (1) exception:"+e.getMessage());
			}
		}else{                        //D case:[AA.AuId,AA.AuId]
			JSONObject ajson = httpService("Composite(AA.AuId = "+a+")");
			JSONObject bjson = httpService("Composite(AA.AuId = "+b+")");
			/*
			 * instance (9,10)
			 * todo: check if a and b from the same affiliation
			 * */
			JSONArray aEntitiesArray = new JSONArray();
			JSONArray bEntitiesArray = new JSONArray();
			long aAfId = 0;
			long bAfId = 0;
			try {
				aEntitiesArray = ajson.getJSONArray("entities");
				bEntitiesArray = bjson.getJSONArray("entities");
				int i = 0;
				boolean found = false;
				while(!found && i < aEntitiesArray.length()){
					JSONArray AAArray = aEntitiesArray.getJSONObject(i).getJSONArray("AA");
					for(int j = 0;j < AAArray.length(); j++){
						if((AAArray.getJSONObject(j).getLong("AuId") == a) && AAArray.getJSONObject(j).has("AfId")){
							aAfId = AAArray.getJSONObject(j).getLong("AfId");
							found = true;
							break;
						}
					}
					if(found){
						break;
					}
					i++;
				}
				i = 0;
				found = false;
				while(!found && i < bEntitiesArray.length()){
					JSONArray AAArray = bEntitiesArray.getJSONObject(i).getJSONArray("AA");
					for(int j = 0;j < AAArray.length(); j++){
						if((AAArray.getJSONObject(j).getLong("AuId") == a) && AAArray.getJSONObject(j).has("AfId")){
							bAfId = AAArray.getJSONObject(j).getLong("AfId");
							found = true;
							break;
						}
					}
					if(found){
						break;
					}
					i++;
				}
				if((aAfId == bAfId) && (aAfId != 0) && (bAfId != 0)){
					jsonArray.put(a);jsonArray.put(aAfId);jsonArray.put(b);
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (9,10) exception:"+e.getMessage());
			}
		}
		logger.info("end with 2-hops paths.......................");
		return jsonArray;
	}

	private JSONArray get1HopPaths() {
		logger.info("start with 1-hop paths +++++++++++++++++++++++++++");
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			/*
			 * instance (1)
			 * */
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
						if(aRidArray.getLong(i) == bRidArray.getLong(j)){
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
				logger.info("instance (1) exception:"+e.getMessage());
			}
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			/*
			 * instance （5）
			 * */
			JSONObject ajson = httpService("Id="+a);
			System.out.println("a:"+ajson.toString());
			JSONArray aAAArray = new JSONArray();
			try {
				aAAArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				for(int i = 0;i < aAAArray.length();i++){
					if(aAAArray.getJSONObject(i).getLong("AuId") == b){
						jsonArray.put(a);
						jsonArray.put(b);
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.info("instance (5) exception:"+e.getMessage());
			}
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			/*
			 * instance （11）
			 * */
			JSONObject bjson = httpService("Id="+b);
			System.out.println("b:"+bjson.toString());
			JSONArray bAAArray = new JSONArray();
			try {
				bAAArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				for(int i = 0;i < bAAArray.length();i++){
					if(bAAArray.getJSONObject(i).getLong("AuId") == a){
						jsonArray.put(a);
						jsonArray.put(b);
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.info("instance (11) exception:"+e.getMessage());
			}
		}else{                        //D case:[AA.AuId,AA.AuId]
			//no paths
		}
		logger.info("end with 1-hop paths+++++++++++++++++++++++++++++++");
		return jsonArray;
	}

	/*
	 * 传入expr的表达式，返回查询结果
	 * eg: https://oxfordhk.azure-api.net/academic/v1.0/evaluate?expr=Composite(AA.AuN==%27jaime%20teevan%27)&count=10000&attributes=Id,AA.AuId,AA.AfId,F.FId,J.JId,C.CId,RId&subscription-key=f7cc29509a8443c5b3a5e56b0e38b5a6
	 * */
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
	
	public long getA() {
		return a;
	}

	public void setA(long a2) {
		this.a = a2;
	}

	public long getB() {
		return b;
	}

	public void setB(long b2) {
		this.b = b2;
	}

	public Boolean getaIsID() {
		return aIsID;
	}

	public void setaIsID(long a2){
//		URIBuilder builder;
		try {
//			builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
//			builder.setParameter("expr", "Composite(AA.AuId="+id+")");
//	        builder.setParameter("model", "latest");
//	        builder.setParameter("count", "10000");
//	        builder.setParameter("attributes", "Id");
//	        builder.setParameter("subscription-key", Constant.subscribeKey);
//	        URI uri = builder.build();
			
			String expr = "Composite(AA.AuId="+a2+")";
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

	public void setbIsID(long b2) {
//		URIBuilder builder;
		try {
//			builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
//			builder.setParameter("expr", "Composite(AA.AuId="+id+")");
//	        builder.setParameter("model", "latest");
//	        builder.setParameter("count", "10000");
//	        builder.setParameter("attributes", "Id");
//	        builder.setParameter("subscription-key", Constant.subscribeKey);
//	        URI uri = builder.build();

	        String expr = "Composite(AA.AuId="+b2+")";
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
