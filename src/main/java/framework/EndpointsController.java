package framework;

import framework.annotations.GET;
import framework.annotations.POST;
import framework.annotations.Path;
import framework.request.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EndpointsController {

    private static EndpointsController instance;
    private final Map<String, Endpoint> endpoints = new HashMap<>();


    private EndpointsController(){  }

    public static EndpointsController getInstance(){
        if(instance == null)
            instance = new EndpointsController();

        return instance;
    }

    public Object invokeEndpoint(String endpointPath, Object... objects){
        return endpoints.containsKey(endpointPath) ? endpoints.get(endpointPath).invoke(objects) : null;
    }

    public void initControllersEndpoints(Set<Class<?>> clControllers) {
        Object controllerInstance;
        for(Class<?> cl: clControllers){
            try {
                controllerInstance = cl.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            initEndpointsForController(controllerInstance, cl.getDeclaredMethods());
        }
    }

    private void initEndpointsForController(Object controllerInstance, Method[] methods) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(Path.class)) {
                String httpMethod = getHttpMethod(method);
                String route = httpMethod + method.getAnnotation(Path.class).path();

                if (endpoints.containsKey(route)) {
                    throw new RuntimeException("Endpoint already exists: " + route);
                }

                Endpoint endpoint = new Endpoint(controllerInstance, method);
                endpoints.put(route, endpoint);
            }
        }
    }

    private String getHttpMethod(Method method) {
        if (method.isAnnotationPresent(GET.class)) {
            return "GET ";
        } else if (method.isAnnotationPresent(POST.class)) {
            return "POST ";
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method for method: " + method.getName());
        }
    }




    static class Endpoint {
        private final Object controller;
        private final Method method;

        public Endpoint(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public Object invoke(Object... objects) {
            try {
                return objects.length == 0 ? method.invoke(controller) : method.invoke(controller, objects);
                
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
