package org.mule.extension.javabr.smb.internal;

import java.io.IOException;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.Share;

/**
 * This class represents an extension connection just as example (there is no
 * real connection with anything here c:).
 */
public final class JavabrsmbConnection {

  private final String id;

  private Connection connection = null;

  private Share shareConnection = null;

  public JavabrsmbConnection(String domain, String host, String share, String username, String password)
      throws IOException {
    this.id = domain + ":" + host + ":" + share + ":" + username;
    connect(domain, host, share, username, password);

  }

  private void connect(String domain, String host, String share, String username, String password) throws IOException {
    try {
      SMBClient smbClient = new SMBClient();
      connection = smbClient.connect(host);
      AuthenticationContext ac = new AuthenticationContext(username, password.toCharArray(), domain);
      Session session = connection.authenticate(ac);
      shareConnection = session.connectShare(share);
    } catch (IOException ioe) {
      throw ioe;
    }
  }

  public String getId() {
    return id;
  }

  public void invalidate() throws IOException {
    connection.close();
  }

  public Share getSession() {
    return shareConnection;
  }
}