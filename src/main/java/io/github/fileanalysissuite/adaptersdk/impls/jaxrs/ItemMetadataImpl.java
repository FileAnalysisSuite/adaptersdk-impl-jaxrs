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
import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

final class ItemMetadataImpl implements ItemMetadata
{
    private final io.github.fileanalysissuite.adapters.rest.contract.serverstubs.jaxrs.model.ItemMetadata metadata;

    public ItemMetadataImpl(
        final io.github.fileanalysissuite.adapters.rest.contract.serverstubs.jaxrs.model.ItemMetadata metadata
    )
    {
        this.metadata = Objects.requireNonNull(metadata);
    }

    @Nonnull
    @Override
    public String getItemLocation()
    {
        return metadata.getItemLocation();
    }

    @Nonnull
    @Override
    public String getName()
    {
        return metadata.getName();
    }

    @Override
    public String getTitle()
    {
        return metadata.getTitle();
    }

    @Override
    public long getSize()
    {
        return metadata.getSize();
    }

    @Override
    public Instant getCreatedTime()
    {
        // TODO: Check the actual format - better still put it in the contract
        return Optional.ofNullable(metadata.getCreatedTime()).map(Instant::parse).orElse(null);
    }

    @Override
    public Instant getAccessedTime()
    {
        // TODO: Check the actual format - better still put it in the contract
        return Optional.ofNullable(metadata.getAccessedTime()).map(Instant::parse).orElse(null);
    }

    @Nonnull
    @Override
    public Instant getModifiedTime()
    {
        return Instant.parse(metadata.getModifiedTime());
    }

    @Override
    public Integer getVersion()
    {
        return metadata.getVersion();
    }

    @Override
    public String getContentHash()
    {
        // TODO: This doesn't seem to be in the contract!
        return null;
    }

    @Override
    public String getMetadataHash()
    {
        // TODO: This doesn't seem to be in the contract!
        return null;
    }

    @Nonnull
    @Override
    public Map<String, Serializable> getAdditionalMetadata()
    {
        final Map<String, String> additionalMetadata = metadata.getAdditionalMetadata();

        return additionalMetadata == null
            ? Collections.emptyMap()
            : Collections.unmodifiableMap(additionalMetadata);
    }
}
