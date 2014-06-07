package co.wds.testingtools;

import static co.wds.testingtools.RandomAnnotation.randomise;
import static co.wds.testingtools.RandomAnnotation.randomiseFields;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import co.wds.testingtools.RandomAnnotation.Randomise;

public class RandomAnnotationTest {
	@Randomise String randomString_1;
	@Randomise String randomString_2;
	
	@Before
	public void setup() throws Exception {
		randomiseFields(this);
	}
	
	@Test
	public void shouldGiveMeANonNullString() throws Exception {
		assertThat(randomString_1, is(not(nullValue())));
		assertThat(randomString_2, is(not(nullValue())));
	}
	
	@Test
	public void shouldGiveMeDifferentValues() throws Exception {
		assertThat(randomString_1, is(not(randomString_2)));
	}
	
	@Test
	public void shouldGiveMeLotsOfDifferentRandomValues() throws Exception {
		Set<String> randomValues = new HashSet<String>();
		
		for (int i = 0; i < 1000; i++) {
			String s = randomise(String.class);
			randomValues.add(s);
		}
		
		assertThat(randomValues.size(), is(1000));
	}
}
