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

import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FailureDetails;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileDataItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataResponse;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetryFileInfo;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.utils.FileMetadataFunctions;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.FileMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.OpenStreamFunction;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.CancellationToken;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileDataResultsHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FileDataResultsHandlerImpl extends FailureRegistrationImpl implements FileDataResultsHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDataResultsHandlerImpl.class);

    private final RetrieveFileDataResponse response;

    public FileDataResultsHandlerImpl(final RetrieveFileDataResponse response)
    {
        this.response = Objects.requireNonNull(response);
    }

    @Override
    public void queueFile(
        final String fileId,
        final OpenStreamFunction fileContents,
        final FileMetadata fileMetadata,
        final CancellationToken cancellationToken
    )
    {
        Objects.requireNonNull(fileId);
        Objects.requireNonNull(fileContents);
        Objects.requireNonNull(fileMetadata);
        Objects.requireNonNull(cancellationToken);

        // TODO: We're going to have to change this as we shouldn't be putting the entire contents of the file into a string
        response.addFilesItem(new FileDataItem()
            .fileId(fileId)
            .fileContents(Base64.getEncoder().encodeToString(toByteArray(fileContents, fileId)))
            .fileMetadata(FileMetadataFunctions.convertToModel(fileMetadata)));
    }

    @Override
    public void retryAfter(final String fileId, final Duration delay, final CancellationToken cancellationToken)
    {
        Objects.requireNonNull(fileId);
        Objects.requireNonNull(delay);
        Objects.requireNonNull(cancellationToken);

        response.addRetriesItem(new RetryFileInfo()
            .fileId(fileId)
            .retryAfter(delay.toString()));
    }

    @Nonnull
    private static byte[] toByteArray(final OpenStreamFunction openStreamFunction, final String fileId)
    {
        try (final InputStream inputStream = openStreamFunction.openInputStream()) {
            return IOUtils.toByteArray(inputStream);
        } catch (final IOException ex) {
            LOGGER.warn("Exception reading {}", fileId, ex);

            // TODO: What should we do here?  Add a failure to the response?
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    protected void addFailuresItem(final FailureDetails failureDetails)
    {
        response.addFailuresItem(failureDetails);
    }
}
