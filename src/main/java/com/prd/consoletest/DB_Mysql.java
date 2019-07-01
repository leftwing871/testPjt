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
import java.time.format.DateTimeFormatter;

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

		
	    try(Connection con = DriverManager.getConnection(environment.getJdbcUrl(), environment.getDbUserID(), environment.getDbUserPW());)
	    {
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
	        
	        System.out.println("datetimeValue : " + utilDate);
	        
	        System.out.println("--------------");

		    PreparedStatement insertPst = null;
	        insertPst = con.prepareStatement("INSERT dev.test (owner, stringValue, datetimeValue) value (?,?,?);");
	        LocalDateTime datetimeValue = LocalDateTime.now();//.plusDays(7);
	        insertPst.setLong(1, owner);
	        insertPst.setString(2, "str : " + datetimeValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) );
	        insertPst.setDate(3, new java.sql.Date(  utilDate2.getTime() ) );

			insertPst.executeUpdate();
			DbUtils.close(insertPst);

	    } catch(Exception e)
	    {
	    	System.out.println(e);
	    	//context.getLogger().log(e.getMessage());
	    	//throw new RuntimeException(FORBIDDEN_ERROR + " not key found");
	    } finally {
	    	try {
				DbUtils.close(rs);
				DbUtils.close(pst);

	          } catch (SQLException ex) {
//	            context.getLogger().log("Error toString : " + ex.toString());
//	            context.getLogger().log("Error getMessage : " + ex.getMessage());
	            
	            throw new RuntimeException( ex );
	          }
		}

	}

}

/*


java.sql.Date <==> java.util.Date
http://younch.egloos.com/10505615

Java 로 DB 에서 데이터를 가져올때
#### 1. java.sql.Date ==> java.util.Date
java.util.Date utilDate = new java.util.Date(rs.getDate("regdate").getTime());

java.util.Date utilDate = rs.getTimestamp("regdate");    //->java.sql.Timestamp가  Date을 상속하기 때문에 이렇게 쓸 수 있음


#### 2. java.util.Date ==> java.sql.Date
pstmt.setDate(4, new java.sql.Date(java.util.Date.getTime()) );

pstmt.setTimestamp(4, new java.sql.Timestamp(java.util.Date.getTime()) );

 

 

//밀리초로 바꿔주는 cal.getTimeInMillis()  함수를 이용해 다양한 방식?으로 넣을 수 있습니다.
pstmt.setDate(int parameterIndex, java.sql.Date(cal.getTimeInMillis()))
pstmt.setTime(int parameterIndex, java.sql.Time(cal.getTimeInMillis()))
pstmt.setTimestamp(int parameterIndex, java.sql.Timestamp(cal.getTimeInMillis()))

 

#### 3. 문자열로 사용하기

java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

String regdateStr = sdf.format(rs.getTimestamp("regdate"));

 

####

java.util.Date.getTime()


DateUtil

getDate()메서드로 값을 가지고 와서 해당 Date를

// Calendar 를 DB 의 등록일로 설정함
Calendar cal = Calendar.getInstance();
cal.setTime(rs.getDate("regdate"));

cla.getTime() 이 입력시간하고 같아야 한다.

 


java.sql.Date 클래스는 JDBC등을 이용해서 데이터베이스에 격납된 날짜나 시각정보를 데이터로서 추출하거나

데이터베이스에 격납할 때의 영역으로 사용합니다. 문자열 형식이 "2008-12-30 12:30:20" 와 같은 형태의 데이터

에 특화해서 사용하는데에 적합합니다.

연월일시분초등의 요소를 따로 따로 분리하지 않고 하나의 정보로서 다룹니다.

 

java.util.Date 클래스는 보다 범용적인 날짜, 시각정보를 다루는 경우에 사용합니다.

문자열 형식이 "Mon Dec 30 12:30:30 GMT-0700 2008"과 같은 GMT일시를 포함해서 각종 포맷에 대응됩니다.

java.sql.Date 와같이 연월일시분초등의 요소를 따로 따로 분리하지 않고 하나의 정보로서 다루는 것도 가능하고

연, 월, 일, 시, 분, 초 등의 요소를 따로 따로 분리하거나 비교, 계산하는 데도 사용됩니다.

 

 

#############################################

# 오라클에서 사용하기 - 09.08.28

#############################################

 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
 SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss"); // 오전/오후


 SimpleDateFormat SDF_DEFAULT  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
 SimpleDateFormat SDF_SHORT   = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
 SimpleDateFormat SDF_YMD    = new SimpleDateFormat("yyyy-MM-dd");
 SimpleDateFormat SDF_YMD_SHORT = new SimpleDateFormat("yy-MM-dd");
 SimpleDateFormat SDF_HMS    = new SimpleDateFormat("hh:MM:ss");


 // Oracle 의 Date 형은 getTimestamp() 를 사용해야됨
 // getDate() 는 YYYY/MM/DD 까지만 표시됨

 

sdfStr = sdf2.format(rs.getTimestamp("WARRANT_YYYYMMDD").getTime());

 

rs.getDate("WARRANT_YYYYMMDD")

*/

