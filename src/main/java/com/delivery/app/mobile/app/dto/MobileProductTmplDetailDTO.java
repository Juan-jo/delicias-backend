package com.delivery.app.mobile.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
public record MobileProductTmplDetailDTO(
        Integer id,
        String name,
        String description,
        String picture,
        Integer rate,
        Double priceList,
        Restaurant restaurant,
        int qty,
        List<Attribute> attributes
) {

    @Builder
    public record Restaurant(
            Integer id,
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
            Double extraPrice,
            String picture
    ) { }

}
