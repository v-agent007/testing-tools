testing-tools
=============

A suite of useful Java testing tools, including useful annotations for randomising fields and testing code against web services, as well as useful utility classes for injecting fields and 

# Annotations

## Randomiser

To use the randomiser annotation, add the following imports into your class:

```Java
import static co.wds.testingtools.annotations.RandomAnnotation.randomise;
import static co.wds.testingtools.annotations.RandomAnnotation.randomiseFields;
import co.wds.testingtools.annotations.RandomAnnotation.Randomise;
```

The annotation itself can be applied to `Long`, `Integer`, `Boolean`, `Double`, `Float`, `Byte`, and `String`, plus their primitive equivalents.

To randomise a value, simply apply `@Randomise` before the value (if it is a field) or use the static `randomise` method.

```Java
@Randomise public String randomString_1;
@Randomise public String randomString_2;
@Randomise public Long randomLong;
@Randomise public Integer randomInt;
@Randomise public Boolean randomBool;
@Randomise public Double randomDouble;
@Randomise public Float randomFloat;
@Randomise public Byte randomByte;
@Randomise public boolean randomBooleanPrimitive;
@Randomise public long randomLongPrimitive;
@Randomise public int randomIntPrimitive;
@Randomise public float randomFloatPrimitive;
@Randomise public double randomDoublePrimitive;
@Randomise public byte randomBytePrimitive;

// alternative using method
public String randomAlternative = randomise(String.class);
public Long randonLongAlt = randomise(Long.class);
```

If you are using the annotation, you will need to run `randomiseFields` in a before method:

```Java
@Before
public void setup() throws Exception {
  randomiseFields(this);
}
```

## Mapper Servlet

To use the mapper servlet annotation, you will need to add the following imports:

```Java
import co.wds.testingtools.annotations.MapperServlet.RespondTo;
import co.wds.testingtools.annotations.MapperServlet.ResponseData;
import co.wds.testingtools.annotations.MapperServlet.TestServlet;

import static co.wds.testingtools.annotations.MapperServlet.startMapperServlet;
import static co.wds.testingtools.annotations.MapperServlet.stopMapperServlet;
import static co.wds.testingtools.annotations.MapperServlet.mostRecent;
```

You can then annotate your test class with the following (example):

```Java
@TestServlet(port=54321, contentType="text/plain")
@RespondTo({
	@ResponseData(url="hamlet", resourceFile="hamlet.txt"),
	@ResponseData(url="test", resourceFile="test.html", contentType="text/html"),
	@ResponseData(url="data", resourceFile="data.xml", contentType="application/xml"),
	@ResponseData(url="fake", resourceFile="fake.json", contentType="application/json")
	})
```

This should be fairly self explanatory - it will spin up a mapper servlet on port `54321`, with a default content type of `text/plain`. It will respond to the url `hamlet` with the contents of a file called `hamlet.txt` etc.

The resource files should be located in the resources folder alongside your test - see the example tests for more details and how everything works.

You can also get the most recent request - useful to test headers passed etc.

```Java
@Test
public void showMostRecent() {
  unit.doWork();

  Request recent = mostRecent();
  assertThat(recent.parameters.get("test-param")[0], is("one"));
}
```

# Utilities

## TestInjectionUtils

# injectPrivateMember(Object destination, String destinationField, Object objectToInject);

The purpose of this tool is to provide the ability to inject a private member of a class under test with any object.

 example of use;

    private class Controller {
         private Dao dao;

         private doSomethingWithADao(gubbins) {
             dao.save(gubbins)
         }
    }

    ***************************************************

public class TestController {

    Controller controller;
    Dao mockDao;

    @Before
    public void setUp(){
        mockDao = Mockito.mock(Dao.class);
        controller = new Controller();

        TestInjectionUtils.injectPrivateMember(controller, "dao", mockDao);
    }

    @Test
    public void controllerTest() {
        Mockito.verify(mockDao).save(Mockito.any(String.class));
    }
}


## Testing for System.exit

If you have a class that you want to exit the Java system in response to certain stimulus, testing this can prove difficult. We have created the `SystemExitTestUtils` class. This handles creating a specialised Security Manager to capture System Exits, and switching it "on" and "off".

A (trivial) example in Java (from the test case for this class):

```Java
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
```