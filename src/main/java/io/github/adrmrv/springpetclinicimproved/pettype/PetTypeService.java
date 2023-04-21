package io.github.adrmrv.springpetclinicimproved.pettype;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PetTypeService {
    @Autowired
    NamedParameterJdbcTemplate template;

    public List<PetType> findAll() {
        return template.query("select * from PetType",
            BeanPropertyRowMapper.newInstance(PetType.class));
    }

    public PetType findById(int id) {
        return template.queryForObject("select * from PetType where id = :id",
            Map.of("id", id),
            BeanPropertyRowMapper.newInstance(PetType.class));
    }

    public int add(PetType petType) {
        return template.update("insert into PetType (Name) values (:Name)",
            Map.of("Name", petType.getName()));
    }

    public int delete(int id) {
        return template.update("delete from PetType where id = :id",
            Map.of("id", id));
    }
    
    public int update(PetType petType) {
        return template.update("update PetType set Name = :Name where Id = :Id",
            new BeanPropertySqlParameterSource(petType));
    }
}
