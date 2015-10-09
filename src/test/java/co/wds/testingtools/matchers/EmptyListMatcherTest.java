package co.wds.testingtools.matchers;

import static co.wds.testingtools.matchers.WDSMatchers.emptyList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class EmptyListMatcherTest {
	@Test
	public void emptyListShouldBeEmpty() throws Exception {
		assertThat(Collections.emptyList(), is(emptyList()));
	}

	@Test
	public void singleListShouldNotBeEmpty() throws Exception {
		assertThat(Collections.singletonList("one"), is(not(emptyList())));
	}
	
	@Test
	public void listWithItemsShouldNotBeEmpty() throws Exception {
		assertThat(Arrays.asList("one", "two", "three"), is(not(emptyList())));
	}
}
