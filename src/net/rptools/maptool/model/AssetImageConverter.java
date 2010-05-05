/**
 *
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
