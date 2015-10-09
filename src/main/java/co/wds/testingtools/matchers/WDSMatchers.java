package co.wds.testingtools.matchers;

import java.util.List;

import org.hamcrest.Matcher;

public class WDSMatchers {
	private WDSMatchers() {
		
	}
	
	public static Matcher<List<? extends Object>> emptyList() {
		return new EmptyListMatcher();
	}
}
