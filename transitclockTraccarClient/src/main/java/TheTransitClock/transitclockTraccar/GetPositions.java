package TheTransitClock.transitclockTraccar;

import java.util.List;
import java.util.Map;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.Device;
import io.swagger.client.model.Position;
import io.swagger.client.model.User;


/**
 * @author Sean Óg Crudden
 * Test App that reads all device positions from a traccar server. 
 * Uses classes generated from swagger file provided with traccar.
 */
public class GetPositions 
{
	private static String email="";
	private static String password="";
	private static String baseUrl="https://kerala-traccar.mymetro.au/api";
	
    public static void main( String[] args )
    {        
        DefaultApi api=new DefaultApi();
        ApiClient client=new ApiClient();
        client.setBasePath(baseUrl);
        client.setUsername(email);
        client.setPassword(password);        
        api.setApiClient(client);                               
        try {
			User user = api.sessionPost(email, password);

			long startTime = System.currentTimeMillis();
			List<Device> devices = api.devicesGet(true, user.getId(), null, null);
			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime) / 1000;
			System.out.println("Device list retrieved in: " + duration + " seconds");


			startTime = System.currentTimeMillis();
			List<Position> results = api.positionsGet(null, null, null, null);
			endTime = System.currentTimeMillis();
			duration = (endTime - startTime) / 1000;
			System.out.println("Device position retrieved in: " + duration + " seconds");
			
			for(Position result:results)
			{
				// System.out.println(result);
				Object tripId = stripAgencyId(findAttribute(result, "tripId"));
				System.out.println("Trip ID:" + tripId);
				Object routeId = stripAgencyId(findAttribute(result, "routeId"));
				System.out.println("Route ID:" + routeId);
				Object blockId = stripAgencyId(findAttribute(result, "blockId"));
				System.out.println("Block ID:" + blockId);
			}
			
		} catch (ApiException e) {
			
			e.printStackTrace();
		}
    }

	public static String stripAgencyId(Object id) {
		if (id != null) {
			return stripAgencyId(id.toString());
		} else {
			return null;
		}
	}
	public static String stripAgencyId(String id) {
		if (id == null || id.isBlank()) {
			return id;
		}

		int index = id.indexOf('_');
		if (index != -1 && index < (id.length() - 1)) {
			return id.substring(index + 1);
		} else {
			return id;
		}
	}


	public static Device findDeviceById(List<Device> devices, Integer id) {
		for (Device device : devices) {
			if (device.getId() == id)
				return device;
		}
		return null;
	}

	public static Object findAttribute(Position position, String attrName) {
		if (position == null || position.getAttributes() == null) {
			return null;
		}

		Object attrsObj = position.getAttributes();
		if (attrsObj instanceof Map) {
			return ((Map<?, ?>) attrsObj).get(attrName);
		}

		return null;
	}
}
