public class TimeStamp{

	private String text;
	private Time time;

	public TimeStamp(String text, Time time){
		this.time = time;
		this.text = text;
	}

	String getTimeStamp(){
		return this.text + this.time.getHours() + ":" + this.time.getMinutes() + ":" + this.time.getSeconds() + "." + this.time.getMiliseconds();
	}

}