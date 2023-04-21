package io.github.adrmrv.springpetclinicimproved.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

	@Autowired
	NamedParameterJdbcTemplate template;

	public Client getClient(int Id) {
		return template.queryForObject(
				"SELECT * FROM Client WHERE Id = :Id",
				Map.of("Id", Id),
				new RowMapper<Client>() {
					@Override
					public Client mapRow(ResultSet rs, int rn) throws SQLException {
						Client c = new Client();
						c.Id = rs.getInt("Id");
						c.FirstName = rs.getString("FirstName");
						c.LastName = rs.getString("LastName");
						c.Email = rs.getString("Email");
						c.BirthDate = LocalDate.parse(rs.getString("BirthDate"), DateTimeFormatter.ISO_LOCAL_DATE);
						return c;
					}
				});
	}

	public int updateClient(Client c) {
		return template.update(
				"update Client set "
						+ "FirstName = :FirstName, "
						+ "LastName = :LastName,"
						+ "Email = :Email, "
						+ "BirthDate = :BirthDate "
						+ "WHERE Id = :Id",
				new BeanPropertySqlParameterSource(c));
	}

	public int addClient(Client c) {
		return template.update("INSERT INTO Client "
				+ "(FirstName, LastName, Email, BirthDate) "
				+ "VALUES (:FirstName, :LastName, :Email, :BirthDate)",
				new BeanPropertySqlParameterSource(c));
	}

	public int deleteClient(int Id) {
		return template.update("DELETE FROM Client WHERE Id = :Id", Map.of("Id", Id));
	}

	public List<Client> listClients(String name, int offset) {

		return template.query("SELECT * FROM Client c "
				+ "WHERE c.FirstName like :name "
				+ "OR c.LastName like :name "
				+ "OR c.Email like :name "
				+ "LIMIT 10 OFFSET :offset",
				Map.of("name", "%" + name + "%", "offset", offset),
				new RowMapper<Client>() {
					@Override
					public Client mapRow(ResultSet rs, int rn) throws SQLException {
						Client c = new Client();
						c.Id = rs.getInt("Id");
						c.FirstName = rs.getString("FirstName");
						c.LastName = rs.getString("LastName");
						c.Email = rs.getString("Email");
						c.BirthDate = LocalDate.parse(rs.getString("BirthDate"), DateTimeFormatter.ISO_LOCAL_DATE);
						return c;
					}
				});
	}

	public Integer countClients(String name) {

		return template.queryForObject("SELECT count(*) FROM Client c "
				+ "WHERE c.FirstName like :name "
				+ "OR c.LastName like :name "
				+ "OR c.Email like :name ",
				Map.of("name", "%" + name + "%"),
				Integer.class);
	}

	public Client getOwnerOfPet(int PetId) {
		return template.queryForObject("""
				SELECT FirstName, LastName FROM Client WHERE
				Id = (SELECT ClientId FROM Pet WHERE Id = :PetId)
				""", Map.of("PetId", PetId), BeanPropertyRowMapper.newInstance(Client.class));
	}
}
