package test.qualifiers.impl;

import framework.annotations.Bean;
import framework.annotations.Qualifier;
import test.qualifiers.Interface1;

@Qualifier("IMPL-INTERFACE-1")
@Bean
public class ImplInterface1 implements Interface1 {

    public ImplInterface1() {

    }
}
