package JsonUtils.PriceHistory;

import java.util.Calendar;

public class DayTime implements Comparable<DayTime> {
    private int month;  // Mese aggiunto alla classe DayTime
        private int day;
        private long timestamp;

        public DayTime(long timestamp) {
            this.timestamp = timestamp;
            // Conversione del timestamp in data
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp * 1000);
            this.month = calendar.get(Calendar.MONTH) + 1; // +1 perché i mesi in Calendar partono da 0
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public int compareTo(DayTime other) {
            // Prima confronta per mese
            int monthComparison = Integer.compare(this.month, other.month);
            if (monthComparison != 0) {
                return monthComparison;
            }
            
            // Poi confronta per giorno
            int dayComparison = Integer.compare(this.day, other.day);
            if (dayComparison != 0) {
                return dayComparison;
            }
            
            // Infine confronta per timestamp
            return Long.compare(this.timestamp, other.timestamp);
        }

        @Override
        public String toString() {
            return "DayTime{month=" + month + ", day=" + day + ", timestamp=" + timestamp + "}";
        }
}
