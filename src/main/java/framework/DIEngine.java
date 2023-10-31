package framework;

import framework.annotations.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;

public class DIEngine {
    private static DIEngine instance;
    private static String PATH_TO_USER_CODE = "";
    private final Set<Class<?>> controllers = new HashSet<>();
    private final Set<Class<?>> qualifiers = new HashSet<>();
    private final Map<Class<?>, Object> beans = new HashMap<>();

    private DIEngine(){
        PATH_TO_USER_CODE = getPathToUserCode();
    }

    public static DIEngine getInstance() {
        if(instance == null)
            instance = new DIEngine();

        return instance;
    }

    public void engineStart() {
        loadControllersAndQualifiers();
        initQualifiers();
        initControllers();
    }

    public void loadControllersAndQualifiers() {
        File classesDir = new File(PATH_TO_USER_CODE);
        loadControllersAndQualifiers(Objects.requireNonNull(classesDir.listFiles()));
    }

    private void loadControllersAndQualifiers(File[] files) {
        Arrays.stream(files)
                .filter(File::isDirectory)
                .forEach(directory -> loadControllersAndQualifiers(Objects.requireNonNull(directory.listFiles())));

        Arrays.stream(files)
                .filter(file -> !file.isDirectory())
                .map(this::getClassPath)
                .map(this::getClass)
                .forEach(this::processClass);
    }

    private String getClassPath(File file) {
        return file.getAbsolutePath().replace(PATH_TO_USER_CODE, "").replaceAll("\\\\", ".").replace(".java", "");
    }

    private Class<?> getClass(String classPath) {
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            // Handle the exception or log it, as needed.
            throw new RuntimeException("Error loading class: " + classPath, e);
        }
    }

    private void processClass(Class<?> cl) {
        if (cl.isAnnotationPresent(Controller.class)) {
            controllers.add(cl);
        } else if (cl.isAnnotationPresent(Qualifier.class)) {
            qualifiers.add(cl);
        }
    }

    private void initQualifiers(){
        DependencyContainer.getInstance().initQualifiers(qualifiers);
    }

    private void initControllers(){
        initControllersFields();
        initControllersEndpoints();
    }

    private void initControllersFields() {
        Object controllerInstance;
        for(Class<?> controllerClass: controllers){
            try {
                controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                initClassFields(controllerInstance, controllerClass.getDeclaredFields());

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initClassFields(Object fieldsOwner, Field[] fields) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldClassType = field.getType();
            if (field.isAnnotationPresent(Autowired.class)) {
                Object fieldInstance = getBeanInstance(field, fieldClassType);
                field.set(fieldsOwner, fieldInstance);
                logInitialization(field, fieldsOwner, fieldInstance, fieldClassType);
            }
        }
    }

    private Object getBeanInstance(Field field, Class<?> fieldClassType) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (fieldClassType.isInterface()) {
            if (!field.isAnnotationPresent(Qualifier.class)) {
                throw new RuntimeException("Field: " + field.getName() + ", in class: " + fieldClassType.getName() + ", is an interface and must have @Qualifier annotation!");
            }

            String qualifierValue = field.getAnnotation(Qualifier.class).value();
            Class<?> classByQualifier = DependencyContainer.getInstance().getClassByQualifier(qualifierValue);
            return getBeanInstance(field, classByQualifier);
        }

        if (fieldClassType.isAnnotationPresent(Bean.class)) {
            ScopeType scopeType = fieldClassType.getAnnotation(Bean.class).scope();
            return getBeanInstanceWithScope(field, fieldClassType, scopeType);
        }

        if (fieldClassType.isAnnotationPresent(Service.class)) {
            return getBeanInstanceWithScope(field, fieldClassType, ScopeType.SINGLETON);
        }

        if (fieldClassType.isAnnotationPresent(Component.class)) {
            return fieldClassType.getDeclaredConstructor().newInstance();
        }

        System.out.println("######" + field.getName());

        throw new RuntimeException("Class must have one of annotations [@Component, @Bean, @Service, @Qualifier]!");
    }

    private Object getBeanInstanceWithScope(Field field, Class<?> fieldClassType, ScopeType scopeType) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (scopeType == ScopeType.SINGLETON) {
            Object fieldInstance = beans.get(fieldClassType);
            if (fieldInstance == null) {
                fieldInstance = fieldClassType.getDeclaredConstructor().newInstance();
                beans.put(fieldClassType, fieldInstance);
            }
            return fieldInstance;
        } else {
            return fieldClassType.getDeclaredConstructor().newInstance();
        }
    }

    private void logInitialization(Field field, Object fieldsOwner, Object fieldInstance, Class<?> fieldClassType) {
        boolean verbose = field.getAnnotation(Autowired.class).verbose();
        if (verbose) {
            System.out.println("Initialized <" + fieldClassType.getName() + "> <" + field.getName() + "> in <" + fieldsOwner.getClass().getName() + "> on <" + LocalDateTime.now() + "> with <" + fieldInstance.hashCode() + ">");
        }
    }

    private void initControllersEndpoints() {
        EndpointsController.getInstance().initControllersEndpoints(controllers);
    }

    private String getPathToUserCode(){
        return System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
    }

}
