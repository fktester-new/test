package activitytracker;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityDao {

    private DataSource dataSource;

    public ActivityDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertActivity(Activity activity){
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

    public Activity selectActivityById(long id){
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

    public List<Activity> selectAllActivities(){
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from activities")){

            return selectActivityByPreparadStatement(stmt);

        } catch (SQLException sqle){
            throw new IllegalStateException("Cannot excecute!");
        }
    }

}
