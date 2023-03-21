package com.apgsga;

import org.junit.jupiter.api.Test;
import org.openrewrite.Issue;
import org.openrewrite.java.GenerateGetterAndSetterVisitor;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.test.RewriteTest.toRecipe;


class GenerateGetterAndSetterVisitorTest implements RewriteTest {

    @Issue("https://github.com/openrewrite/rewrite/issues/1301")
    @Test
    void getterAndSetterForPrimitiveInteger() {
        rewriteRun(
          spec -> spec.recipe(toRecipe(() -> new GenerateGetterAndSetterVisitor<>("counter"))),
          java(
            """
              class T {
                  int counter;
              }
              """,
            """
              class T {
                  int counter;
                  public int getCounter() {
                      return counter;
                  }
                  public void setCounter(int counter) {
                      this.counter = counter;
                  }
              }
              """
          )
        );
    }

    @Test
    void getterAndSetterForNonPrimitive() {
        rewriteRun(
          spec -> spec.recipe(toRecipe(() -> new GenerateGetterAndSetterVisitor<>("size"))),
          java(
            """
              class T {
                  Float size;
              }
              """,
            """
              class T {
                  Float size;
                  public Float getSize() {
                      return size;
                  }
                  public void setSize(Float size) {
                      this.size = size;
                  }
              }
              """
          )
        );
    }

    @Test
    void getterAndSetterPrimitiveBoolean() {
        rewriteRun(
          spec -> spec.recipe(toRecipe(() -> new GenerateGetterAndSetterVisitor<>("valid"))),
          java(
            """
              class T {
                  boolean valid;
              }
              """,
            """
              class T {
                  boolean valid;
                  public boolean isValid() {
                      return valid;
                  }
                  public void setValid(boolean valid) {
                      this.valid = valid;
                  }
              }
              """
          )
        );
    }
}
