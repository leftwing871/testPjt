package es;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class App3 {

	//private static String _wsUrl = "http://113.124.6.240:9200";
	private static String _wsUrlAuth = "http://113.124.240.182:9200";

    public static void main(String[] args) throws ClientProtocolException, IOException, ParseException {

    	//App3.txt
    	//http://13.124.6.240:9200,blogs,company,url,url,1440m
    	
    	/*
    	System.out.print("Enter Severinfo (http://13.124.6.240:9200) -> ");
    	_wsUrl = System.console().readLine();//"http://13.124.6.240:9200";
    	
    	System.out.print("Enter leftIndex (blogs) -> ");
    	String leftIndex = System.console().readLine();//"blogs";
    	
    	System.out.print("Enter rightIndex (company) -> ");
    	String rightIndex = System.console().readLine();//"company";
    	
    	System.out.print("Enter leftField (url) ex -> ");
    	String leftField = System.console().readLine();//"url";
    	
    	System.out.print("Enter rightField (url) -> ");
    	String rightField = System.console().readLine();//"url";
    	*/
    	
    	String _wsUrl = "http://172.28.230.55:9200";
    	
    	String leftIndex = "kcem_salinfo_20181217_test";
    	
    	String rightIndex = "d_cttr_comcsinfo";
    	
    	String leftField = "enc_comcsno";
    	
    	String rightField = "enc_comcsno";
    	
    	String scrollTime = "1440m";
    	
    	String parameterString = readFile("App3.txt", Charset.defaultCharset());
    	/*
    	 * App3.txt example
    	 * http://13.124.6.240:9200,blogs,company,url,url,1440m
    	 * 
    	 * http://13.124.6.240:9200, <---server
    	 * blogs, <---left index
    	 * company, <---right index
    	 * url,	<--- left field
    	 * url, <--- right field
    	 * 1440m <--- scroll minutes(in memory)
    	 * 
    	 * 
    	 */
    	
    	String[] parameters = parameterString.split(",");
    	if(parameters.length < 6 )
    	{
    		System.out.println("invalid parameter");
    		System.exit(0);
    		return;
    	}
    	
		_wsUrl = parameters[0];
		leftIndex = parameters[1];
		rightIndex = parameters[2];
		leftField = parameters[3];
		rightField = parameters[4];
		scrollTime = parameters[5];
    	
    	//left index ��ȸ(scroll)
    	JSONObject jsonObj = searchScrollBegin(_wsUrl, leftIndex, scrollTime);
    	//System.out.println(jsonObj.get("_scroll_id"));
    	
    	String _scroll_id = jsonObj.get("_scroll_id").toString();
    	System.out.println("_scroll_id : " + _scroll_id);//scroll id
    	
    	JSONObject total = (JSONObject)((JSONObject)jsonObj.get("hits")).get("total");
    	long _total_value = (long) total.get("value");
    	
    	System.out.println("total count : " + _total_value);//��ü��
    	
    	if(_total_value == 0)
    	{
    		//��ȸ�� �����Ͱ� ����
    		System.out.println("No data found. -Left- ");
    		return;
    	}
    	
    	//�۾��ϱ�
    	JSONArray arrHits = (JSONArray)((JSONObject)jsonObj.get("hits")).get("hits");
    	
    	
    	Join(_wsUrl, arrHits, leftField, rightIndex, rightField);
    	
    	int i = 0;
    	while(true)
    	{
    		System.out.println(i++);
    		
    		//��ȸscroll
        	JSONObject jsonObjScroll = searchScroll(_wsUrl, _scroll_id, scrollTime);
        	JSONArray arrHitsScroll = (JSONArray)((JSONObject)jsonObjScroll.get("hits")).get("hits");
        	
        	//�۾��ϱ�
        	int hitsCnt = Join(_wsUrl, arrHitsScroll, leftField, rightIndex, rightField);
        	
        	if(hitsCnt == 0)
        	{
        		System.out.println("No data to work with.");

        		System.out.println("Begin delete scroll");
        		DELETE(_wsUrl + "/_search/scroll/", _scroll_id);
        		System.out.println("End delete scroll");

        		break;
        	}
        	
        	hitsCnt = 0;
        	
        	try {
        		
				Thread.sleep(100);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	
    	System.out.println("The operation is over.");

    }
    
    static String readFile(String path, Charset encoding) throws IOException 
	{
    	byte[] encoded = Files.readAllBytes(Paths.get(path));
    	return new String(encoded, encoding);
	}    

    public static int Join(String pUrl, JSONArray leftIndex, String leftField, String rightIndex, String rightField) throws IOException, ParseException
    {
    	//check exists
    	//�۾��ϱ�
    	int hitsCnt = 0;
    	for (Object o : leftIndex) {
    		if ( o instanceof JSONObject ) {
    	        //System.out.println("Scroll : " + o);
    	        
    	        //System.out.println( ((JSONObject)(((JSONObject) o).get("_source"))).get(sourceField) );
            	//JSONArray arrHitsScroll = (JSONArray)((JSONObject)jsonObjScroll.get("hits")).get("hits");
    	        
    			JSONObject sourceObj = ((JSONObject)(((JSONObject) o).get("_source")));
    			//System.out.println("[0]" + sourceObj);
    			//System.out.println("[1]" + sourceField + "--" + (sourceObj.get(sourceField)));
    			
    			String searchValue = (sourceObj.get(leftField)).toString();
    			
    			
    			final String objSourceIndexName = ((JSONObject) o).get("_index").toString();
    			final String objSourceID = ((JSONObject) o).get("_id").toString();

    			JSONObject objRight = POST(pUrl + "/" + rightIndex + "/_search", "{\"query\": {\"match\": {\""+ rightField + "\": \"" + searchValue + "\"}}}");
    			
    			//Join
				JSONArray objRightArrHits = (JSONArray)((JSONObject)objRight.get("hits")).get("hits");
    			
    			if(!objRight.isEmpty() && objRightArrHits.size() > 0)
    	        {	
    	        	JSONObject objRightSource = ((JSONObject)(((JSONObject) objRightArrHits.get(0)).get("_source")));
    	        	//Object objRightID = ((JSONObject) objRightArrHits.get(0)).get("_id");
    	        	
    	        	//System.out.println("++objRightID+++" + objRightID);
        			
    	        	if(!objRightSource.isEmpty())
    	        	{
//    	        		for (Object key : objRightSource.keySet()) {
//    	        			//System.out.println("-----" + key);
//    	        			if(!key.toString().equals(destField))
//    	        			{
//    	        				//sourceObj.put(key, objRightSource.get(key));
//    	        				
//    	        				//objRightSource.remove(key);
//    	        			}
//						}
    	        		
    	        		objRightSource.remove(rightField);
    	        		objRightSource.put("_join_status", "joined");
    	        		
    	        		JSONObject objUpdate = new JSONObject();
    	        		objUpdate.put("doc", objRightSource);
    	        		
    	        		//Update
    	        		//System.out.println("[update]" + objUpdate);
    	        		//System.out.println("[update url]" + pUrl + "/" + objSourceIndexName + "/_update/" + objSourceID);
    	        		
    	        		JSONObject objUpdateResult = POST(pUrl + "/" + objSourceIndexName + "/_update/" + objSourceID, objUpdate.toString());
    	        		
    	        		System.out.println(objUpdateResult);
    	        		
    	        	}
    	        	
    	        }
    			else
    			{
    				
    				JSONObject objRightSource = new JSONObject();
    				objRightSource.put("_join_status", "not_exist");
	        		
	        		JSONObject objUpdate = new JSONObject();
	        		objUpdate.put("doc", objRightSource);
	        		
	        		JSONObject objUpdateResult = POST(pUrl + "/" + objSourceIndexName + "/_update/" + objSourceID, objUpdate.toString());

    				//System.out.println("��Ī������ ����");
    			}
    			
    	        hitsCnt++;
    	    }
		}
    	
    	return hitsCnt;
    	
    	//update
    	
    }
    
    public static JSONObject POST(String pUrl, String query) throws IOException, ParseException
    {
    	//System.out.println("POST pUrl : " + pUrl);
    	//System.out.println("POST query : " + query);
    	
    	URL url = new URL(pUrl);
    	URLConnection con = url.openConnection();
    	HttpURLConnection http = (HttpURLConnection)con;
    	http.setRequestMethod("POST"); // PUT is another valid option
    	http.setDoOutput(true);
    	
    	byte[] out = query.getBytes(StandardCharsets.UTF_8);
    	
    	int length = out.length;

    	http.setFixedLengthStreamingMode(length);
    	http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    	http.connect();
    	try(OutputStream os = http.getOutputStream()) {
    	    os.write(out);
    	}
    	
    	InputStream is = http.getInputStream();
    	
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line;
		StringBuffer response = new StringBuffer(); 
		while((line = rd.readLine()) != null) {
		 response.append(line);
		 response.append('\r');
		}
		rd.close();
		
		JSONParser parser = new JSONParser();
        Object obj = parser.parse( response.toString() );
			
        JSONObject jsonObj = (JSONObject) obj;
        
        return jsonObj;

   

    }
    
    public static JSONObject DELETE(String pUrl, String scrollID)
    {
    	CloseableHttpClient client = HttpClients.custom()
                .setRetryHandler(new MyRequestRetryHandler()).build();

    	HttpDelete method = new HttpDelete(pUrl + scrollID);
        // Execute the method.

        try {
            CloseableHttpResponse response = client.execute(method);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + response.getStatusLine());
            }else{
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                System.out.println("responseBody is " + responseBody);
                
                JSONParser parser = new JSONParser();
                Object obj;
				try {
					obj = parser.parse( responseBody );
					
	                JSONObject jsonObj = (JSONObject) obj;
	                //JSONObject _source = (JSONObject)jsonObj.get("_source");

					return jsonObj;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            } 
            

        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        
        return null;
    }

    public static JSONObject searchScroll(String wsUrl, String scrollID, String scrollTime) throws IOException, ParseException
    {
    	//System.out.println("_searchScroll_");
    	return POST(wsUrl + "/_search/scroll", "{\"scroll\":\"" + scrollTime + "\",\"scroll_id\":\""+ scrollID + "\"}");

    }
    
    public static JSONObject searchScrollBegin(String wsUrl, String indexName, String scrollTime) throws IOException, ParseException
    {
		String query = "";
		query 		+=	"{"
		    		+"		\"size\": 10,"
		    		+"	    \"query\": {"
		    		+"	        \"bool\": {"
		    		+"	            \"must_not\": {"
		    		+"	                \"exists\": {"
		    		+"	                    \"field\": \"_join_status\""
		    		+"	                }"
		    		+"	            }"
		    		+"	        }"
		    		+"	    }"
		    		+"	}";
    	
    	//query = "{\"size\": 10,\"query\": {\"match_all\": {}},\"sort\": [{\"_doc\": {\"order\": \"asc\"}}]}";
    	
    	return POST(wsUrl + "/" + indexName + "/_search?scroll=" + scrollTime, query);    	
    }
    
//    public static void insert() throws IOException
//    {
//
//    	URL url = new URL(_wsUrl + "/aaa/_doc");
//    	URLConnection con = url.openConnection();
//    	HttpURLConnection http = (HttpURLConnection)con;
//    	http.setRequestMethod("POST"); // PUT is another valid option
//    	http.setDoOutput(true);
//    	
//    	byte[] out = "{\"username\":\"root\",\"password\":\"password\"}".getBytes(StandardCharsets.UTF_8);
//    	int length = out.length;
//
//    	http.setFixedLengthStreamingMode(length);
//    	http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//    	http.connect();
//    	try(OutputStream os = http.getOutputStream()) {
//    	    os.write(out);
//    	}
//    	
//    	try(InputStream is = http.getInputStream())
//    	{
//    		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//    		String line;
//    		StringBuffer response = new StringBuffer(); 
//    		while((line = rd.readLine()) != null) {
//    		 response.append(line);
//    		 response.append('\r');
//    		}
//    		rd.close();
//    		
//    		System.out.println(response.toString());
//    	}
//
//    }

//    public static JSONObject httpClient(String index, String type, String id)
//    {
//    	CloseableHttpClient client = HttpClients.custom()
//                .setRetryHandler(new MyRequestRetryHandler()).build();
//
//        HttpGet method = new HttpGet(_wsUrl+"/" + index + "/" + type + "/" + id);
//        // Execute the method.
//
//        try {
//            CloseableHttpResponse response = client.execute(method);
//
//            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//                System.err.println("Method failed: " + response.getStatusLine());
//            }else{
//                HttpEntity entity = response.getEntity();
//                String responseBody = EntityUtils.toString(entity);
//                System.out.println("responseBody is " + responseBody);
//                
//                JSONParser parser = new JSONParser();
//                Object obj;
//				try {
//					obj = parser.parse( responseBody );
//					
//	                JSONObject jsonObj = (JSONObject) obj;
//	                JSONObject _source = (JSONObject)jsonObj.get("_source");
//
//					return _source;
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//            } 
//            
//
//        } catch (IOException e) {
//            System.err.println("Fatal transport error: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            // Release the connection.
//            method.releaseConnection();
//        }
//        
//        return null;
//    }
    
    public static void httpClientBasicAuth()
    {
    	HttpHost targetHost = new HttpHost("13.124.240.182", 9200, "http");
    	CredentialsProvider credsProvider = new BasicCredentialsProvider();
    	credsProvider.setCredentials(
    			new AuthScope(targetHost.getHostName(), targetHost.getPort()),
    			new UsernamePasswordCredentials("elastic", "elastic")
    			);
    	
    	//AuthCache �ν��Ͻ� ����
    	AuthCache authCache = new BasicAuthCache();
    	
    	//Generate BASIC scheme object and add int to local auth cache
    	//BasicScheme ��ü�� �����ؼ� ���� ���� ĳ�ÿ� �߰��Ѵ�.
    	BasicScheme basicAuth = new BasicScheme();
    	authCache.put(targetHost, basicAuth);
    	
    	//���� ���ؽ�Ʈ�� AuthCache�� �߰��Ѵ�.
    	HttpClientContext context = HttpClientContext.create();
    	context.setCredentialsProvider(credsProvider);
    	
    	CloseableHttpClient client = HttpClients.custom()
                .setRetryHandler(new MyRequestRetryHandler()).build();

        HttpGet method = new HttpGet(_wsUrlAuth+"/test-index/test-type/1");
        // Execute the method.

        try {
            CloseableHttpResponse response = client.execute(method, context);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + response.getStatusLine());
            }else{
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                System.out.println("responseBody is " + responseBody);
            }

        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
    }
}
