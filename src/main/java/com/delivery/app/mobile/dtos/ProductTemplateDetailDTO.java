package com.delivery.app.mobile.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Builder
public record ProductTemplateDetailDTO(
        Integer id,
        String name,
        String description,
        String picture,
        Integer rate,
        Double priceList,
        Restaurant restaurant,
        List<Attribute> attributes
) {

    @Builder
    public record Restaurant(
            String name,
            String picture
    ) { }


    @Setter
    @Getter
    @Builder
    public static class Attribute implements Comparable<Attribute> {

        @JsonIgnore
        Integer id;

        @JsonIgnore
        Integer sequence;

        String name;
        String displayType;
        List<AttributeValue> attributeValues;

        @Override
        public int compareTo(Attribute o) {
            return this.sequence.compareTo(o.sequence);
        }

    }

    @Builder
    public record AttributeValue(
            String name,
            Integer id,
            String displayType,
            Double extraPrice,
            String picture
    ) { }

}
