package test.controllers;

import framework.annotations.*;
import framework.request.Request;
import framework.response.JsonResponse;
import framework.response.Response;
import test.beans.Bean1;
import test.components.Component1;
import test.qualifiers.Interface1;
import test.qualifiers.Interface2;
import test.services.Service1;

import java.util.HashMap;
import java.util.Map;

@Controller
public class Controller1 {

    private static final String controllerName = "Controller-1";

    @Autowired(verbose = true)
    @Qualifier(value = "IMPL-INTERFACE-1")
    private Interface1 interface1;

    @Autowired(verbose = true)
    @Qualifier(value = "IMPL-INTERFACE-2")
    private Interface2 interface2;

    @Autowired(verbose = true)
    private Bean1 bean1;

    @Autowired(verbose = true)
    private Service1 service1;

    @Autowired(verbose = true)
    private Component1 component1;


    public Controller1() {

    }

    @GET
    @Path(path = "/controller1/endpoint1")
    public Response endpoint1(Request request) {
        System.out.println(controllerName + ": GET /controller1");

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("route_location", request.getLocation());
        responseMap.put("route_method", request.getMethod().toString());
        responseMap.put("parameters", request.getParameters());
        responseMap.put("controller_name", controllerName);
        responseMap.put("method_name", "endpoint1");

        return new JsonResponse(responseMap);
    }

    @GET
    @Path(path = "/controller1/endpoint2")
    public void endpoint2() {
        System.out.println(controllerName + ": GET /controller1/method1");
    }

    @POST
    @Path(path = "/controller1/endpoint3")
    public Response endpoint3(Request request) {
        System.out.println(controllerName + ": POST /controller1/method2");

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("route_location", request.getLocation());
        responseMap.put("route_method", request.getMethod().toString());
        responseMap.put("parameters", request.getParameters());
        responseMap.put("controller_name", controllerName);
        responseMap.put("method_name", "endpoint3");

        return new JsonResponse(responseMap);
    }
}
