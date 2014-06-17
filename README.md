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
