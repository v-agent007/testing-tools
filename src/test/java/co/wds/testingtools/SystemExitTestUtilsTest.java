package co.wds.testingtools;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static co.wds.testingtools.SystemExitTestUtils.disableSystemExit;
import static co.wds.testingtools.SystemExitTestUtils.enableSystemExit;
import static co.wds.testingtools.annotations.RandomAnnotation.randomise;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.wds.testingtools.SystemExitTestUtils.SystemExitException;

public class SystemExitTestUtilsTest {
	@Before
	public void setup() {
		disableSystemExit();
	}
	
	@After
	public void teardown() {
		enableSystemExit();
	}
	
	@Test
	public void shouldNotSystemExitAndThrowAnExceptionInstead() throws Exception {
		SystemExitException expected = null;
		
		int status = randomise(int.class);
		
		try {
			System.exit(status);
		} catch (SystemExitException see) {
			expected = see;
		}
		
		assertThat(expected, is(not(nullValue())));
		assertThat(expected.getStatus(), is(status));
	}
}
