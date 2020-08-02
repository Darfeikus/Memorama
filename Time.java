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

	public Time(Time s) {
		this.hours = s.getHours();
		this.minutes = s.getMinutes();
		this.seconds = s.getSeconds();
		this.miliseconds = s.getMiliseconds();
	}

	void addSeconds(int sec) {
		seconds += sec;
		simplifyTime();
	}

	// Overwriting for different argument type
	void addSeconds(double sec) {
		this.seconds += (int)sec;
		this.miliseconds += sec*1000-((int)sec*1000);
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
		difference.miliseconds = minuend.miliseconds - subtrahend.miliseconds;		
		if(difference.miliseconds < 0){
			difference.miliseconds = 1+difference.miliseconds;
			difference.seconds--;
		}
		difference.seconds = minuend.seconds - subtrahend.seconds;
		if(difference.seconds < 0){
			difference.seconds = 1+difference.seconds;
			difference.minutes--;
		}
		difference.minutes = minuend.minutes - subtrahend.minutes;
		if(difference.minutes < 0){
			difference.minutes = 1+difference.minutes;
			difference.hours--;
		}
		difference.hours = minuend.hours - subtrahend.hours;
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

	void print(){
		System.out.println(hours + "h: " + minutes + "m: " + seconds + "s: " + miliseconds + "ms");
	}

	Time getTime() {
		return this;
	}

}