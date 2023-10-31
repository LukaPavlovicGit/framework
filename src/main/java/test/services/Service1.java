package test.services;

import framework.annotations.Autowired;
import framework.annotations.Service;

@Service
public class Service1 {

    @Autowired(verbose = true)
    private Service2 service2;

    public Service1() {

    }
}
