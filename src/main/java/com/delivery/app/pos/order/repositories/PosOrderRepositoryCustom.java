package com.delivery.app.pos.order.repositories;

import com.delivery.app.pos.order.models.PosOrder;

public interface PosOrderRepositoryCustom {
    void refresh(PosOrder order);
}
