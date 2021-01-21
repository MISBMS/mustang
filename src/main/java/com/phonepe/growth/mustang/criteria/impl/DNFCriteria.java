package com.phonepe.growth.mustang.criteria.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.phonepe.growth.mustang.common.RequestContext;
import com.phonepe.growth.mustang.composition.impl.Conjunction;
import com.phonepe.growth.mustang.criteria.Criteria;
import com.phonepe.growth.mustang.criteria.CriteriaForm;
import com.phonepe.growth.mustang.criteria.CriteriaVisitor;
import com.phonepe.growth.mustang.traverse.DebugResult;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DNFCriteria extends Criteria {
    @Valid
    @NotEmpty
    private List<Conjunction> conjunctions;

    @Builder
    @JsonCreator
    public DNFCriteria(@JsonProperty("id") String id,
                       @JsonProperty("conjunctions") @Singular List<Conjunction> conjunctions) {
        super(CriteriaForm.DNF, id);
        this.conjunctions = conjunctions;
    }

    @Override
    public boolean evaluate(RequestContext context) {
        return conjunctions.stream()
                .anyMatch(conjunction -> conjunction.evaluate(context));
    }

    @Override
    public DebugResult debug(RequestContext context) {
        return DebugResult.builder()
                .result(evaluate(context))
                .form(CriteriaForm.DNF)
                .compositionResults(conjunctions.stream()
                        .map(conjunction -> conjunction.debug(context))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public double getScore(RequestContext context) {
        // score of a DNF is the max of scores of its constituent conjunctions.
        return conjunctions.stream()
                .mapToDouble(conjunction -> conjunction.getScore(context))
                .max()
                .orElse(0);
    }

    @Override
    public <T> T accept(CriteriaVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
