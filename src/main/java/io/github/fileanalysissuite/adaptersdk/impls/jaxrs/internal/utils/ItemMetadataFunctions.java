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
package io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.utils;

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.ItemMetadata;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public final class ItemMetadataFunctions
{
    private ItemMetadataFunctions()
    {
    }

    @Nonnull
    public static io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.ItemMetadata convertToModel(
        final ItemMetadata itemMetadata
    )
    {
        Objects.requireNonNull(itemMetadata);

        final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.ItemMetadata modelItemMetadata
            = new io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.ItemMetadata()
                .itemLocation(itemMetadata.getItemLocation())
                .name(itemMetadata.getName())
                .title(itemMetadata.getTitle())
                .size(itemMetadata.getSize())
                .createdTime(Optional.ofNullable(itemMetadata.getCreatedTime()).map(t -> toRFC3339DateTimeString(t)).orElse(null))
                .accessedTime(Optional.ofNullable(itemMetadata.getAccessedTime()).map(t -> toRFC3339DateTimeString(t)).orElse(null))
                .modifiedTime(toRFC3339DateTimeString(itemMetadata.getModifiedTime()))
                .version(itemMetadata.getVersion());

        // TODO: What are we meant to do with these?
        final String contentHash = itemMetadata.getContentHash();
        final String metadataHash = itemMetadata.getMetadataHash();

        final Map<String, Serializable> additionalMetadata = itemMetadata.getAdditionalMetadata();
        if (additionalMetadata != null) {
            for (final Map.Entry<String, Serializable> entry : additionalMetadata.entrySet()) {
                modelItemMetadata.putAdditionalMetadataItem(entry.getKey(), SerializableFunctions.convertToString(entry.getValue()));
            }
        }

        return modelItemMetadata;
    }

    private static String toRFC3339DateTimeString(final Instant time)
    {
        if (time.isBefore(Instant.ofEpochSecond(-62167219200L))) {
            return "0000-01-01T00:00:00Z";
        } else if (time.isAfter(Instant.ofEpochSecond(253402300799L))) {
            return "9999-12-31T23:59:59Z";
        } else {
            return time.toString();
        }
    }
}
