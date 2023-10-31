package test.components;

import framework.annotations.Autowired;
import framework.annotations.Component;
import test.beans.Bean2;

@Component
public class Component2 {

    @Autowired(verbose = true)
    private Bean2 bean2;

    public Component2() {

    }

}
