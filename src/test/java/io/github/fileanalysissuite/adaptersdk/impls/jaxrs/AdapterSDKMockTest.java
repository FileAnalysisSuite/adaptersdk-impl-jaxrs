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
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.TypeCode;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class AdapterSDKMockTest extends JerseyTest
{
    private RepositoryAdapter mockAdapter;

    public AdapterSDKMockTest()
    {
        super(new InMemoryTestContainerFactory());
    }

    @Override
    protected ResourceConfig configure()
    {
        mockAdapter = mock(RepositoryAdapter.class);
        return new ResourceConfig().registerInstances(AdapterSdk.wrap(mockAdapter));
    }

    @Test
    void testAdapterDescriptorGet()
    {
        final io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositorySettingDefinition repoSettingDefinition
            = ConvenientRepositorySettingDefinition.create("Fake name", TypeCode.TEXT, true, true);

        final io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.AdapterDescriptor descriptor
            = ConvenientAdapterDescriptor.create("Fake adapter type", repoSettingDefinition);

        when(mockAdapter.createDescriptor()).thenReturn(descriptor);

        final AdapterDescriptor actualDescriptor = target("/adapterDescriptor").request().get(AdapterDescriptor.class);

        final RepositorySettingDefinition expectedRepoSettingDefinition = new RepositorySettingDefinition()
            .name("Fake name")
            .isEncrypted("true")
            .isRequired(true);

        final AdapterDescriptor expectedDescriptor = new AdapterDescriptor()
            .adapterType("Fake adapter type")
            .addPropertyDefinitionItem(expectedRepoSettingDefinition);

        assertThat(actualDescriptor, equalTo(expectedDescriptor));
    }
}
