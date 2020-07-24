package com.step902020.capstone;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class GcsStore {

  @Autowired
  Storage storage;

  /**
   * generates a form for the user to submit profile picture
   * @param projectId id of the current project
   * @param bucketName bucket name preset to spring-bucket-jennysheng
   * @param blobName name of the object matches user email
   * @return html form in String
   */
  public String generateSignedPostPolicyV4(
          String projectId, String bucketName, String blobName) {

    PostPolicyV4.PostFieldsV4 fields =
            PostPolicyV4.PostFieldsV4.newBuilder().AddCustomMetadataField("test", "data").build();

    PostPolicyV4 policy =
            storage.generateSignedPostPolicyV4(
                    BlobInfo.newBuilder(bucketName, blobName).build(), 10, TimeUnit.MINUTES, fields);

    // create html form
    StringBuilder htmlForm =
            new StringBuilder(
                    "<form action='"
                            + policy.getUrl()
                            + "' method='POST' enctype='multipart/form-data'>\n");
    htmlForm.append("<label>Profile Image (After clicking submit the change will take effect after 8 seconds):</label>");
    for (Map.Entry<String, String> entry : policy.getFields().entrySet()) {
      htmlForm.append(
              "  <input name='"
                      + entry.getKey()
                      + "' value='"
                      + entry.getValue()
                      + "' type='hidden' />\n");
    }
    htmlForm.append("  <input type='file' name='file' required/><br />\n");
    htmlForm.append("  <input type='submit' value='Upload File' name='x-ignore-submit' onclick='closeImageForm();'/><br />\n");
    htmlForm.append("</form>\n");

    return htmlForm.toString();
  }

  /**
   * return the byte array of the image
   * @param projectId id of the current project
   * @param bucketName bucket name preset to spring-bucket-jennysheng
   * @param objectName object name matches user email
   * @return content of the image in a byte array
   * @throws StorageException
   */
  public byte[] serveImage(
          String projectId, String bucketName, String objectName) throws StorageException {

    Blob blob = storage.get(BlobId.of(bucketName, objectName));
    if (blob == null) {
      // load default image
      blob = storage.get(BlobId.of(bucketName, "default.jpg"));
    }
    return blob.getContent(Blob.BlobSourceOption.generationMatch());
  }
}