package org.mule.extension.javabr.smb.internal;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;

import java.io.IOException;
import java.util.logging.Logger;

import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

/**
 * This class provides connections for the JavaBr SMB module by implementing
 * the {@link PoolingConnectionProvider} interface. It handles the creation,
 * validation, and disconnection of {@link JavabrsmbConnection} objects.
 */
public class JavabrsmbConnectionProvider implements PoolingConnectionProvider<JavabrsmbConnection> {

  private final Logger LOGGER = Logger.getLogger(JavabrsmbConnectionProvider.class.getName());

  /**
   * The domain name for the SMB connection.
   */
  @Parameter
  @DisplayName("Domain")
  private String domain;

  /**
   * The host address of the SMB server.
   */
  @Parameter
  @DisplayName("Host")
  private String host;

  /**
   * The shared folder on the SMB server.
   */
  @Parameter
  @DisplayName("Share")
  private String share;

  /**
   * The username for SMB authentication.
   */
  @Parameter
  @DisplayName("Username")
  private String username;

  /**
   * The password for SMB authentication.
   */
  @Parameter
  @DisplayName("Password")
  private String password;

  /**
   * Establishes a connection to the SMB server using the provided parameters.
   *
   * @return a {@link JavabrsmbConnection} object representing the connection to
   *         the SMB server.
   * @throws ConnectionException if an error occurs during the connection process.
   */
  @Override
  public JavabrsmbConnection connect() throws ConnectionException {
    JavabrsmbConnection connection = null;
    try {
      connection = new JavabrsmbConnection(domain, host, share, username, password);
    } catch (IOException ioe) {
      throw new ConnectionException(ioe);
    }
    return connection;
  }

  /**
   * Disconnects the given {@link JavabrsmbConnection}, invalidating the session.
   *
   * @param connection the connection object to be disconnected.
   */
  @Override
  public void disconnect(JavabrsmbConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      LOGGER.severe("Error while disconnecting [" + connection.getId() + "]: " + e.getMessage());
    }
  }

  /**
   * Validates the given {@link JavabrsmbConnection}.
   *
   * @param connection the connection object to be validated.
   * @return a {@link ConnectionValidationResult} indicating the result of the
   *         validation.
   */
  @Override
  public ConnectionValidationResult validate(JavabrsmbConnection connection) {
    return ConnectionValidationResult.success();
  }
}