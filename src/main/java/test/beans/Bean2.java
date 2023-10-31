package test.beans;

import framework.annotations.Bean;
import framework.annotations.ScopeType;

@Bean(scope = ScopeType.PROTOTYPE)
public class Bean2 {

    public Bean2() {

    }
}
