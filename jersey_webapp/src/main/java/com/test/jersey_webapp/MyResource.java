
package com.test.jersey_webapp;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.test.pojo.User;

/** Example resource class hosted at the URI path "/myresource"
 */
@Path("/")
public class MyResource {
    
    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET 
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
    	HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://oxfordhk.azure-api.net/academic/v1.0/evaluate");

            builder.setParameter("expr", "Composite(AA.AuN=='jaime teevan')");
            builder.setParameter("model", "latest");
            builder.setParameter("count", "10000");
            builder.setParameter("attributes", "Id,AA.AuId,AA.AfId,Ti,Y,RId");
            builder.setParameter("subscription-key", "f7cc29509a8443c5b3a5e56b0e38b5a6");

            URI uri = builder.build();
            
            System.out.println("uri is:"+uri.toString());
            HttpGet request = new HttpGet(uri);
            

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) 
            {
//                System.out.println("response is:\n"+EntityUtils.toString(entity));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return "Hi there!";
    }
    

    @GET
    @Path("/getPaths")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONArray getArrayPairs(@QueryParam("query") JSONArray query) throws JSONException {
//    	System.out.println("array is:\n"+query.toString());
    	for(int i = 0;i < query.length();i++){
    		JSONArray temp = query.getJSONArray(i);
    		System.out.println("pair["+i+"]:"+temp.getInt(0)+" "+temp.getInt(1));
    	}
    	query.put(56);
//    	Collection collection = new Collection();
    	System.out.println("array is:\n"+query.toString());
    	return query;
    }
    
    @GET
    @Path("/getUserXml")
    @Produces(MediaType.APPLICATION_XML)
    public User getUserXml() {
     User user  = new User();
     user.setName("snail");
     user.setAge("22");
     user.setSex("male");
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
