package test.components;

import framework.annotations.Autowired;
import framework.annotations.Component;

@Component
public class Component1 {

    @Autowired(verbose = true)
    private Component2 component2;

    public Component1() {

    }
}
