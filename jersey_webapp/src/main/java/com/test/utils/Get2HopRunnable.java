package com.test.utils;

import java.util.HashSet;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

public class Get2HopRunnable implements Runnable{
	
	private long a;
	private long b;
	private HashSet<Long> idHashSet;
	private boolean fromA;
	public JSONArray jsonArray;

	private Iterator<Long> iterator;
	
	public Get2HopRunnable(HashSet<Long> hs,long al,long bl,boolean froma) {
		// TODO Auto-generated constructor stub
		idHashSet = hs;
		iterator = idHashSet.iterator();
		a = al;
		b = bl;
		fromA = froma;
		jsonArray = new JSONArray();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (iterator.hasNext()) {
			JSONArray temp2Hop = new JSONArray();
			if(fromA){
				temp2Hop = PathFinder.get2HopPaths(iterator.next(),b);
				for(int j = 0;j < temp2Hop.length();j++){
					JSONArray tpArray = new JSONArray();
					tpArray.put(a);
					try {
						tpArray.put(temp2Hop.getJSONArray(j).getLong(0));
						tpArray.put(temp2Hop.getJSONArray(j).getLong(1));
						tpArray.put(temp2Hop.getJSONArray(j).getLong(2));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					jsonArray.put(tpArray);
				}
			}else{
				temp2Hop = PathFinder.get2HopPaths(a,iterator.next());
				for(int j = 0;j < temp2Hop.length();j++){
					JSONArray tpArray = new JSONArray();
					try {
						tpArray.put(temp2Hop.getJSONArray(j).getLong(0));
						tpArray.put(temp2Hop.getJSONArray(j).getLong(1));
						tpArray.put(temp2Hop.getJSONArray(j).getLong(2));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tpArray.put(b);
					jsonArray.put(tpArray);
				}
			}
			
		}
	}
}
