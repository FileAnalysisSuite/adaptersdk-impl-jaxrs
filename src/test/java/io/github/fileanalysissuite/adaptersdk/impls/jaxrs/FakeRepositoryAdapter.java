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
package io.github.fileanalysissuite.adaptersdk.impls.jaxrs;

import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientAdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientFailureDetails;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientFileMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.OpenStreamFunction;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.CancellationToken;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileDataResultsHandler;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileListResultsHandler;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.OptionsProvider;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryFile;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RetrieveFileListRequest;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryFilesRequest;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.FileMetadata;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientRepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.TypeCode;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import javax.annotation.Nonnull;

final class FakeRepositoryAdapter implements RepositoryAdapter
{
    @Nonnull
    @Override
    public AdapterDescriptor createDescriptor()
    {
        final RepositorySettingDefinition settings = ConvenientRepositorySettingDefinition.create("Fake name", TypeCode.TEXT, true, true);
        return ConvenientAdapterDescriptor.create("FakeRepositoryAdapter", settings);
    }

    @Override
    public void retrieveFileList(
        final RetrieveFileListRequest request,
        final FileListResultsHandler handler,
        final CancellationToken cancellationToken
    )
    {
        final OptionsProvider repositoryOptions = request.getRepositoryProperties().getRepositoryOptions();

        final FileMetadata fileMetadata = ConvenientFileMetadata.builder()
            .fileLocation(repositoryOptions.getOption("Path").get())
            .name("Fake name")
            .title("Fake title")
            .createdTime(Instant.MIN)
            .accessedTime(Instant.MAX)
            .modifiedTime(Instant.EPOCH)
            .version(2)
            .additionalMetadata("Fake key", "Fake value")
            .build();

        handler.queueFile(fileMetadata, "-", cancellationToken);
        handler.registerFailure("Fake file location", ConvenientFailureDetails.create("Failed to read file attributes"));
    }

    @Override
    public void retrieveFilesData(
        final RepositoryFilesRequest request,
        final FileDataResultsHandler handler,
        final CancellationToken cancellationToken
    )
    {
        for (final RepositoryFile file : request.getFiles()) {
            final String fileId = file.getFileId();
            final FileMetadata fileMetadata = file.getMetadata();
            final OpenStreamFunction contentStream
                = () -> new BufferedInputStream(new ByteArrayInputStream("Fake contents".getBytes(StandardCharsets.UTF_8)));

            handler.queueFile(fileId, contentStream, fileMetadata, cancellationToken);
            handler.registerFailure("Fake file location", ConvenientFailureDetails.create("Failed to read file attributes"));
            handler.retryAfter("Fake file id", Duration.ofHours(1), cancellationToken);
        }
    }
}
