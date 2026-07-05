import java.sql.*;

public class Book {

    public static void addBook(String title, String author, int copies) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if book already exists
            PreparedStatement check = conn.prepareStatement(
                "SELECT book_id FROM books WHERE title = ? AND author = ?"
            );
            check.setString(1, title);
            check.setString(2, author);
            ResultSet rs = check.executeQuery();

            int bookId;
            if (rs.next()) {
                bookId = rs.getInt("book_id");
                System.out.println("ℹ Book already exists: " + title + " by " + author);
            } else {
                // Insert new book
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO books(title, author) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, title);
                ps.setString(2, author);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                bookId = keys.getInt(1);

                System.out.println("✅ New book added: " + title + " by " + author);
            }

            // Add copies
            PreparedStatement insertCopy = conn.prepareStatement(
                "INSERT INTO book_copies(book_id, available) VALUES (?, TRUE)"
            );
            for (int i = 0; i < copies; i++) {
                insertCopy.setInt(1, bookId);
                insertCopy.addBatch();
            }
            insertCopy.executeBatch();

            System.out.println("📚 " + copies + " copies added for book: " + title);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listBooks() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                SELECT b.book_id, b.title, b.author,
                       COUNT(c.copy_id) AS total_copies,
                       SUM(CASE WHEN c.available = TRUE THEN 1 ELSE 0 END) AS available_copies
                FROM books b
                LEFT JOIN book_copies c ON b.book_id = c.book_id
                GROUP BY b.book_id, b.title, b.author
            """;

            ResultSet rs = conn.createStatement().executeQuery(query);

            boolean found = false;
            System.out.println("\n===== All Books =====");
            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getInt("book_id") +
                                   ", Title: " + rs.getString("title") +
                                   ", Author: " + rs.getString("author") +
                                   ", Total Copies: " + rs.getInt("total_copies") +
                                   ", Available: " + rs.getInt("available_copies"));
            }
            if (!found) System.out.println("⚠ No books found in library.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
