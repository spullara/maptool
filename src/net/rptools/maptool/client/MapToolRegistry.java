package net.rptools.maptool.client;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;

public class MapToolRegistry {

	private static final String SERVICE_URL = "http://rptools.net/services/maptool_registry.php";

	private static MapToolRegistryService service;
	
	static {
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			service = (MapToolRegistryService) factory.create(MapToolRegistryService.class, SERVICE_URL);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		}
	}
	
    public static String findInstance(String id, String password) {
        checkService();
        return service.findInstance(id, password);
    }

    public static String getAddress() {
    	checkService();
    	return service.getAddress();
    }
    
    public static int registerInstance(String id, int port, String password) {
		checkService();
		return service.registerInstance(id, port, password);
	}
	
	public static void unregisterInstance(int port) {
		checkService();
		service.unregisterInstance(port);
	}
	
	public static boolean testConnection(int port) {
		checkService();
		return service.testConnection(port);
	}
	
	public static void heartBeat(int port) {
		checkService();
		service.heartBeat(port);
	}

	private static void checkService() {
		if (service == null) {
			throw new RuntimeException("Service is not available");
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		long delay = 0;
		
		Thread.sleep(delay);
		System.out.println ("Register");
		registerInstance("my test", 4444, null);
		
		Thread.sleep(delay);
		System.out.println ("Heartbeat");

		heartBeat(4444);
		
		Thread.sleep(delay);
		System.out.println ("Find: " + findInstance("my test", null));

		Thread.sleep(delay);
        System.out.println ("RERegister");
        registerInstance("my test", 4444, "my password");
        
        Thread.sleep(delay);
        System.out.println ("Find: " + findInstance("my test", null));
		
        Thread.sleep(delay);
        System.out.println ("Find: " + findInstance("my test", "my password"));

        Thread.sleep(delay);
		System.out.println ("UnRegister");
		
		unregisterInstance(4444);
	}
}
