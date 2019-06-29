package tool.json;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

//junit4 테스트 라이브러리 기본 사용법
//https://www.youtube.com/watch?v=tyZMdwT3rIY
	
public class JsonHelperTest {
	
	private JsonHelper jh;
	
	@Before
	public void setup() {
		jh = new JsonHelper();
	}

	@Test
	public void test() throws ParseException {
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
		
		JSONObject jObj = jh.convertJSONObject(query);
		
		JSONObject jObjTest = jh.findWithXpath(jObj, "query//bool//must_not//exists");
		
		//String resultStr = jObjTest.toString();//((JSONObject)((JSONObject)((JSONObject)((JSONObject)jObj.get("query")).get("bool")).get("must_not")).get("exists")).get("field").toString();
		String resultStr = jObjTest.get("field").toString();
		assertEquals("_join_status", resultStr);//resultStr);
	}

}
