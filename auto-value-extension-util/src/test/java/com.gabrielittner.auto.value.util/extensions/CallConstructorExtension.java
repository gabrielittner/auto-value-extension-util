package com.gabrielittner.auto.value.util.extensions;

import com.gabrielittner.auto.value.util.AutoValueUtil;
import com.google.auto.value.extension.AutoValueExtension;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.Set;

import static com.gabrielittner.auto.value.util.AutoValueUtil.newTypeSpecBuilder;

public class CallConstructorExtension extends AutoValueExtension {

    @Override
    public boolean applicable(Context context) {
        return true;
    }

    @Override
    public String generateClass(
            Context context, String className, String classToExtend, boolean isFinal) {
        TypeSpec subclass =
                newTypeSpecBuilder(context, className, classToExtend, isFinal)
                        .addMethod(methodCallingConstructor(context))
                        .build();
        return JavaFile.builder(context.packageName(), subclass).build().toString();
    }

    private MethodSpec methodCallingConstructor(Context context) {
        Set<String> propertyKeySet = context.properties().keySet();
        String[] names = new String[propertyKeySet.size()];
        int i = 0;
        for (String s : propertyKeySet) {
            names[i] = s + "()";
            i++;
        }
        return MethodSpec.methodBuilder("test")
                .returns(AutoValueUtil.getAutoValueClassTypeName(context))
                .addCode("return ")
                .addCode(AutoValueUtil.newFinalClassConstructorCall(context, names))
                .build();
    }
}
