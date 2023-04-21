package io.github.adrmrv.springpetclinicimproved.pet;

import lombok.Data;

@Data
public class Pet {
    int Id; 
    String Name; 
    String PetType;
    int PetTypeId;
    String Owner;
    int ClientId;
}
