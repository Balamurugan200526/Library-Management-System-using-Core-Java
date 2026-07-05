import java.sql.*;

public class Member {

    public static void addMember(String name, String email) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement check = conn.prepareStatement(
                "SELECT * FROM members WHERE email = ?"
            );
            check.setString(1, email);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                System.out.println("❌ Member already exists: " + email);
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO members(name, email) VALUES(?, ?)"
            );
            ps.setString(1, name);
            ps.setString(2, email);
            ps.executeUpdate();

            System.out.println("✅ Member registered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listMembers() {
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM members");

            System.out.println("\n===== All Members =====");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                                   ", Name: " + rs.getString("name") +
                                   ", Email: " + rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
