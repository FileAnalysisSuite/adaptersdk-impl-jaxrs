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

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientAdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientRepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.TypeCode;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.ItemMetadata;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryProperties;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataRequest;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertTrue;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AdapterSDKTest extends AdapterSDKContainer
{
    public static AdapterDescriptor adapterDescriptor;
    public static RetrieveFileListRequest retrieveFileListRequest;
    public static RetrieveFileDataRequest retrieveFileDataRequest;

    @BeforeClass
    public static void setup()
    {
        final RepositorySettingDefinition repositorySettingDefinition
            = ConvenientRepositorySettingDefinition.create("MARK", TypeCode.TEXT, true, true);

        adapterDescriptor = ConvenientAdapterDescriptor.create("TEST", repositorySettingDefinition);

        final Map<String, String> configurationOptions = new HashMap<>();
        configurationOptions.put("a", "b");
        configurationOptions.put("c", "d");

        final Map<String, String> repositoryOptions = new HashMap<>();
        repositoryOptions.put("e", "f");
        repositoryOptions.put("g", "h");

        final RepositoryProperties repositoryProperties
            = new RepositoryProperties().configurationOptions(configurationOptions).repositoryOptions(repositoryOptions);

        retrieveFileListRequest
            = new RetrieveFileListRequest().additionalFilter("additionalFilter").repositoryProperties(repositoryProperties);

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

        retrieveFileDataRequest
            = new RetrieveFileDataRequest().repositoryProperties(repositoryProperties).items(Arrays.asList(repositoryItem));
    }

    @Test
    public void testAdapterDescriptorGet()
    {
        Mockito.when(adapter.createDescriptor()).thenReturn(adapterDescriptor);

        final Response response = target("/adapterDescriptor").request().get();
        final JsonObject jsonResponse = JsonParser.parseString(response.readEntity(String.class)).getAsJsonObject();

        assertTrue(jsonResponse.size() == 2);
        assertTrue("TEST".equals(jsonResponse.get("adapterType").getAsString()));

        final JsonArray propertyDefinition = jsonResponse.getAsJsonArray("propertyDefinition");
        assertTrue(propertyDefinition.size() == 1);

        final JsonObject propertyDefinitionVals = propertyDefinition.get(0).getAsJsonObject();

        assertTrue("MARK".equals(propertyDefinitionVals.get("name").getAsString()));
        assertTrue(propertyDefinitionVals.get("isRequired").getAsBoolean() == true);
        assertTrue("true".equals(propertyDefinitionVals.get("isEncrypted").getAsString()));
        assertTrue(response.getStatus() == 200);
    }

    @Test
    public void testRetrieveFileListPost()
    {
        Mockito.doNothing().when(adapter).retrieveFileList(any(), any(), any());

        final Entity<RetrieveFileListRequest> rEntity = Entity.entity(retrieveFileListRequest, MediaType.APPLICATION_JSON_TYPE);

        final Response response = target("/retrieveFileList").request().post(rEntity);
        final JsonObject json = JsonParser.parseString(response.readEntity(String.class)).getAsJsonObject();

        assertTrue(json.size() == 2);
        assertTrue(json.get("items").getAsJsonArray().size() == 0);
        assertTrue(json.get("failures").getAsJsonArray().size() == 0);
    }

    @Test
    public void testRetrieveFilesDataPost()
    {
        Mockito.doNothing().when(adapter).retrieveFilesData(any(), any(), any());

        final Entity<RetrieveFileDataRequest> rEntity = Entity.entity(retrieveFileDataRequest, MediaType.APPLICATION_JSON_TYPE);

        final Response response = target("/retrieveFilesData").request().post(rEntity);
        final JsonObject json = JsonParser.parseString(response.readEntity(String.class)).getAsJsonObject();

        assertTrue(json.size() == 2);
        assertTrue(json.get("items").getAsJsonArray().size() == 0);
        assertTrue(json.get("failures").getAsJsonArray().size() == 0);
    }
}
