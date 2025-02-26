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

import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FailureDetails;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileListItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListResponse;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.utils.FileMetadataFunctions;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.FileMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.CancellationToken;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileListResultsHandler;
import java.util.Objects;

final class FileListResultsHandlerImpl extends FailureRegistrationImpl implements FileListResultsHandler
{
    private final RetrieveFileListResponse response;

    public FileListResultsHandlerImpl(final RetrieveFileListResponse response)
    {
        this.response = Objects.requireNonNull(response);
    }

    @Override
    public void queueFile(
        final FileMetadata fileMetadata,
        final String partitionHint,
        final CancellationToken cancellationToken
    )
    {
        Objects.requireNonNull(fileMetadata);
        Objects.requireNonNull(partitionHint);
        Objects.requireNonNull(cancellationToken);

        response.addFilesItem(new FileListItem()
            .fileMetadata(FileMetadataFunctions.convertToModel(fileMetadata))
            .partitionHint(partitionHint));
    }

    @Override
    protected void addFailuresItem(final FailureDetails failureDetails)
    {
        response.addFailuresItem(failureDetails);
    }
}
