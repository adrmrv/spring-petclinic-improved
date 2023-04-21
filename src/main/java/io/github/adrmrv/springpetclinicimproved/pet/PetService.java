package io.github.adrmrv.springpetclinicimproved.pet;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PetService {
    
    @Autowired
    NamedParameterJdbcTemplate template;

    public Integer countPets(int pt, String Name) {

        String sql = """
                select count(*)
                from Pet p
                inner join Client c on c.Id = p.ClientId
                INNER JOIN PetType pt on pt.Id = p.PetTypeId
                WHERE 1=1
                """;

        if (Name.length() > 0) {
            sql = sql + """
                    AND (p.Name LIKE '%' || :Name || '%'
                        or c.FirstName LIKE '%' || :Name || '%'
                        or c.LastName LIKE '%' || :Name || '%')
                    """;
        }

        if (pt > 0) {
            sql = sql + "AND pt.Id = :pt ";
        }

        return template.queryForObject(sql, 
            Map.of("Name", Name, "pt", pt), Integer.class);
    }

    public Object list(int pt, String name, int offset) {
        
        String sql = """
                select p.Id, p.Name, pt.Name PetType, 
                c.FirstName || ' ' || c.LastName Owner,
                c.Id ClientId 
                from Pet p
                inner join Client c on c.Id = p.ClientId
                INNER JOIN PetType pt on pt.Id = p.PetTypeId
                WHERE 1=1
                """;

        if (name.length() > 0) {
            sql = sql + """
                    AND (p.Name LIKE '%' || :Name || '%'
                        or c.FirstName LIKE '%' || :Name || '%'
                        or c.LastName LIKE '%' || :Name || '%')
                    """;
        }

        if (pt > 0) {
            sql = sql + "AND pt.Id = :pt ";
        }
        
        sql = sql + " LIMIT 10 OFFSET " + offset;

        return template.query(sql,
            Map.of("Name", name, "pt", pt),
            BeanPropertyRowMapper.newInstance(Pet.class));
    }

    public int delete(int id) {
        return template.update("delete from Pet where Id = :Id",
        Map.of("Id", id));
    }

    public int add(Pet p) {
        return template.update("""
            INSERT INTO Pet (ClientId, PetTypeId, Name)
            VALUES (:ClientId, :PetTypeId, :Name)
            """, 
        new BeanPropertySqlParameterSource(p));
    }

    public Pet get(int id) {
        return template.queryForObject(
            "select * from Pet where Id = :Id", 
            Map.of("Id", id), 
            BeanPropertyRowMapper.newInstance(Pet.class));
    }

    public int save(Pet p) {
        return template.update(
            "update pet set Name = :Name, PetTypeId = :PetTypeId where Id = :Id", 
            new BeanPropertySqlParameterSource(p));
    }

}
