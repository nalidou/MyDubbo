package provider.impl;

import provider.api.MyService;

public class MyServiceImpl implements MyService {
    public String getName(String id) {
        System.out.println("call getName id = " + id);
        return "getName " + id + " " + System.currentTimeMillis();
    }
}
