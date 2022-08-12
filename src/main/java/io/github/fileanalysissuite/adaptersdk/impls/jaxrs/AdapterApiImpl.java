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

import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.api.AdapterApi;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataResponse;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListResponse;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.utils.AdapterDescriptorFunctions;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;
import java.util.Objects;
import javax.annotation.Nonnull;

final class AdapterApiImpl implements AdapterApi
{
    private final RepositoryAdapter repositoryAdapter;

    public AdapterApiImpl(final RepositoryAdapter repositoryAdapter)
    {
        this.repositoryAdapter = Objects.requireNonNull(repositoryAdapter);
    }

    @Nonnull
    @Override
    public AdapterDescriptor adapterDescriptorGet()
    {
        return AdapterDescriptorFunctions.convertToModel(repositoryAdapter.createDescriptor());
    }

    @Nonnull
    @Override
    public RetrieveFileListResponse retrieveFileListPost(final RetrieveFileListRequest request)
    {
        final RetrieveFileListResponse response = new RetrieveFileListResponse();

        repositoryAdapter.retrieveFileList(
            new RetrieveFileListRequestImpl(request),
            new FileListResultsHandlerImpl(response),
            StandardCancellationTokens.UNCANCELABLE);

        return response;
    }

    @Nonnull
    @Override
    public RetrieveFileDataResponse retrieveFilesDataPost(final RetrieveFileDataRequest request)
    {
        final RetrieveFileDataResponse response = new RetrieveFileDataResponse();

        repositoryAdapter.retrieveFilesData(
            new RetrieveFilesDataRequestImpl(request),
            new FileDataResultsHandlerImpl(response),
            StandardCancellationTokens.UNCANCELABLE);

        return response;
    }
}
