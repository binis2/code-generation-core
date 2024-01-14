package net.binis.codegen.options;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2022 Binis Belev
 * %%
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
 * #L%
 */

public final class Options {

    private Options() {
        //Do nothing
    }

    //Creation options
    public static final Class<? extends CodeOption> HIDDEN_CREATE_METHOD = HiddenCreateMethodOption.class;

    //Modifier options
    public static final Class<? extends CodeOption> SUPPRESS_SPOTBUGS_WARNINGS = SuppressSpotBugsWarningOption.class;

    //Validation options
    public static final Class<? extends CodeOption> VALIDATION_FORM = ValidationFormOption.class;
    public static final Class<? extends CodeOption> EXPOSE_VALIDATE_METHOD = ExposeValidateMethodOption.class;

    //OpenApi
    public static final Class<? extends CodeOption> GENERATE_OPENAPI_ALWAYS = GenerateOpenApiAlwaysOption.class;
    public static final Class<? extends CodeOption> GENERATE_OPENAPI_IF_AVAILABLE = GenerateOpenApiIfAvailableOption.class;

    //Jackson
    public static final Class<? extends CodeOption> HANDLE_JACKSON_ALWAYS = HandleJacksonAlwaysOption.class;
    public static final Class<? extends CodeOption> HANDLE_JACKSON_IF_AVAILABLE = HandleJacksonIfAvailableOption.class;

    //ToString
    public static final Class<? extends CodeOption> TO_STRING_ONLY_EXPLICITLY_INCLUDED = ToStringOnlyExplicitlyIncludedOption.class;
    public static final Class<? extends CodeOption> TO_STRING_FULL_COLLECTION_INFO = ToStringFullCollectionInfoOption.class;

}
