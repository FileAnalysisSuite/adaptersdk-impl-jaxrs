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

import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileDataItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileListItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.ItemMetadata;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryProperties;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataResponse;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class AdapterSDKTest extends AdapterSDKContainer
{
    static RepositoryProperties repositoryProperties;
    static ItemMetadata itemMetadata;

    @BeforeClass
    public static void setup() throws IOException
    {
        //does nothing currently???
        final Map<String, String> configurationOptions = new HashMap<>();
        configurationOptions.put("configurationOptions", "configurationOptionsVal");

        final Map<String, String> repositoryOptions = new HashMap<>();
        repositoryOptions.put("Path", filePathString);

        BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);

        itemMetadata = new ItemMetadata()
            .itemLocation(filePathString + "\\test.txt")
            .name("test.txt")
            .title(null)
            .size(Long.valueOf(9))
            .createdTime(basicFileAttributes.creationTime().toString())
            .accessedTime(basicFileAttributes.lastAccessTime().toString())
            .modifiedTime(basicFileAttributes.lastModifiedTime().toString())
            .version(null)
            .additionalMetadata(Collections.emptyMap());

        repositoryProperties = new RepositoryProperties().configurationOptions(configurationOptions).repositoryOptions(repositoryOptions);
    }

    @Test
    public void testAdapterDescriptor()
    {
        final AdapterDescriptor actualAdapterDescriptor = target("/adapterDescriptor").request().get(AdapterDescriptor.class);
        assertEquals("FakeFileSystem", actualAdapterDescriptor.getAdapterType());
        assertEquals(Collections.emptyList(), actualAdapterDescriptor.getPropertyDefinition());
    }

    @Test
    public void testRetrieveFileList()
    {
        final RetrieveFileListRequest retrieveFileListRequest
            = new RetrieveFileListRequest().additionalFilter("additionalFilter").repositoryProperties(repositoryProperties);
        
        final Entity<RetrieveFileListRequest> retrieveFileListRequestJSON
            = Entity.entity(retrieveFileListRequest, MediaType.APPLICATION_JSON_TYPE);
        
        final RetrieveFileListResponse actualRetrieveFileListResponse
            = target("/retrieveFileList").request().post(retrieveFileListRequestJSON).readEntity(RetrieveFileListResponse.class);

        assertEquals(Collections.emptyList(), actualRetrieveFileListResponse.getFailures());
        
        final List<FileListItem> items = actualRetrieveFileListResponse.getItems();
        final FileListItem item = items.get(0);
        assertEquals(1, items.size());
        assertEquals("-", item.getPartitionHint());
        assertEquals(itemMetadata, item.getItemMetadata());
    }

    @Test
    public void testRetrieveFilesData()
    {
        final RepositoryItem repositoryItem = new RepositoryItem().itemId("test.txt").metadata(itemMetadata);

        final RetrieveFileDataRequest retrieveFileDataRequest
            = new RetrieveFileDataRequest().repositoryProperties(repositoryProperties).items(Arrays.asList(repositoryItem));

        final Entity<RetrieveFileDataRequest> retrieveFileDataRequestJSON
            = Entity.entity(retrieveFileDataRequest, MediaType.APPLICATION_JSON_TYPE);

        final RetrieveFileDataResponse actualRetrieveFileDataResponse
            = target("/retrieveFilesData").request().post(retrieveFileDataRequestJSON).readEntity(RetrieveFileDataResponse.class);

        final List<FileDataItem> items = actualRetrieveFileDataResponse.getItems();
        final FileDataItem item = items.get(0);
        assertEquals(1, items.size());
        assertEquals(repositoryItem.getItemId(), item.getItemId());
        final String expectedFileContent = Base64.getEncoder().encodeToString("Test text".getBytes());
        assertEquals(expectedFileContent, item.getFileContents());
        assertEquals(itemMetadata, item.getItemMetadata());
    }
}
