package io.github.adrmrv.springpetclinicimproved.client;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Client {
	int Id;
	String FirstName;
	String LastName;
	String Email;
	LocalDate BirthDate;
}
