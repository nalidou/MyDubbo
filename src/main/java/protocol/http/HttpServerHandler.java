package protocol.http;

import framework.Invocation;
import org.apache.commons.io.IOUtils;
import provider.LocalMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

public class HttpServerHandler {

    public void hander(HttpServletRequest request, HttpServletResponse response){

        try {
            InputStream inputStream = request.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Invocation invocation = (Invocation) objectInputStream.readObject();

            Class classImpl = LocalMap.get(invocation.getInterfaceName());
            Method method = classImpl.getMethod(invocation.getMethodName(), invocation.getParamTypes());

            //得到方法执行结果
            Object result = method.invoke(classImpl.newInstance(), invocation.getParams());

            //结果写回到response
            IOUtils.write((String) result, response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
