package net.binis.codegen.tools;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2023 Binis Belev
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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class BaseStringInterpolator<T> {

    protected static final String INVALID_EXPRESSION = "Invalid expression!";
    protected Character identifier;

    protected T buildExpression(String exp) {
        var list = new ArrayList<T>();
        var flag = true;
        var start = 0;
        for (var i = 0; i < exp.length(); i++) {
            if ((isNull(identifier) && '{' == exp.charAt(i)) || (nonNull(identifier) && identifier == exp.charAt(i) && i + 1 < exp.length() && '{' == exp.charAt(i + 1))) {
                if (!flag) {
                    throw new InvalidParameterException(INVALID_EXPRESSION + exp);
                }

                if (start < i) {
                    var constant = exp.substring(start, i);
                    if (constant.length() != 0) {
                        list.add(buildConstantExpression(constant));
                    }
                }

                start = i + 1;

                if (nonNull(identifier)) {
                    start++;
                }

                flag = false;
            } else if ('}' == exp.charAt(i)) {
                if (flag && nonNull(identifier)) {
                    continue;
                }
                if (flag) {
                    throw new InvalidParameterException(INVALID_EXPRESSION + exp);
                }

                if (start < i) {
                    var e = exp.substring(start, i);
                    list.add(buildParamExpression(e));
                }

                start = i + 1;

                flag = true;
            }

        }

        if (!flag) {
            throw new InvalidParameterException(INVALID_EXPRESSION + exp);
        }

        if (start < exp.length()) {
            list.add(buildConstantExpression(exp.substring(start)));
        }

        return complexExpression(list);
    }

    protected abstract T buildConstantExpression(String exp);

    protected abstract T buildComplexExpression(List<T> list);

    protected T complexExpression(List<T> list) {
        if (list.size() == 1) {
            return list.get(0);
        } else {
            return buildComplexExpression(list);
        }
    }

    protected abstract T buildParamExpression(String exp);

}
