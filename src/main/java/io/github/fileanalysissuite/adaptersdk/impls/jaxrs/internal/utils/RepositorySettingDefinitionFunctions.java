/*
 * Copyright 2022-2026 Open Text.
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

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.TypeCode;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class RepositorySettingDefinitionFunctions
{
    private RepositorySettingDefinitionFunctions()
    {
    }

    @Nonnull
    public static io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositorySettingDefinition
        convertToModel(final RepositorySettingDefinition definition)
    {
        Objects.requireNonNull(definition);

        // TODO: What are we meant to do with the type code?
        final TypeCode typeCode = definition.getTypeCode();

        return new io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositorySettingDefinition()
            .name(definition.getName())
            .isRequired(definition.isRequired())
            .isEncrypted(definition.isEncrypted() ? "true" : "false");  // TODO: Why is this a string?
    }
}
