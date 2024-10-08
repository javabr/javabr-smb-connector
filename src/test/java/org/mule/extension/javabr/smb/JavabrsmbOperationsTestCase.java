package org.mule.extension.javabr.smb;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.junit.Test;

public class JavabrsmbOperationsTestCase extends MuleArtifactFunctionalTestCase {

  /**
   * Specifies the mule config xml with the flows that are going to be executed in
   * the tests, this file lives in the test resources.
   */
  @Override
  protected String getConfigFile() {
    return "test-mule-config.xml";
  }

  @Test
  public void list() throws Exception {
    List payloadValue = (List) flowRunner("list-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();
    assertThat("list return files", (payloadValue.size() > 0));
  }

  @Test
  public void write() throws Exception {

    flowRunner("write-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test
  public void writeLong32k() throws Exception {

    flowRunner("write-long32k")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test
  public void writeLong32kAppend() throws Exception {

    flowRunner("write-long32kAppend")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test
  public void moveTest1() throws Exception {
    flowRunner("move-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test
  public void readTest1() throws Exception {
    flowRunner("read-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();

  }

  @Test
  public void deleteTest1() throws Exception {
    flowRunner("delete-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();
  }

  @Test
  public void createDirectoryTest1() throws Exception {
    flowRunner("createDirectory-test1")
        .run()
        .getMessage()
        .getPayload()
        .getValue();
  }

}
