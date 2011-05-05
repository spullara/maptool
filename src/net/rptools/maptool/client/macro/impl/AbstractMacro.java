/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;

public abstract class AbstractMacro implements Macro {


	protected String processText(String incoming) {
		return "\002" + MapTool.getFrame().getCommandPanel().getChatProcessor().process(incoming) + "\003";
	}
	

	
//	public static void main(String[] args) {
//		new AbstractMacro(){
//			public void execute(String macro) {
//
//				System.out.println(getWords(macro));
//			}
//		}.execute("one \"two three\" \"four five\"");
//	}
}
