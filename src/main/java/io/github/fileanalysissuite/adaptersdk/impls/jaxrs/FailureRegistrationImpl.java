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

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.FailureDetails;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FailureRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class FailureRegistrationImpl implements FailureRegistration
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FailureRegistrationImpl.class);

    @Override
    public final void registerFailure(final String fileLocation, final FailureDetails failureDetails)
    {
        registerFailureImpl(fileLocation, failureDetails);
    }

    @Override
    public final void registerFailure(final FailureDetails failureDetails)
    {
        registerFailureImpl(null, failureDetails);
    }

    private void registerFailureImpl(final String fileLocation, final FailureDetails failureDetails)
    {
        final io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FailureDetails responseFailureDetails
            = new io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FailureDetails();

        responseFailureDetails.setFileLocation(fileLocation);
        responseFailureDetails.setMessage(failureDetails.getMessage());

        final Iterable<Exception> exceptions = failureDetails.getExceptions();
        if (exceptions != null) {
            for (final Exception exception : exceptions) {
                // TODO: What are we meant to do with the exceptions now that the exceptions array has been removed:
                // https://github.com/FileAnalysisSuite/adapter-rest-contract/pull/8#discussion_r941539108
                LOGGER.warn("Exception in failure details...", exception);
            }
        }

        addFailuresItem(responseFailureDetails);
    }

    protected abstract void addFailuresItem(
        io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.serverstubs.model.FailureDetails failureDetails
    );
}
