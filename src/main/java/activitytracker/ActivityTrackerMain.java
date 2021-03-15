package activitytracker;

import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerMain {


    public static void main(String[] args) {

        MariaDbDataSource dataSource;
        try {
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/activity?useUnicode=true");
            dataSource.setUser("activity");
            dataSource.setPassword("activity");
        } catch (SQLException se) {
            throw new IllegalStateException("Can not create data source", se);
        }
        Activity activity = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Biking in Bakony", ActivityType.BIKING);
        Activity activity2 = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Hiking in Bakony", ActivityType.HIKING);
        Activity activity3 = new Activity(LocalDateTime.of(2021, 02, 23, 10, 22), "Running in Bakony", ActivityType.RUNNING);

        ActivityDao ad = new ActivityDao(dataSource);
        ad.insertActivity(activity);
        ad.insertActivity(activity2);
        ad.insertActivity(activity3);
        System.out.println(ad.selectActivityById(3));
        System.out.println(ad.selectAllActivities());
    }
}
