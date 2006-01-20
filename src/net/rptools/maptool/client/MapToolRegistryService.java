package net.rptools.maptool.client;

public interface MapToolRegistryService {

	public static final int CODE_UNKNOWN = 0;
	public static final int CODE_OK = 1;
	public static final int CODE_COULD_CONNECT_BACK = 2;

	public int registerInstance(String id, int port, String password);

	public void unregisterInstance(int port);
	
	public boolean testConnection(int port);
	
	public void heartBeat(int port);
}
