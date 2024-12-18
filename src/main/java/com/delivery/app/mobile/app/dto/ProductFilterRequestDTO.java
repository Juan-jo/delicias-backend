package com.delivery.app.mobile.app.dto;

import com.delivery.app.configs.validation.common.OnFilter;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record ProductFilterRequestDTO(
        @NotNull(message = "The page number parameter is mandatory.", groups = { OnFilter.class })
        Integer page,

        @NotNull(message = "The elements number parameter is mandatory.", groups = { OnFilter.class })
        Integer size
) {


    public Pageable pageable() {
        return PageRequest.of(page, size);
    }

}
