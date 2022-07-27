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

import io.github.fileanalysissuite.adaptersdk.interfaces.extensibility.RepositoryAdapter;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;

public final class AdapterSdk
{
    private AdapterSdk()
    {
    }

    /**
     * Returns a set of objects that need to be registered as singleton components in order to call into the specified adapter
     * implementation.
     *
     * @param adapter the implementation of the adapter
     * @return a set of objects that need to be registered as singleton components
     */
    @Nonnull
    public static Set<Object> wrap(final RepositoryAdapter adapter)
    {
        return Collections.singleton(new AdapterApiImpl(adapter));
    }
}
