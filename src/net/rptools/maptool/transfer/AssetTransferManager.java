package net.rptools.maptool.transfer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class AssetTransferManager {

	private Map<Serializable, AssetConsumer> consumerMap = new HashMap<Serializable, AssetConsumer>();
	private List<ConsumerListener> consumerListenerList = new CopyOnWriteArrayList<ConsumerListener>();
	
	private List<AssetProducer> producerList = new LinkedList<AssetProducer>();
	
	/**
	 * Add a new producer to the chunk queue.  Assumes that the header has already been transferred
	 * to the consumer.  Producer chunks can then be retrieved via nextChunk()
	 */
	public synchronized void addProducer(AssetProducer producer) {
		producerList.add(producer);
	}

	/**
	 * Get the next chunk from the available producers
	 * @param size size of the data to retrieve
	 * @throws IOException
	 */
	public synchronized AssetChunk nextChunk(int size) throws IOException {
		
		if (producerList.size() == 0) {
			return null;
		}
		
		AssetProducer producer = producerList.remove(0);
		
		AssetChunk chunk = producer.nextChunk(size);
		
		if (!producer.isComplete()) {
			producerList.add(producer);
		}
		
		return chunk;
	}
	
	/**
	 * Add the corresponding consumer that is expecting to receive chunks.
	 * Add a ConsumerListener to know when the asset is complete
	 */
	public synchronized void addConsumer(AssetConsumer consumer) {
		if (consumerMap.get(consumer.getId()) != null) {
			throw new IllegalArgumentException("Asset is already being downloaded: " + consumer.getId());
		}
		
		consumerMap.put(consumer.getId(), consumer);
	}

	/**
	 * Update the appropriate asset.  To be notified when the asset is complete add a ConsumerListener.
	 * When the asset is complete it will be removed from the internal map automatically
	 * @throws IOException
	 */
	public synchronized void update(AssetChunk chunk) throws IOException {
		
		AssetConsumer consumer = consumerMap.get(chunk.getId());
		if (consumer == null) {
			throw new IllegalArgumentException("Not expecting chunk: " + chunk.getId());
		}
		
		consumer.update(chunk);
		if (consumer.isComplete()) {
			for (ConsumerListener listener : consumerListenerList) {
				listener.assetComplete(consumer.getId(), consumer.getFilename());
			}
			consumerMap.remove(consumer.getId());
		}
	}
	
	/**
	 * Get a list of current asset consumers, this is a good way to know what's going on in the system
	 */
	public synchronized List<AssetConsumer> getAssetConsumers() {
		List<AssetConsumer> consumerList = new ArrayList<AssetConsumer>();
		consumerList.addAll(consumerMap.values());
		
		return consumerList;
	}
	
	public void addConsumerListener(ConsumerListener listener) {
		consumerListenerList.add(listener);
	}

	public void removeConsumerListener(ConsumerListener listener) {
		consumerListenerList.remove(listener);
	}
	
}
