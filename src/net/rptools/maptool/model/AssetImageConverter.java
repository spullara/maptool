/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.model;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AssetImageConverter extends EncodedByteArrayConverter {
	    @Override
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
	    		// Ignore the image when creating 1.3.b65+ campaigns with assets...
//	    		System.out.println("Would be writing an image now...");
	    }
/*
		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
	    		// But be sure to read them in if they exist.
	    		System.out.println("Unmarshalling an old asset image now...");
	    		return super.unmarshal(reader, context);
	    }
*/
	    @Override
		public boolean canConvert(Class type) {
//	    		System.out.println("Checking for valid conversion of " + type.toString());
	    		return true;		// Tell XStream that we can convert the image so it uses our methods
	    }
}
