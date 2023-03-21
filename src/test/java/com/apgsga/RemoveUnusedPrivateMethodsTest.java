package com.apgsga;

import org.junit.jupiter.api.Test;
import org.openrewrite.Issue;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.cleanup.RemoveUnusedPrivateMethods;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveUnusedPrivateMethodsTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveUnusedPrivateMethods())
          .parser(JavaParser.fromJavaVersion().classpath("junit-jupiter-params"));
    }

    @Test
    void removeUnusedPrivateMethods() {
        rewriteRun(
          java(
            """
              class Test {
                  private void unused() {
                  }
                            
                  public void dontRemove() {
                      dontRemove2();
                  }
                  
                  private void dontRemove2() {
                  }
              }
              """,
            """
              class Test {
                            
                  public void dontRemove() {
                      dontRemove2();
                  }
                  
                  private void dontRemove2() {
                  }
              }
              """
          )
        );
    }

    @SuppressWarnings("MissingSerialAnnotation")
    @Test
    void doNotRemoveCustomizedSerialization() {
        rewriteRun(
          java(
            """
              class Test implements java.io.Serializable {
                  private void writeObject(java.io.ObjectOutputStream out) {}
                  private void readObject(java.io.ObjectInputStream in) {}
                  private void readObjectNoData() {}
              }
              """
          )
        );
    }

    @Test
    void doNotRemoveMethodsWithAnnotations() {
        rewriteRun(
          java(
            """
              import org.junit.jupiter.params.provider.MethodSource;
              import java.util.stream.Stream;

              class Test {
                  @MethodSource("sourceExample")
                  void test(String input) {
                  }
                  private Stream<Object> sourceExample() {
                      return null;
                  }
              }
              """
          )
        );
    }

    @Issue("https://github.com/openrewrite/rewrite/issues/1536")
    @Test
    void privateMethodWithBoundedGenericTypes() {
        rewriteRun(
          java(
            """
              public class TestClass {
                  void method() {
                      checkMethodInUse("String", "String");
                  }

                  private static void checkMethodInUse(String arg0, String arg1) {
                  }

                  private static <T> void checkMethodInUse(String arg0, T arg1) {
                  }
              }
              """
          )
        );
    }
}