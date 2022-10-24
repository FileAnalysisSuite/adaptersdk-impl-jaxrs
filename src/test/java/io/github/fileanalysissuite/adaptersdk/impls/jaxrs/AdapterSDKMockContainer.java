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

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;

import static org.mockito.Mockito.mock;

public class AdapterSDKMockContainer extends JerseyTest
{
    RepositoryAdapter mockAdapter;

    public AdapterSDKMockContainer()
    {
        super(new InMemoryTestContainerFactory());
    }

    @Override
    protected ResourceConfig configure()
    {
        mockAdapter = mock(RepositoryAdapter.class);
        return new ResourceConfig().register(new AdapterApiImpl(mockAdapter));
    }
}
