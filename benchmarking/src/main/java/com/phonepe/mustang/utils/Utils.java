/**
 * Copyright (c) 2022 Original Author(s), PhonePe India Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.phonepe.mustang.utils;

import java.security.SecureRandom;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
    public static final String INDEX_NAME = "PERF";
    public static final SecureRandom RANDOM = new SecureRandom();
    public static final List<String> PATHS = Lists.newArrayList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
        "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");

    public static int getRandom() {
        return RANDOM.nextInt(100);
    }

}
