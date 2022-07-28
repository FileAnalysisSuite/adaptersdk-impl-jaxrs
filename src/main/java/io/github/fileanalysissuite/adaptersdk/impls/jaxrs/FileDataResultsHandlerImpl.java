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
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.utils.ItemMetadataFunctions;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.CancellationToken;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.FileContents;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.ItemMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.OpenStreamFunction;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileDataResultsHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
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
    public void queueItem(
        final String itemId,
        final FileContents fileContents,
        final ItemMetadata itemMetadata,
        final CancellationToken cancellationToken
    )
    {
        Objects.requireNonNull(itemId);
        Objects.requireNonNull(fileContents);
        Objects.requireNonNull(itemMetadata);
        Objects.requireNonNull(cancellationToken);

        // TODO: We're going to have to change this as we shouldn't be putting the entire contents of the file into a string
        response.addItemsItem(new FileDataItem()
            .itemId(itemId)
            .fileContents(toByteArray(fileContents.getContentStream(), itemId).map(Base64.getEncoder()::encodeToString).orElse(null))
            .metadata(ItemMetadataFunctions.convertToModel(itemMetadata)));
    }

    private static Optional<byte[]> toByteArray(final OpenStreamFunction openStreamFunction, final String itemId)
    {
        try (final InputStream inputStream = openStreamFunction.openInputStream()) {
            return Optional.of(IOUtils.toByteArray(inputStream));
        } catch (final IOException ex) {
            LOGGER.warn("Exception reading {}", itemId, ex);

            // TODO: What should we do here?  Add a failure to the response?
            return Optional.empty();
        }
    }

    @Override
    protected void addFailuresItem(final FailureDetails failureDetails)
    {
        response.addFailuresItem(failureDetails);
    }
}
