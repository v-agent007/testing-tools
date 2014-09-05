package co.wds.testingtools.testinjection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by extreme on 05/09/14.
 */
public class TestInjectionUtilsTest {

    FakeController controller;
    FakeDao mockDao;

    @Before
    public void setUp(){
        mockDao = Mockito.mock(FakeDao.class);
    }

    @Test
    public void iCanInjectAPrivateMemberInAClassWithAnObject() throws NoSuchFieldException, IllegalAccessException {
        controller = new FakeController();
        Assert.assertTrue(controller.getMockDao() == null);
        TestInjectionUtils.injectPrivateMember(controller, "mockDao", mockDao);
        Assert.assertTrue(controller.getMockDao() != null);
        Assert.assertTrue(controller.getMockDao() instanceof FakeDao);
    }

    @Test(expected = NoSuchFieldException.class)
    public void handleAnExceptionGracefullyWithWrongFieldName() throws NoSuchFieldException, IllegalAccessException {
        controller = new FakeController();
        Assert.assertTrue(controller.getMockDao() == null);
        TestInjectionUtils.injectPrivateMember(controller, "wrongFieldName", mockDao);
        Assert.assertTrue(controller.getMockDao() == null);
    }

    @Test(expected = NullPointerException.class)
    public void handleAnExceptionGracefullyWithNullDestinationObject() throws NoSuchFieldException, IllegalAccessException {
        controller = new FakeController();
        Assert.assertTrue(controller.getMockDao() == null);
        TestInjectionUtils.injectPrivateMember(null, "mockDao", mockDao);
        Assert.assertTrue(controller.getMockDao() == null);
    }

    @Test(expected = NullPointerException.class)
    public void handleAnExceptionGracefullyWithNullInjectObject() throws NoSuchFieldException, IllegalAccessException {
        controller = new FakeController();
        Assert.assertTrue(controller.getMockDao() == null);
        TestInjectionUtils.injectPrivateMember(controller, "mockDao", null);
        Assert.assertTrue(controller.getMockDao() == null);
    }

    @Test
    public void iCanInjectAPrivateMemberWithAMockObjectAndThenPerformMockitoOperationsOnIt() throws NoSuchFieldException, IllegalAccessException {
        controller = new FakeController();
        Assert.assertTrue(controller.getMockDao() == null);
        TestInjectionUtils.injectPrivateMember(controller, "mockDao", mockDao);
        Assert.assertTrue(controller.getMockDao() != null);
        Assert.assertTrue(controller.getMockDao() instanceof FakeDao);

        controller.doSomthingInAController();

        Mockito.verify(mockDao).doSomething();
    }

    private class FakeDao {

        private String doSomething() {
            return "I am a string";
        }
    }

    private class FakeController {
        private FakeDao mockDao;

        private void doSomthingInAController(){
            mockDao.doSomething();
        }

        public FakeDao getMockDao() {
            return mockDao;
        }
    }
}
