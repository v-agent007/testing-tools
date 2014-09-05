package co.wds.testingtools.testinjection;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by extreme on 05/09/14.
 */
public class TestInjectionUtils {

    private final static Logger LOGGER = Logger.getLogger(TestInjectionUtils.class.getName());

    public static void injectPrivateMember(Object destination, String destinationField, Object objectToInject) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = destination.getClass();
        Field fieldToSet = clazz.getDeclaredField(destinationField);
        fieldToSet.setAccessible(true);
        fieldToSet.set(destination, objectToInject);

        if(objectToInject == null){
            throw new NullPointerException("The object to inject is into the destination is null");
        }
    }
}

