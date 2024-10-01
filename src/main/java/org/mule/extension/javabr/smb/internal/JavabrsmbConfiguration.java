package org.mule.extension.javabr.smb.internal;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

/**
 * This class represents an extension configuration, values set in this class
 * are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(JavabrsmbOperations.class)
@ConnectionProviders(JavabrsmbConnectionProvider.class)
public class JavabrsmbConfiguration {

  /**
   * How many bytes will be buffered in the read and write operations
   */
  @Parameter
  @Optional(defaultValue = "16384")
  private String bufferSize;

  public String getBufferSize() {
    return bufferSize;
  }

}
