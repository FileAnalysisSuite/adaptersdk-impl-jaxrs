package io.github.fileanalysissuite.adaptersdk.impls.jaxrs;

import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientAdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientFailureDetails;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientItemMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.OpenStreamFunction;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.CancellationToken;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileDataResultsHandler;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileListResultsHandler;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.OptionsProvider;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryItem;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RetrieveFileListRequest;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RetrieveFilesDataRequest;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.ItemMetadata;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientRepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositorySettingDefinition;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.TypeCode;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.time.Instant;

import javax.annotation.Nonnull;

public final class FakeRepositoryAdapter implements RepositoryAdapter
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
        ItemMetadata itemMetadata = ConvenientItemMetadata.builder()
            .itemLocation(repositoryOptions.getOption("Path").get())
            .name("Fake name")
            .title("Fake title")
            .size(9L)
            .createdTime(Instant.MIN)
            .accessedTime(Instant.MAX)
            .modifiedTime(Instant.EPOCH)
            .version(2)
            .additionalMetadata("Fake key", "Fake value")
            .build();

        handler.queueItem(itemMetadata, "-", cancellationToken);
        handler.registerFailure("Fake item location", ConvenientFailureDetails.create("Failed to read item attributes"));
    }

    @Override
    public void retrieveFilesData(
        final RetrieveFilesDataRequest request,
        final FileDataResultsHandler handler,
        final CancellationToken cancellationToken
    )
    {
        for (final RepositoryItem item : request.getItems()) {
            final String itemId = item.getItemId();
            final ItemMetadata itemMetadata = item.getMetadata();
            final OpenStreamFunction contentStream = () -> new BufferedInputStream(new ByteArrayInputStream("Fake contents".getBytes()));

            handler.queueItem(itemId, contentStream, itemMetadata, cancellationToken);
            handler.registerFailure("Fake item location", ConvenientFailureDetails.create("Failed to read item attributes"));
        }
    }
}