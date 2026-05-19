package com.hms.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface Schedulable {
    List<String> getSchedule(LocalDate date);
    boolean isAvailable(LocalDate date, LocalTime time);
}
