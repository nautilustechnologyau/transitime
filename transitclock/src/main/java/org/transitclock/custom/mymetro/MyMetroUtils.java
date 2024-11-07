package org.transitclock.custom.mymetro;

import io.swagger.client.model.Device;
import io.swagger.client.model.Position;

import java.util.List;
import java.util.Map;

public class MyMetroUtils {
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

    public static String stripAgencyId(Object id) {
        if (id != null) {
            return stripAgencyId(id.toString());
        } else {
            return null;
        }
    }


    public static Device findDeviceById(List<Device> devices, Integer id) {
        for (Device device : devices) {
            if (device.getId().equals(id))
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
