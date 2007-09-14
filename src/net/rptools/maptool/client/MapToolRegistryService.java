package net.rptools.maptool.client;

import java.util.List;

public interface MapToolRegistryService {

	public static final int CODE_UNKNOWN = 0;
	public static final int CODE_OK = 1;
	public static final int CODE_COULD_CONNECT_BACK = 2;
    public static final int CODE_ID_IN_USE = 3;

	public int registerInstance(String id, int port, String password);
	public void unregisterInstance(int port);

    public String findInstance(String id, String deprecated);
    public List<String> findAllInstances();
    
	public boolean testConnection(int port);
	
	public void heartBeat(int port);
	
	public String getAddress();
}
