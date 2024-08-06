package com.delivery.app.deliverer.service;

import com.delivery.app.deliverer.dto.DeliverUpdateLastLocationDTO;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class DeliverService {

    private final JdbcTemplate jdbcTemplate;


    public void updateLastLocation(DeliverUpdateLastLocationDTO lastLocationDTO) {

        try {
            jdbcTemplate.update("""
                    UPDATE      deliverers
                        SET     last_position = ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                        WHERE   deliverer_id = ?::uuid;
                    """, lastLocationDTO.longitude(), lastLocationDTO.latitude(), lastLocationDTO.deliverId());


            jdbcTemplate.update("""
                    UPDATE 		delivery_order_rel
                    	SET 	last_position = ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                    	WHERE 	deliverer_id = ?::uuid
                    			AND
                    			status = 'ROAD_TO_DELIVERY'::deliver_order_rel_status_type;
                    """, lastLocationDTO.longitude(), lastLocationDTO.latitude(), lastLocationDTO.deliverId());
        }
        catch (Exception e) {

            System.err.println(e.getMessage());
        }
    }
}
