import java.sql.*;

public class Issue {

    // Issue a book to a member
    public static void issueBook(int bookId, int memberId) {
        try (Connection conn = DBConnection.getConnection()) {
            // Find available copy
            PreparedStatement ps = conn.prepareStatement(
                "SELECT copy_id FROM book_copies WHERE book_id = ? AND available = TRUE LIMIT 1"
            );
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ No available copies for this book.");
                return;
            }

            int copyId = rs.getInt("copy_id");

            // Mark copy as unavailable
            PreparedStatement update = conn.prepareStatement(
                "UPDATE book_copies SET available = FALSE WHERE copy_id = ?"
            );
            update.setInt(1, copyId);
            update.executeUpdate();

            // Insert into issued_books
            PreparedStatement issue = conn.prepareStatement(
                "INSERT INTO issued_books(copy_id, member_id) VALUES(?, ?)"
            );
            issue.setInt(1, copyId);
            issue.setInt(2, memberId);
            issue.executeUpdate();

            System.out.println("✅ Book issued successfully (Copy ID: " + copyId + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Return a book
    public static void returnBook(int copyId, int memberId) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if issued
            PreparedStatement check = conn.prepareStatement(
                "SELECT * FROM issued_books WHERE copy_id = ? AND member_id = ? AND return_date IS NULL"
            );
            check.setInt(1, copyId);
            check.setInt(2, memberId);
            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ No record found for this issued book.");
                return;
            }

            int issueId = rs.getInt("id");

            // Update issued_books return_date
            PreparedStatement updateIssue = conn.prepareStatement(
                "UPDATE issued_books SET return_date = CURRENT_TIMESTAMP WHERE id = ?"
            );
            updateIssue.setInt(1, issueId);
            updateIssue.executeUpdate();

            // Mark copy available
            PreparedStatement updateCopy = conn.prepareStatement(
                "UPDATE book_copies SET available = TRUE WHERE copy_id = ?"
            );
            updateCopy.setInt(1, copyId);
            updateCopy.executeUpdate();

            System.out.println("✅ Book returned successfully (Copy ID: " + copyId + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // List all issued books with copy IDs
    public static void listIssuedBooks() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                SELECT i.id AS issue_id, c.copy_id, b.title, m.name, i.issue_date, i.return_date
                FROM issued_books i
                JOIN book_copies c ON i.copy_id = c.copy_id
                JOIN books b ON c.book_id = b.book_id
                JOIN members m ON i.member_id = m.id
            """;
            ResultSet rs = conn.createStatement().executeQuery(query);

            System.out.println("\n===== Issued Books =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                    "Issue ID: " + rs.getInt("issue_id") +
                    ", Copy ID: " + rs.getInt("copy_id") +
                    ", Book: " + rs.getString("title") +
                    ", Member: " + rs.getString("name") +
                    ", Issued: " + rs.getTimestamp("issue_date") +
                    ", Returned: " + rs.getTimestamp("return_date")
                );
            }
            if (!found) System.out.println("⚠ No issued books found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
