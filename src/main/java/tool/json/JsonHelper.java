package tool.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonHelper {
	
	JSONObject _jsonObj;
	
	public JsonHelper()
	{
		
	}
	
	public JsonHelper(String str) throws ParseException
	{
		JSONParser parser = new JSONParser();
        Object obj = parser.parse( str );
			
        _jsonObj = (JSONObject) obj;
	}

	JSONObject convertJSONObject(String str) throws ParseException
	{
		JSONParser parser = new JSONParser();
        Object obj = parser.parse( str );
			
        JSONObject jsonObj = (JSONObject) obj;
        
        return jsonObj;

	}
	
	JSONObject findWithXpath(JSONObject jObj, String path) {
		String[] strArray = path.split("//");
		
		for(String s : strArray)
		{
			System.out.println(s);
			jObj = (JSONObject)jObj.get(s);
			System.out.println(jObj.toJSONString());
		}
		
		return jObj;
	}
	
}
