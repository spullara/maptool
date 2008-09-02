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
package net.rptools.maptool.model;


/**
 * @author drice
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TestModelSerialization {
//
//    private static final Zone generateZone() {
//        Zone z = new Zone("FOOBAR".getBytes());
//        z.setGridScale(107);
//        
//        return z;
//    }
//
//    public static void main(String[] args) throws IOException {
//        ByteArrayOutputStream bout = new ByteArrayOutputStream();
//        HessianOutput hout = new HessianOutput(bout);
//
//        try {
//            hout.call("test", new Object[] { generateZone() });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        byte[] data = bout.toByteArray();
//
//        HessianInput in = new HessianInput(new ByteArrayInputStream(data));
//        in.startCall();
//        List<Object> arguments = new ArrayList<Object>();
//        while (!in.isEnd()) {
//            arguments.add(in.readObject());
//        }
//        in.completeCall();
//        
//        Zone z = (Zone) arguments.get(0);
//        
//        System.out.println("background: " + new String(z.getBackground()));
//        
//        System.out.println(z.getGridScale());
//
//    }
}
