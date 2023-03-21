package com.apgsga;

import lombok.Data;

@Data
public class RoWerbemittelbedarf {
  private String produktformat;

  private String textFarbe;

  private String sujet;

  private boolean isSet;

  public long asNormalTotalAnzahl() {
    return 0l;
  }

  public boolean isSet() {
    return this.isSet;
  }
}