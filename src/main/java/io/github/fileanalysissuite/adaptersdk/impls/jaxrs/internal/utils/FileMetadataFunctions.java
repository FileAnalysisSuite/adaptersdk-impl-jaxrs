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

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.FileMetadata;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public final class FileMetadataFunctions
{
    private FileMetadataFunctions()
    {
    }

    @Nonnull
    public static io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileMetadata convertToModel(
        final FileMetadata fileMetadata
    )
    {
        Objects.requireNonNull(fileMetadata);

        final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileMetadata modelFileMetadata
            = new io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileMetadata()
                .fileLocation(fileMetadata.getFileLocation())
                .name(fileMetadata.getName())
                .title(fileMetadata.getTitle())
                .createdTime(Optional.ofNullable(fileMetadata.getCreatedTime())
                    .map(InstantFunctions::toRFC3339DateTimeString).orElse(null))
                .accessedTime(Optional.ofNullable(fileMetadata.getAccessedTime())
                    .map(InstantFunctions::toRFC3339DateTimeString).orElse(null))
                .modifiedTime(InstantFunctions.toRFC3339DateTimeString(fileMetadata.getModifiedTime()))
                .version(fileMetadata.getVersion());

        final Map<String, ? extends Iterable<String>> additionalMetadata = fileMetadata.getAdditionalMetadata();
        if (additionalMetadata != null) {
            for (final Map.Entry<String, ? extends Iterable<String>> entry : additionalMetadata.entrySet()) {
                modelFileMetadata.putAdditionalMetadataItem(
                    entry.getKey(),
                    StreamSupport.stream(entry.getValue().spliterator(), false).collect(Collectors.toList()));
            }
        }

        return modelFileMetadata;
    }
}
