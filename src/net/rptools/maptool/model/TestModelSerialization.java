/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
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
