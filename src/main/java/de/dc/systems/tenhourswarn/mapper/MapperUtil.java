package de.dc.systems.tenhourswarn.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import de.dc.systems.tenhourswarn.persistence.entities.Timer;

/**
 * @author Thomas Reno
 *
 */
public class MapperUtil {

	public static List<Timer> mapToTimerList(ResultSet rs) {
		List<Timer> list = new ArrayList<>();
		try {
			while (rs.next()) {
				Object objDate = rs.getObject("date");
				LocalDate date = null != objDate ? LocalDate.parse((String) objDate) : null;
				Object objTime = rs.getObject("time");
				LocalTime time = null != objTime ? LocalTime.parse((String) objTime) : null;
				Timer timer = new Timer(date, time);
				list.add(timer);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
