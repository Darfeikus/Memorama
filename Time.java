/* Author: Pablo Vera Teran */
public class Time {

	private int hours;
	private int minutes;
	private int seconds;
	private int miliseconds;

	/* Constructor of class Time */
	public Time() {
		this.hours = 0;
		this.minutes = 0;
		this.seconds = 0;
		this.miliseconds = 0;
	}

	void addSeconds(int sec) {
		seconds += sec;
		simplifyTime();
	}

	// Overwriting for different argument type
	void addSeconds(float sec) {
		this.miliseconds += (sec % 1) * 1000;
		this.seconds += sec - (sec % 1);
		simplifyTime();
	}

	void addHour(int hr) {
		this.hours += hr;
		simplifyTime();
	}

	void addMinute(int min) {
		this.minutes += min;
		simplifyTime();
	}

	static Time substractTimes(Time minuend, Time subtrahend) {
		Time difference = new Time();
		difference.hours = minuend.hours - subtrahend.hours;
		difference.minutes = minuend.minutes - subtrahend.minutes;
		difference.seconds = minuend.seconds - subtrahend.seconds;
		difference.miliseconds = minuend.miliseconds - subtrahend.miliseconds;
		difference.simplifyTime();
		return difference;
	}

	void simplifyTime() {
		if (this.miliseconds >= 1000) {
			this.seconds += (this.miliseconds - this.miliseconds % 1000) / 1000;
			this.miliseconds = this.miliseconds % 1000;
		}
		if (this.seconds >= 60) {
			this.minutes += (this.seconds - this.seconds % 60) / 60;
			this.seconds = this.seconds % 60;
		}
		if (this.minutes >= 60) {
			this.hours += (this.minutes - this.minutes % 60) / 60;
			this.minutes = this.minutes % 60;
		}
	}

	int getHours() {
		return this.hours;
	}

	int getMinutes() {
		return this.minutes;
	}

	int getSeconds() {
		return this.seconds;
	}

	int getMiliseconds() {
		return this.miliseconds;
	}

	Time getTime() {
		return this;
	}

}