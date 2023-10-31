package test.qualifiers.impl;

import framework.annotations.Bean;
import framework.annotations.Qualifier;
import test.qualifiers.Interface2;

@Qualifier("IMPL-INTERFACE-2")
@Bean
public class ImplInterface2 implements Interface2 {

    public ImplInterface2() {

    }
}
