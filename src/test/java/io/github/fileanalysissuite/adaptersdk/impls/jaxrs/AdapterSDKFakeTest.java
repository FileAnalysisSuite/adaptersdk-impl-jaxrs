/*
 * Copyright 2022-2023 Open Text.
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
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileListItem;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FileMetadata;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryFile;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RepositoryProperties;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataRequest;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileDataResponse;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetrieveFileListResponse;
import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.RetryFileInfo;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;

import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

final class AdapterSDKFakeTest extends JerseyTest
{
    private static RepositoryProperties repositoryProperties;
    private static FileMetadata fileMetadata;
    private static List<FailureDetails> failureDetails;
    private RepositoryAdapter fakeAdapter;

    public AdapterSDKFakeTest()
    {
        super(new InMemoryTestContainerFactory());
    }

    @Override
    protected ResourceConfig configure()
    {
        fakeAdapter = new FakeRepositoryAdapter();
        return new ResourceConfig().registerInstances(AdapterSdk.wrap(fakeAdapter));
    }

    @BeforeAll
    static void setup() throws IOException
    {
        fileMetadata = new FileMetadata()
            .fileLocation("Fake path")
            .name("Fake name")
            .title("Fake title")
            .createdTime("0001-01-01T00:00:00Z")
            .accessedTime("9999-12-31T23:59:59Z")
            .modifiedTime("1970-01-01T00:00:00Z")
            .version(2)
            .additionalMetadata(Collections.singletonMap("Fake key", Arrays.asList("Fake value")));

        repositoryProperties = new RepositoryProperties().repositoryOptions(Collections.singletonMap("Path", "Fake path"));

        failureDetails = Arrays.asList(new FailureDetails().fileLocation("Fake file location").message("Failed to read file attributes"));
    }

    @Test
    void testRetrieveFileListPost()
    {
        final RetrieveFileListRequest retrieveFileListRequest
            = new RetrieveFileListRequest().repositoryProperties(repositoryProperties);

        final Entity<RetrieveFileListRequest> retrieveFileListRequestJSON
            = Entity.entity(retrieveFileListRequest, MediaType.APPLICATION_JSON_TYPE);

        final RetrieveFileListResponse actualRetrieveFileListResponse
            = target("/retrieveFileList").request().post(retrieveFileListRequestJSON).readEntity(RetrieveFileListResponse.class);

        assertThat(failureDetails, equalTo(actualRetrieveFileListResponse.getFailures()));
        final List<FileListItem> files = actualRetrieveFileListResponse.getFiles();
        final FileListItem item = files.get(0);
        assertThat(((long) files.size()), equalTo(1L));
        assertThat(item.getPartitionHint(), equalTo("-"));
        assertThat(item.getFileMetadata(), equalTo(fileMetadata));
    }

    @Test
    void testRetrieveFilesDataPost()
    {
        final RepositoryFile repositoryFile = new RepositoryFile().fileId("test.txt").metadata(fileMetadata);

        final RetrieveFileDataRequest retrieveFileDataRequest
            = new RetrieveFileDataRequest().repositoryProperties(repositoryProperties).files(Arrays.asList(repositoryFile));

        final Entity<RetrieveFileDataRequest> retrieveFileDataRequestJSON
            = Entity.entity(retrieveFileDataRequest, MediaType.APPLICATION_JSON_TYPE);

        final RetrieveFileDataResponse actualRetrieveFileDataResponse
            = target("/retrieveFilesData").request().post(retrieveFileDataRequestJSON).readEntity(RetrieveFileDataResponse.class);

        assertThat(actualRetrieveFileDataResponse.getFailures(), equalTo(failureDetails));
        final List<FileDataItem> files = actualRetrieveFileDataResponse.getFiles();
        final FileDataItem file = files.get(0);
        assertThat(files.size(), equalTo(1));
        assertThat(file.getFileId(), equalTo(repositoryFile.getFileId()));
        final String expectedFileContent = Base64.getEncoder().encodeToString("Fake contents".getBytes());
        assertThat(file.getFileContents(), equalTo(expectedFileContent));
        assertThat(file.getFileMetadata(), equalTo(fileMetadata));

        final List<RetryFileInfo> retries = actualRetrieveFileDataResponse.getRetries();
        final RetryFileInfo retry = retries.get(0);

        assertThat(retry.getFileId(), equalTo("Fake file id"));
        assertThat(retry.getRetryAfter(), equalTo(Duration.ofHours(1).toString()));
    }
}
