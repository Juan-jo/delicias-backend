package com.delivery.app.configs.dto;

import com.delivery.app.configs.validation.common.OnFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseFilterDTO {

    @NotNull(message = "The page number parameter is mandatory.", groups = { OnFilter.class })
    private Integer page;

    @NotNull(message = "The elements number parameter is mandatory.", groups = { OnFilter.class })
    private Integer size;

    @NotNull(message = "The column name to sort parameter is mandatory.", groups = { OnFilter.class })
    private String orderColumn;

    @NotNull(message = "The order dir parameter is mandatory.", groups = { OnFilter.class })
    private String orderDir;

    public BaseFilterDTO(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public Pageable pageable() {
        return PageRequest.of(getPage(), getSize(), JpaSort.unsafe(Sort.Direction.fromString(getOrderDir()), getOrderColumn()));
    }


}
