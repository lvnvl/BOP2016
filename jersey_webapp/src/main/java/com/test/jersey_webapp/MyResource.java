
package com.test.jersey_webapp;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.test.pojo.User;
import com.test.utils.PathFinder;

/** Example resource class hosted at the URI path "/myresource"
 */
@Path("/")
public class MyResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getArrayPairs(@QueryParam("id1") long a , @QueryParam("id2") long b) throws JSONException {
    	System.out.println("sum is:\n"+(a+b));
    	LoggerFactory.getLogger(this.getClass()).info("******************************************************************"
    			+ "\nlogger info output!-----------"
    			+"[id1,id2]:["+a+","+b+"]");
//    	JSONArray resultArray = new JSONArray();
//    	if(a == Long.parseLong("2251253715") && b == Long.parseLong("2180737804")){
//    		resultArray = new JSONArray("[[2251253715,2180737804],[2251253715,2180737804,35927321,2180737804],[2251253715,2180737804,2048498903,2180737804],[2251253715,2180737804,2223920688,2180737804],[2251253715,2180737804,949266530,2180737804],[2251253715,2180737804,1783833040,2180737804],[2251253715,2180737804,2251253715,2180737804],[2251253715,2180737804,2108096461,2180737804],[2251253715,2299839756,2048498903,2180737804],[2251253715,2299839756,949266530,2180737804],[2251253715,2299839756,2223920688,2180737804],[2251253715,2299839756,1783833040,2180737804],[2251253715,2299839756,2251253715,2180737804],[2251253715,2299839756,2108096461,2180737804]]");
//    	}else if(a == Long.parseLong("2147152072") && b == Long.parseLong("189831743")){
//    		resultArray = new JSONArray("[[2147152072,2151561903,189831743],[2147152072,41008148,189831743],[2147152072,134022301,41008148,189831743],[2147152072,1965061793,41008148,189831743],[2147152072,2114804204,41008148,189831743],[2147152072,35738896,41008148,189831743],[2147152072,2041565863,41008148,189831743],[2147152072,2107827038,41008148,189831743],[2147152072,2151561903,41008148,189831743],[2147152072,41008148,2086513752,189831743],[2147152072,186311912,1974415342,189831743],[2147152072,41008148,2120932642,189831743],[2147152072,41008148,2051032335,189831743],[2147152072,186311912,2151561903,189831743],[2147152072,41008148,2151561903,189831743],[2147152072,135954941,2151561903,189831743],[2147152072,23123220,2109539048,189831743],[2147152072,41008148,2109539048,189831743]]");
//    	}else if(a == Long.parseLong("2332023333") && b == Long.parseLong("2310280492")){
//    		resultArray = new JSONArray("[[2332023333,1158167855,2310280492]]");
//    	}else if(a == Long.parseLong("2332023333") && b == Long.parseLong("57898110")){
//    		resultArray = new JSONArray("[[2332023333,1158167855,2310280492,57898110]]");
//    	}else{
//    		resultArray = new JSONArray("[[57898110,91712215,2014261844],[57898110,4923324,2014261844],[57898110,2052243599,2014261844],[57898110,2150635919,2014261844],[57898110,2080526711,2014261844],[57898110,2251676003,2014261844],[57898110,1807911131,2014261844],[57898110,2310280492,2014261844],[57898110,2261888986,2014261844],[57898110,1808135090,2014261844],[57898110,2180648442,2014261844],[57898110,2052207545,2014261844],[57898110,2294471017,2014261844],[57898110,2179812682,2014261844],[57898110,2296127659,2014261844],[57898110,2296099950,2014261844],[57898110,2249684012,2014261844],[57898110,2150635919,2114621449,2014261844],[57898110,2150635919,2143039717,2014261844],[57898110,2080526711,2149284486,2014261844],[57898110,2251676003,2149284486,2014261844],[57898110,2052207545,2149284486,2014261844],[57898110,2150635919,1988539193,2014261844],[57898110,2080526711,2048359624,2014261844],[57898110,2080526711,2052243599,2014261844],[57898110,1807911131,2052243599,2014261844],[57898110,2052207545,2052243599,2014261844]]");
//    	}
    	PathFinder pathFinder = new PathFinder(a,b);
    	JSONArray resultArray = pathFinder.getPaths();
    	LoggerFactory.getLogger(getClass()).info("===================find:\n"+resultArray.toString());
    	return resultArray;
    }
    
    @GET
    @Path("/getUserXml")
    @Produces(MediaType.APPLICATION_XML)
    public User getUserXml() {
     User user  = new User();
     try {

         user.setName("snail");
         user.setAge("22");
         user.setSex("male");
	} catch (Exception e) {
		// TODO: handle exception
	}
     user.setAge("88");
     return user;
    }
    
    @GET
    @Path("/getUserJson")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserJson(@QueryParam("query") JSONObject query) throws JSONException {
    	User user  = new User();
    	user.setAge(query.getString("age"));
    	user.setName(query.getString("name"));
    	user.setSex(query.getString("sex"));
    	return user;
    }
}
