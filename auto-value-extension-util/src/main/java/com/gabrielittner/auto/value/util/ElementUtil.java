package com.gabrielittner.auto.value.util;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

import static com.google.auto.common.MoreElements.getLocalAndInheritedMethods;

public final class ElementUtil {

    /**
     * Returns a method of {@code cls} that is static, has {@code returns} as return type and the
     * number and types of parameters match {@code takes}. Returns null if such a method doesn't
     * exist.
     */
    public static Optional<ExecutableElement> getMatchingStaticMethod(
            TypeElement cls, TypeName returns, TypeName... takes) {
        for (Element element : cls.getEnclosedElements()) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) element;
            if (methodMatches(method, Modifier.STATIC, returns, takes)) {
                return Optional.of(method);
            }
        }
        return Optional.absent();
    }

    /**
     * Returns a method of {@code cls} that is abstract, has {@code returns} as return type and the
     * number and types of parameters match {@code takes}. Returns null if such a method doesn't
     * exist.
     */
    public static Optional<ExecutableElement> getMatchingAbstractMethod(
            Set<ExecutableElement> methods, TypeName returns, TypeName... takes) {
        for (ExecutableElement method : methods) {
            if (methodMatches(method, Modifier.ABSTRACT, returns, takes)) {
                return Optional.of(method);
            }
        }
        return Optional.absent();
    }

    private static boolean methodMatches(
            ExecutableElement method, Modifier modifier, TypeName returns, TypeName[] takes) {
        return hasModifier(method, modifier)
                && methodTakes(method, takes)
                && methodReturns(method, returns);
    }

    static boolean hasModifier(ExecutableElement method, Modifier modifier) {
        return method.getModifiers().contains(modifier);
    }

    static boolean methodTakes(ExecutableElement method, TypeName... takes) {
        List<? extends VariableElement> parameters = method.getParameters();
        if (parameters.size() != takes.length) {
            return false;
        }
        for (int i = 0; i < takes.length; i++) {
            if (!takes[i].equals(TypeName.get(parameters.get(i).asType()))) {
                return false;
            }
        }
        return true;
    }

    static boolean methodReturns(ExecutableElement method, TypeName returns) {
        return returns.equals(ClassName.get(method.getReturnType()));
    }

    /**
     * Returns true if given {@code className} is on the current classpath.
     */
    public static boolean typeExists(Elements elements, ClassName className) {
        String name = className.toString();
        return elements.getTypeElement(name) != null;
    }

    /**
     * Returns true if the given {@code element} is annotated with an annotation named
     * {@code simpleName}.
     */
    public static boolean hasAnnotationWithName(Element element, String simpleName) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            String name = mirror.getAnnotationType().asElement().getSimpleName().toString();
            if (simpleName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Builds a {@link ImmutableSet} containing the names of all annotations of the given
     * {@code element}.
     */
    public static ImmutableSet<String> buildAnnotations(ExecutableElement element) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            builder.add(annotation.getAnnotationType().asElement().getSimpleName().toString());
        }
        return builder.build();
    }

    /**
     * If the given {@code element} is annotated with an {@link Annotation} of class {@code clazz}
     * it's value for {@code key} will be returned. Otherwise it will return null.
     *
     * @throws IllegalArgumentException if no element is defined with the given key.
     */
    public static Object getAnnotationValue(
            Element element, Class<? extends Annotation> clazz, String key) {
        Optional<AnnotationMirror> annotation = MoreElements.getAnnotationMirror(element, clazz);
        if (annotation.isPresent()) {
            return AnnotationMirrors.getAnnotationValue(annotation.get(), key).getValue();
        }
        return null;
    }

    private ElementUtil() {
        throw new AssertionError("No instances.");
    }
}
