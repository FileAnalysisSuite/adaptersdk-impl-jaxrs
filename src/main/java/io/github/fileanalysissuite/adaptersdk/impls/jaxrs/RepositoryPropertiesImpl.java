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

import io.github.fileanalysissuite.adaptersdk.interfaces.framework.OptionsProvider;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryProperties;
import java.util.Optional;
import javax.annotation.Nonnull;

final class RepositoryPropertiesImpl implements RepositoryProperties
{
    private final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryProperties properties;

    public RepositoryPropertiesImpl(
        final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryProperties properties
    )
    {
        this.properties = Optional.ofNullable(properties)
            .orElseGet(io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryProperties::new);
    }

    @Nonnull
    @Override
    public OptionsProvider getConfigurationOptions()
    {
        return new OptionsProviderImpl(properties.getConfigurationOptions());
    }

    @Nonnull
    @Override
    public OptionsProvider getRepositoryOptions()
    {
        return new OptionsProviderImpl(properties.getRepositoryOptions());
    }
}
