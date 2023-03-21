package com.apgsga.vkvp;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.java.AnnotationMatcher;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.Annotation;
import org.openrewrite.java.tree.J.VariableDeclarations.NamedVariable;
import org.openrewrite.java.tree.JavaType;

public class MigratePojoLombok extends Recipe {

    @Override
    public String getDisplayName() {
        return "Test Recipe, which removes all methods of a Class";
    }

    @Override
    public String getDescription() {
        return "Only for testing and learning purposes.";
    }

    @Override
    public Set<String> getTags() {
        return Collections.singleton("RSPEC-1144");
    }

    @Override
    public Duration getEstimatedEffortPerOccurrence() {
        return Duration.ofMinutes(5);
    }

    @Override
    public JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {

            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl,
                    ExecutionContext executionContext) {
                J.ClassDeclaration cd = super.visitClassDeclaration(classDecl, executionContext);
                String simpleClassName = cd.getSimpleName();
                List<Annotation> allAnnotations = cd.getAllAnnotations();
                if (simpleClassName.startsWith("Ro")
                        && allAnnotations.stream().noneMatch(new AnnotationMatcher("@lombok.Data")::matches)) {
                    JavaTemplate template = JavaTemplate.builder(this::getCursor, "@Data")
                            .imports("lombok.Data")
                            .javaParser(() -> JavaParser.fromJavaVersion()
                                    .dependsOn("package lombok; public @interface Data {}")
                                    .build())
                            .build();
                    maybeAddImport("lombok.Data");
                    cd = cd.withTemplate(template,
                            cd.getCoordinates().addAnnotation(Comparator.comparing(J.Annotation::getSimpleName)));

                }
                return cd;
            }

            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext ctx) {
                J.MethodDeclaration m = super.visitMethodDeclaration(method, ctx);
                JavaType.Method methodType = method.getMethodType();
                String simpleMethodName = m.getSimpleName();
                if (methodType != null &&
                        !method.isConstructor()) {

                    J.ClassDeclaration classDeclaration = getCursor().firstEnclosing(J.ClassDeclaration.class);
                    if (classDeclaration == null) {
                        return m;
                    }
                    String simpleClassName = classDeclaration.getSimpleName();
                    if (!simpleClassName.startsWith("Ro"))
                        return m;

                    if (!(simpleMethodName.startsWith("get") || simpleMethodName.startsWith("set") || simpleMethodName.startsWith("isSet_")))
                        return m;

                    return null;
                }

                return m;
            }
            @Override
            public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext executionContext) {

                List<NamedVariable> variables = multiVariable.getVariables();
                if (variables.size() == 1 && variables.get(0).getSimpleName().startsWith("isSet_") ) {
                    return null;
                }
                return super.visitVariableDeclarations(multiVariable, executionContext);
            }
    
            @Override
            public J.VariableDeclarations.NamedVariable visitVariable(J.VariableDeclarations.NamedVariable variable, ExecutionContext executionContext) {
                if (variable.getSimpleName().startsWith("isSet_")) {
                    return null;
                }
                return super.visitVariable(variable, executionContext);
            }
        };
 
    
    }


}
