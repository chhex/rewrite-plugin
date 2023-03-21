package com.apgsga;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import com.apgsga.vkvp.MigratePojoLombok;

import static org.openrewrite.java.Assertions.java;

class MigratePojoLombokTests implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new MigratePojoLombok())
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
    void removeUnusedPrivateMethods() {
        rewriteRun(
                java(
                        """
                                class RoTest {

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
                                """,
                        """
                                import lombok.Data;

                                @Data
                                class RoTest {

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

    @Test
    void removeIsSetFieldsTest() {
        rewriteRun(
                java(
                        """
                                class RoTest {

                                    private Long testLong;

                                    private boolean isSet_testLong;

                                    private boolean isSet;

                                    public Long getTestLong() {
                                        return testLong;
                                    }

                                    public void setTestLong(Long testLong) {
                                        this.testLong = testLong;
                                    }

                                    public boolean isSet_testLong()  {
                                        return isSet_testLong;
                                    }

                                    public boolean isSet() {
                                        return isSet;
                                    }
                                }
                                """,
                        """
                                import lombok.Data;

                                @Data
                                class RoTest {

                                    private Long testLong;

                                    private boolean isSet;

                                    public boolean isSet() {
                                        return isSet;
                                    }
                                }
                                """));
    }

    @Test
    void testWerbeMittel() {
        rewriteRun(
                java(
                        """
                                public class RoWerbemittelbedarf {
                                    private String produktformat;

                                    private String textFarbe;

                                    private String sujet;

                                    public long asNormalTotalAnzahl() {
                                        return 0l;
                                    }

                                    private boolean isSet_produktformat;

                                    public String getProduktformat() {
                                      return this.produktformat;
                                    }

                                    public void setProduktformat(final String produktformat) {
                                      if (isSet_produktformat) {
                                        throw new IllegalStateException();
                                      }
                                      isSet_produktformat = true;
                                      this.produktformat = produktformat;
                                      isSet = true;
                                    }

                                    private boolean isSet_textFarbe;

                                    public String getTextFarbe() {
                                      return this.textFarbe;
                                    }

                                    public void setTextFarbe(final String textFarbe) {
                                      if (isSet_textFarbe) {
                                        throw new IllegalStateException();
                                      }
                                      isSet_textFarbe = true;
                                      this.textFarbe = textFarbe;
                                      isSet = true;
                                    }

                                    private boolean isSet_sujet;

                                    public String getSujet() {
                                      return this.sujet;
                                    }

                                    public void setSujet(final String sujet) {
                                      if (isSet_sujet) {
                                        throw new IllegalStateException();
                                      }
                                      isSet_sujet = true;
                                      this.sujet = sujet;
                                      isSet = true;
                                    }

                                    private boolean isSet;

                                    public boolean isSet() {
                                      return this.isSet;
                                    }
                                }
                                """,
                                """
                                import lombok.Data;

                                @Data
                                public class RoWerbemittelbedarf {
                                    private String produktformat;

                                    private String textFarbe;

                                    private String sujet;

                                    public long asNormalTotalAnzahl() {
                                        return 0l;
                                    }

                                    private boolean isSet;

                                    public boolean isSet() {
                                      return this.isSet;
                                    }
                                }
                                """));
    }

}