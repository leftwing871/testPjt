package tool.json;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
	
	@Test
	public void testExample1()
	{
        //JSON 데이터
        String jsonInfo = "{\"books\":[{\"genre\":\"소설\",\"price\":\"100\",\"name\":\"사람은 무엇으로 사는가?\",\"writer\":\"톨스토이\",\"publisher\":\"톨스토이 출판사\"},{\"genre\":\"소설\",\"price\":\"300\",\"name\":\"홍길동전\",\"writer\":\"허균\",\"publisher\":\"허균 출판사\"},{\"genre\":\"소설\",\"price\":\"900\",\"name\":\"레미제라블\",\"writer\":\"빅토르 위고\",\"publisher\":\"빅토르 위고 출판사\"}],\"persons\":[{\"nickname\":\"남궁민수\",\"age\":\"25\",\"name\":\"송강호\",\"gender\":\"남자\"},{\"nickname\":\"예니콜\",\"age\":\"21\",\"name\":\"전지현\",\"gender\":\"여자\"}]}";
 
        /*
        {
            "books": [
                {
                    "genre": "소설",
                    "price": "100",
                    "name": "사람은 무엇으로 사는가?",
                    "writer": "톨스토이",
                    "publisher": "톨스토이 출판사"
                },
                {
                    "genre": "소설",
                    "price": "300",
                    "name": "홍길동전",
                    "writer": "허균",
                    "publisher": "허균 출판사"
                },
                {
                    "genre": "소설",
                    "price": "900",
                    "name": "레미제라블",
                    "writer": "빅토르 위고",
                    "publisher": "빅토르 위고 출판사"
                }
            ],
            "persons": [
                {
                    "nickname": "남궁민수",
                    "age": "25",
                    "name": "송강호",
                    "gender": "남자"
                },
                {
                    "nickname": "예니콜",
                    "age": "21",
                    "name": "전지현",
                    "gender": "여자"
                }
            ]
        }
         */
 
        try {
 
            JSONParser jsonParser = new JSONParser();
             
            //JSON데이터를 넣어 JSON Object 로 만들어 준다.
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonInfo);
             
            //books의 배열을 추출
            JSONArray bookInfoArray = (JSONArray) jsonObject.get("books");
 
            System.out.println("* BOOKS *");
 
            for(int i=0; i<bookInfoArray.size(); i++){
 
                System.out.println("=BOOK_"+i+" ===========================================");
                 
                //배열 안에 있는것도 JSON형식 이기 때문에 JSON Object 로 추출
                JSONObject bookObject = (JSONObject) bookInfoArray.get(i);
                 
                //JSON name으로 추출
                System.out.println("bookInfo: name==>"+bookObject.get("name"));
                System.out.println("bookInfo: writer==>"+bookObject.get("writer"));
                System.out.println("bookInfo: price==>"+bookObject.get("price"));
                System.out.println("bookInfo: genre==>"+bookObject.get("genre"));
                System.out.println("bookInfo: publisher==>"+bookObject.get("publisher"));
 
            }
 
            JSONArray personInfoArray = (JSONArray) jsonObject.get("persons");
 
            System.out.println("\r\n* PERSONS *");
 
            for(int i=0; i<personInfoArray.size(); i++){
 
                System.out.println("=PERSON_"+i+" ===========================================");
                JSONObject personObject = (JSONObject) personInfoArray.get(i);
                System.out.println("personInfo: name==>"+personObject.get("name"));
                System.out.println("personInfo: age==>"+personObject.get("age"));
                System.out.println("personInfo: gender==>"+personObject.get("gender"));
                System.out.println("personInfo: nickname==>"+personObject.get("nickname"));
 
            }
 
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
	}

}
