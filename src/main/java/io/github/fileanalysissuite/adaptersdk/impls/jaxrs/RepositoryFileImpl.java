/*
 * Copyright 2022-2022 Open Text.
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

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.FileMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryFile;
import java.util.Objects;
import javax.annotation.Nonnull;

final class RepositoryFileImpl implements RepositoryFile
{
    private final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryFile file;

    public RepositoryFileImpl(
        final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryFile file
    )
    {
        this.file = Objects.requireNonNull(file);
    }

    @Nonnull
    @Override
    public String getFileId()
    {
        return file.getFileId();
    }

    @Nonnull
    @Override
    public FileMetadata getMetadata()
    {
        return new FileMetadataImpl(file.getMetadata());
    }
}
