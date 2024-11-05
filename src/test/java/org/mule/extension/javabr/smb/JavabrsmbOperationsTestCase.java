package org.mule.extension.javabr.smb;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class JavabrsmbOperationsTestCase extends MuleArtifactFunctionalTestCase {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(100000);

  /**
   * Specifies the mule config xml with the flows that are going to be executed in
   * the tests, this file lives in the test resources.
   */
  @Override
  protected String getConfigFile() {
    return "test-mule-config.xml";
  }

  @Test(timeout = 100000)
  public void list() throws Exception {
    String r = (String) flowRunner("list-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

    System.out.println(r);
  }

  @Test(timeout = 100000)
  public void listWithFilter() throws Exception {
    String r = (String) flowRunner("list-withFilter")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

    System.out.println(r);
  }

  @Test(timeout = 100000)
  public void write() throws Exception {
    flowRunner("write-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test(timeout = 100000)
  public void writeLong32k() throws Exception {

    flowRunner("write-long32k")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test(timeout = 100000)
  public void writeLong32kAppend() throws Exception {
    flowRunner("write-long32kAppend")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test(timeout = 100000)
  public void moveTest1() throws Exception {
    flowRunner("move-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test(timeout = 100000)
  public void readAndMoveTest1() throws Exception {
    flowRunner("read-and-then-moveit-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test(timeout = 100000)
  public void readTest1() throws Exception {
    flowRunner("read-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test(timeout = 100000)
  public void deleteTest1() throws Exception {
    flowRunner("delete-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();
  }

  @Test(timeout = 100000)
  public void createDirectoryTest1() throws Exception {
    flowRunner("createDirectory-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();
  }

}
