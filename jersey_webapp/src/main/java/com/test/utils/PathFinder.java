package com.test.utils;

import java.net.URI;
import java.util.ArrayList;
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


public class PathFinder {
	
	public long a ;
	public long b ;
	public Boolean aIsID = false ;
	public Boolean bIsID = false ;
	public long startTime ;
	
	private Logger logger ;

	
	public PathFinder() {
		super();
	}

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
		JSONArray twoHopArray = get2HopPaths(a,b,true,aIsID,bIsID);
		logger.info("find 2-hop cost :"+(System.currentTimeMillis()-startTime)+"ms");
		JSONArray triHopArray = get3HopPaths();
		logger.info("find 3-hop cost :"+(System.currentTimeMillis()-startTime)+"ms");
		JSONArray resultArray = new JSONArray();
		if(0 != oneHopArray.length()){
			resultArray.put(oneHopArray);
		}
		int i = 0;
		if(twoHopArray != null && twoHopArray.length() != 0){
			for(i = 0;i < twoHopArray.length();i++){
				try {
					resultArray.put(twoHopArray.getJSONArray(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					logger.info("add twoHopArray error,exception:"+e.getMessage());
				}	
			}
		}
		if(triHopArray != null && triHopArray.length() != 0){
			for(i = 0;i < triHopArray.length();i++){
				try {
					resultArray.put(triHopArray.getJSONArray(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					logger.info("add twoHopArray error,exception:"+e.getMessage());
				}	
			}
		}
//		//System.out.println("cost :"+(System.currentTimeMillis()-startTime)+"ms");
		return resultArray;
	}

	private JSONArray get3HopPaths() {
		logger.info("start with 3-hops paths /////////////////////////");
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		int i = 0;
		int j = 0;
		if(aIsID && bIsID){           //A case:[Id,Id]
			//[start] A case
			JSONObject ajson = httpService("Id="+a);
			/*
			 * instance (1,1,1) (1,2,6) (1,3,7) (1,4,8) (1,5,11)
			 * todo: find every Id from RIds from aId
			 *       use get2HopPaths() (A case,instance(1,1)) to cal paths
			 * */
			JSONArray aRIdArray = new JSONArray();
			HashSet<Long> idSet = new HashSet<Long>();
			try {
				aRIdArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				for(i = 0;i < aRIdArray.length();i++){
					idSet.add(aRIdArray.getLong(i));
				}
				logger.info("instance (1,1,1)--(1,5,11) find all a's related Ids:"+ idSet.size() +" consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				Get2HopRunnable runnable = new Get2HopRunnable(idSet,a, b, true,true,this,aIsID,bIsID);
				
				//[start] use three threads to run
				Thread t0 = new Thread(runnable);
				Thread t1 = new Thread(runnable);
				Thread t2 = new Thread(runnable);
				t0.start();
				t1.start();
				t2.start();
				t0.join();
				t1.join();
				t2.join();
				//[end]
				jsonArray = runnable.jsonArray;
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (1,1,1)--(1,5,11) exception:"+e.getMessage());
			}
			//System.out.println("instance (1,1,1)--(1,5,11) found:"+ jsonArray==null?"[]":jsonArray.length() + "条");
			logger.info("instance (1,1,1)--(1,5,11) done!");
			
			/*
			 * instance (2,6,1) (3,7,1) (4,8,1) (5,11,1)
			 * todo: find every Id whoes RId equals to b
			 *       use get2HopPaths() (A case,instance(1,1)) to cal paths
			 * */
			JSONArray bEntititesArray = new JSONArray();
			idSet.clear();
			try {
				bEntititesArray = httpService("RId=" + b).getJSONArray("entities");
				if(bEntititesArray.length() != 0){
					for(i = 0;i < bEntititesArray.length();i++){
						idSet.add(bEntititesArray.getJSONObject(i).getLong("Id"));
					}
				}
				logger.info("instance (2,6,1)--(5,11,1) find all a's related Ids :"+ idSet.size() +"  consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				Get2HopRunnable runnable = new Get2HopRunnable(idSet,a, b, false,false,this,aIsID,bIsID);
				
				//[start] use three threads to run
				Thread t0 = new Thread(runnable);
				Thread t1 = new Thread(runnable);
				Thread t2 = new Thread(runnable);
				t0.start();
				t1.start();
				t2.start();
				t0.join();
				t1.join();
				t2.join();
				//[end]
				JSONArray resultArray = runnable.jsonArray;
				if(resultArray != null && resultArray.length() != 0){
					for(i = 0;i < resultArray.length();i ++){
						jsonArray.put(resultArray.getJSONArray(i));
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (2,6,1)--(5,11,1) exception:"+e.getMessage());
			}
			//System.out.println("instance (2,6,1)--(5,11,1) found:"+ jsonArray==null?"[]":jsonArray.length() + "条");
			logger.info("instance (2,6,1)--(5,11,1) done!");

			//[end] A case
			
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			//[start] B case
			JSONObject ajson = httpService("Id="+a);
			JSONObject bjson = httpService("Composite(AA.AuId="+b+")");
			HashSet<Long> idSet = new HashSet<Long>();
			/*
			 * instance (1,1,5) (2,6,5) (3,7,5) (4,8,5) (5,11,5)
			 * todo: find every Id from AuId b
			 *       use get2HopPaths() (A case ) to cal paths
			 * */
			JSONArray bEntitiesArray = new JSONArray();
			try {
				bEntitiesArray = bjson.getJSONArray("entities");
				for(i = 0;i < bEntitiesArray.length();i++){
					idSet.add(bEntitiesArray.getJSONObject(i).getLong("Id"));
				}
				logger.info("instance (1,1,5)--(5,11,5) find all a's related Ids consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				Get2HopRunnable runnable = new Get2HopRunnable(idSet,a, b, false,true,this,true,true);
				
				//[start] use three threads to run
				Thread t0 = new Thread(runnable);
				Thread t1 = new Thread(runnable);
				Thread t2 = new Thread(runnable);
				t0.start();
				t1.start();
				t2.start();
				t0.join();
				t1.join();
				t2.join();
				//[end]
				jsonArray = runnable.jsonArray;
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (1,1,5)--(5,11,5) exception:"+e.getMessage());
			}
			//logger.info("instance (11,1,5) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				logger.info("instance (11,1,5) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
			//System.out.println("instance (1,1,5)--(5,11,5) found:"+ jsonArray==null?"[]":jsonArray.toString());
			/*
			 * instance (5,9,10)
			 * todo: find every AuId from Id a
			 *       find AfId of AuId b
			 *       find out who work with b
			 * */
			JSONArray aAAArray = new JSONArray();
			long bfId = 0;
			try {
				aAAArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				boolean pass = false;
				//get b's AfId
				for(i = 0;i < bEntitiesArray.length();i ++){
					JSONArray bAAArray = bEntitiesArray.getJSONObject(i).getJSONArray("AA");
					for(j = 0;j < bAAArray.length();j ++){
						if(a == bAAArray.getJSONObject(j).getLong("AuId") && bAAArray.getJSONObject(j).has("AfId")){
							bfId = bAAArray.getJSONObject(j).getLong("AfId");
							pass = true;
							break;
						}
					}
					if(pass){
						break;
					}
				}
				//get a's AuId compare with AfId
				if(bfId != 0){
					aAAArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
					for(i = 0;i < aAAArray.length();i++){
						JSONObject cojson = httpService("And(Composite(AA.AuId="+ aAAArray.getJSONObject(i).getLong("AuId") +",AA.AfId="+ bfId +"))");
						if(cojson.getJSONArray("entities").length() != 0){
							JSONArray tempArray = new JSONArray();
							tempArray.put(a);
							tempArray.put(aAAArray.getJSONObject(i).getLong("AuId"));
							tempArray.put(bfId);
							tempArray.put(b);
							jsonArray.put(tempArray);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (5,9,10) exception:"+e.getMessage());
			}
			//System.out.println("instance (5,9,10) found:"+ jsonArray==null?"[]":jsonArray.toString());
			logger.info("instance (5,9,10) done! time consumed:"+ (System.currentTimeMillis()-startTime) +"ms");
			
			//[end]
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			
			//[start] C case
			
			JSONObject ajson = httpService("Composite(AA.AuId = "+ a +")");
			JSONObject bjson = httpService("Id="+b);
			HashSet<Long> idSet = new HashSet<Long>();
			
			/*
			 * instance (11,1,1) (11,2,6) (11,3,7) (11,4,8) (11,5,11)
			 * todo: find every Id from Id from Id a
			 *       use get2HopPaths() (A case) to cal paths
			 * */
			JSONArray aEntitiesArray = new JSONArray();
			try {
				aEntitiesArray = ajson.getJSONArray("entities");
				for(i = 0;i < aEntitiesArray.length();i++){
					idSet.add(aEntitiesArray.getJSONObject(i).getLong("Id"));
				}
				logger.info("instance (11,1,1)--(11,5,11) find all a's related Ids:"+ idSet.size() +" consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				Get2HopRunnable runnable = new Get2HopRunnable(idSet, a, b, true,true,this,true,true);
				
				//[start] use ten threads to run
				Thread t0 = new Thread(runnable);
				Thread t1 = new Thread(runnable);
				Thread t2 = new Thread(runnable);
				t0.start();
				t1.start();
				t2.start();
				t0.join();
				t1.join();
				t2.join();
				//[end]
				jsonArray = runnable.jsonArray;
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (11,1,1)--(11,5,11) exception:"+e.getMessage());
			}
			logger.info("instance (11,1,1)--(11,5,11) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				logger.info("instance (11,1,1)--(11,5,11) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
			//System.out.println("instance (11,1,1)--(11,5,11) found:"+ jsonArray==null?"[]":jsonArray.toString());
			/*
			 * instance (9,10,11)
			 * todo: find every AuId from Id b
			 *       find AfId from a
			 *       find AuId in b who work in AfId
			 * */
			JSONArray bAAArray = new JSONArray();
			idSet.clear();
			long afId = 0;
			try {
				boolean pass = false;
				//get a's AfId
				for(i = 0;i < aEntitiesArray.length();i ++){
					JSONArray aAAArray = aEntitiesArray.getJSONObject(i).getJSONArray("AA");
					for(j = 0;j < aAAArray.length();j ++){
						if(a == aAAArray.getJSONObject(j).getLong("AuId") && aAAArray.getJSONObject(j).has("AfId")){
							afId = aAAArray.getJSONObject(j).getLong("AfId");
							pass = true;
							break;
						}
					}
					if(pass){
						break;
					}
				}
				//get b's AuId compare with AfId
				if(afId != 0){
					bAAArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
					for(i = 0;i < bAAArray.length();i++){
						JSONObject cojson = httpService("And(Composite(AA.AuId="+ bAAArray.getJSONObject(i).getLong("AuId") +",AA.AfId="+ afId +"))");
						if(cojson.getJSONArray("entities").length() != 0){
							JSONArray tempArray = new JSONArray();
							tempArray.put(a);
							tempArray.put(afId);
							tempArray.put(bAAArray.getJSONObject(i).getLong("AuId"));
							tempArray.put(b);
							jsonArray.put(tempArray);
						}
					}
				}	
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (9,10,11) exception:"+e.getMessage());
			}
			//System.out.println("instance (9,10,11) found:"+ jsonArray==null?"[]":jsonArray.toString());
			logger.info("instance (9,10,11) done! consumed:"+(System.currentTimeMillis()-startTime)+"ms");
			
			//[end]
		}else{                        //D case:[AA.AuId,AA.AuId]
			
			//[start] D case
			
			JSONObject bjson = httpService("Composite(AA.AuId="+ b +")");
			/*
			 * instance (11,1,5)
			 * todo: find every Id from AuId b
			 *       find RIds from above and AuId a composite paths
			 * */
			JSONArray bEntitiesArray = new JSONArray();
			HashSet<Long> idSet = new HashSet<Long>();
			try {
				bEntitiesArray = bjson.getJSONArray("entities");
				for(i = 0;i < bEntitiesArray.length();i++){
					idSet.add(bEntitiesArray.getJSONObject(i).getLong("Id"));
				}
				logger.info("instance (11,1,5) find all b's related Ids consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				Get2HopRunnable runnable = new Get2HopRunnable(idSet,a, b, false,true,this,false,true);
				
				//[start] use three threads to run
				Thread t0 = new Thread(runnable);
				Thread t1 = new Thread(runnable);
				Thread t2 = new Thread(runnable);
				t0.start();
				t1.start();
				t2.start();
				t0.join();
				t1.join();
				t2.join();
				//[end]
				jsonArray = runnable.jsonArray;
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (11,1,5) exception:"+e.getMessage());
			}
//			try {
//				bEntitiesArray = bjson.getJSONArray("entities");
//				if(bEntitiesArray.length() != 0){
//					for(i = 0;i < bEntitiesArray.length();i++){
//						JSONObject ridAndAuIdjson = httpService("And(RId="+ bEntitiesArray.getJSONObject(i).getLong("Id") +",Composite(AA.AuId="+ a +"))");
//						if(ridAndAuIdjson.getJSONArray("entities").length() != 0){
//							for(j = 0;j < ridAndAuIdjson.getJSONArray("entities").length(); j++){
//								JSONArray tempArray = new JSONArray();
//								tempArray.put(a);
//								tempArray.put(ridAndAuIdjson.getJSONArray("entities").getJSONObject(j).getLong("Id"));
//								tempArray.put(bEntitiesArray.getJSONObject(i).getLong("Id"));
//								tempArray.put(b);
//								jsonArray.put(tempArray);
//							}
//						}
//					}
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				logger.info("instance (11,1,5) exception:"+e.getMessage());
//			}
			
//			/*
//			 * instance (11,1,5)
//			 * todo: find every Id from AuId
//			 *       use get2HopPaths() (B case,instance(1,5)) to cal paths
//			 * */
//			JSONArray aIdArray = new JSONArray();
//			try {
//				aIdArray = ajson.getJSONArray("entities");
//				for(i = 0;i < aIdArray.length();i++){
//					JSONArray temp2Hop = get2HopPaths(aIdArray.getJSONObject(i).getLong("Id"),b,true,true,false);
//					for(j = 0;j < temp2Hop.length();j++){
//						JSONArray tpArray = new JSONArray();
//						tpArray.put(a);
//						tpArray.put(temp2Hop.getJSONArray(j).getLong(0));
//						tpArray.put(temp2Hop.getJSONArray(j).getLong(1));
//						tpArray.put(temp2Hop.getJSONArray(j).getLong(2));
//						jsonArray.put(tpArray);
//					}
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				logger.info("instance (11,1,5) exception:"+e.getMessage());
//			}
			//System.out.println("instance (11,1,5) found:"+ jsonArray==null?"[]":jsonArray.toString());
			logger.info("instance (11,1,5) done! consumed:"+(System.currentTimeMillis()-startTime)+"ms");
			//[end]
		}
		logger.info("end with 3-hops paths /////////////////////////");
		return jsonArray;
	}

	public JSONArray get2HopPaths(long a,long b,boolean first,boolean aIsID,boolean bIsID) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			//[start] A case
			JSONObject ajson = httpService("Id="+a);
			JSONObject bjson = httpService("Id="+b);
			
			try {
				if(ajson.getJSONArray("entities").length() == 0 || bjson.getJSONArray("entities").length() == 0 ){
					//System.out.println("now 2-hop A case has empty.ids:["+ a +","+ b +"]");
					return null;
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("now 2-hop A case has empty.ids:["+ a +","+ b +"]");
			}
			
			/*
			 * instance (1,1)        可选是否执行 first   为真是才执行
			 * todo: find RIds both in ajson and bjson then from them get different Ids
			 * */
			JSONArray aRIdArray = new JSONArray();
			try {
				aRIdArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				/*
				 * find RIds in ajson and check is there exist b
				 * */
				if(first){
					for(int i = 0;i < aRIdArray.length();i++){
						JSONObject ridAndBJson = httpService("And(Id="+ aRIdArray.getLong(i) +",RId="+ b +")");
						if(ridAndBJson.getJSONArray("entities").length() != 0){
							JSONArray tempArray = new JSONArray();
							tempArray.put(a);tempArray.put(aRIdArray.getLong(i));tempArray.put(b);
							jsonArray.put(tempArray);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (1,1) exception:"+e.getMessage());
			}
			/*
			 * instance (2,6)
			 * todo: find FIds both in ajson and bjson
			 * */
			JSONArray aFIdArray = new JSONArray();
			JSONArray bFIdArray = new JSONArray();
			JSONObject aFIdObject = new JSONObject();
			JSONObject bFIdObject = new JSONObject();
			try {
				aFIdObject = ajson.getJSONArray("entities").getJSONObject(0);
				bFIdObject = bjson.getJSONArray("entities").getJSONObject(0);
				if(aFIdObject.has("F") && bFIdObject.has("F")){
					aFIdArray = aFIdObject.getJSONArray("F");
					bFIdArray = bFIdObject.getJSONArray("F");
					for(int i = 0;i < aFIdArray.length();i++){
						for(int j = 0;j < bFIdArray.length();j++){
							if(aFIdArray.getJSONObject(i).getLong("FId") == bFIdArray.getJSONObject(j).getLong("FId")){
								JSONArray temp = new JSONArray();
								temp.put(a);
								temp.put(aFIdArray.getJSONObject(i).getLong("FId"));
								temp.put(b);
								jsonArray.put(temp);
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("2-hop instance (2,6) exception:"+e.getMessage());
			}
			/*
			 * instance (3,7)
			 * todo: find CId is equal within ajson and bjson
			 * */
			JSONObject aCjson = new JSONObject();
			JSONObject bCjson = new JSONObject();
			try {
				aCjson = ajson.getJSONArray("entities").getJSONObject(0);
				bCjson = bjson.getJSONArray("entities").getJSONObject(0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				logger.info("↑↑↑↑↑↑instance (3,7) exception:" + e1.getMessage());
			}
			if(aCjson.has("C") && bCjson.has("C")){
				try {
					if(aCjson.getJSONObject("C").getLong("CId") == bCjson.getJSONObject("C").getLong("CId")){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(aCjson.getJSONObject("C").getLong("CId"));
						temp.put(b);
						jsonArray.put(temp);
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.info("instance (3,7) exception:"+e.getMessage());
				}
			}
			/*
			 * instance (4,8)
			 * todo: find JId is equal within ajson and bjson
			 * */
			JSONObject aJjson = new JSONObject();
			JSONObject bJjson = new JSONObject();
			try {
				aJjson = ajson.getJSONArray("entities").getJSONObject(0);
				bJjson = bjson.getJSONArray("entities").getJSONObject(0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				logger.info("↑↑↑↑↑instance (4,8) exception:" + e1.getMessage());
			}
			if(aJjson.has("J") && bJjson.has("J")){
				try {
					if(aJjson.getJSONObject("J").getLong("JId") == bJjson.getJSONObject("J").getLong("JId")){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(aJjson.getJSONObject("J").getLong("JId"));
						temp.put(b);
						jsonArray.put(temp);
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.info("instance (4,8) exception:"+e.getMessage());
				}
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
			//[end]
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			//[start] B case
			JSONObject ajson = httpService("Id="+a);
			try {
				if(ajson.getJSONArray("entities").length() == 0 ){
					//System.out.println("now 2-hop B case has empty.ids:["+ a +","+ b +"]");
					return null;
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("now 2-hop B case has empty.ids:["+ a +","+ b +"]");
			}
			/*
			 * instance (1,5)
			 * todo: find RIds from a
			 *       check weather exist Id from RIds and AuId paper 
			 * */
			JSONArray aRIdArray = new JSONArray();
			try {
				aRIdArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");				
				for(int i = 0;i < aRIdArray.length();i++){
					JSONObject ridAndBJson = httpService("And(Id="+ aRIdArray.getLong(i) +",Composite(AA.AuId="+ b +"))");
					if(ridAndBJson.getJSONArray("entities").length() != 0){
						JSONArray tempArray = new JSONArray();
						tempArray.put(a);tempArray.put(aRIdArray.getLong(i));tempArray.put(b);
						jsonArray.put(tempArray);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (1,1) exception:"+e.getMessage());
			}
			//[end]
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			//[start] C case
			/*
			 * instance (11,1)
			 * todo: get RId from b ,get AuId from a, find Ids contain both
			 * */
			JSONObject idsjson = httpService("And(RId="+ b +",Composite(AA.AuId="+ a +"))");
			JSONArray entitiesArray = new JSONArray();
			try {
				entitiesArray = idsjson.getJSONArray("entities");
				for(int i = 0;i < entitiesArray.length();i++){
					JSONArray tempArray = new JSONArray();
					tempArray.put(a);tempArray.put(entitiesArray.getJSONObject(i).getLong("Id"));tempArray.put(b);
					jsonArray.put(tempArray);
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("now 2-hop C case (11,1) exception:"+e.getMessage());
			}
			//[end]
		}else{                        //D case:[AA.AuId,AA.AuId]
			//[start] D case
			JSONObject ajson = httpService("Composite(And(AA.AuId="+ a +",AA.AfN=''...))");
			JSONObject bjson = httpService("Composite(And(AA.AuId="+ b +",AA.AfN=''...))");
			try {
				if(ajson.getJSONArray("entities").length() == 0 || bjson.getJSONArray("entities").length() == 0 ){
					//System.out.println("now 2-hop D case has empty.ids:["+ a +","+ b +"]");
					return null;
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("now 2-hop D case has empty.ids:["+ a +","+ b +"]");
			}
			/*
			 * instance (9,10)
			 * todo: check if a and b from the same affiliation
			 * */
			JSONArray aEntitiesArray = new JSONArray();
			JSONArray bEntitiesArray = new JSONArray();
			HashSet<Long> aAfId = new HashSet<Long>();
			HashSet<Long> bAfId = new HashSet<Long>();
			try {
				aEntitiesArray = ajson.getJSONArray("entities");
				bEntitiesArray = bjson.getJSONArray("entities");
				int i = 0,j = 0;
				while(i < aEntitiesArray.length()){
					JSONArray AAArray = aEntitiesArray.getJSONObject(i).getJSONArray("AA");
					for(j = 0;j < AAArray.length();j++){
						if(a == AAArray.getJSONObject(j).getLong("AuId")){
							aAfId.add(AAArray.getJSONObject(0).getLong("AfId"));
							break;
						}
					}
					i++;
				}
				i = 0;
				while(i < bEntitiesArray.length()){
					JSONArray AAArray = bEntitiesArray.getJSONObject(i).getJSONArray("AA");
					for(j = 0;j < AAArray.length();j++){
						if(a == AAArray.getJSONObject(j).getLong("AuId")){
							bAfId.add(AAArray.getJSONObject(0).getLong("AfId"));
							break;
						}
					}
					i++;
				}
				Iterator<Long> iterator = aAfId.iterator();
				Iterator<Long> iterator2 = bAfId.iterator();
				ArrayList<Long> aAfIdList = new ArrayList<Long>();
				ArrayList<Long> bAfIdList = new ArrayList<Long>();
				while(iterator.hasNext()){
					long al = iterator.next();
//					logger.info("instance (9,10) aAfIds:"+ al);
					aAfIdList.add(al);
				}
				while(iterator2.hasNext()){
					long bl = iterator2.next();
//					logger.info("instance (9,10) bAfIds:"+ bl);
					bAfIdList.add(bl);
				}
				for(i = 0;i < aAfIdList.size();i++){
//					logger.info("instance (9,10) i:"+ i);
					for(j = 0;j < bAfIdList.size();j++){
//						logger.info("instance (9,10) j:"+ j);
						if(aAfIdList.get(i).equals(bAfIdList.get(j))){
							JSONArray temp = new JSONArray();
							temp.put(a);
							temp.put(aAfIdList.get(i));
							temp.put(b);
							jsonArray.put(temp);
//							logger.info("instance (9,10) common Ids:"+ aAfIdList.get(i));
							break;
						}
					}
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (9,10) exception:"+e.getMessage());
			}
			/*
			 * instance (11,5)
			 * todo: find all paper both has a and b
			 * */
			JSONObject jsonObject = httpService("Composite(AA.AuId="+a+")");
			JSONArray entitiesArray = new JSONArray();
			try {
				entitiesArray = jsonObject.getJSONArray("entities");
				for(int i = 0;i < entitiesArray.length();i++){
					JSONArray aAAArray = entitiesArray.getJSONObject(i).getJSONArray("AA");
					for(int j = 0;j < aAAArray.length();j++){
						if(b == aAAArray.getJSONObject(j).getLong("AuId")){
							JSONArray temp = new JSONArray();
							temp.put(a);
							temp.put(entitiesArray.getJSONObject(i).getLong("Id"));
							temp.put(b);
							jsonArray.put(temp);
							break;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.info("instance (11,5) exception:"+e.getMessage());
			}
			//[end]
		}
		return jsonArray;
	}

	private JSONArray get1HopPaths() {
		//logger.info("start with 1-hop paths +++++++++++++++++++++++++++");
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			logger.info("------------A     CASE----------------");
			/*
			 * instance (1)
			 * */
			JSONObject ajson = httpService("Id="+a);
//			//System.out.println("a:"+ajson.toString()+"\nb:"+bjson.toString());
			JSONArray aRidArray = new JSONArray();
			try {
				if(ajson.getJSONArray("entities").length() != 0){
					aRidArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
					for(int i = 0;i < aRidArray.length();i++){
						if(aRidArray.getLong(i) == b){
							jsonArray.put(a);
							jsonArray.put(b);
							break;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				logger.info("instance (1) exception:"+e.getMessage());
			}
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			logger.info("------------B     CASE----------------");
			/*
			 * instance （5）
			 * */
			JSONObject ajson = httpService("Id="+a);
//			//System.out.println("a:"+ajson.toString());
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
			logger.info("------------C     CASE----------------");
			/*
			 * instance （11）
			 * */
			JSONObject bjson = httpService("Id="+b);
//			//System.out.println("b:"+bjson.toString());
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
			logger.info("------------D     CASE----------------");
			//no paths
		}
		//logger.info("end with 1-hop paths+++++++++++++++++++++++++++++++");
		return jsonArray;
	}

	/*
	 * 传入expr的表达式，返回查询结果
	 * eg: https://oxfordhk.azure-api.net/academic/v1.0/evaluate?expr=Composite(AA.AuN==%27jaime%20teevan%27)&count=10000&attributes=Id,AA.AuId,AA.AfId,F.FId,J.JId,C.CId,RId&subscription-key=f7cc29509a8443c5b3a5e56b0e38b5a6
	 * */
	public static JSONObject httpService(String exp) {
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
			//System.out.println("http Exception:"+e.getMessage());
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
		try {
			String expr = "Composite(AA.AuId="+a2+")";
	        JSONObject responseJson = httpService(expr);
	        if(responseJson.getJSONArray("entities").length() == 0){
	        	this.aIsID = true;
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println("Exception:"+e.getMessage());
		}
	}

	public Boolean getbIsID() {
		return bIsID;
	}

	public void setbIsID(long b2) {
		try {
	        String expr = "Composite(AA.AuId="+b2+")";
	        JSONObject responseJson = httpService(expr);
	        if(responseJson.getJSONArray("entities").length() == 0){
	        	this.bIsID = true;
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println(e.getMessage());
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime() {
		this.startTime = System.currentTimeMillis();
	}
}
