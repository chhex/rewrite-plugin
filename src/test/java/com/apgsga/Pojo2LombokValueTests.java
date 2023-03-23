package com.apgsga;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import com.apgsga.vkvp.mig.Pojo2LombokValue;

import static org.openrewrite.java.Assertions.java;

class Pojo2LombokValueTests implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new Pojo2LombokValue())
                .parser(JavaParser.fromJavaVersion().classpath(""));
    }

    @Test
    void testRemoveNone() {
        rewriteRun(
                java(
                        """
                                class Test {

                                    private Long testLong;

                                    private void unusedDontRemove() {
                                    }

                                    public void dontRemove() {
                                        dontRemove2();
                                    }

                                    private void dontRemove2() {
                                    }

                                    public Long getTestLong() {
                                        return testLong;
                                    }

                                    public void setTestLong(Long testLong) {
                                        this.testLong = testLong;
                                    }
                                }
                                """));
    }

    @Test
    void toLombokData() {
        rewriteRun(
                java(
                        """
                                package groupby; 
                                class Test {

                                    private Long testLong;

                                    public Test(final Long testLong) {
                                        super();
                                        this.testLong = testLong;
                                    }

                                    private void unusedDontRemove() {
                                    }

                                    public void dontRemove() {
                                        dontRemove2();
                                    }

                                    private void dontRemove2() {
                                    }

                                    public Long getTestLong() {
                                        return testLong;
                                    }

                                    public void setTestLong(Long testLong) {
                                        this.testLong = testLong;
                                    }

                                    @Override
                                    public boolean equals(final Object obj) {
                                      if (this == obj) {
                                        return true;
                                      }
                                      if (obj == null) {
                                        return false;
                                      }
                                      if (getClass() != obj.getClass()) {
                                        return false;
                                      }
                                      return true;
                                    }

                                    @Override
                                    public String toString() {
                                      return "whatever";
                                    }
                                }
                                """,
                        """
                                package groupby; 

                                import lombok.AllArgsConstructor;
                                import lombok.Value;

                                @AllArgsConstructor
                                @Value
                                class Test {

                                    private Long testLong;

                                    private void unusedDontRemove() {
                                    }

                                    public void dontRemove() {
                                        dontRemove2();
                                    }

                                    private void dontRemove2() {
                                    }
                                }
                                """));
    }

 

}