package com.prd.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.prd.Environment;
import com.prd.type.SSBDatabase;
import com.prd.type.StaticValueSupplier;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaAsyncClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Utils {
	public static boolean IsEnableZone(String selectedZone)
	{
		selectedZone = selectedZone.toUpperCase();
		
        if(!selectedZone.equals("DEV") && !selectedZone.equals("QA") && !selectedZone.equals("QABETA") && !selectedZone.equals("REVIEW") && !selectedZone.equals("LIVE"))
        	return false;
        
        return true;
	}
	
	public static Environment getEnvironment(Object paramInstance, String selectedZone)
	{
		Environment environment = null;

        try {
	        	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	        	ClassLoader classLoader = paramInstance.getClass().getClassLoader();
	        	
	        	return environment = mapper.readValue(classLoader.getResource("Environment" + selectedZone + ".yml"), Environment.class);
        } catch (Exception ex) {
        		throw new RuntimeException( ex );
        }
	}
	
    public static InvokeResult runWithoutPayload(String regionName, String functionName) {
    		return runWithPayload(regionName, functionName, null);
    }

    public static InvokeResult runWithPayload(String regionName, String functionName, String payload) {
        AWSLambdaAsyncClient client = new AWSLambdaAsyncClient();
        client.withRegion( Regions.fromName(regionName) );

        InvokeRequest request = new InvokeRequest();
        request.withFunctionName(functionName).withPayload(payload);
        InvokeResult invoke = client.invoke(request);
        
        return invoke;
    }
    
    
    public static String downloadFromURL(String url,String fileName, Boolean isZip, String path) throws URISyntaxException{
    	
    	String folderName = null;
    	String tempName = "";
    	if(isZip){
    		tempName = fileName.substring(0, fileName.indexOf("."));
    	}
    	
    	try{

    		//이전 파일 삭제 - 성공 후 file download
    		File preFile = new File(path+fileName);
    		
    		if(preFile.exists()){
    			if(preFile.delete()){
        			//file 다운로드 
    				InputStream in = new URL(url).openStream();
            		Files.copy(in, Paths.get(path+fileName), StandardCopyOption.REPLACE_EXISTING);
            		System.out.println("삭제성공");
        		}else {
        			System.out.println("zipFile 삭제 실패");
        		}
    		}else {
    			//file 다운로드 
        		InputStream in = new URL(url).openStream();
        		Files.copy(in, Paths.get(path+fileName), StandardCopyOption.REPLACE_EXISTING);
        		System.out.println("다운로드성공");
    		}
    		
    		if(isZip){
        		//압축 풀기
    			folderName = tarGzArchive(new File(path+fileName),path,tempName);
        		preFile.delete();
    		}

    	} catch(IOException ie){
    	
    	}
    	return folderName;
    }
    
    
    public static String tarGzArchive(File file, String path,String fileName) {
    	Map<String, byte[]> content = new HashMap<String, byte[]>();
    	File parent = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            GZIPInputStream gis = new GZIPInputStream(fis);
            TarInputStream tis = new TarInputStream(gis);
 
            TarEntry tarEntry = null;
            ByteArrayOutputStream baos = null;
            while ((tarEntry = tis.getNextEntry()) != null) {
                String entryName = tarEntry.getName();
                if (entryName != null) {
                    baos = new ByteArrayOutputStream();
                    tis.copyEntryContents(baos);
                    byte[] bytes = baos.toByteArray();
                    content.put(entryName, bytes);
                }
            }
            
            tis.close();
            
    		Set<String> entries = content.keySet();
    		
    		for(String entry : entries){
    			String tmp = entry.substring(entry.indexOf("/")+1, entry.length());
//    			System.out.println("entry:" + tmp);
                File newFile = new File(path+fileName+"/"+tmp);
                
                if (entry.endsWith("/")) {
                    newFile.mkdirs();
                    continue;
                }
                
                parent = new File(newFile.getParentFile().getParentFile()+"/"+fileName);
                if ((parent != null) && (!parent.exists())) {
                    parent.mkdirs();
                }
                
                FileOutputStream fos = new FileOutputStream(newFile);
                byte[] bytes = content.get(entry);
                fos.write(bytes);
                fos.close();
    		}
    		
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return parent.getName();
    }
    
    public static String leftPad(String originalString, int length, char padCharacter) {
		StringBuilder sb = new StringBuilder();
		while (sb.length() + originalString.length() < length) {
			sb.append(padCharacter);
		}
		sb.append(originalString);
		String paddedString = sb.toString();
		return paddedString;
	}
    
    static long _startTime;
    static long _endTime;
    
    public static void setStartTime()
    {
    		_startTime = System.currentTimeMillis();
    }
    
    public static void setEndTime()
    {
    		_endTime = System.currentTimeMillis();
    }
    
    public static long getElapsedTime()
    {
    		return (Utils._endTime - Utils._startTime) / 1000;
    }
    
    public static boolean upload(String regionName, String keyId, String accessKey, String bucket, String srcPath, String distPath, ObjectMetadata md) throws Exception
	{
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(keyId, accessKey);
		AmazonS3 s3Client = new AmazonS3Client(awsCreds);
		s3Client.setRegion(Region.getRegion(Regions.fromName(regionName)));
		
		try
		{
			File file = new File(srcPath);
			if(!file.exists())
				throw new Exception(srcPath + "file not exists");
						
			s3Client.putObject(new PutObjectRequest(bucket, distPath, file)
						.withCannedAcl(CannedAccessControlList.PublicRead)
						.withMetadata(md));
			
			return true;
		}
		catch(AmazonClientException e)
		{
			throw e;
		}
	}
    public static HashMap<String, String> callPOST(String address, HashMap<String, Object> params, HashMap<String, String> headers)
	{
    		Unirest.setTimeouts(2000, 2000);
    	
    		HashMap<String, String> resultMap = new HashMap<String, String>();
	    	String res = "";
	    	int status = 0;
	    	
	    	String paramString = Utils.convertHashMapToJsonString(params);
	
	    	try{
	    		if(headers.isEmpty()){
	    			HttpResponse<String> resData = Unirest.post(address)
	    					.body(paramString)
	    					.asString();
	    			res = resData.getBody();
	    			status = resData.getStatus();
	    		}else{
	    			HttpResponse<String> resData = Unirest.post(address)
	        				.headers(headers)
	        				.body(paramString)
	    					.asString();
	    			res = resData.getBody();
	    			status = resData.getStatus();
	    		}
	    		
	    	}catch(UnirestException e){
	    		status = -1;
	    	}catch(Exception e){
	    		status = -1;
	    	}
	    	
	    	resultMap.put("body", res);
	    	resultMap.put("status", String.valueOf(status));
	    	
	    	return resultMap;
	}
    
    /**
	   * Convert a JSON string to pretty print version
	   * @param jsonString
	   * @return
	   */
	  public static String toPrettyFormat(String jsonString) 
	  {
	      JsonParser parser = new JsonParser();
	      JsonObject json = parser.parse(jsonString).getAsJsonObject();

	      Gson gson = new GsonBuilder().setPrettyPrinting().create();
	      String prettyJson = gson.toJson(json);

	      return prettyJson;
	  }
	  
	  
	  public static String convertHashMapToJsonString(HashMap<String, Object> map) 
	  {
	      Gson gson = new Gson();
	      String json = gson.toJson(map);

	      return json;
	  }
	  
	  public static String log(String zone, String jobName, String dateStr, String product) {
		  HashMap<String, Object> tmpParam = new HashMap<String, Object>();
		  HashMap<String, String> tmpHeaders = new HashMap<String, String>();

		  tmpParam.put("zone", zone);
		  tmpParam.put("jobName", jobName);
		  tmpParam.put("dateStr", dateStr);
		  tmpParam.put("product", product);

		  final String LOG_API_URL = "https://z5648409xh.execute-api.ap-northeast-2.amazonaws.com/LIVE/ssbbatchjoblogger";

		  HashMap<String, String> result = Utils.callPOST(LOG_API_URL, tmpParam, tmpHeaders);
//		  System.out.println(result.get("status"));
//		  System.out.println(result.get("body"));
		  
		  if(result.get("status").toString().equals("200")) {
			  return result.get("body").toString();
		  }
		  
		  return "-1";
	  }


	static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	  
	  public static Date GetUTCdatetimeAsDate()
	  {
	      //note: doesn't check for null
	      return StringDateToDate(GetUTCdatetimeAsString());
	  }
	
	  public static String GetUTCdatetimeAsString()
	  {
	      final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
	      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	      final String utcTime = sdf.format(new Date());
	
	      return utcTime;
	  }
	
	  public static Date StringDateToDate(String StrDate)
	  {
	      Date dateToReturn = null;
	      SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
	
	      try
	      {
	          dateToReturn = (Date)dateFormat.parse(StrDate);
	      }
	      catch (ParseException e)
	      {
	          e.printStackTrace();
	      }
	
	      return dateToReturn;
	  }
	  
	  
	  public static LocalDateTime getLocalDateTimeSeoul()
	  {
  		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  		String utcNowStr = Utils.GetUTCdatetimeAsString();
  		LocalDateTime ldt = LocalDateTime.parse(utcNowStr, formatter).plusHours(9);

  		return ldt;
	  }
	  
	  public static String[] getDBAccount(String region, String selectedZone)
	  {
	        HashMap<String, Object> inputParams = new HashMap<String, Object>();
	        inputParams.put("zone", selectedZone);
			
			
	        InvokeResult res = Utils.runWithPayload(region, StaticValueSupplier.getARNdecrypt_db_account(selectedZone), JsonUtil.getJsonStringFromMap(inputParams).toString());
	        if(res.getStatusCode() != 200)
	        		throw new RuntimeException("fail get db account");
	        
	        ByteBuffer response_payload = res.getPayload();
//	        System.out.println("-------" + new String(response_payload.array()).split("\\|")[0]);
//	        System.out.println("-------" + new String(response_payload.array()).split("\\|")[1]);
	        String[] dbAccount = new String(response_payload.array()).replaceAll("\"", "").split("\\|");
	        
	        return dbAccount;
	  }
	  
	  public static String changeDatabase(String jdbcURL, SSBDatabase from, SSBDatabase to) {
			return jdbcURL.replaceAll(from.toString(), to.toString());
	  }
	  
	  public static <T> T converJsonStringToObject(String json, Class<T> classOfT) 
	  {
			Gson gson = new Gson();
			
			try {
				T x = gson.fromJson(json, classOfT);
				return x;
			} catch ( Exception ex)
			{
				return null;
			}

	  }
	  
	  public static HashMap<String, String> callPOST(String address, HashMap<String, Object> params, HashMap<String, String> headers, long connectionTimeout, long socketTimeout)
	  {
	    		Unirest.setTimeouts(connectionTimeout, socketTimeout);
	    	
	    		HashMap<String, String> resultMap = new HashMap<String, String>();
		    	String res = "";
		    	int status = 0;
		    	
		    	String paramString = Utils.convertHashMapToJsonString(params);
		
		    	try{
		    		if(headers.isEmpty()){
		    			HttpResponse<String> resData = Unirest.post(address)
		    					.body(paramString)
		    					.asString();
		    			res = resData.getBody();
		    			status = resData.getStatus();
		    		}else{
		    			HttpResponse<String> resData = Unirest.post(address)
		        				.headers(headers)
		        				.body(paramString)
		    					.asString();
		    			res = resData.getBody();
		    			status = resData.getStatus();
		    		}
		    		
		    	}catch(UnirestException e){
		    		status = -1;
		    	}catch(Exception e){
		    		status = -1;
		    	}
		    	
		    	resultMap.put("body", res);
		    	resultMap.put("status", String.valueOf(status));
		    	
		    	return resultMap;
	  }
}
