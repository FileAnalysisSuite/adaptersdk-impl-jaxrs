package io.github.fileanalysissuite.adaptersdk.impls.jaxrs;

import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientAdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientFailureDetails;
import io.github.fileanalysissuite.adaptersdk.convenience.ConvenientItemMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.AdapterDescriptor;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.ItemMetadata;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.OpenStreamFunction;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.CancellationToken;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileDataResultsHandler;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FileListResultsHandler;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.OptionsProvider;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RepositoryItem;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RetrieveFileListRequest;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.RetrieveFilesDataRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FakeRepositoryAdapter implements RepositoryAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeRepositoryAdapter.class);

    private final Path basePath;

    public FakeRepositoryAdapter()
    {
        this(null);
    }

    public FakeRepositoryAdapter(final Path basePath)
    {
        this.basePath = resolveBasePath(basePath);
        LOGGER.info("Base path: {}", this.basePath);
    }

    @Nonnull
    private static Path resolveBasePath(final Path basePath)
    {
        if (basePath == null) {
            LOGGER.info("Base path not specified.  Automatically selecting default filesystem root directory...");
            return FileSystems.getDefault().getRootDirectories().iterator().next();
        } else {
            return basePath;
        }
    }

    @Nonnull
    @Override
    public AdapterDescriptor createDescriptor()
    {
        return ConvenientAdapterDescriptor.create("FakeFileSystem");
    }

    @Override
    public void retrieveFileList(
        final RetrieveFileListRequest request,
        final FileListResultsHandler handler,
        final CancellationToken cancellationToken
    )
    {
        final OptionsProvider repositoryOptions = request.getRepositoryProperties().getRepositoryOptions();
        final Path rootPath = getPath(repositoryOptions);

        try {
            queueAllFiles("-", rootPath, handler, cancellationToken);
        } catch (final IOException ex) {
            handler.registerFailure(ConvenientFailureDetails.create("Failed to queue file hierarchy", ex));
        }
    }

    private static void queueAllFiles(
        final String parentGroupId,
        final Path path,
        final FileListResultsHandler handler,
        final CancellationToken cancellationToken
    ) throws IOException
    {
        final String pathString = path.toString();
        final Iterable<Path> subpaths = Files.list(path)::iterator;

        for (final Path subpath : subpaths) {
            final BasicFileAttributes subpathAttributes
                = Files.readAttributes(subpath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

            if (subpathAttributes.isDirectory()) {
                queueAllFiles(pathString, subpath, handler, cancellationToken);
            } else {
                final ItemMetadata itemMetadata = ConvenientItemMetadata.create(
                    subpath.toString(),
                    subpath.getFileName().toString(),
                    subpathAttributes.size(),
                    subpathAttributes.lastModifiedTime().toInstant());

                handler.queueItem(itemMetadata, parentGroupId, cancellationToken);
            }
        }
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

            final String name = itemMetadata.getName();
            final String itemLocation = itemMetadata.getItemLocation();

            final Path itemLocationPath = getPath(itemLocation);
            final BasicFileAttributes itemLocationAttributes;
            try {
                itemLocationAttributes
                    = Files.readAttributes(itemLocationPath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            } catch (final IOException ex) {
                handler.registerFailure(itemLocation, ConvenientFailureDetails.create("Failed to read item attributes", ex));
                continue;
            }

            final OpenStreamFunction contentStream = () -> new BufferedInputStream(Files.newInputStream(itemLocationPath));

            final ItemMetadata newItemMetadata = ConvenientItemMetadata.builder()
                .name(name)
                .itemLocation(itemLocation)
                .size(itemLocationAttributes.size())
                .createdTime(itemLocationAttributes.creationTime().toInstant())
                .accessedTime(itemLocationAttributes.lastAccessTime().toInstant())
                .modifiedTime(itemLocationAttributes.lastModifiedTime().toInstant())
                .additionalMetadata("IS_SYMBOLIC_LINK", Boolean.toString(itemLocationAttributes.isSymbolicLink()))
                .build();

            handler.queueItem(itemId, contentStream, newItemMetadata, cancellationToken);
        }
    }

    @Nonnull
    private Path getPath(final OptionsProvider repositoryOptions) throws RuntimeException
    {
        return getPath(repositoryOptions.getOption("Path").orElseThrow(() -> new RuntimeException("Path not supplied!")));
    }

    @Nonnull
    private Path getPath(final String path) throws RuntimeException
    {
        final Path suppliedPath = basePath.resolve(path).normalize();
        if (!suppliedPath.startsWith(basePath)) {
            throw new RuntimeException("Adapter not does allows browsing outside " + basePath);
        }

        return suppliedPath;
    }
}