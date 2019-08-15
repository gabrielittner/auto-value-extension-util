package com.gabrielittner.auto.value.util;

import com.google.common.collect.Sets;
import com.google.testing.compile.CompilationRule;
import com.squareup.javapoet.TypeName;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static javax.lang.model.util.ElementFilter.methodsIn;

public class PropertyTest {
    @Rule public final CompilationRule compilation = new CompilationRule();

    private Elements elements;

    @Before
    public void setUp() {
        elements = compilation.getElements();
    }

    private TypeElement getElement(Class<?> clazz) {
        return elements.getTypeElement(clazz.getCanonicalName());
    }

    private ExecutableElement findFirst(Collection<ExecutableElement> elements, String name) {
        for (ExecutableElement executableElement : elements) {
            if (executableElement.getSimpleName().toString().equals(name)) {
                return executableElement;
            }
        }
        throw new IllegalArgumentException(name + " not found in " + elements);
    }

    private abstract static class HasNullable {
        @Nullable
        public abstract String foo();
    }

    @Test
    public void getsNullable() {
        TypeElement element = getElement(HasNullable.class);
        List<ExecutableElement> methods = methodsIn(elements.getAllMembers(element));
        Property prop = new Property("foo", findFirst(methods, "foo"));
        assertThat(prop.nullable()).isTrue();
    }

    private abstract static class NonNullable {
        public abstract String bar();
    }

    @Test
    public void getsNonNullable() {
        TypeElement element = getElement(NonNullable.class);
        List<ExecutableElement> methods = methodsIn(elements.getAllMembers(element));
        Property prop = new Property("foo", findFirst(methods, "bar"));
        assertThat(prop.nullable()).isFalse();
    }

    @Test
    public void getsReturnType() {
        TypeElement element = getElement(HasNullable.class);
        List<ExecutableElement> methods = methodsIn(elements.getAllMembers(element));
        Property prop = new Property("foo", findFirst(methods, "foo"));
        assertThat(prop.type()).isEqualTo(TypeName.get(String.class));
    }

    @Test
    public void getsBasicProperties() {
        TypeElement element = getElement(HasNullable.class);
        List<ExecutableElement> methods = methodsIn(elements.getAllMembers(element));
        ExecutableElement method = findFirst(methods, "foo");
        Property prop = new Property("foo", method);
        assertThat(prop.methodName()).isEqualTo("foo");
        assertThat(prop.humanName()).isEqualTo("foo");
        assertThat(prop.element()).isEqualTo(method);
        assertThat(prop.annotations()).containsAtLeastElementsIn(Sets.newHashSet("Nullable"));
    }
}
