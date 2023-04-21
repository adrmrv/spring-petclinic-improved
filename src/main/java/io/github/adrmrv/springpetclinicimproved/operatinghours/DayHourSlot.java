package io.github.adrmrv.springpetclinicimproved.operatinghours;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DayHourSlot {

    String HourLabel;
    DaySlot[] data;

    @Data
    public static class DaySlot {
        String Hour;
        int Day;
        boolean enabled;
    }

    public static List<DayHourSlot> of() {
        List<DayHourSlot> res = new ArrayList<DayHourSlot>();

        for (int h = 8; h < 19; ++h) {
            for (int m = 0; m < 60; m += 30) {
                DayHourSlot dhs = new DayHourSlot();
                dhs.HourLabel = String.format("%02d", h) + ":" + String.format("%02d", m);
                dhs.data = new DaySlot[5];
                for (int i = 1; i < 6; ++i) {
                    DaySlot ds = new DaySlot();
                    ds.Hour = dhs.HourLabel;
                    ds.Day = i;
                    ds.enabled = false;
                    dhs.data[i-1] = ds;
                }

                res.add(dhs);
            }
        }

        return res;
    }
}