package by.pirog.ReverseGanttChart.mapper.qualifier;

import by.pirog.ReverseGanttChart.storage.entity.ProjectComponentEntity;
import jdk.jfr.Name;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Named("DateTimeQualifier")
public class DateTimeQualifier {

    @Named("InstantToLocalDateTime")
    public LocalDateTime instantToLocalDateTime(Instant instant) {
        return convertInstantToLocalDateTime(instant, ZoneId.systemDefault());
    }

    @Named("InstantToLocalDate")
    public LocalDate instantToLocalDate(Instant instant) {
        LocalDateTime localDateTime = convertInstantToLocalDateTime(instant, ZoneId.systemDefault());
        return localDateTime != null ? localDateTime.toLocalDate() : null;
    }

    @Named("LocalDateTimeToInstant")
    public Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }


    private LocalDateTime convertInstantToLocalDateTime(Instant instant, ZoneId zoneId) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, zoneId);
    }
}
