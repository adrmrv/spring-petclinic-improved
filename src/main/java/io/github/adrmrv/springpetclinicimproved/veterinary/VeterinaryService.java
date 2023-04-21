package io.github.adrmrv.springpetclinicimproved.veterinary;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class VeterinaryService {

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	public List<Veterinary> getAllVets() {
		return template.query("SELECT * FROM Veterinary ORDER BY Id", BeanPropertyRowMapper.newInstance(Veterinary.class));
	}
	
	public Veterinary getVet(int Id) {
		return template.queryForObject("SELECT * FROM Veterinary WHERE Id = :Id", Map.of("Id", Id), BeanPropertyRowMapper.newInstance(Veterinary.class));
	}
	
	public int updateVet(Veterinary vet) {
		return template.update("update veterinary set FirstName = :FirstName, LastName = :LastName WHERE Id = :Id", new BeanPropertySqlParameterSource(vet));
	}
	
	public int addVet(Veterinary vet) {
		return template.update("INSERT INTO Veterinary (FirstName, LastName) VALUES (:FirstName, :LastName)", new BeanPropertySqlParameterSource(vet));
	}

	public int deleteVet(int Id) {
		return template.update("DELETE FROM Veterinary WHERE Id = :Id", Map.of("Id", Id));
	}
}
