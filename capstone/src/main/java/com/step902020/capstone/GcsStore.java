package com.step902020.capstone;

import com.google.cloud.storage.*;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GcsStore {
  public static String generateSignedPostPolicyV4(
          String projectId, String bucketName, String blobName) {

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    storage.delete(bucketName, blobName);

    PostPolicyV4.PostFieldsV4 fields =
            PostPolicyV4.PostFieldsV4.newBuilder().AddCustomMetadataField("test", "data").build();

    PostPolicyV4 policy =
            storage.generateSignedPostPolicyV4(
                    BlobInfo.newBuilder(bucketName, blobName).build(), 10, TimeUnit.MINUTES, fields);

    StringBuilder htmlForm =
            new StringBuilder(
                    "<form action='"
                            + policy.getUrl()
                            + "' method='POST' enctype='multipart/form-data'>\n");
    for (Map.Entry<String, String> entry : policy.getFields().entrySet()) {
      htmlForm.append(
              "  <input name='"
                      + entry.getKey()
                      + "' value='"
                      + entry.getValue()
                      + "' type='hidden' />\n");
    }
    htmlForm.append("  <input type='file' name='file'/><br />\n");
    htmlForm.append("  <input type='submit' value='Upload File' name='submit'/><br />\n");
    htmlForm.append("</form>\n");

    return htmlForm.toString();
  }

  public static byte[] generateV4GetObjectSignedUrl(
          String projectId, String bucketName, String objectName) throws StorageException {

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    Blob blob = storage.get(BlobId.of(bucketName, objectName));
    return blob.getContent(Blob.BlobSourceOption.generationMatch());
  }
}