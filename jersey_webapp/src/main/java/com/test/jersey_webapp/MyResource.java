
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
//    
//    /** Method processing HTTP GET requests, producing "text/plain" MIME media
//     * type.
//     * @return String that will be send back as a response of type "text/plain".
//     */
//    @GET 
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getIt() {
//    	HttpClient httpclient = HttpClients.createDefault();
//
//        try
//        {
//            URIBuilder builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");
//
//            builder.setParameter("expr", "Composite(AA.AuN=='jaime teevan')");
//            builder.setParameter("model", "latest");
//            builder.setParameter("count", "10000");
//            builder.setParameter("attributes", "Id,AA.AuId,AA.AfId,Ti,Y,RId");
//            builder.setParameter("subscription-key", "f7cc29509a8443c5b3a5e56b0e38b5a6");
//
//            URI uri = builder.build();
//            
//            System.out.println("uri is:"+uri.toString());
//            HttpGet request = new HttpGet(uri);
//            
//
//            HttpResponse response = httpclient.execute(request);
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) 
//            {
////                System.out.println("response is:\n"+EntityUtils.toString(entity));
//            }
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//        }
//        return "Hi there!";
//    }
//    

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getArrayPairs(@QueryParam("id1") long a , @QueryParam("id2") long b) throws JSONException {
    	System.out.println("sum is:\n"+(a+b));
    	LoggerFactory.getLogger(this.getClass()).info("\n\nlogger info output!-----------"
    			+"[id1,id2]:["+a+","+b+"]");
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
