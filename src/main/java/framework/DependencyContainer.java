package framework;

import framework.annotations.Qualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DependencyContainer {
    private static DependencyContainer instance;

    private Map<String, Class<?>> resolvedMapForQualifiers = new HashMap<>();

    public static DependencyContainer getInstance() {
        if (instance == null)
            instance = new DependencyContainer();
        return instance;
    }

    protected void initQualifiers(Set<Class<?>> qualifiers){
        String key;
        for(Class<?> cl: qualifiers){
            key = cl.getAnnotation(Qualifier.class).value();
            if(resolvedMapForQualifiers.get(key) != null){
                throw new RuntimeException("Mapping qualifier issue. Ambiguous mapping !");
            }
            resolvedMapForQualifiers.put(key, cl);
        }
    }

    public Class<?> getClassByQualifier(String qualifier){
        return resolvedMapForQualifiers.get(qualifier);
    }
}
