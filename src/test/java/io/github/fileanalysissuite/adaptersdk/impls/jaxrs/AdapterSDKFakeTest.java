/*
 * Copyright 2022 Micro Focus or one of its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.fileanalysissuite.adaptersdk.impls.jaxrs;

import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FailureDetails;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileDataItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileListItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.ItemMetadata;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryProperties;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataResponse;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListResponse;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.time.Instant;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class AdapterSDKFakeTest extends AdapterSDKFakeContainer
{
    static RepositoryProperties repositoryProperties;
    static ItemMetadata itemMetadata;
    static List<FailureDetails> failureDetails;

    @BeforeClass
    public static void setup() throws IOException
    {
        itemMetadata = new ItemMetadata()
            .itemLocation("Fake path")
            .name("Fake name")
            .title("Fake title")
            .size(9L)
            .createdTime(Instant.MIN.toString())
            .accessedTime(Instant.MAX.toString())
            .modifiedTime(Instant.EPOCH.toString())
            .version(2)
            .additionalMetadata(Collections.singletonMap("Fake key", "Fake value"));

        repositoryProperties = new RepositoryProperties().repositoryOptions(Collections.singletonMap("Path", "Fake path"));

        failureDetails = Arrays.asList(new FailureDetails().itemLocation("Fake item location").message("Failed to read item attributes"));
    }

    @Test
    public void testRetrieveFileListPost()
    {
        final RetrieveFileListRequest retrieveFileListRequest
            = new RetrieveFileListRequest().repositoryProperties(repositoryProperties);

        final Entity<RetrieveFileListRequest> retrieveFileListRequestJSON
            = Entity.entity(retrieveFileListRequest, MediaType.APPLICATION_JSON_TYPE);

        final RetrieveFileListResponse actualRetrieveFileListResponse
            = target("/retrieveFileList").request().post(retrieveFileListRequestJSON).readEntity(RetrieveFileListResponse.class);

        assertEquals(failureDetails, actualRetrieveFileListResponse.getFailures());
        final List<FileListItem> items = actualRetrieveFileListResponse.getItems();
        final FileListItem item = items.get(0);
        assertEquals(1, items.size());
        assertEquals("-", item.getPartitionHint());
        assertEquals(itemMetadata, item.getItemMetadata());
    }

    @Test
    public void testRetrieveFilesDataPost()
    {
        final RepositoryItem repositoryItem = new RepositoryItem().itemId("test.txt").metadata(itemMetadata);

        final RetrieveFileDataRequest retrieveFileDataRequest
            = new RetrieveFileDataRequest().repositoryProperties(repositoryProperties).items(Arrays.asList(repositoryItem));

        final Entity<RetrieveFileDataRequest> retrieveFileDataRequestJSON
            = Entity.entity(retrieveFileDataRequest, MediaType.APPLICATION_JSON_TYPE);

        final RetrieveFileDataResponse actualRetrieveFileDataResponse
            = target("/retrieveFilesData").request().post(retrieveFileDataRequestJSON).readEntity(RetrieveFileDataResponse.class);

        assertEquals(failureDetails, actualRetrieveFileDataResponse.getFailures());
        final List<FileDataItem> items = actualRetrieveFileDataResponse.getItems();
        final FileDataItem item = items.get(0);
        assertEquals(1, items.size());
        assertEquals(repositoryItem.getItemId(), item.getItemId());
        final String expectedFileContent = Base64.getEncoder().encodeToString("Fake contents".getBytes());
        assertEquals(expectedFileContent, item.getFileContents());
        assertEquals(itemMetadata, item.getItemMetadata());
    }
}
