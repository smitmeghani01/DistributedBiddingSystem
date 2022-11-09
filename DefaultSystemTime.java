import java.time.Instant;
public class DefaultSystemTime implements SystemTime{
public long getSystemTime(){
long time = Instant.now().toEpochMilli();
return time;
}
}
