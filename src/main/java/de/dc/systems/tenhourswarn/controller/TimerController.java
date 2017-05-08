package de.dc.systems.tenhourswarn.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;

import org.controlsfx.control.Notifications;

import de.dc.systems.tenhourswarn.enums.NotificationType;
import de.dc.systems.tenhourswarn.persistence.TimerDAO;
import de.dc.systems.tenhourswarn.persistence.entities.Timer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.util.Duration;

/**
 * @author Thomas Reno
 *
 */
public class TimerController {

	private static final double BUSINESS_HOURS = 7.6;
	private static final double BREAK_HOURS = 0.5;

	private static final double BUSINESS_HOURS_MAX = 10;
	private static final double BREAK_HOURS_MAX = 0.75;

	private static final long ENDOFWORKINGDAY_MINUTES = (long) ((BUSINESS_HOURS + BREAK_HOURS) * 60);
	private static final long TENHOURLINE_MINUTES = (long) ((BUSINESS_HOURS_MAX + BREAK_HOURS_MAX) * 60);

	private long minutes;

	private TimerDAO timerDAO = new TimerDAO();

	public TimerController() {
		Timer timer = new Timer(LocalDate.now(), LocalTime.now());
		List<Timer> timerList = timerDAO.select();
		if (timerList.contains(timer)) {
			minutes = calcMinutes(timerList.get(timerList.indexOf(timer)).getTime().getLong(ChronoField.MINUTE_OF_DAY));
			return;
		}
		timerDAO.insert(timer);
		minutes = TENHOURLINE_MINUTES;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		String time = timer.getTime().format(formatter);
		String endOfWorkingDay = timer.getTime().plusMinutes(ENDOFWORKINGDAY_MINUTES).format(formatter);
		StringBuilder builder = new StringBuilder();
		builder.append("Guten Morgen.");
		builder.append(" Es ist ").append(time).append(" Uhr.");
		builder.append(" ").append(endOfWorkingDay).append(" Uhr könnte Feierabend sein.");
		showNotification(NotificationType.INFO, builder.toString(), Duration.seconds(30));
	}

	public void run() {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		KeyFrame frame = new KeyFrame(Duration.minutes(1), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				minutes--;
				if (0 > minutes) {
					if (-60 == minutes) {
						timeline.stop();
						Platform.exit();
						System.exit(0);
					}
					return;
				}
				handleNotification(minutes);
			}
		});
		timeline.getKeyFrames().add(frame);
		timeline.playFromStart();
	}

	private long calcMinutes(long minuteOfDay) {
		return TENHOURLINE_MINUTES - (LocalTime.now().getLong(ChronoField.MINUTE_OF_DAY) - minuteOfDay);
	}

	private void handleNotification(long minutes) {
		if (0 == minutes) {
			String msg = "10-Stunden-Grenze erreicht.";
			showNotification(NotificationType.FATAL, msg, Duration.hours(1));
		} else if (15 >= minutes) {
			String msg = "Noch " + minutes + ((1 == minutes) ? " Minute" : " Minuten") + " bis zur 10-Stunden-Grenze.";
			showNotification(NotificationType.WARN, msg, Duration.seconds(45));
		} else if (60 == minutes) {
			String msg = "Noch eine Stunde bis zur 10-Stunden-Grenze.";
			showNotification(NotificationType.WARN, msg, Duration.seconds(30));
		} else if (/* == 159 */(TENHOURLINE_MINUTES - ENDOFWORKINGDAY_MINUTES) == minutes) {
			String msg = "SOLL-Stunden-Grenze erreicht.";
			showNotification(NotificationType.INFO, msg, Duration.seconds(30));
		} else if (/* == 219 */(TENHOURLINE_MINUTES - ENDOFWORKINGDAY_MINUTES + 60) == minutes) {
			String msg = "Noch eine Stunde bis zur SOLL-Stunden-Grenze.";
			showNotification(NotificationType.INFO, msg, Duration.seconds(30));
		}
	}

	private void onNotificationClicked(ActionEvent event) {
		System.out.println("on notification clicked");
	}

	private void showNotification(NotificationType type, String text, Duration hideAfter) {
		Notifications notifications = Notifications.create() //
				.title("10-Stunden-Grenze") //
				.text(text) //
				.position(Pos.TOP_LEFT) //
				.darkStyle() //
				.hideAfter(hideAfter) //
				.onAction(this::onNotificationClicked);
		switch (type) {
		case INFO:
			notifications.showInformation();
			break;
		case WARN:
			notifications.showWarning();
			break;
		case FATAL:
			notifications.showError();
			break;
		default:
			break;
		}
	}
}
