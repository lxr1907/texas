package yuelj;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import yuelj.utils.logs.SystemLog;
import yuelj.utils.serialize.JsonUtils;

public class JavaScriptTest {
	public static void main(String[] args) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");

		try {
			engine.eval(new FileReader("src/test/java/yuelj/javascriptTest.js"));
			int a = 10;
			int b = 20;
			Map<String, Integer> map = new HashMap<>();
			map.put("a", 10);
			map.put("b", 10);
			JsonUtils.toJson(map, map.getClass());
			Object result = engine
					.eval("javascriptTest.aPlusB(" + JsonUtils.toJson(map, map.getClass()) + ").toString();");

			SystemLog.printlog(result);
		} catch (FileNotFoundException | ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
