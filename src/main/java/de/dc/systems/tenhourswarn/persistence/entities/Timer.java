package de.dc.systems.tenhourswarn.persistence.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Thomas Reno
 *
 */
public class Timer implements Table {

	private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	private ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();

	public Timer(LocalDate date, LocalTime time) {
		this.date.set(date);
		this.time.set(time);
	}

	public LocalDate getDate() {
		return date.get();
	}

	public void setDate(LocalDate date) {
		this.date.set(date);
	}

	public LocalTime getTime() {
		return time.get();
	}

	public void setTime(LocalTime time) {
		this.time.set(time);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date.get() == null) ? 0 : date.get().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Timer other = (Timer) obj;
		if (date.get() == null) {
			if (other.date.get() != null)
				return false;
		} else if (!date.get().equals(other.date.get()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Timer [date=");
		builder.append(date.get());
		builder.append(", time=");
		builder.append(time.get());
		builder.append("]");
		return builder.toString();
	}
}
