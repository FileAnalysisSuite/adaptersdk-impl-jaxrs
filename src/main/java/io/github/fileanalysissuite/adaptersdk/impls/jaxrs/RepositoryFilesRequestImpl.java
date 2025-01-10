/*
 * Copyright 2022-2025 Open Text.
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

import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryFile;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryProperties;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryFilesRequest;
import java.util.Objects;
import javax.annotation.Nonnull;

final class RepositoryFilesRequestImpl implements RepositoryFilesRequest
{
    private final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataRequest request;

    public RepositoryFilesRequestImpl(
        final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataRequest request
    )
    {
        this.request = Objects.requireNonNull(request);
    }

    @Nonnull
    @Override
    public Iterable<RepositoryFile> getFiles()
    {
        return request.getFiles().stream().<RepositoryFile>map(RepositoryFileImpl::new)::iterator;
    }

    @Nonnull
    @Override
    public RepositoryProperties getRepositoryProperties()
    {
        return new RepositoryPropertiesImpl(request.getRepositoryProperties());
    }
}
