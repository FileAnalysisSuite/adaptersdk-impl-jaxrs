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

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.ItemMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryItem;
import java.util.Objects;
import javax.annotation.Nonnull;

final class RepositoryItemImpl implements RepositoryItem
{
    private final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryItem item;

    public RepositoryItemImpl(
        final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryItem item
    )
    {
        this.item = Objects.requireNonNull(item);
    }

    @Nonnull
    @Override
    public String getItemId()
    {
        return item.getItemId();
    }

    @Nonnull
    @Override
    public ItemMetadata getMetadata()
    {
        return new ItemMetadataImpl(item.getMetadata());
    }
}
