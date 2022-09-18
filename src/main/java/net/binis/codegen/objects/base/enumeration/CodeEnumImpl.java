package net.binis.codegen.objects.base.enumeration;

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

public class CodeEnumImpl implements CodeEnum {

    protected final int ordinal;
    protected final String name;
    protected boolean unknown;

    public CodeEnumImpl(int ordinal, String name) {
        this.ordinal = ordinal;
        this.name = name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean unknown() {
        return unknown;
    }

    public void setUnknown() {
        unknown = true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(getClass().equals(o.getClass()))) {
            return false;
        }
        CodeEnumImpl other = (CodeEnumImpl) o;
        return other.ordinal() == this.ordinal();
    }

    @Override
    public int hashCode() {
        return this.ordinal();
    }

}
