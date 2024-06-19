package com.delivery.app.product.template.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

@Entity
@Subselect("select * from product_tmpl_attr_values")
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class ViewProductTmplAttrValue {

    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_tmpl_id", referencedColumnName = "id")
    private ProductTemplate productTmpl;

    @Column(name = "name_attr_val")
    private String nameAttrValue;

    @Column(name = "sequence_attr_val")
    private Integer sequenceAttrValue;

    @Column(name = "name_attr")
    private String nameAttr;

    @Column(name = "sequence_attr")
    private Integer sequenceAttr;

    @Column(name = "default_extra_price")
    private Double extraPrice;
}
