package com.gabrielittner.auto.value.util;

import com.gabrielittner.auto.value.util.extensions.CallConstructorExtension;
import com.gabrielittner.auto.value.util.extensions.ErrorExtension;
import com.gabrielittner.auto.value.util.extensions.AbstractExtension;
import com.gabrielittner.auto.value.util.extensions.FinalExtension;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.auto.value.processor.AutoValueProcessor;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import java.util.Collections;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class AutoValueUtilTest {

    @Test
    public void simpleFinal() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue public abstract class Test {\n"
                + "  public abstract int a();\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
                + "package test;\n"
                + "final class AutoValue_Test extends $AutoValue_Test {\n"
                + "  AutoValue_Test(int a) {\n"
                + "    super(a);\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Collections.singletonList(source))
                .processedWith(newProcessor(new FinalExtension()))
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void simpleAbstract() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue public abstract class Test {\n"
                + "  public abstract int a();\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("test/$AutoValue_Test", ""
                + "package test;\n"
                + "abstract class $AutoValue_Test extends $$AutoValue_Test {\n"
                + "  AutoValue_Test(int a) {\n"
                + "    super(a);\n"
                + "  }\n"
                + "}\n");

        JavaFileObject expected2 = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
                + "package test;\n"
                + "final class AutoValue_Test extends $AutoValue_Test {\n"
                + "  AutoValue_Test(int a) {\n"
                + "    super(a);\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Collections.singletonList(source))
                .processedWith(newProcessor(new FinalExtension(), new AbstractExtension()))
                .compilesWithoutError()
                .and()
                .generatesSources(expected, expected2);
    }

    @Test
    public void simpleNestedClass() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "public class Test {\n"
                + "  @AutoValue public static abstract class Inner {\n"
                + "    public abstract int a();\n"
                + "    public abstract String b();\n"
                + "    public abstract boolean c();\n"
                + "  }\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Test_Inner", ""
                + "package test;\n"
                + "import java.lang.String;"
                + "final class AutoValue_Test_Inner extends $AutoValue_Test_Inner {\n"
                + "  AutoValue_Test_Inner(int a, String b, boolean c) {\n"
                + "    super(a, b, c);\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Collections.singletonList(source))
                .processedWith(newProcessor(new FinalExtension()))
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void genericClass() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue public abstract class Test<T> {\n"
                + "  public abstract T t();\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
                + "package test;\n"
                + "final class AutoValue_Test<T> extends $AutoValue_Test<T> {\n"
                + "  AutoValue_Test(T t) {\n"
                + "    super(t);\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Collections.singletonList(source))
                .processedWith(newProcessor(new AbstractExtension()))
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void callingFinalClassConstructor() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue public abstract class Test {\n"
                + "  public abstract int a();\n"
                + "  public abstract String b();\n"
                + "  public abstract boolean c();\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("test/$AutoValue_Test", ""
                + "package test;\n"
                + "import java.lang.String;"
                + "abstract class $AutoValue_Test extends $$AutoValue_Test {\n"
                + "  $AutoValue_Test(int a, String b, boolean c) {\n"
                + "    super(a, b, c);\n"
                + "  }\n"
                + "  Test test() {\n"
                + "    return new AutoValue_Test(a(), b(), c());\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Collections.singletonList(source))
                .processedWith(newProcessor(new CallConstructorExtension(), new FinalExtension()))
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void nestedClassCallingFinalClassConstructor() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "public class Test {\n"
                + "  @AutoValue public static abstract class Inner {\n"
                + "    public abstract int a();\n"
                + "    public abstract String b();\n"
                + "    public abstract boolean c();\n"
                + "  }\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("test/$AutoValue_Test_Inner", ""
                + "package test;\n"
                + "import java.lang.String;"
                + "abstract class $AutoValue_Test_Inner extends $$AutoValue_Test_Inner {\n"
                + "  $AutoValue_Test_Inner(int a, String b, boolean c) {\n"
                + "    super(a, b, c);\n"
                + "  }\n"
                + "  Test.Inner test() {\n"
                + "    return new AutoValue_Test_Inner(a(), b(), c());\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Collections.singletonList(source))
                .processedWith(newProcessor(new CallConstructorExtension(), new FinalExtension()))
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void genericFinalClassConstructor() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue public abstract class Test<X, Y, Z> {\n"
                + "  public abstract X a();\n"
                + "  public abstract Y b();\n"
                + "  public abstract Z c();\n"
                + "}\n");

        JavaFileObject expected = JavaFileObjects.forSourceString("test/$AutoValue_Test", ""
                + "package test;\n"
                + "abstract class $AutoValue_Test<X, Y, Z> extends $$AutoValue_Test<X, Y, Z> {\n"
                + "  $AutoValue_Test(X a, Y b, Z c) {\n"
                + "    super(a, b, c);\n"
                + "  }\n"
                + "  Test<X, Y, Z> test() {\n"
                + "    return new AutoValue_Test<>(a(), b(), c());\n"
                + "  }\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Collections.singletonList(source))
                .processedWith(newProcessor(new CallConstructorExtension(), new FinalExtension()))
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void error() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                + "package test;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue public abstract class Test {\n"
                + "  public abstract int a();\n"
                + "}\n");

        assertAbout(javaSources())
                .that(Collections.singletonList(source))
                .processedWith(newProcessor(new ErrorExtension()))
                .failsToCompile()
                .withErrorContaining(
                        "Error generating AutoValue_Test extending $AutoValue_Test with isFinal = true");
    }

    private AutoValueProcessor newProcessor(AutoValueExtension... extension) {
        return new AutoValueProcessor(Arrays.asList(extension));
    }
}
