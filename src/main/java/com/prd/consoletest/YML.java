package com.prd.consoletest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.prd.Environment;

import java.io.IOException;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.JSONObject;

public class YML {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String selectedZone = "DEV";
		Environment environment = null;
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		ClassLoader classLoader = YML.class.getClassLoader();//getClass().getClassLoader();
		environment = mapper.readValue(classLoader.getResource("Environment" + selectedZone + ".yml"), Environment.class);

		System.out.println(ReflectionToStringBuilder.toString(environment, ToStringStyle.MULTI_LINE_STYLE));
	}

}
