import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. Register Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. View All Books");
            System.out.println("6. View Issued Books");
            System.out.println("7. View All Members");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String input = sc.nextLine();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    System.out.print("Book title: ");
                    String title = sc.nextLine();
                    System.out.print("Author: ");
                    String author = sc.nextLine();
                    System.out.print("Number of copies: ");
                    int copies = Integer.parseInt(sc.nextLine());
                    Book.addBook(title, author, copies);
                    break;

                case 2:
                    System.out.print("Member name: ");
                    String name = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    Member.addMember(name, email);
                    break;

                case 3:
                    System.out.print("Book ID: ");
                    int bookId = Integer.parseInt(sc.nextLine());
                    System.out.print("Member ID: ");
                    int memberId = Integer.parseInt(sc.nextLine());
                    Issue.issueBook(bookId, memberId);
                    break;

                case 4:
                    System.out.print("Copy ID to return: ");
                    int copyId = Integer.parseInt(sc.nextLine());
                    System.out.print("Member ID: ");
                    int returnMemberId = Integer.parseInt(sc.nextLine());
                    Issue.returnBook(copyId, returnMemberId);
                    break;

                case 5:
                    Book.listBooks();
                    break;

                case 6:
                    Issue.listIssuedBooks();
                    break;

                case 7:
                    Member.listMembers();
                    break;

                case 0:
                    System.out.println("Exiting system.");
                    break;

                default:
                    System.out.println("❌ Invalid option.");
            }

        } while (choice != 0);

        sc.close();
    }
}
