package com.prd.consoletest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.GsonBuilder;
import com.prd.Environment;
import com.prd.models.AdvertisementRewardTransaction;



public class DB_Mysql {
	
	static final String FORBIDDEN_ERROR = "403 Forbidden: ";
	static final String NULLPOINT_ERROR = "404 Null Exception: ";

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {
		
		String selectedZone = "DEV";
		Environment environment = null;
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		ClassLoader classLoader = YML.class.getClassLoader();//getClass().getClassLoader();
		environment = mapper.readValue(classLoader.getResource("Environment" + selectedZone + ".yml"), Environment.class);

		System.out.println(ReflectionToStringBuilder.toString(environment, ToStringStyle.MULTI_LINE_STYLE));

		long owner = 1111111l;
		int currency = 0;
		
	    //Connection con = null;
	    PreparedStatement pst = null;
	    ResultSet rs = null;
	    PreparedStatement insertPst = null;

		
	    try(Connection con = DriverManager.getConnection(environment.getJdbcUrl(), environment.getDbUserID(), environment.getDbUserPW());){
	    	//con = DriverManager.getConnection(environment.getJdbcUrl(), environment.getDbUserID(), environment.getDbUserPW());
	    	String sql = "SELECT u.intValue, u.owner, u.stringValue, u.datetimeValue "
	    			+ "FROM dev.test u "
	    			+ "WHERE u.owner = ?;";

	    	pst = con.prepareStatement(sql);
	    	pst.setLong(1, owner);
	        rs = pst.executeQuery(); 
	        
	        if(!rs.next()) {
	        	throw new RuntimeException(FORBIDDEN_ERROR + " not exists User");
	        }
	        
	        int intValue = rs.getInt("intValue");
	        String stringValue = rs.getString("stringValue");
	        
	        java.util.Date utilDate = new java.util.Date(rs.getDate("datetimeValue").getTime());

	        java.util.Date utilDate2 = rs.getTimestamp("datetimeValue");    //->java.sql.Timestamp가  Date을 상속하기 때문에 이렇게 쓸 수 있음

	        pst.close();
	        rs.close();
	        
	        System.out.println(utilDate);
	        
	        if(1==1)
	        {
	        	return;	
	        }
	        ////

			//context.getLogger().log("USER_FIND");
//	        sql = "SELECT stringValue FROM ss_bts_master.CommonCode WHERE category = 'AD' AND code = 'OFFER_WALL'";
//	    	pst = con.prepareStatement(sql);
//	        rs = pst.executeQuery(); 
//
//			if(!rs.next()) {
//	        	throw new RuntimeException("Not Exists CommonCode");
//			}
//			//context.getLogger().log("COMMON_CODE_SEARCH");
//			
//	        String[] localeCodes = rs.getString("stringValue").split(",");
//	        
//	        insertPst = con.prepareStatement("INSERT ss_bts_account_1.Message (expiredAt, owner, description, provideItem, quantity, status, subject, type) value (?,?,?,?,?,?,?,?);");
//	        LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
//	        insertPst.setDate(1, new Date(Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant()).getTime()));
//	        insertPst.setLong(2, owner);
//			insertPst.setString(3, "Locale@"+ localeCodes[1]);
//			insertPst.setLong(4, 2);
//			insertPst.setInt(5, currency);
//			insertPst.setShort(6, (short)0);
//			insertPst.setString(7, "Locale@"+ localeCodes[0]);
//			insertPst.setShort(8, (short)0);
//			insertPst.executeUpdate();

			//context.getLogger().log("MESSAGE_SEND");

//
//            HttpClientConfig clientConfig = new HttpClientConfig.Builder(environment.getElasticsearchUrl())
//	    			.multiThreaded(true)
//	    			.readTimeout(180000)
//	    			.gson(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()).build();
//	    	factory.setHttpClientConfig(clientConfig);
//	    	jestClient = factory.getObject();
//	    	JSONObject note = new JSONObject();
//	    	note.put("id", id);
			//AdvertisementRewardTransaction transaction = new AdvertisementRewardTransaction(owner, 1, (short)5, 1, 1, "".toString());
			
//			Index index = new Index.Builder(transaction).index(transaction.getIndexName()).type("data").build();
//	    	
//			DocumentResult r = jestClient.execute(index);
			
//			if(!r.isSucceeded()) {
//				throw new RuntimeException(NULLPOINT_ERROR + "do not insert tansaction");
//			}
			
	    } catch(Exception e)
	    {
	    	System.out.println(e);
	    	//context.getLogger().log(e.getMessage());
	    	//throw new RuntimeException(FORBIDDEN_ERROR + " not key found");
	    } finally {
	    	try {
	            DbUtils.close(rs);
	            DbUtils.close(pst);
	            DbUtils.close(insertPst);

	            //jestClient.shutdownClient();
	          } catch (SQLException ex) {
//	            context.getLogger().log("Error toString : " + ex.toString());
//	            context.getLogger().log("Error getMessage : " + ex.getMessage());
	            
	            throw new RuntimeException( ex );
	          }
		}

	}

}
