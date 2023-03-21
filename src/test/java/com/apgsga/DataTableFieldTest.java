package com.apgsga;

import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.ExtractField;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType.Variable;
import org.openrewrite.test.RewriteTest;

import com.google.common.collect.Lists;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.openrewrite.java.Assertions.java;

public class DataTableFieldTest implements RewriteTest {

    @Test
    void extractField() {
        rewriteRun(
          spec -> spec.recipe(RewriteTest.toRecipe(() -> new JavaIsoVisitor<>() {
              @Override
              public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext ctx) {
                  boolean isLocalVariable = getCursor().dropParentUntil(J.Block.class::isInstance).getParentTreeCursor().getValue() instanceof J.MethodDeclaration;
                  if (isLocalVariable) {
                      doAfterVisit(new ExtractField<>(multiVariable));
                  }
                  return multiVariable;
              }
          })),
          java(
            """
              import java.util.Date;
              class Test {
                  public Test() {
                     String someThingElse = "Whole of rocking"; 
                     Date today = new Date();
               
                  }
              }
              """,
            """
              import java.util.Date;
              class Test {
                  private Date today;
                  private String someThingElse;
                            
                  public Test() {
                      this.someThingElse = "Whole of rocking"; 
                      this.today = new Date();

                  }
              }
              """,
            spec -> spec.afterRecipe(cu -> new JavaIsoVisitor<>() {
                @Override
                public J.VariableDeclarations.NamedVariable visitVariable(J.VariableDeclarations.NamedVariable variable, Object o) {
                    @Nullable
                    Variable variableType = variable.getVariableType();
                    String string = requireNonNull(variableType).toString();
                    assertThat(string).isIn(Lists.newArrayList("Test{name=today,type=java.util.Date}","Test{name=someThingElse,type=java.lang.String}"));
                    return variable;
                }

                @Override
                public J.Assignment visitAssignment(J.Assignment assignment, Object o) {
                    String string = requireNonNull(assignment.getType()).toString();
                    assertThat(string).isIn(Lists.newArrayList("java.util.Date","java.lang.String"));
                    return super.visitAssignment(assignment, o);
                }
            }.visit(cu, 0))
          )
        );
    }

}