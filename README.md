testing-tools
=============

A suite of useful Java testing tools

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
@Randomise String randomString_1;
@Randomise String randomString_2;
@Randomise Long randomLong;
@Randomise Integer randomInt;
@Randomise Boolean randomBool;
@Randomise Double randomDouble;
@Randomise Float randomFloat;
@Randomise Byte randomByte;
@Randomise boolean randomBooleanPrimitive;
@Randomise long randomLongPrimitive;
@Randomise int randomIntPrimitive;
@Randomise float randomFloatPrimitive;
@Randomise double randomDoublePrimitive;
@Randomise byte randomBytePrimitive;
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
import co.wds.testingtools.annotations.MapperServletAnnotations.RespondTo;
import co.wds.testingtools.annotations.MapperServletAnnotations.ResponseData;
import co.wds.testingtools.annotations.MapperServletAnnotations.TestServlet;

import static co.wds.testingtools.annotations.MapperServletAnnotations.startMapperServlet;
import static co.wds.testingtools.annotations.MapperServletAnnotations.stopMapperServlet;
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
