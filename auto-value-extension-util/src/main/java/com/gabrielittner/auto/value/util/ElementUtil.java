package com.gabrielittner.auto.value.util;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

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

    static TypeMirror getResolvedReturnType(
            Types typeUtils, TypeElement type, ExecutableElement method) {
        TypeMirror returnType = method.getReturnType();
        if (returnType.getKind() == TypeKind.TYPEVAR) {
            List<HierarchyElement> hierarchy =
                    getHierarchyUntilClassWithElement(typeUtils, type, method);
            return resolveGenericType(hierarchy, returnType);
        }
        return returnType;
    }

    private static List<HierarchyElement> getHierarchyUntilClassWithElement(
            Types typeUtils, TypeElement start, Element target) {

        for (TypeMirror superType : typeUtils.directSupertypes(start.asType())) {
            TypeElement superTypeElement = (TypeElement) typeUtils.asElement(superType);
            if (superTypeElement.getEnclosedElements().contains(target)) {
                HierarchyElement base = new HierarchyElement(superTypeElement, null);
                HierarchyElement current = new HierarchyElement(start, superType);
                return new ArrayList<>(Arrays.asList(base, current));
            }
        }

        for (TypeMirror superType : typeUtils.directSupertypes(start.asType())) {
            TypeElement superTypeElement = (TypeElement) typeUtils.asElement(superType);
            List<HierarchyElement> result =
                    getHierarchyUntilClassWithElement(typeUtils, superTypeElement, target);
            if (result != null) {
                result.add(new HierarchyElement(start, superType));
                return result;
            }
        }
        return null;
    }

    private static TypeMirror resolveGenericType(
            List<HierarchyElement> hierarchy, TypeMirror type) {

        int position = indexOfParameter(hierarchy.get(0).element, type.toString());
        for (int i = 1; i < hierarchy.size(); i++) {
            HierarchyElement hierarchyElement = hierarchy.get(i);

            type = ((DeclaredType) hierarchyElement.superType).getTypeArguments().get(position);
            if (type.getKind() != TypeKind.TYPEVAR) {
                return type;
            }

            position = indexOfParameter(hierarchyElement.element, type.toString());
        }
        throw new IllegalArgumentException("Couldn't resolve type " + type);
    }

    private static class HierarchyElement {
        private final TypeElement element;
        private final TypeMirror superType;

        private HierarchyElement(TypeElement element, TypeMirror superType) {
            this.element = element;
            this.superType = superType;
        }
    }

    private static int indexOfParameter(TypeElement element, String param) {
        List<? extends TypeParameterElement> params = element.getTypeParameters();
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getSimpleName().toString().equals(param)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Param " + param + "not not found in list");
    }

    private ElementUtil() {
        throw new AssertionError("No instances.");
    }
}
