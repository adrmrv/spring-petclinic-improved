package io.github.adrmrv.springpetclinicimproved.operatinghours;

import lombok.Data;

@Data
public class OperatingHour {
    int Id;
    int VetId;
    String TimeOfDay;
    int DayOfWeek; 
}
