package org.iakuh.kauth.cas.helper;

import java.util.Base64;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class TokenGenerator {

  public static String generate() {
    UUID uuid = UUID.randomUUID();
    String uuidStr = uuid.toString().replaceAll("-", "");
    return StringUtils.stripEnd(Base64.getEncoder().encodeToString(uuidStr.getBytes()), "=");
  }
}
