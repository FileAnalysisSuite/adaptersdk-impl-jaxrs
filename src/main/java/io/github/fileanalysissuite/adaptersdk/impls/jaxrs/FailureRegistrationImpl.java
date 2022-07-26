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

import io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.utils.ThrowableFunctions;
import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.FailureDetails;
import io.github.fileanalysissuite.adaptersdk.interfaces.framework.FailureRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class FailureRegistrationImpl implements FailureRegistration
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FailureRegistrationImpl.class);

    @Override
    public final void registerFailure(final String itemLocation, final FailureDetails failureDetails)
    {
        // TODO: Logging item location because there doesn't seem to be a way to pass it back in the response...
        LOGGER.warn("Registering failure for {}...", itemLocation);

        registerFailureImpl(failureDetails);
    }

    @Override
    public final void registerFailure(final FailureDetails failureDetails)
    {
        registerFailureImpl(failureDetails);
    }

    private void registerFailureImpl(final FailureDetails failureDetails)
    {
        final io.github.fileanalysissuite.adapters.rest.contract.serverstubs.jaxrs.model.FailureDetails responseFailureDetails
            = new io.github.fileanalysissuite.adapters.rest.contract.serverstubs.jaxrs.model.FailureDetails();

        responseFailureDetails.setMessage(failureDetails.getMessage());

        final Iterable<Exception> exceptions = failureDetails.getExceptions();
        if (exceptions != null) {
            for (final Exception exception : exceptions) {
                final String exceptionString = exception.getMessage() + "\n===\n" + ThrowableFunctions.getStackTrace(exception);
                responseFailureDetails.addExceptionsItem(exceptionString);
            }
        }

        addFailuresItem(responseFailureDetails);
    }

    protected abstract void addFailuresItem(
        io.github.fileanalysissuite.adapters.rest.contract.serverstubs.jaxrs.model.FailureDetails failureDetails
    );
}
