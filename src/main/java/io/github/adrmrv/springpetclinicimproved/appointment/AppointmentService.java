package io.github.adrmrv.springpetclinicimproved.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
public class AppointmentService {

    @Autowired
    NamedParameterJdbcTemplate template;

    public Object getSchedule(LocalDate Day) {
        List<Map<String, Object>> list = template.queryForList("""
                with cte as (
                    select :Day || ' 08:00:00' d
                    union all
                    select datetime(d, '+30 minutes') d from cte where d < :Day ||' 18:30:00'
                )
                select STRFTIME('%H:%M', cte.d) HM,
                c.FirstName || ' ' || c.LastName Owner, pt.Name PetType
                FROM cte
                cross join Veterinary v
                left join Appointment a on a.VetId = v.Id AND a.Date = cte.d
                left join Pet p on p.Id = a.PetId
                left join Client c on c.Id = p.ClientId
                LEFT  JOIN PetType pt on pt.Id = p.PetTypeId
                ORDER BY d, v.Id
                """, Map.of("Day", Day));

        TreeMap<String, List<String>> sm = new TreeMap<String, List<String>>();

        for (Map<String, Object> m : list) {
            List<String> values;
            values = sm.getOrDefault((String) m.get("HM"), new ArrayList<String>());
            String val = (String) m.getOrDefault("Owner", "");
            val = val == null ? "" : val;
            values.add(val);
            sm.put((String) m.get("HM"), values);
        }

        return sm;
    }

    public Object getWeeklySchedule(LocalDate day, int VetId) {
        var list = template.queryForList(
                """
                        WITH Hours as (
                        	SELECT :Day || ' 08:00:00' d
                        	union all
                        	select datetime(d, '+30 minutes') from Hours WHERE d < datetime(:Day, '+5 days')
                        ), OpHours as (
                        	SELECT d, STRFTIME('%w', d) w, STRFTIME('%H:%M', d) tod
                        	from Hours
                        	left join OperatingHours oh on oh.DayOfWeek = STRFTIME('%w', d) and STRFTIME('%H:%M', d) = oh.TimeOfDay and oh.VetId = :Id
                        	WHERE STRFTIME('%HH', d) BETWEEN '08:00:00' and '19:00:00'
                        )
                        SELECT tod, FirstName || ' ' || LastName Owner
                        FROM OpHours
                        left join Appointment a on a.Date = OpHours.d and a.VetId = :Id
                        LEFT JOIN Pet p on p.Id = a.PetId
                        left join Client c on c.Id = p.ClientId
                        order by tod, w
                                """,
                Map.of("Id", VetId, "Day", day));

        TreeMap<String, List<String>> sm = new TreeMap<>();

        for (var m : list) {
            String tod = (String) m.get("tod");
            List<String> values = sm.getOrDefault(tod, new ArrayList<String>());
            String value = (String) m.get("Owner");
            value = value == null ? "" : value;
            values.add(value);
            sm.put(tod, values);
        }

        return sm;
    }

    public List<String> getWeeklyScheduleDays(LocalDate Day) {
        List<String> res = new ArrayList<>();

        do {
            res.add(Day.getDayOfWeek().name() +
                    " " + Day.getMonthValue() + "/" +
                    Day.getDayOfMonth());
            Day = Day.plusDays(1);
        } while (Day.getDayOfWeek().ordinal() < 5);

        return res;
    }

    @Data
    private static class Slot {
        LocalDateTime app;
        String FirstName;
        String LastName;
        int VetId;
    }

    public List<Slot> getAvailabilities(LocalDate startDate, int VetId, String minHour, String maxHour, int page,
            Integer[] dow) {
        var sql = """
                WITH Hours as (
                	SELECT :startDate || ' 08:00:00' d
                	union all
                	select datetime(d, '+30 minutes') from Hours WHERE d < datetime(:startDate, '+12 months')
                ), OpHours as (
                	SELECT d, STRFTIME('%w', d) w, STRFTIME('%H:%M', d) tod, oh.VetId
                	from Hours
                	inner join OperatingHours oh on oh.DayOfWeek = STRFTIME('%w', d) and STRFTIME('%H:%M', d) = oh.TimeOfDay
                	WHERE STRFTIME('%HH', d) BETWEEN :minHour and :maxHour
                """;

        if (VetId > 0) {
            sql = sql + "and oh.VetId = :VetId ";
        }

        if (dow.length > 0) {
            var s = Arrays.toString(dow);
            s = s.substring(1, s.length() - 1);
            sql = sql + "and oh.DayOfWeek in (" + s + ")";
        }

        sql += """
                	AND NOT EXISTS (SELECT * FROM Appointment a WHERE a.VetId = oh.VetId and a.Date = d)
                )
                SELECT o.d app, FirstName, LastName, v.Id VetId
                from ophours o
                inner join Veterinary v on v.Id = o.VetId
                limit 10 offset :offset
                """;

        return template.query(sql,
                Map.of("startDate", startDate, "VetId", VetId,
                        "minHour", minHour, "maxHour", maxHour,
                        "offset", (page - 1) * 10),
                BeanPropertyRowMapper.newInstance(Slot.class));
    }

    public Integer getAvailabilityCount(LocalDate startDate, int VetId, String minHour, String maxHour, int page,
            Integer[] dow) {
        var sql = """
                WITH Hours as (
                	SELECT :startDate || ' 08:00:00' d
                	union all
                	select datetime(d, '+30 minutes') from Hours WHERE d < datetime(:startDate, '+12 months')
                ), OpHours as (
                	SELECT d, STRFTIME('%w', d) w, STRFTIME('%H:%M', d) tod, oh.VetId
                	from Hours
                	inner join OperatingHours oh on oh.DayOfWeek = STRFTIME('%w', d) and STRFTIME('%H:%M', d) = oh.TimeOfDay
                	WHERE STRFTIME('%HH', d) BETWEEN :minHour and :maxHour
                """;

        if (VetId > 0) {
            sql = sql + "and oh.VetId = :VetId ";
        }

        if (dow.length > 0) {
            var s = Arrays.toString(dow);
            s = s.substring(1, s.length() - 1);
            sql = sql + "and oh.DayOfWeek in (" + s + ")";
        }

        sql += """
                	AND NOT EXISTS (SELECT * FROM Appointment a WHERE a.VetId = oh.VetId and a.Date = d)
                )
                SELECT count(*)
                from ophours o
                inner join Veterinary v on v.Id = o.VetId
                """;

        return template.queryForObject(sql,
                Map.of("startDate", startDate, "VetId", VetId,
                        "minHour", minHour, "maxHour", maxHour,
                        "offset", (page - 1) * 10),
                Integer.class);
    }

    public void create(int vetId, int petId, LocalDateTime app) {
        template.update("""
                    INSERT  INTO Appointment (PetId, VetId, Date)
                    VALUES (:PetId, :VetId, :App)
                """,
                Map.of("VetId", vetId, "PetId", petId, "App", app.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    }
}
