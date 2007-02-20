package net.rptools.maptool.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class AssetTransferManagerTest extends TestCase {

	public void testBasicTransfer() throws Exception {
		
		byte[] data = new byte[1024];
		for (int i = 0; i < 1024; i++) {
			data[i] = (byte)i;
		}
		
		File tmpFile = createTempFile(data);
		
		// PRODUCER
		AssetProducer producer = new AssetProducer("Testing", tmpFile);
		AssetHeader header = producer.getHeader();
		
		assertNotNull(header);
		assertEquals(data.length, header.getSize());
		assertFalse(producer.isComplete());
		
		// CONSUMER
		AssetConsumer consumer = new AssetConsumer(new File("."), header);

		assertFalse(consumer.isComplete());
		
		// TEST
		while (!producer.isComplete()) {
			AssetChunk chunk = producer.nextChunk(10);
			
			consumer.update(chunk);
		}
		
		// CHECK
		assertTrue(consumer.isComplete());
		assertTrue(consumer.getFilename().exists());
		assertEquals(header.getSize(), consumer.getFilename().length());

		int count = 0;
		int val;
		FileInputStream in = new FileInputStream(consumer.getFilename());
		while ((val = in.read()) != -1) {
			assertEquals(data[count], (byte)val);
			count ++;
		}
		in.close();
		assertEquals(data.length, count);
		
		// CLEANUP
		tmpFile.delete();
		consumer.getFilename().delete();
		
	}
	
	private File createTempFile(byte[] data) throws IOException {
		
		File file = new File("tmp.dat");
		FileOutputStream out = new FileOutputStream(file);
		
		out.write(data);
		
		out.close();
		
		return file;
	}
}
