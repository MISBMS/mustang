package com.phonepe.growth.mustang.ratify;

import java.util.Set;

import com.phonepe.growth.mustang.common.RequestContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RatificationDetail {
    private RequestContext context;
    private Set<String> expected;
    private Set<String> actual;
}
