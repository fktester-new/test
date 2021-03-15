package activitytracker;

import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerMain {

    public void insertActivity(DataSource dataSource, Activity activity){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("insert into activities(start_time, activity_desc, activity_type) values(?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf(activity.getStartTime()));
            stmt.setString(2, activity.getDesc());
            stmt.setString(3, activity.getType().toString());
            stmt.executeUpdate();
        } catch (SQLException se) {
            throw new IllegalStateException("Can not connect", se);
        }
    }

    private List<Activity> selectActivityByPreparadStatement(PreparedStatement stmt){
        try(ResultSet rs = stmt.executeQuery()){
            List<Activity> result = new ArrayList<>();
            while (rs.next()){
                Activity activity = new Activity(rs.getLong("id"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getString("activity_desc"),
                        ActivityType.valueOf(rs.getString("activity_type")));
                result.add(activity);
            }
            return result;
        }
        catch(SQLException sqle){
            throw new IllegalStateException("Excecute failed!", sqle);
        }
    }

    public Activity selectActivityById(DataSource dataSource, long id){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("select * from activities where id = ?")
                ){
            stmt.setLong(1, id);

            List<Activity> result = selectActivityByPreparadStatement(stmt);

            if(result.size() == 1){
                return result.get(0);
            }
            throw new IllegalArgumentException("Wrong id!");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Connection failed!", sqle);
        }
    }

    public List<Activity> selectAllActivities(DataSource dataSource){
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from activities")){

            return selectActivityByPreparadStatement(stmt);

                    } catch (SQLException sqle){
            throw new IllegalStateException("Cannot excecute!");
        }
    }

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

        ActivityTrackerMain activityTrackerMain = new ActivityTrackerMain();
        activityTrackerMain.insertActivity(dataSource, activity);
        activityTrackerMain.insertActivity(dataSource, activity2);
        activityTrackerMain.insertActivity(dataSource, activity3);
        System.out.println(activityTrackerMain.selectActivityById(dataSource, 3));
        System.out.println(activityTrackerMain.selectAllActivities(dataSource));
    }
}
