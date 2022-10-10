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

import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientAdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientRepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.TypeCode;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.AdapterDescriptor;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class AdapterSDKTest extends AdapterSDKContainer
{
    static RepositoryProperties repositoryProperties;

    @BeforeClass
    public static void setup()
    {
        final Map<String, String> configurationOptions = new HashMap<>();
        configurationOptions.put("configurationOptions", "configurationOptionsVal");

        final Map<String, String> repositoryOptions = new HashMap<>();
        repositoryOptions.put("repositoryOptions", "repositoryOptionsVal");

        repositoryProperties = new RepositoryProperties().configurationOptions(configurationOptions).repositoryOptions(repositoryOptions);
    }

    @Test
    public void testAdapterDescriptorGet()
    {
        final RepositorySettingDefinition repositorySettingDefinition
            = ConvenientRepositorySettingDefinition.create("name", TypeCode.TEXT, true, true);

        final io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.AdapterDescriptor expectedAdapterDescriptor
            = ConvenientAdapterDescriptor.create("adapterType", repositorySettingDefinition);

        when(adapter.createDescriptor()).thenReturn(expectedAdapterDescriptor);

        final AdapterDescriptor actualAdapterDescriptor = target("/adapterDescriptor").request().get(AdapterDescriptor.class);
        assertEquals(expectedAdapterDescriptor.getAdapterType(), actualAdapterDescriptor.getAdapterType());

        int expectedCount = 0;
        final Iterator<RepositorySettingDefinition> expectedIterator = expectedAdapterDescriptor.getSettingDefinitions().iterator();

        while (expectedIterator.hasNext()) {
            final RepositorySettingDefinition expected = expectedIterator.next();
            final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositorySettingDefinition actual
                = actualAdapterDescriptor.getPropertyDefinition().get(expectedCount);
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.isEncrypted(), Boolean.valueOf(actual.getIsEncrypted()));
            assertEquals(expected.isRequired(), actual.getIsRequired());
            expectedCount++;
        }
        assertEquals(expectedCount, actualAdapterDescriptor.getPropertyDefinition().size());
    }

    @Test
    public void testRetrieveFileListPost()
    {
        doNothing().when(adapter).retrieveFileList(any(), any(), any());

        final RetrieveFileListRequest retrieveFileListRequest
            = new RetrieveFileListRequest().additionalFilter("additionalFilter").repositoryProperties(repositoryProperties);

        final Entity<RetrieveFileListRequest> retrieveFileListRequestJSON
            = Entity.entity(retrieveFileListRequest, MediaType.APPLICATION_JSON_TYPE);

        final RetrieveFileListResponse expectedRetrieveFileListResponse = new RetrieveFileListResponse();

        final RetrieveFileListResponse actualRetrieveFileListResponse
            = target("/retrieveFileList").request().post(retrieveFileListRequestJSON).readEntity(RetrieveFileListResponse.class);

        assertEquals(expectedRetrieveFileListResponse, actualRetrieveFileListResponse);
    }

    @Test
    public void testRetrieveFilesDataPost()
    {
        doNothing().when(adapter).retrieveFilesData(any(), any(), any());

        final ItemMetadata itemMetadata = new ItemMetadata()
            .accessedTime("accessedTime")
            .createdTime("createdTime")
            .itemLocation("itemLocation")
            .modifiedTime("modifiedTime")
            .name("name")
            .size(Long.MIN_VALUE)
            .title("title")
            .version(Integer.SIZE);

        final RepositoryItem repositoryItem = new RepositoryItem().itemId("itemId").metadata(itemMetadata);

        final RetrieveFileDataRequest retrieveFileDataRequest
            = new RetrieveFileDataRequest().repositoryProperties(repositoryProperties).items(Arrays.asList(repositoryItem));

        final Entity<RetrieveFileDataRequest> retrieveFileDataRequestJSON
            = Entity.entity(retrieveFileDataRequest, MediaType.APPLICATION_JSON_TYPE);

        final RetrieveFileDataResponse expectedRetrieveFileDataResponse = new RetrieveFileDataResponse();

        final RetrieveFileDataResponse actualRetrieveFileDataResponse
            = target("/retrieveFilesData").request().post(retrieveFileDataRequestJSON).readEntity(RetrieveFileDataResponse.class);

        assertEquals(expectedRetrieveFileDataResponse, actualRetrieveFileDataResponse);
    }
}
