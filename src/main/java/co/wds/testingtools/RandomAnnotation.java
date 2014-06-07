package co.wds.testingtools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomAnnotation {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface Randomise {

	}
	
	private static final Random randomiser = new Random();
	
	public static void randomiseFields(Object testClass) throws IllegalArgumentException, IllegalAccessException {
		Class<? extends Object> theClass = testClass.getClass();

		Set<Field> fieldsToBeRandomised = extractFieldsToBeRandomised(theClass);
	
		randomise(testClass, fieldsToBeRandomised);
	}

	private static Set<Field> extractFieldsToBeRandomised(Class<? extends Object> theClass) {
		Set<Field> fieldsToBeRandomised = new HashSet<Field>();
		Field[] fields = theClass.getDeclaredFields();
		for (Field field : fields ) {
			if (null != field.getAnnotation(Randomise.class)) {
				fieldsToBeRandomised.add(field);
			}
		}
		
		return fieldsToBeRandomised;
	}

	private static void randomise(Object testClass, Set<Field> fieldsToBeRandomised) throws IllegalArgumentException, IllegalAccessException {
		for (Field field: fieldsToBeRandomised) {
			field.set(testClass, randomise(String.class));
		}
	}
	
	public static <S> S randomise(Class<S> classToRandomise) {
		return (S) String.format("%h",  randomiser.nextLong());
	}
}
