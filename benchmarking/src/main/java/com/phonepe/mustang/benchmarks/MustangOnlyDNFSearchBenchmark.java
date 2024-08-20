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
package com.phonepe.mustang.benchmarks;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.phonepe.mustang.MustangEngine;
import com.phonepe.mustang.common.RequestContext;
import com.phonepe.mustang.composition.impl.Disjunction;
import com.phonepe.mustang.criteria.Criteria;
import com.phonepe.mustang.criteria.impl.CNFCriteria;
import com.phonepe.mustang.predicate.impl.ExcludedPredicate;
import com.phonepe.mustang.predicate.impl.IncludedPredicate;
import com.phonepe.mustang.utils.Utils;

import lombok.Getter;

public class MustangOnlyDNFSearchBenchmark {

    @Getter
    @State(Scope.Benchmark)
    public static class BenchmarkContext {
        @Param({ "10", "100", "1000", "10000" })
        private int indexSize;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private MustangEngine mustangEngine;
        private RequestContext context;

        @Setup(Level.Trial)
        public void setUp() {

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mustangEngine = MustangEngine.builder()
                    .mapper(objectMapper)
                    .build();

            for (int j = 0; j < indexSize; j++) {
                Collections.shuffle(Utils.PATHS);

                final Criteria c = CNFCriteria.builder()
                        .id(UUID.randomUUID()
                                .toString())
                        .disjunction(Disjunction.builder()
                                .predicates(Utils.PATHS.subList(0, Utils.RANDOM.nextInt(5) + 1)
                                        .stream()
                                        .map(p -> {
                                            if (Utils.RANDOM.nextInt(5) != 0) { // 80-20 split
                                                return IncludedPredicate.builder()
                                                        .lhs("$." + p)
                                                        .values(Sets.newHashSet(Utils.getRandom()))
                                                        .build();
                                            }
                                            return ExcludedPredicate.builder()
                                                    .lhs("$." + p)
                                                    .values(Sets.newHashSet(Utils.getRandom()))
                                                    .build();
                                        })
                                        .collect(Collectors.toList()))
                                .build())
                        .build();
                mustangEngine.add(Utils.INDEX_NAME, c);

            }

        }

        @Setup(Level.Invocation)
        public void prepareContext() {
            Collections.shuffle(Utils.PATHS);
            context = RequestContext.builder()
                    .node(objectMapper.valueToTree(Utils.PATHS.stream()
                            .limit(Utils.RANDOM.nextInt(Utils.PATHS.size()))
                            .collect(Collectors.toMap(x -> x, x -> Utils.getRandom()))))
                    .build();
        }

    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 1)
    @Threads(Threads.MAX)
    @BenchmarkMode(Mode.Throughput)
    public void search(final Blackhole blackhole, final BenchmarkContext benchmarkContext) {
        blackhole.consume(benchmarkContext.getMustangEngine()
                .search(Utils.INDEX_NAME, benchmarkContext.getContext(), false));
    }

}
