package org.mule.extension.javabr.smb.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.msfscc.fileinformation.FileStandardInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.protocol.commons.EnumWithValue;
import com.hierynomus.protocol.commons.buffer.Buffer.BufferException;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

public class JavabrsmbOperations {

  /**
   * Writes data to a file on the SMB share. Depending on the WritingType, this
   * method can either overwrite the file or append to it.
   *
   * @param filename      the name of the file to write to
   * @param content       the content to write as an InputStream
   * @param writingType   determines whether to overwrite or append to the file
   * @param configuration the SMB configuration
   * @param connection    the SMB connection
   * @throws IOException if an I/O error occurs
   */
  @MediaType(value = ANY, strict = false)
  public void write(String filename,
      @Optional(defaultValue = "#[payload]") TypedValue<InputStream> content,
      WritingType writingType,
      @Config JavabrsmbConfiguration configuration,
      @org.mule.runtime.extension.api.annotation.param.Connection JavabrsmbConnection connection) throws IOException {
    DiskShare share = (DiskShare) connection.getSession();
    File file = null;
    long offset = 0L;
    FileStandardInformation fileInfo = null;
    if (writingType == WritingType.OVERWRITE) {
      file = share.openFile(
          filename,
          EnumSet.of(AccessMask.GENERIC_WRITE),
          null,
          SMB2ShareAccess.ALL,
          SMB2CreateDisposition.FILE_OVERWRITE_IF,
          EnumSet.of(SMB2CreateOptions.FILE_WRITE_THROUGH));
    } else if (writingType == WritingType.APPEND) {
      file = share.openFile(
          filename,
          EnumSet.of(AccessMask.GENERIC_READ, AccessMask.GENERIC_WRITE),
          null,
          SMB2ShareAccess.ALL,
          SMB2CreateDisposition.FILE_OPEN,
          null);
      fileInfo = file.getFileInformation(FileStandardInformation.class);
      offset = fileInfo.getEndOfFile();
    }

    byte[] buffer = new byte[Integer.parseInt(configuration.getBufferSize())];
    int bytesRead;

    while ((bytesRead = content.getValue().read(buffer)) != -1) {
      file.write(buffer, offset, 0, bytesRead);
      offset += bytesRead;
    }
    file.close();
  }

  /**
   * Moves a file from the source location to the target location. This is done
   * by reading the source file, writing it to the target, and then deleting the
   * source.
   *
   * @param sourceFilename the source file to move
   * @param targetFilename the target location to move the file to
   * @param configuration  the SMB configuration
   * @param connection     the SMB connection
   * @throws BufferException if an SMB buffer error occurs
   * @throws IOException
   */
  @MediaType(value = ANY, strict = false)
  public void move(String sourceFilename, String targetFilename,
      @Config JavabrsmbConfiguration configuration,
      @org.mule.runtime.extension.api.annotation.param.Connection JavabrsmbConnection connection)
      throws BufferException, IOException {
    DiskShare share = (DiskShare) connection.getSession();

    File sourceFile = share.openFile(
        sourceFilename,
        EnumSet.of(AccessMask.GENERIC_READ),
        null,
        SMB2ShareAccess.ALL,
        SMB2CreateDisposition.FILE_OPEN,
        null);

    write(targetFilename, new TypedValue<InputStream>(sourceFile.getInputStream(),
        DataType.BYTE_ARRAY), WritingType.OVERWRITE,
        configuration, connection);

    sourceFile.close();
    delete(sourceFilename, configuration, connection);
  }

  /**
   * Deletes a file from the SMB share.
   *
   * @param filename      the name of the file to delete
   * @param configuration the SMB configuration
   * @param connection    the SMB connection
   */
  @MediaType(value = ANY, strict = false)
  public void delete(String filename,
      @Config JavabrsmbConfiguration configuration,
      @org.mule.runtime.extension.api.annotation.param.Connection JavabrsmbConnection connection) {
    DiskShare share = (DiskShare) connection.getSession();
    share.rm(filename);
  }

  private static     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

  private static String convertToZulu(long epochMillis) {
    Instant instant = Instant.ofEpochMilli(epochMillis);
    return formatter.format(instant);
}

  /**
   * Lists all files in a given directory on the SMB share.
   *
   * @param path          the path of the directory to list files from
   * @param filter        regex to filter files
   * @param configuration the SMB configuration
   * @param connection    the SMB connection
   * @return a list of file names in the directory
   * @throws JsonProcessingException
   */
  @MediaType(MediaType.APPLICATION_JSON)
  public String list(String path, @Optional(defaultValue = "*.*") String fileMask,
      @Config JavabrsmbConfiguration configuration,
      @org.mule.runtime.extension.api.annotation.param.Connection JavabrsmbConnection connection)
      throws JsonProcessingException {
    DiskShare share = (DiskShare) connection.getSession();

    List<FileIdBothDirectoryInformation> files = share.list(path, fileMask);

    // Create a JSON Array (List of JSON objects)
    List<ObjectNode> jsonFiles = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();

    for (FileIdBothDirectoryInformation file : files) {
      // Create JSON Object for each file
      ObjectNode jsonFile = objectMapper.createObjectNode();

      jsonFile.put("name", file.getFileName());

      boolean isDirectory = (EnumWithValue.EnumUtils.isSet(file.getFileAttributes(),
          FileAttributes.FILE_ATTRIBUTE_DIRECTORY));
      jsonFile.put("isDirectory", isDirectory);

      jsonFile.put("createdAt", convertToZulu(file.getCreationTime().toEpochMillis()));
      jsonFile.put("updatedAt", convertToZulu(file.getLastWriteTime().toEpochMillis()));
      jsonFile.put("size", file.getEndOfFile());
      jsonFiles.add(jsonFile);
    }

    // Convert the list of JSON objects to a JSON array (as string for example)
    String jsonArray = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonFiles);

    return jsonArray;
  }

  /**
   * Reads a file from the SMB share and returns its content as an InputStream.
   *
   * @param filename      the name of the file to read
   * @param configuration the SMB configuration
   * @param connection    the SMB connection
   * @return the content of the file as an InputStream
   */
  @MediaType(value = ANY, strict = false)
  public InputStream read(String filename, @Config JavabrsmbConfiguration configuration,
      @org.mule.runtime.extension.api.annotation.param.Connection JavabrsmbConnection connection) {
    DiskShare share = (DiskShare) connection.getSession();
    File sourceFile = share.openFile(
        filename,
        EnumSet.of(AccessMask.GENERIC_READ),
        null,
        SMB2ShareAccess.ALL,
        SMB2CreateDisposition.FILE_OPEN,
        null);
    return sourceFile.getInputStream();
  }

  /**
   * Creates a directory in the SMB share.
   *
   * @param directory     the name of the directory to create
   * @param configuration the SMB configuration
   * @param connection    the SMB connection
   */
  @MediaType(value = ANY, strict = false)
  public void create_directory(String directory,
      @Config JavabrsmbConfiguration configuration,
      @org.mule.runtime.extension.api.annotation.param.Connection JavabrsmbConnection connection) {
    DiskShare share = (DiskShare) connection.getSession();
    share.mkdir(directory);
  }
}
