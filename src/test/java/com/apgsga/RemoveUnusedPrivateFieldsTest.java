package com.apgsga;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.cleanup.RemoveUnusedPrivateFields;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveUnusedPrivateFieldsTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveUnusedPrivateFields());
    }

    @Test
    void doNotRemoveSerialVersionUid() {
        rewriteRun(
          java(
            """
              public class Test implements java.io.Serializable {
                  private static final long serialVersionUID = 42L;
              }
              """
          )
        );
    }

    @Test
    void doNotRemoveAnnotatedField() {
        rewriteRun(
          java(
            """
              public class Test {
                  @Deprecated
                  public String annotated;
              }
              """
          )
        );
    }

    @Test
    void doNotChangeFieldsOnClassWithNativeMethod() {
        rewriteRun(
          java(
            """
              public class Test {
                  public String notUsed;
                  public native void method();
              }
              """
          )
        );
    }

    @Test
    void notPrivateField() {
        rewriteRun(
          java(
            """
              public class Test {
                  public String notUsed;
              }
              """
          )
        );
    }

    @Test
    void fieldIsUsed() {
        rewriteRun(
          java(
            """
              public class Test {
                  private String value;
                  void method() {
                      String useValue = value;
                  }
              }
              """
          )
        );
    }

    @Test
    void usedInClassScope() {
        rewriteRun(
          java(
            """
              public class Test {
                  private String value = "";
                  private String useValue = method(value);
                  String method(String arg0) {
                      return arg0 + useValue;
                  }
              }
              """
          )
        );
    }

    @Test
    void removeUnusedPrivateField() {
        rewriteRun(
          java(
            """
              public class Test {
                  private String notUsed;
              }
              """,
            """
              public class Test {
              }
              """
          )
        );
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    void nameIsShadowed() {
        rewriteRun(
          java(
            """
              public class Test {
                  private String value;
                  void method() {
                      String value = "name shadow";
                      String shadowedUse = value;
                  }
              }
              """,
            """
              public class Test {
                  void method() {
                      String value = "name shadow";
                      String shadowedUse = value;
                  }
              }
              """
          )
        );
    }

    @Test
    void onlyRemoveUnusedNamedVariable() {
        rewriteRun(
          java(
            """
              public class Test {
                  private String aOne, aTwo, aThree;
                  private String bOne, bTwo, bThree;
                  private String cOne, cTwo, cThree;
                  void method() {
                      String removeAOne = aTwo + aThree;
                      String removeBTwo = bOne + bThree;
                      String removeCThree = cOne + cTwo;
                  }
              }
              """,
            """
              public class Test {
                  private String aTwo, aThree;
                  private String bOne, bThree;
                  private String cOne, cTwo;
                  void method() {
                      String removeAOne = aTwo + aThree;
                      String removeBTwo = bOne + bThree;
                      String removeCThree = cOne + cTwo;
                  }
              }
              """
          )
        );
    }
}

