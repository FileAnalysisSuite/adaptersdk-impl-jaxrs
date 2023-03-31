/*
 * Copyright 2022-2022 Open Text.
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

import java.time.Instant;

public final class InstantFunctions
{
    private static final Instant MIN_RFC3339_DATE_TIME = Instant.parse("0001-01-01T00:00:00Z");
    private static final Instant MAX_RFC3339_DATE_TIME = Instant.parse("9999-12-31T23:59:59Z");

    private InstantFunctions()
    {
    }

    public static String toRFC3339DateTimeString(final Instant time)
    {
        if (time.isBefore(MIN_RFC3339_DATE_TIME)) {
            return "0001-01-01T00:00:00Z";
        } else if (time.isAfter(MAX_RFC3339_DATE_TIME)) {
            return "9999-12-31T23:59:59Z";
        } else {
            return time.toString();
        }
    }
}
