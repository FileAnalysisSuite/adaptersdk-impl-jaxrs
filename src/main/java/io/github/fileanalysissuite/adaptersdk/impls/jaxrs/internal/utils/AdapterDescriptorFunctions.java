/*
 * Copyright 2022-2024 Open Text.
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
package io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.utils;

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositorySettingDefinition;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class AdapterDescriptorFunctions
{
    private AdapterDescriptorFunctions()
    {
    }

    @Nonnull
    public static io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.AdapterDescriptor convertToModel(
        final AdapterDescriptor descriptor
    )
    {
        Objects.requireNonNull(descriptor);

        final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.AdapterDescriptor modelAdapterDescriptor
            = new io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.AdapterDescriptor()
                .adapterType(descriptor.getAdapterType());

        for (final RepositorySettingDefinition definition : descriptor.getSettingDefinitions()) {
            modelAdapterDescriptor.addPropertyDefinitionItem(RepositorySettingDefinitionFunctions.convertToModel(definition));
        }

        return modelAdapterDescriptor;
    }
}
