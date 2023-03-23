package com.apgsga.vkvp.mig;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.java.AnnotationMatcher;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.Annotation;
import org.openrewrite.java.tree.JavaType;

public class Pojo2LombokValue extends Recipe {

    @Override
    public String getDisplayName() {
        return "Pojo to Lombok Value Migration";
    }

    @Override
    public String getDescription() {
        return "This Recipe migrates Pojo Classes (groupBy) of VKVP to Lombok Value Classes.";
    }

    @Override
    public Duration getEstimatedEffortPerOccurrence() {
        return Duration.ofMinutes(3);
    }

    @Override
    public JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {

            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl,
                    ExecutionContext executionContext) {
                J.ClassDeclaration cd = super.visitClassDeclaration(classDecl, executionContext);
                List<Annotation> allAnnotations = cd.getAllAnnotations();
                if (isSelectedClass(cd)
                        && allAnnotations.stream().noneMatch(new AnnotationMatcher("@lombok.Value")::matches)
                        && allAnnotations.stream().noneMatch(new AnnotationMatcher("@lombok.AllArgsConstructor")::matches)) {
                    JavaTemplate template = JavaTemplate.builder(this::getCursor, "@Value")
                            .imports("lombok.Value")
                            .javaParser(() -> JavaParser.fromJavaVersion()
                                    .dependsOn("package lombok; public @interface Value {}")
                                    .build())
                            .build();
                    cd = cd.withTemplate(template,
                            cd.getCoordinates().addAnnotation(Comparator.comparing(J.Annotation::getSimpleName)));
                    template = JavaTemplate.builder(this::getCursor, "@AllArgsConstructor")
                            .imports("lombok.AllArgsConstructor")
                            .javaParser(() -> JavaParser.fromJavaVersion()
                                    .dependsOn("package lombok; public @interface AllArgsConstructor {}")
                                    .build())
                            .build();
                    cd = cd.withTemplate(template,
                    cd.getCoordinates().addAnnotation(Comparator.comparing(J.Annotation::getSimpleName)));
                    maybeAddImport("lombok.Value");
                    maybeAddImport("lombok.AllArgsConstructor");

                }
                return cd;
            }

            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
                J.MethodDeclaration m = super.visitMethodDeclaration(method, ctx);
                JavaType.Method methodType = method.getMethodType();
                String simpleMethodName = m.getSimpleName();
                if (methodType != null) {

                    J.ClassDeclaration classDeclaration = getCursor().firstEnclosing(J.ClassDeclaration.class);
                    if (classDeclaration == null) {
                        return m;
                    }
                    if (!isSelectedClass(classDeclaration))
                        return m;

                    if (!(method.isConstructor() || simpleMethodName.startsWith("get") || simpleMethodName.startsWith("set")
                            || simpleMethodName.startsWith("isSet") || simpleMethodName.startsWith("hashCode")
                            || simpleMethodName.startsWith("toString") || simpleMethodName.startsWith("equals"))) 
                        return m;

                    return null;
                }

                return m;
            }


            private boolean isSelectedClass(J.ClassDeclaration cd) {
                String packageName = cd.getType().getPackageName();
                if (packageName.endsWith(".groupby") || packageName.equals("groupby"))
                    return true;
                return false;

            }

        };

    }

}
