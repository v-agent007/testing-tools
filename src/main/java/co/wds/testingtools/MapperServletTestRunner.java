package co.wds.testingtools;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import co.wds.testingtools.annotations.MapperServlet.ResponseData;

import static co.wds.testingtools.annotations.MapperServlet.startMapperServlet;
import static co.wds.testingtools.annotations.MapperServlet.stopMapperServlet;
import static co.wds.testingtools.annotations.MapperServlet.addNewMapping;

public class MapperServletTestRunner extends BlockJUnit4ClassRunner {
	private Class<?> klass;

	public MapperServletTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
		this.klass = klass;
	}

	@Override
	protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
		startMapperServlet(klass);
		ResponseData data = method.getAnnotation(ResponseData.class); 
		if (data != null) {
			addNewMapping(data);
		}
		
		super.runChild(method, notifier);
		
		stopMapperServlet();
	}

}
