/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.script.api.TokenApi;
import net.sf.json.JSONObject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;

public class ScriptManager {
	private static final String[] JAVASCRIPT_FILES = {
			"net/rptools/maptool/client/script/js/rptools.js",
			"net/rptools/maptool/client/script/js/rptools.test.js"
	};

	static boolean useDynamicSCope = false;
	static {
		ContextFactory.initGlobal(new MapToolContextFactory());
	}

	// private Context jsContext;
	private static ScriptableObject jsScope;

	public static synchronized void init() throws IOException {
		if (jsScope != null)
			return;
		try {
			Context jsContext = ContextFactory.getGlobal().enterContext();
			jsContext.setClassShutter(new SecurityClassShutter());

			jsScope = jsContext.initStandardObjects(null, true);

			Object o = Context.javaToJS(new TokenApi(), jsScope);
			ScriptableObject.putProperty(jsScope, "rptools_global_tokens", o);

			for (String script : JAVASCRIPT_FILES) {
				Reader reader = new InputStreamReader(ScriptManager.class.getClassLoader().getResourceAsStream(script));
				Script compiled = jsContext.compileReader(reader, script, 1, null);
				compiled.exec(jsContext, jsScope);
			}
			// jsScope.sealObject();
		} finally {
			Context.exit();
		}
	}

	public static Object evaluate(Map<String, Object> globals, String script) throws IOException {
		init();
		try {
			Context jsContext = ContextFactory.getGlobal().enterContext();
			jsContext.setClassShutter(new SecurityClassShutter());
			jsContext.setWrapFactory(new PrimitiveWrapFactory());

			Scriptable instanceScope = jsContext.newObject(jsScope);
			instanceScope.setPrototype(jsScope);
			instanceScope.setParentScope(null);

			if (globals != null) {
				for (Map.Entry<String, Object> entry : globals.entrySet()) {
					Object wrappedObject = Context.javaToJS(entry.getValue(), instanceScope);
					ScriptableObject.putProperty(instanceScope, entry.getKey(), wrappedObject);
				}
			}
			Object o = jsContext.evaluateString(instanceScope, script, "evaluate", 1, null);
			return o;
		} finally {
			Context.exit();
		}
	}

	private static Pattern registerPattern = Pattern.compile("^\\/\\/\\s*@register\\s+(.*)", Pattern.CASE_INSENSITIVE);

	public static void registerFunctions(Reader reader) throws IOException {
		BufferedReader r = new BufferedReader(reader);
		String line;
		while ((line = r.readLine()) != null) {
			Matcher m = registerPattern.matcher(line);
			if (m.matches()) {
				JSONObject o = JSONObject.fromObject(m.group(1));
//                System.out.println(m.group(1));
//                System.out.println(o);
			}
		}
	}

//	public Object evaluate(Script script) throws IOException {
//		return this.evaluate(Collections.emptyMap(), script);
//	}

	private static class MapToolContextFactory extends ContextFactory {
		@Override
		protected void observeInstructionCount(Context cx, int instructionCount) {
//			if (System.currentTimeMillis() - lastCancelPromptTime > RUNNING_TOO_LONG_MS) {
//				int opt = JOptionPane.showConfirmDialog(MapTool.getFrame(),
//						"The macro has been running too long\n would you like to cancel?", "Macro Running too long",
//						JOptionPane.YES_NO_OPTION);
//				lastCancelPromptTime = System.currentTimeMillis();
//				if (opt == 0) {
//					throw new Error("Script running too long.");
//				}
//			}
		}

		@Override
		protected boolean hasFeature(Context cx, int featureIndex) {
			if (featureIndex == Context.FEATURE_DYNAMIC_SCOPE)
				return useDynamicSCope;
			return super.hasFeature(cx, featureIndex);
		}
	}

	private static class PrimitiveWrapFactory extends WrapFactory {
		@SuppressWarnings("rawtypes")
		@Override
		public Object wrap(Context cx, Scriptable scope, Object obj, Class staticType) {
			if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
				return obj;
			} else if (obj instanceof Character) {
				char[] a = { ((Character) obj).charValue() };
				return new String(a);
			}
			return super.wrap(cx, scope, obj, staticType);
		}
	}
}
