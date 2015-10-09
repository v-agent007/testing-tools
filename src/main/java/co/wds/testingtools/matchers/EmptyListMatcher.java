package co.wds.testingtools.matchers;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class EmptyListMatcher extends BaseMatcher<List<? extends Object>> {
	@Override
	public boolean matches(Object match) {
		@SuppressWarnings("unchecked")
		List<? extends Object> list = (List<? extends Object>)match; 
		
		return list.isEmpty();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("empty list");
	}
}
