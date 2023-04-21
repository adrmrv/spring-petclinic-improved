package io.github.adrmrv.springpetclinicimproved.operatinghours;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OperatingHoursService {

    @Autowired
    private NamedParameterJdbcTemplate template;

    public List<DayHourSlot> getDayHourSlots(int Id) {
        List<DayHourSlot> dhs = DayHourSlot.of();

        List<OperatingHour> loh = template.query("select * from OperatingHours oh where VetId = :Id",
                Map.of("Id", Id),
                BeanPropertyRowMapper.newInstance(OperatingHour.class));

        for (OperatingHour oh : loh) {
            for (DayHourSlot hs : dhs) {
                if (hs.HourLabel.equals(oh.TimeOfDay)) {
                    hs.data[oh.DayOfWeek - 1].enabled = true;
                    break;
                }
            }
        }

        return dhs;
    }

    public void updateHourSlots(Map<String, String> body, int Id) {

        template.update("delete from OperatingHours WHERE VetId = :VetId",
                Map.of("VetId", Id));

        for (String name : body.keySet()) {
            String hour = name.split("-")[0];
            Integer day = Integer.valueOf(name.split("-")[1]);

            template.update("""
                    insert into OperatingHours (VetId, TimeOfDay, DayOfWeek)
                    VALUES (:VetId, :TimeOfDay, :DayOfWeek)""",
                    Map.of("VetId", Id, "TimeOfDay", hour, "DayOfWeek", day));
        }
    }

}
