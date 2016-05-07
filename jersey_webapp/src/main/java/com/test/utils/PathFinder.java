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


public class PathFinder {
	
	public static long a ;
	public static long b ;
	public static Boolean aIsID = false ;
	public static Boolean bIsID = false ;
	public static  long startTime ;
	
	private static Logger logger ;

	
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
		JSONArray twoHopArray = get2HopPaths(a,b);
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
				//logger.info("add twoHopArray error,exception:"+e.getMessage());
			}	
		}
		for(i = 0;i < triHopArray.length();i++){
			try {
				resultArray.put(triHopArray.getJSONArray(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//logger.info("add twoHopArray error,exception:"+e.getMessage());
			}	
		}
//		System.out.println("cost :"+(System.currentTimeMillis()-startTime)+"ms");
		return resultArray;
	}

	private JSONArray get3HopPaths() {
		//logger.info("start with 3-hops paths /////////////////////////");
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		int i = 0;
		int j = 0;
		int k = 0;
		if(aIsID && bIsID){           //A case:[Id,Id]
			//[start] A case
			JSONObject ajson = httpService("Id="+a);
			JSONObject bjson = httpService("Id="+b);
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
					JSONArray aEntitiesFromRIdArray = httpService("RId="+aRIdArray.getLong(i)).getJSONArray("entities");
					for(k = 0;k < aEntitiesFromRIdArray.length();k++){
						idSet.add(aEntitiesFromRIdArray.getJSONObject(k).getLong("Id"));
					}
				}
				logger.info("instance (1,1,1)--(1,5,11) find all a's related Ids consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				Get2HopRunnable runnable = new Get2HopRunnable(idSet,a, b, true);
				
				//[start] use ten threads to run
				Thread t0 = new Thread(runnable);
				Thread t1 = new Thread(runnable);
				Thread t2 = new Thread(runnable);
				Thread t3 = new Thread(runnable);
				Thread t4 = new Thread(runnable);
				Thread t5 = new Thread(runnable);
				Thread t6 = new Thread(runnable);
				Thread t7 = new Thread(runnable);
				Thread t8 = new Thread(runnable);
				Thread t9 = new Thread(runnable);
				t0.start();
				t1.start();
				t2.start();
				t3.start();
				t4.start();
				t5.start();
				t6.start();
				t7.start();
				t8.start();
				t9.start();
				t0.join();
				t1.join();
				t2.join();
				t3.join();
				t4.join();
				t5.join();
				t6.join();
				t7.join();
				t8.join();
				t9.join();
				//[end]
				jsonArray = runnable.jsonArray;
//				
//				Iterator<Long> iterator = idSet.iterator();
//				while(iterator.hasNext()){
//					//logger.info("instance (1,1,1)--(1,5,11) time consumed:"+(System.currentTimeMillis()-startTime)+"ms(for for "+k+")");
//					
//					JSONArray temp2Hop = get2HopPaths(iterator.next(),b);
//					for(j = 0;j < temp2Hop.length();j++){
//						JSONArray tpArray = new JSONArray();
//						tpArray.put(a);
//						tpArray.put(temp2Hop.getJSONArray(j).getLong(0));
//						tpArray.put(temp2Hop.getJSONArray(j).getLong(1));
//						tpArray.put(temp2Hop.getJSONArray(j).getLong(2));
//						jsonArray.put(tpArray);
//					}
//					if((System.currentTimeMillis()-startTime) > 280000){
//						logger.info("instance (1,1,1)--(1,5,11) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//						try {
//							this.finalize();
//						} catch (Throwable e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("instance (1,1,1)--(1,5,11) exception:"+e.getMessage());
			}
			//logger.info("instance (1,1,1)--(1,5,11) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (1,1,1)--(1,5,11) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}	
			
			/*
			 * instance (1,1,1) (2,6,1) (3,7,1) (4,8,1) (5,11,1)
			 * todo: find every Id from RIds from aId
			 *       use get2HopPaths() (A case,instance(1,1)) to cal paths
			 * */
			JSONArray bRIdArray = new JSONArray();
			idSet.clear();
			try {
				bRIdArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				for(i = 0;i < bRIdArray.length();i++){
					JSONArray aEntitiesFromRIdArray = httpService("RId="+bRIdArray.getLong(i)).getJSONArray("entities");
					for(k = 0;k < aEntitiesFromRIdArray.length();k++){
						idSet.add(aEntitiesFromRIdArray.getJSONObject(k).getLong("Id"));
					}
				}
				logger.info("instance (1,1,1)--(5,11,1) find all a's related Ids consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				Iterator<Long> iterator = idSet.iterator();
				while(iterator.hasNext()){
					//logger.info("instance (1,1,1)--(1,5,11) time consumed:"+(System.currentTimeMillis()-startTime)+"ms(for for "+k+")");
					JSONArray temp2Hop = get2HopPaths(a,iterator.next());
					for(j = 0;j < temp2Hop.length();j++){
						JSONArray tpArray = new JSONArray();
						tpArray.put(temp2Hop.getJSONArray(j).getLong(0));
						tpArray.put(temp2Hop.getJSONArray(j).getLong(1));
						tpArray.put(temp2Hop.getJSONArray(j).getLong(2));
						tpArray.put(b);
						jsonArray.put(tpArray);
					}
					if((System.currentTimeMillis()-startTime) > 280000){
						logger.info("instance (1,1,1)--(5,11,1) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
						return jsonArray;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (1,1,1)--(5,11,1) exception:"+e.getMessage());
			}
			//logger.info("instance (1,1,1)--(5,11,1) done!");

			//[end] A case
			
//			/*
//			 * instance (1,2,6)
//			 * todo: find every RId from aId
//			 *       find every FId from bId
//			 *       use RId and FId to cal Idt1
//			 * */
//			JSONArray bFArray = new JSONArray();
//			try {
//				bFArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("F");
//				for(i = 0;i < aRIdArray.length();i++){
//					for(j = 0;j < bFArray.length();j++){
//						JSONObject Idsjson = httpService("And(RId="+aRIdArray.getLong(i)+",Composite(F.FId="+bFArray.getLong(j)+"))");
//						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
//							JSONArray temp = new JSONArray();
//							temp.put(a);
//							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
//							temp.put(bFArray.getLong(j));
//							temp.put(b);
//							jsonArray.put(temp);
//						}
//					}
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				//logger.info("instance (1,2,6) exception:"+e.getMessage());
//			}
//			//logger.info("instance (1,2,6) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				//logger.info("instance (1,2,6) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
//			/*
//			 * instance (1,3,7)
//			 * todo: find every RId from aId
//			 *       find every CId from bId
//			 *       use RId and CId to cal Idt1
//			 * */
//			JSONObject bCjson = new JSONObject();
//			try {
//				bCjson = bjson.getJSONArray("entities").getJSONObject(0);
//			} catch (JSONException e2) {
//				// TODO Auto-generated catch block
//				//logger.info("instance (1,3,7) exception:"+e2.getMessage());
//			}
//			if(bCjson.has("C")){
//				try {
//					bCjson = bCjson.getJSONObject("C");
//					for(i = 0;i < aRIdArray.length();i++){
//						JSONObject Idsjson = httpService("And(RId="+aRIdArray.getLong(i)+",Composite(C.CId="+bCjson.getLong("CId")+"))");
//						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
//							JSONArray temp = new JSONArray();
//							temp.put(a);
//							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
//							temp.put(bCjson.getLong("CId"));
//							temp.put(b);
//							jsonArray.put(temp);
//						}
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					//logger.info("instance (1,3,7) exception:"+e.getMessage());
//				}
//			}
//			//logger.info("instance (1,3,7) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				//logger.info("instance (1,3,7) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
//			/*
//			 * instance (1,4,8)
//			 * todo: find every RId from aId
//			 *       find every JId from bId
//			 *       use RId and JId to cal Idt1
//			 * */
//			JSONObject bjsObject = new JSONObject();
//			try {
//				bjsObject = bjson.getJSONArray("entities").getJSONObject(0);
//			} catch (JSONException e1) {
//				// TODO Auto-generated catch block
//				//logger.info("instance (1,4,8) exception:"+e1.getMessage());
//			} 
//			if(bjsObject.has("J")){
//				try {
//					bjsObject = bjsObject.getJSONObject("J");
//					for(i = 0;i < aRIdArray.length();i++){
//						JSONObject Idsjson = httpService("And(RId="+aRIdArray.getLong(i)+",Composite(J.JId="+bjsObject.getLong("JId")+"))");
//						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
//							JSONArray temp = new JSONArray();
//							temp.put(a);
//							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
//							temp.put(bjsObject.getLong("JId"));
//							temp.put(b);
//							jsonArray.put(temp);
//						}	
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					//logger.info("instance (1,4,8) exception:"+e.getMessage());
//				}
//			}
//			//logger.info("instance (1,4,8) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				//logger.info("instance (1,4,8) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
//			/*
//			 * instance (1,5,11)
//			 * todo: find every RId from aId
//			 *       find every AuId from bId
//			 *       use RId and AuId to cal Idt1
//			 * */
//			JSONArray bAAArray = new JSONArray();
//			try {
//				bAAArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
//				for(i = 0;i < aRIdArray.length();i++){
//					for(j = 0;j < bAAArray.length();j++){
//						JSONObject Idsjson = httpService("And(RId="+aRIdArray.getLong(i)+",Composite(AA.AuId="+bAAArray.getJSONObject(j).getLong("AuId")+"))");
//						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
//							JSONArray temp = new JSONArray();
//							temp.put(a);
//							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
//							temp.put(bAAArray.getJSONObject(j).getLong("AuId"));
//							temp.put(b);
//							jsonArray.put(temp);
//						}
//					}
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				//logger.info("instance (1,5,11) exception:"+e.getMessage());
//			}
//			//logger.info("instance (1,5,11) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				//logger.info("instance (1,5,11) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
//			/*
//			 * instance (2,6,1)
//			 * todo: find every RId from bId
//			 *       find every FId from aId
//			 *       use RId and FId to cal Idt2
//			 * */
//			JSONArray bRIdArray = new JSONArray();
//			JSONArray aFArray = new JSONArray();
//			try {
//				bRIdArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
//				aFArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("F");
//				for(i = 0;i < bRIdArray.length();i++){
//					for(j = 0;j < aFArray.length();j++){
//						JSONObject Idsjson = httpService("And(RId="+bRIdArray.getLong(i)+",Composite(F.FId="+aFArray.getJSONObject(j).getLong("FId")+"))");
//						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
//							JSONArray temp = new JSONArray();
//							temp.put(a);
//							temp.put(aFArray.getJSONObject(j).getLong("FId"));
//							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
//							temp.put(b);
//							jsonArray.put(temp);
//						}
//					}
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				//logger.info("instance (2,6,1) exception:"+e.getMessage());
//			}
//			//logger.info("instance (2,6,1) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				//logger.info("instance (2,6,1) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
//			/*
//			 * instance (3,7,1)
//			 * todo: find every RId from bId
//			 *       find CId from aId
//			 *       use RId and CId to cal Idt2
//			 * */
//			JSONObject aCjson = new JSONObject();
//			try {
//				aCjson = ajson.getJSONArray("entities").getJSONObject(0);
//			} catch (JSONException e2) {
//				// TODO Auto-generated catch block
//				//logger.info("instance (3,7,1) exception:"+e2.getMessage());
//			}
//			if(aCjson.has("C")){
//				try {
//					aCjson = aCjson.getJSONObject("C");
//					for(i = 0;i < bRIdArray.length();i++){
//						JSONObject Idsjson = httpService("And(RId="+bRIdArray.getLong(i)+",Composite(C.CId="+aCjson.getLong("CId")+"))");
//						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
//							JSONArray temp = new JSONArray();
//							temp.put(a);
//							temp.put(aCjson.getLong("CId"));
//							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
//							temp.put(b);
//							jsonArray.put(temp);
//						}
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					//logger.info("instance (3,7,1) exception:"+e.getMessage());
//				}
//				
//			}
//			//logger.info("instance (3,7,1) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				//logger.info("instance (3,7,1) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
//			/*
//			 * instance (4,8,1)
//			 * todo: find every RId from bId
//			 *       find JId from aId
//			 *       use RId and JId to cal Idt2
//			 * */
//			JSONObject ajsObject = new JSONObject();
//			try {
//				ajsObject = ajson.getJSONArray("entities").getJSONObject(0);
//			} catch (JSONException e1) {
//				// TODO Auto-generated catch block
//				//logger.info("instance (4,8,1) exception:"+e1.getMessage());
//			} 
//			if(ajsObject.has("J")){
//				try {
//					ajsObject = ajsObject.getJSONObject("J");
//					for(i = 0;i < bRIdArray.length();i++){
//						JSONObject Idsjson = httpService("And(RId="+bRIdArray.getLong(i)+",Composite(J.JId="+ajsObject.getLong("JId")+"))");
//						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
//							JSONArray temp = new JSONArray();
//							temp.put(a);
//							temp.put(ajsObject.getLong("JId"));
//							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
//							temp.put(b);
//							jsonArray.put(temp);
//						}
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					//logger.info("instance (4,8,1) exception:"+e.getMessage());
//				}
//			}
//			//logger.info("instance (4,8,1) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				//logger.info("instance (4,8,1) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
//			/*
//			 * instance (5,11,1)
//			 * todo: find every RId from bId
//			 *       find every AuId from aId
//			 *       use RId and AuId to cal Idt2
//			 * */
//			JSONArray aAAArray = new JSONArray();
//			try {
//				aAAArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
//				for(i = 0;i < bRIdArray.length();i++){
//					for(j = 0;j < aAAArray.length();j++){
//						JSONObject Idsjson = httpService("And(RId="+bRIdArray.getLong(i)+",Composite(AA.AuId="+aAAArray.getJSONObject(j).getLong("AuId")+"))");
//						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
//							JSONArray temp = new JSONArray();
//							temp.put(a);
//							temp.put(aAAArray.getJSONObject(j).getLong("AuId"));
//							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
//							temp.put(b);
//							jsonArray.put(temp);
//						}
//					}
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//				//logger.info("instance (5,11,1) exception:"+e.getMessage());
//			}
//			//logger.info("instance (5,11,1) done!");
//			if((System.currentTimeMillis()-startTime) > 280000){
//				//logger.info("instance (5,11,1) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
//				return jsonArray;
//			}
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			//[start] B case
			JSONObject ajson = httpService("Id="+a);
			JSONObject bjson = httpService("Composite(AA.AuId="+b+")");
			/*
			 * instance (1,1,5)
			 * todo: find every Id from Id a
			 *       use get2HopPaths() (B case,instance(1,5)) to cal paths
			 * */
			JSONArray aRIdArray = new JSONArray();
			try {
				aRIdArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				for(i = 0;i < aRIdArray.length();i++){
					JSONObject tempjson = httpService("RId="+aRIdArray.getLong(i));
					JSONArray tempIdArray = tempjson.getJSONArray("entities");
					for(k = 0;k < tempIdArray.length();k++){
						JSONArray temp2Hop = get2HopPaths(tempIdArray.getJSONObject(i).getLong("Id"),b);
						for(j = 0;j < temp2Hop.length();j++){
							JSONArray tpArray = new JSONArray();
							tpArray.put(a);
							tpArray.put(temp2Hop.getJSONArray(j).getLong(0));
							tpArray.put(temp2Hop.getJSONArray(j).getLong(1));
							tpArray.put(temp2Hop.getJSONArray(j).getLong(2));
							jsonArray.put(tpArray);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (11,1,5) exception:"+e.getMessage());
			}
			//logger.info("instance (11,1,5) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (11,1,5) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (2,6,5)
			 * todo: find every FId from Id a
			 *       use AuId b and FId to cal Idt2
			 * */
			JSONArray aFArray = new JSONArray();
			try {
				aFArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("F");
				for(j = 0;j < aFArray.length();j++){
					JSONObject Idsjson = httpService("Composite(And(AA.AuId="+b+",F.FId="+aFArray.getJSONObject(j).getLong("FId")+"))");
					for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(aFArray.getJSONObject(j).getLong("FId"));
						temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
						temp.put(b);
						jsonArray.put(temp);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (2,6,5) exception:"+e.getMessage());
			}
			//logger.info("instance (2,6,5) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (2,6,5) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (3,7,5)
			 * todo: find CId from aId
			 *       use AuId b and CId to cal Idt2
			 * */
			JSONObject aCjson = new JSONObject();
			try {
				aCjson = ajson.getJSONArray("entities").getJSONObject(0);
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				//logger.info("instance (3,7,5) exception:"+e2.getMessage());
			}
			if(aCjson.has("C")){
				try {
					aCjson = aCjson.getJSONObject("C");
					JSONObject Idsjson = httpService("Composite(And(AA.AuId="+b+",C.CId="+aCjson.getLong("CId")+"))");
					for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(aCjson.getLong("CId"));
						temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
						temp.put(b);
						jsonArray.put(temp);
					}
				} catch (Exception e) {
					// TODO: handle exception
					//logger.info("instance (3,7,5) exception:"+e.getMessage());
				}
				
			}
			//logger.info("instance (3,7,5) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (3,7,5) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (4,8,5)
			 * todo: find JId from Id a
			 *       use AuId b and JId to cal Idt2
			 * */
			JSONObject ajsObject = new JSONObject();
			try {
				ajsObject = ajson.getJSONArray("entities").getJSONObject(0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				//logger.info("instance (4,8,5) exception:"+e1.getMessage());
			} 
			if(ajsObject.has("J")){
				try {
					ajsObject = ajsObject.getJSONObject("J");
					JSONObject Idsjson = httpService("Composite(And(AA.AuId="+b+",J.JId="+ajsObject.getLong("JId")+"))");
					for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(ajsObject.getLong("JId"));
						temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
						temp.put(b);
						jsonArray.put(temp);
					}
				} catch (Exception e) {
					// TODO: handle exception
					//logger.info("instance (4,8,5) exception:"+e.getMessage());
				}
			}
			//logger.info("instance (4,8,5) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (4,8,5) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (5,11,5)
			 * todo: find every AuId from every Id from AuId b
			 *       find every AuId from Id a
			 *       when equal record
			 *       
			 * */
			JSONArray bEntitiesArray = new JSONArray();
			JSONArray aAAArray = new JSONArray();
			try {
				aAAArray = ajson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				bEntitiesArray = bjson.getJSONArray("entities");
				for(i = 0;i < bEntitiesArray.length();i++){
					JSONArray bAAArray =  httpService("Id="+bEntitiesArray.getJSONObject(i).getLong("Id")).getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
					for(k = 0;k < aAAArray.length();k++){
						for(j = 0;j < bAAArray.length();j++){
							if(aAAArray.getJSONObject(k).getLong("AuId") == bAAArray.getJSONObject(j).getLong("AuId")){
								JSONArray temp = new JSONArray();
								temp.put(a);
								temp.put(aAAArray.getJSONObject(k).getLong("AuId"));
								temp.put(bEntitiesArray.getJSONObject(i).getLong("Id"));
								temp.put(b);
								jsonArray.put(temp);
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (5,11,5) exception:"+e.getMessage());
			}
			//logger.info("instance (5,11,5) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (5,11,5) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (5,9,10)
			 * todo: find every AuId from Id a
			 *       use get2HopPaths() (A case,instance(9,10)) to cal paths
			 * */
			try {
				for(i = 0;i < aAAArray.length();i++){
					JSONArray temp2HopArray =  get2HopPaths(aAAArray.getJSONObject(i).getLong("AuId"), b);
					for(k = 0;k < temp2HopArray.length();k++){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(temp2HopArray.getJSONArray(k).getLong(0));
						temp.put(temp2HopArray.getJSONArray(k).getLong(1));
						temp.put(b);
						jsonArray.put(temp);
					}
						
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (5,9,10) exception:"+e.getMessage());
			}
			//logger.info("instance (5,9,10) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (5,9,10) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			//[end]
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			
			//[start] C case
			
//			JSONObject ajson = httpService("Composite(AA.AuId = "+a+")");
			JSONObject bjson = httpService("Id="+b);
			
			/*
			 * instance (9,10,11)
			 * todo: find every AuId from Id b
			 *       use get2HopPaths() (D case,instance(9,10)) to cal paths
			 * */
			JSONArray bAAArray = new JSONArray();
			try {
				bAAArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("AA");
				for(i = 0;i < bAAArray.length();i++){
					JSONArray temp2HopArray =  get2HopPaths(a,bAAArray.getJSONObject(i).getLong("AuId"));
					for(k = 0;k < temp2HopArray.length();k++){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(temp2HopArray.getJSONArray(k).getLong(1));
						temp.put(temp2HopArray.getJSONArray(k).getLong(2));
						temp.put(b);
						jsonArray.put(temp);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (9,10,11) exception:"+e.getMessage());
			}
			//logger.info("instance (9,10,11) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (9,10,11) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (11,1,1)
			 * todo: find every Id from RId from Id b
			 *       use get2HopPaths() (C case,instance(11,1)) to cal paths
			 * */
			JSONArray bRidArray = new JSONArray();
			try {
				bRidArray = bjson.getJSONArray("entities").getJSONObject(0).getJSONArray("RId");
				for(i = 0;i < bRidArray.length();i++){
					JSONObject bIdsObject = httpService("RId="+bRidArray.getLong(i));
					JSONArray bEntitiesArray = bIdsObject.getJSONArray("entities");
					for(j = 0;j < bEntitiesArray.length();j++){
						JSONArray temp2HopArray = get2HopPaths(a, bEntitiesArray.getJSONObject(j).getLong("Id"));
						for(k = 0;k < temp2HopArray.length();k++){
							JSONArray temp = new JSONArray();
							temp.put(a);
							temp.put(temp2HopArray.getJSONArray(k).getLong(1));
							temp.put(temp2HopArray.getJSONArray(k).getLong(2));
							temp.put(b);
							jsonArray.put(temp);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (11,1,1) exception:"+e.getMessage());
			}
			//logger.info("instance (11,1,1) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (11,1,1) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (11,2,6)
			 * todo: find FId from Id b
			 *       use AuId a and FId to cal Idt1
			 * */
			JSONObject bFObject = new JSONObject();
			try {
				bFObject = bjson.getJSONArray("entities").getJSONObject(0);
				if(bFObject.has("F")){
					JSONArray bFArray = bFObject.getJSONArray("F");;
					for(j = 0;j < bFArray.length();j++){
						JSONObject Idsjson = httpService("Composite(And(AA.AuId="+a+",F.FId="+bFArray.getJSONObject(j).getLong("FId")+"))");
						for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
							JSONArray temp = new JSONArray();
							temp.put(a);
							temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
							temp.put(bFArray.getJSONObject(j).getLong("FId"));
							temp.put(b);
							jsonArray.put(temp);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (11,2,6) exception:"+e.getMessage());
			}
			//logger.info("instance (11,2,6) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (11,2,6) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (11,3,7)
			 * todo: find CId from Id b
			 *       use AuId a and CId to cal Idt1
			 * */
			JSONObject bCjson = new JSONObject();
			try {
				bCjson = bjson.getJSONArray("entities").getJSONObject(0);
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				//logger.info("instance (11,3,7) exception:"+e2.getMessage());
			}
			if(bCjson.has("C")){
				try {
					bCjson = bCjson.getJSONObject("C");
					JSONObject Idsjson = httpService("Composite(And(AA.AuId="+a+",C.CId="+bCjson.getLong("CId")+"))");
					for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
						temp.put(bCjson.getLong("CId"));
						temp.put(b);
						jsonArray.put(temp);
					}
				} catch (Exception e) {
					// TODO: handle exception
					//logger.info("instance (11,3,7) exception:"+e.getMessage());
				}
				
			}
			//logger.info("instance (11,3,7) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (11,3,7) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (11,4,8)
			 * todo: find JId from Id b
			 *       use AuId a and JId to cal Idt1
			 * */
			JSONObject bjsObject = new JSONObject();
			try {
				bjsObject = bjson.getJSONArray("entities").getJSONObject(0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				//logger.info("instance (11,4,8) exception:"+e1.getMessage());
			} 
			if(bjsObject.has("J")){
				try {
					bjsObject = bjsObject.getJSONObject("J");
					JSONObject Idsjson = httpService("Composite(And(AA.AuId="+a+",J.JId="+bjsObject.getLong("JId")+"))");
					for(k = 0;k < Idsjson.getJSONArray("entities").length();k++){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(Idsjson.getJSONArray("entities").getJSONObject(k).getLong("Id"));
						temp.put(bjsObject.getLong("JId"));
						temp.put(b);
						jsonArray.put(temp);
					}
				} catch (Exception e) {
					// TODO: handle exception
					//logger.info("instance (11,4,8) exception:"+e.getMessage());
				}
			}
			//logger.info("instance (11,4,8) done!");
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("instance (11,4,8) runtime exccess,stop running!time consumed:"+(System.currentTimeMillis()-startTime)+"ms");
				return jsonArray;
			}
			/*
			 * instance (11,5,11)
			 * todo: find every AuId from Id b
			 *       use AuId a and AuId from b to cal Idt1
			 *       
			 * */
			try {
				for(i = 0;i < bAAArray.length();i++){
					JSONObject rIdjson =  httpService("Composite(And(AA.AuId="+a+",AA.AuId="+bAAArray.getJSONObject(i).getLong("AuId")+"))");
					JSONArray entitiesArray = rIdjson.getJSONArray("entities");
					for(j = 0;j < entitiesArray.length();k++){
						JSONArray temp = new JSONArray();
						temp.put(a);
						temp.put(entitiesArray.getJSONObject(j).getLong("Id"));
						temp.put(bAAArray.getJSONObject(i).getLong("Id"));
						temp.put(b);
						jsonArray.put(temp);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (5,11,5) exception:"+e.getMessage());
			}
			//logger.info("instance (11,5,11) done!");
			//[end]
		}else{                        //D case:[AA.AuId,AA.AuId]
			
			//[start] D case
			
			JSONObject ajson = httpService("Composite(AA.AuId="+a+")");
			/*
			 * instance (11,1,5)
			 * todo: find every Id from AuId
			 *       use get2HopPaths() (B case,instance(1,5)) to cal paths
			 * */
			JSONArray aIdArray = new JSONArray();
			try {
				aIdArray = ajson.getJSONArray("entities");
				for(i = 0;i < aIdArray.length();i++){
					JSONArray temp2Hop = get2HopPaths(aIdArray.getJSONObject(i).getLong("Id"),b);
					for(j = 0;j < temp2Hop.length();j++){
						JSONArray tpArray = new JSONArray();
						tpArray.put(a);
						tpArray.put(temp2Hop.getJSONArray(j).getLong(0));
						tpArray.put(temp2Hop.getJSONArray(j).getLong(1));
						tpArray.put(temp2Hop.getJSONArray(j).getLong(2));
						jsonArray.put(tpArray);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				//logger.info("instance (11,1,5) exception:"+e.getMessage());
			}
			//logger.info("instance (11,1,5) done!");
			//[end]
		}
		//logger.info("end with 3-hops paths /////////////////////////");
		return jsonArray;
	}

	public static JSONArray get2HopPaths(long a,long b) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		if(aIsID && bIsID){           //A case:[Id,Id]
			//[start] A case
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
				//logger.info("instance (1,1) exception:"+e.getMessage());
			}
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("2-hop instance (1,1) runtime exccess,stop running!");
				return jsonArray;
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
				//logger.info("2-hop instance (2,6) exception:"+e.getMessage());
			}
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("2-hop instance (2,6) runtime exccess,stop running!");
				return jsonArray;
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
				//logger.info("instance (3,7) exception:" + e1.getMessage());
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
					//logger.info("instance (3,7) exception:"+e.getMessage());
				}
			}
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("2-hop instance (3,7) runtime exccess,stop running!");
				return jsonArray;
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
				//logger.info("instance (4,8) exception:" + e1.getMessage());
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
					//logger.info("instance (4,8) exception:"+e.getMessage());
				}
			}
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("2-hop instance (4,8) runtime exccess,stop running!");
				return jsonArray;
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
				//logger.info("instance (5,11) exception:"+e.getMessage());
			}
			//[end]
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			//[start] B case
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
				//logger.info("instance (1,1) exception:"+e.getMessage());
			}
			//[end]
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			//[start] C case
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
				//logger.info("instance (11,1) exception:"+e.getMessage());
			}
			//[end]
		}else{                        //D case:[AA.AuId,AA.AuId]
			//[start] D case
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
				//logger.info("instance (9,10) exception:"+e.getMessage());
			}
			if((System.currentTimeMillis()-startTime) > 280000){
				//logger.info("2-hop runtime exccess,stop running!");
				return jsonArray;
			}
			/*
			 * instance (11,5)
			 * todo: find all paper both has a and b
			 * */
			JSONObject jsonObject = httpService("Composite(And(AA.AuId="+a+",AA.AuId="+b+"))");
			JSONArray entitiesArray = new JSONArray();
			try {
				entitiesArray = jsonObject.getJSONArray("entities");
				for(int i = 0;i < entitiesArray.length();i++){
					JSONArray temp = new JSONArray();
					temp.put(a);
					temp.put(entitiesArray.getJSONObject(i).getLong("Id"));
					temp.put(b);
					jsonArray.put(temp);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//logger.info("instance (11,5) exception:"+e.getMessage());
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
			//logger.info("------------A     CASE----------------");
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
				//logger.info("instance (1) exception:"+e.getMessage());
			}
		}else if(aIsID && !bIsID){    //B case:[Id,AA.AuId]
			//logger.info("------------B     CASE----------------");
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
				//logger.info("instance (5) exception:"+e.getMessage());
			}
		}else if(!aIsID && bIsID){    //C case:[AA.AuId,Id]
			//logger.info("------------C     CASE----------------");
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
				//logger.info("instance (11) exception:"+e.getMessage());
			}
		}else{                        //D case:[AA.AuId,AA.AuId]
			//logger.info("------------D     CASE----------------");
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
			System.out.println("http Exception:"+e.getMessage());
		}
		return null;
	}
	
	public long getA() {
		return a;
	}

	public void setA(long a2) {
		PathFinder.a = a2;
	}

	public long getB() {
		return b;
	}

	public void setB(long b2) {
		PathFinder.b = b2;
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
	        	PathFinder.aIsID = true;
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
	        	PathFinder.bIsID = true;
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
		PathFinder.startTime = System.currentTimeMillis();
	}
}
