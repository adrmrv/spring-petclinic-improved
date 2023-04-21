package io.github.adrmrv.springpetclinicimproved.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HomeService {
    @Autowired
    NamedParameterJdbcTemplate template;

    public Object getCounts() {
        var res = template.query("""
select 
(select count(*) from Client c) clients,
(select count(*) from Pet p) pets,
(select count(*) from PetType pt) pettypes,
(select count(*) from Appointment a) appointments
        """, new ColumnMapRowMapper());
        return res.get(0);
    }
}
