package com.phonepe.growth.mustang.criteria;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.phonepe.growth.mustang.common.EvaluationContext;
import com.phonepe.growth.mustang.criteria.impl.CNFCriteria;
import com.phonepe.growth.mustang.criteria.impl.DNFCriteria;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "form")
@JsonSubTypes({ @JsonSubTypes.Type(name = CriteriaForm.DNF_TEXT, value = DNFCriteria.class),
        @JsonSubTypes.Type(name = CriteriaForm.CNF_TEXT, value = CNFCriteria.class) })
public abstract class Criteria {
    @NotNull
    private CriteriaForm form;
    @NotBlank
    private String id;

    public abstract boolean evaluate(EvaluationContext context);

    public abstract long getScore(EvaluationContext context);

    public abstract <T> T accept(CriteriaVisitor<T> visitor);

}
