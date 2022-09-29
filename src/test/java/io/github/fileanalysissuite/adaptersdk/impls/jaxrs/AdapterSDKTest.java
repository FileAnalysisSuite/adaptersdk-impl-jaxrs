package io.github.fileanalysissuite.adaptersdk.impls.jaxrs;
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

import com.google.gson.JsonArray;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientAdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientRepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.TypeCode;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.Mockito;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AdapterSDKTest extends JerseyTest
{
    public AdapterSDKTest(){
        super(new InMemoryTestContainerFactory());
    }

    RepositoryAdapter adapter;
    @Override
    protected ResourceConfig configure() {
        adapter = Mockito.mock(RepositoryAdapter.class);
        return new ResourceConfig().register(new AdapterApiImpl(adapter));
    }

    @Test
    public void testInMemoryConnectorGet() {
        RepositorySettingDefinition settingDefinition = ConvenientRepositorySettingDefinition
            .create("MARK", TypeCode.TEXT, true, true);

        AdapterDescriptor adapterDescriptor = ConvenientAdapterDescriptor.create("TEST", settingDefinition);
        Mockito.when(adapter.createDescriptor()).thenReturn(adapterDescriptor);
        
        final Response response = target("/adapterDescriptor").request().get();
        final JsonObject json = JsonParser.parseString(response.readEntity(String.class)).getAsJsonObject();
        
        assertTrue(json.size() == 2);
        assertTrue("TEST".equals(json.get("adapterType").getAsString()));

        final JsonArray propertyDefinition = json.getAsJsonArray("propertyDefinition");
        assertTrue(propertyDefinition.size() == 1);

        final JsonObject propertyDefinitionVals = propertyDefinition.get(0).getAsJsonObject();
        
        assertTrue("MARK".equals(propertyDefinitionVals.get("name").getAsString()));
        assertTrue(propertyDefinitionVals.get("isRequired").getAsBoolean() == true);
        assertTrue("true".equals(propertyDefinitionVals.get("isEncrypted").getAsString()));
        assertTrue(response.getStatus() == 200);
    }
}