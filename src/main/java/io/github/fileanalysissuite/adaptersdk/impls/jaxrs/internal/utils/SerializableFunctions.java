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
package io.github.fileanalysissuite.adaptersdk.impls.jaxrs.internal.utils;

import java.io.Serializable;

public final class SerializableFunctions
{
    private SerializableFunctions()
    {
    }

    public static String convertToString(final Serializable object)
    {
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else {
            // TODO: The map only supports strings!
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
