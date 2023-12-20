/*
 * Copyright 2022-2024 Open Text.
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

import io.github.fileanalysissuite.adaptersdk.interfaces.framework.OptionsProvider;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

final class OptionsProviderImpl implements OptionsProvider
{
    private final Map<String, String> options;

    public OptionsProviderImpl(final Map<String, String> options)
    {
        this.options = Optional.ofNullable(options).orElse(Collections.emptyMap());
    }

    @Nonnull
    @Override
    public Iterable<String> getOptionNames()
    {
        return options.keySet();
    }

    @Nonnull
    @Override
    public Optional<String> getOption(final String optionName)
    {
        return Optional.ofNullable(options.get(optionName));
    }
}