/*
Java 1.8 날짜 정리
자바 1.8 이전에는 날짜 연산이 쉽지 않았다. Joda 같은 라이브러리들을 쓰면 된다고 하는데, 필자는 Util 클래스에서 내부적으로 Calendar를 사용하여 연산하고 Date/long/String 간의 변환을 통하여 사용해왔다. 그러던 와중에 자바 1.8의 새로운 날짜들을 보니 신세계가 열렸다. 그래서 간단하게 소개하고 자주 사용될 만한 예제들도 나열하려 한다.

먼저 중요하다고 생각되는 클래스들을 소개하겠다.

클래스
날짜 (Temporal)
Instant : machine time에 유용한 1970년 1월 1일부터 시간을 세는 클래스 (millisecond 뿐만 아니라 nanosecond까지 센다)

LocalDate : [년,월,일]과 같은 날짜만 표현하는 클래스 (시간은 포함하지 않는다)

LocalDateTime : [년,월,일,시,분,초]를 표현하는 클래스 (LocalDate와 함께 가장 많이 쓰이는 클래스가 될 것 같다)

LocalTime : [시,분,초]와 같이 시간만 표현하는 클래스

기간 (TemporalAmount)
Period : 두 날짜 사이의 [년,월,일]로 표현되는 기간 (시간을 다루지 않다 보니 LocalDate를 사용한다)

Duration : 두 시간 사이의 [일,시,분,초]로 표현되는 기간 (Instant 클래스를 사용하고, seconds와 nanoseconds로 측정 되지만 [일,시,분,초]로 변환해 주는 메쏘드를 제공)

기타
ChronoUnit : 한가지의 단위를 표현하기 위한 클래스 (년,월,일,시,분,초 등)

DayOfWeek : 요일

자주 쓰는 메쏘드들
날짜 가져오기

  LocalDate.now(); // 오늘
  LocalDateTime.now(); // 지금
  LocalDate.of(2015, 4, 17); // 2015년4월17일
  LocalDateTime.of(2015, 4, 17, 23, 23, 50); // 2015년4월17일23시23분50초
  Year.of(2015).atMonth(3).atDay(4).atTime(10, 30); // 2015년3월4일 10시30분00초
기간 가져오기

  Period.ofYears(2); // 2년간(P2Y)
  Period.ofMonths(5); // 5개월간(P5M)
  Period.ofWeeks(3); // 3주간(P21D)
  Period.ofDays(20); // 20일간(P20D)

  Duration.ofDays(2); // 48시간(PT48H)
  Duration.ofHours(8); // 8시간(PT8H)
  Duration.ofMinutes(10); // 10분간(PT10M)
  Duration.ofSeconds(30); // 30초간(PT30S)
날짜 + 기간 = 날짜

  LocalTime.of(9, 0, 0).plus(Duration.ofMinutes(10)); // (9시 + 10분간) = 9시10분
  LocalDate.of(2015, 5, 15).plus(Period.ofDays(1)); // (2015년5월15일 + 1일간) = 2015년5월16일
  LocalDateTime.of(2015, 4, 17, 23, 47, 5).minus(Period.ofWeeks(3)); // (2015년4월17일 23시47분05초 - 3주간) = 2015년3월27일 23시47분05초
  LocalDate.now().plusDays(1); // (오늘 + 1일) = 내일
  LocalTime.now().minusHours(3); // (지금 - 3시간) = 3시간 전
날짜 - 날짜 = 기간

  Period.between(LocalDate.of(1950, 6, 25), LocalDate.of(1953, 7, 27)); // (1953년7월27일 - 1950년6월25일) = 3년1개월2일간(P3Y1M2D)
  Period.between(LocalDate.of(1950, 6, 25), LocalDate.of(1953, 7, 27)).getDays(); // 3년1개월2일간 => 2일간
  LocalDate.of(1950, 6, 25).until(LocalDate.of(1953, 7, 27), ChronoUnit.DAYS); // 3년1개월2일간 => 1128일간
  ChronoUnit.DAYS.between(LocalDate.of(1950, 6, 25), LocalDate.of(1953, 7, 27)); // 3년1개월2일간 => 1128일간

  Duration.between(LocalTime.of(10, 50), LocalTime.of(19, 0)); // (19시00분00초 - 10시50분00초) = 8시간10분간(PT8H10M)
  Duration.between(LocalDateTime.of(2015, 1, 1, 0, 0), LocalDateTime.of(2016, 1, 1, 0, 0)).toDays(); // 365일간
  ChronoUnit.YEARS.between(LocalDate.of(2015, 5, 5), LocalDate.of(2017, 2, 1)); // 1년간
날짜 변환하기

LocalDate -> String

LocalDate.of(2020, 12, 12).format(DateTimeFormatter.BASIC_ISO_DATE); // 20201212
LocalDateTime -> String

LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 2015-04-18 00:42:24
LocalDateTime -> java.util.Date

Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()); // Sat Apr 18 01:00:30 KST 2015
LocalDate -> java.sql.Date

Date.valueOf(LocalDate.of(2015, 5, 5)); // 2015-05-05
LocalDateTime -> java.sql.Timestamp

Timestamp.valueOf(LocalDateTime.now()); // 2015-04-18 01:06:55.323
String -> LocalDate

LocalDate.parse("2002-05-09"); // 2002-05-09
LocalDate.parse("20081004", DateTimeFormatter.BASIC_ISO_DATE); // 2008-10-04
String -> LocalDateTime

LocalDateTime.parse("2007-12-03T10:15:30"); // 2007-12-03T10:15:30
LocalDateTime.parse("2010-11-25 12:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 2010-11-25T12:30
java.util.Date -> LocalDateTime

LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()); // 2015-04-18T01:16:46.755
java.sql.Date -> LocalDate

new Date(System.currentTimeMillis()).toLocalDate(); // 2015-04-18
java.sql.Timestamp -> LocalDateTime

new Timestamp(System.currentTimeMillis()).toLocalDateTime(); // 2015-04-18T01:20:07.364
LocalDateTime -> LocalDate

LocalDate.from(LocalDateTime.now()); // 2015-04-18
LocalDate -> LocalDateTime

LocalDate.now().atTime(2, 30); // 2015-04-18T02:30
요일로 날짜 가져오기

  LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)); // 다음 토요일
  LocalDate.of(2016, 5, 1).with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.SUNDAY)); // 2016년5월 세번째 일요일
  LocalDate.of(2015, 7, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)); // 2015년7월 첫번째 월요일
언어별 출력

  DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.ENGLISH); // Monday
  DayOfWeek.MONDAY.getDisplayName(TextStyle.NARROW, Locale.ENGLISH); // M
  DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // Mon

  DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, Locale.KOREAN); // 월요일
  DayOfWeek.MONDAY.getDisplayName(TextStyle.NARROW, Locale.KOREAN); // 월
  DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.KOREAN); // 월

  Month.FEBRUARY.getDisplayName(TextStyle.FULL, Locale.US); // February
  Month.FEBRUARY.getDisplayName(TextStyle.FULL, Locale.KOREA); // 2월
참고
https://docs.oracle.com/javase/tutorial/datetime/iso/overview.html


출처: https://jekalmin.tistory.com/entry/자바-18-날짜-정리 [jekalmin의 블로그]
*/