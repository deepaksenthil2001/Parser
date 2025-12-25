import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 * LibraryApp.java
 *
 * A comprehensive console-based Library Management System in a single file.
 * No external libraries. Data persisted as simple CSV-style text files.
 *
 * Features:
 * - Add / Update / Delete Books
 * - Add / Update / Delete Members
 * - Borrow / Return books with due date and fines
 * - List/Search/Sort books & members
 * - Reports: overdue books, popular books, member loan history
 * - Simple admin authentication
 *
 * Author: ChatGPT (generated)
 * Date: 2025
 *
 * Notes:
 * - This file contains multiple classes (only LibraryApp is public) so keep it
 *   in one .java file.
 * - Data files used: books.db, members.db, loans.db (CSV-style)
 *
 * Extending:
 * - Move persistence to JSON or DB for production
 * - Add unit tests, GUI, web API, or concurrency handling
 *
 */

/* ---------------------------------------------------------------------
 * Entry point: LibraryApp (public)
 * --------------------------------------------------------------------- */
public class LibraryApp {

    // Filenames used for persistence
    private static final String BOOKS_FILE = "books.db";
    private static final String MEMBERS_FILE = "members.db";
    private static final String LOANS_FILE = "loans.db";

    // Date format used in persistence
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    // Application main components
    private static Library library;
    private static ConsoleUI ui;

    public static void main(String[] args) {
        // Initialize components
        library = new Library();
        ui = new ConsoleUI();

        // Load data from disk
        try {
            library.loadAll();
        } catch (Exception e) {
            System.out.println("Warning: Failed to load saved data. Starting with empty library.");
        }

        // Run main menu loop
        ui.showWelcome();
        mainMenuLoop();
        ui.showGoodbye();

        // Save data before exit
        try {
            library.saveAll();
        } catch (Exception e) {
            System.out.println("Error saving data on exit: " + e.getMessage());
        }
    }

    private static void mainMenuLoop() {
        boolean running = true;

        while (running) {
            ui.printMainMenu();
            int choice = ui.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> adminLogin();
                case 2 -> memberLoginOrRegister();
                case 3 -> browseBooksAsGuest();
                case 4 -> {
                    running = false;
                }
                default -> ui.println("Invalid choice. Try again.");
            }
        }
    }

    // Admin login flow
    private static void adminLogin() {
        ui.println("\n-- Admin Login --");
        String username = ui.readString("Username: ");
        String password = ui.readString("Password: ");
        // For demo: hard-coded admin credentials. Replace with secure auth in real app.
        if ("admin".equals(username) && "admin123".equals(password)) {
            ui.println("Admin authenticated.");
            adminMenu();
        } else {
            ui.println("Invalid admin credentials.");
        }
    }

    // Admin menu
    private static void adminMenu() {
        boolean back = false;
        while (!back) {
            ui.printAdminMenu();
            int choice = ui.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> bookManagementMenu();
                case 2 -> memberManagementMenu();
                case 3 -> loanManagementMenu();
                case 4 -> reportsMenu();
                case 5 -> {
                    try {
                        library.saveAll();
                        ui.println("Data saved.");
                    } catch (Exception e) {
                        ui.println("Error saving: " + e.getMessage());
                    }
                }
                case 6 -> back = true;
                default -> ui.println("Invalid option.");
            }
        }
    }

    // Book management menu (CRUD)
    private static void bookManagementMenu() {
        boolean back = false;
        while (!back) {
            ui.printBookMenu();
            int choice = ui.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> addBook();
                case 2 -> updateBook();
                case 3 -> deleteBook();
                case 4 -> listBooks();
                case 5 -> searchBooks();
                case 6 -> back = true;
                default -> ui.println("Invalid option.");
            }
        }
    }

    // Add a book
    private static void addBook() {
        ui.println("\n-- Add New Book --");
        String title = ui.readString("Title: ");
        String author = ui.readString("Author(s): ");
        String isbn = ui.readString("ISBN (unique): ");
        int year = ui.readInt("Publication Year: ");
        int copies = ui.readInt("Number of Copies: ");

        Book b = new Book(library.generateBookId(), title, author, isbn, year, copies);
        boolean ok = library.addBook(b);
        if (ok) ui.println("Book added successfully.");
        else ui.println("Failed to add book (ISBN might be duplicate).");
    }

    // Update a book
    private static void updateBook() {
        ui.println("\n-- Update Book --");
        int id = ui.readInt("Book ID: ");
        Book b = library.getBookById(id);
        if (b == null) {
            ui.println("Book not found.");
            return;
        }
        ui.println("Current: " + b);
        String title = ui.readStringDefault("New Title (leave blank to keep): ", b.getTitle());
        String author = ui.readStringDefault("New Author(s) (leave blank to keep): ", b.getAuthor());
        String isbn = ui.readStringDefault("New ISBN (leave blank to keep): ", b.getIsbn());
        int year = ui.readIntDefault("New Publication Year (0 to keep): ", b.getPublicationYear());
        int copies = ui.readIntDefault("New Total Copies (0 to keep): ", b.getTotalCopies());

        b.setTitle(title);
        b.setAuthor(author);
        b.setIsbn(isbn);
        if (year > 0) b.setPublicationYear(year);
        if (copies > 0) b.setTotalCopies(copies);

        ui.println("Book updated.");
    }

    // Delete a book
    private static void deleteBook() {
        ui.println("\n-- Delete Book --");
        int id = ui.readInt("Book ID to delete: ");
        boolean ok = library.deleteBook(id);
        if (ok) ui.println("Book deleted.");
        else ui.println("Book not found or cannot delete (may have active loans).");
    }

    // List books with sorting options
    private static void listBooks() {
        ui.println("\n-- List Books --");
        ui.println("Sort by: 1) ID 2) Title 3) Author 4) Year 5) Available copies 6) ISBN");
        int sortChoice = ui.readInt("Choose sort option: ");
        List<Book> books = library.listBooks();
        switch (sortChoice) {
            case 2 -> books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
            case 3 -> books.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
            case 4 -> books.sort(Comparator.comparingInt(Book::getPublicationYear).reversed());
            case 5 -> books.sort(Comparator.comparingInt(Book::getAvailableCopies).reversed());
            case 6 -> books.sort(Comparator.comparing(Book::getIsbn, String.CASE_INSENSITIVE_ORDER));
            default -> books.sort(Comparator.comparingInt(Book::getId));
        }
        for (Book b : books) {
            ui.println(b.toDetailedString());
        }
    }

    // Search books by multiple fields
    private static void searchBooks() {
        ui.println("\n-- Search Books --");
        String term = ui.readString("Enter search term (title/author/isbn): ").toLowerCase();
        List<Book> results = library.searchBooks(term);
        if (results.isEmpty()) {
            ui.println("No books found.");
            return;
        }
        ui.println("Found " + results.size() + " book(s):");
        for (Book b : results) ui.println(b.toDetailedString());
    }

    // Member management menu
    private static void memberManagementMenu() {
        boolean back = false;
        while (!back) {
            ui.printMemberMenu();
            int choice = ui.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> addMember();
                case 2 -> updateMember();
                case 3 -> deleteMember();
                case 4 -> listMembers();
                case 5 -> searchMembers();
                case 6 -> back = true;
                default -> ui.println("Invalid option.");
            }
        }
    }

    // Add member
    private static void addMember() {
        ui.println("\n-- Add Member --");
        String name = ui.readString("Name: ");
        String email = ui.readString("Email: ");
        String phone = ui.readString("Phone: ");
        Member m = new Member(library.generateMemberId(), name, email, phone);
        library.addMember(m);
        ui.println("Member added. ID: " + m.getId());
    }

    // Update member
    private static void updateMember() {
        ui.println("\n-- Update Member --");
        int id = ui.readInt("Member ID: ");
        Member m = library.getMemberById(id);
        if (m == null) {
            ui.println("Member not found.");
            return;
        }
        ui.println("Current: " + m);
        String name = ui.readStringDefault("New Name (blank to keep): ", m.getName());
        String email = ui.readStringDefault("New Email (blank to keep): ", m.getEmail());
        String phone = ui.readStringDefault("New Phone (blank to keep): ", m.getPhone());
        m.setName(name);
        m.setEmail(email);
        m.setPhone(phone);
        ui.println("Member updated.");
    }

    // Delete member (only if no active loans)
    private static void deleteMember() {
        ui.println("\n-- Delete Member --");
        int id = ui.readInt("Member ID to delete: ");
        boolean ok = library.deleteMember(id);
        if (ok) ui.println("Member deleted.");
        else ui.println("Cannot delete member (not found or has active loans).");
    }

    // List members
    private static void listMembers() {
        ui.println("\n-- List Members --");
        List<Member> members = library.listMembers();
        members.sort(Comparator.comparingInt(Member::getId));
        for (Member m : members) ui.println(m.toString());
    }

    // Search members
    private static void searchMembers() {
        ui.println("\n-- Search Members --");
        String term = ui.readString("Enter search term (name/email/phone): ").toLowerCase();
        List<Member> results = library.searchMembers(term);
        if (results.isEmpty()) ui.println("No members found.");
        else {
            ui.println("Found " + results.size() + " member(s):");
            for (Member m : results) ui.println(m.toString());
        }
    }

    // Loan management menu
    private static void loanManagementMenu() {
        boolean back = false;
        while (!back) {
            ui.printLoanMenu();
            int choice = ui.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> borrowBookFlow();
                case 2 -> returnBookFlow();
                case 3 -> listLoans();
                case 4 -> memberLoanHistory();
                case 5 -> back = true;
                default -> ui.println("Invalid option.");
            }
        }
    }

    // Borrow book
    private static void borrowBookFlow() {
        ui.println("\n-- Borrow Book --");
        int memberId = ui.readInt("Member ID: ");
        Member m = library.getMemberById(memberId);
        if (m == null) {
            ui.println("Member not found.");
            return;
        }
        int bookId = ui.readInt("Book ID: ");
        Book b = library.getBookById(bookId);
        if (b == null) {
            ui.println("Book not found.");
            return;
        }

        // Check availability
        if (b.getAvailableCopies() <= 0) {
            ui.println("No copies available for this book.");
            return;
        }

        // Borrow period
        int days = ui.readIntDefault("Days to borrow (default 14): ", 14);
        LocalDate due = LocalDate.now().plusDays(days);
        Loan loan = library.borrowBook(memberId, bookId, due);

        if (loan != null) {
            ui.println("Book borrowed successfully. Loan ID: " + loan.getId() + ", Due: " + due.format(DATE_FORMAT));
        } else {
            ui.println("Failed to borrow book.");
        }
    }

    // Return book
    private static void returnBookFlow() {
        ui.println("\n-- Return Book --");
        int loanId = ui.readInt("Loan ID: ");
        Loan loan = library.getLoanById(loanId);
        if (loan == null) {
            ui.println("Loan not found.");
            return;
        }
        if (loan.isReturned()) {
            ui.println("Book already returned on " + loan.getReturnDate().format(DATE_FORMAT));
            return;
        }
        LocalDate returnDate = LocalDate.now();
        double fine = library.returnBook(loanId, returnDate);
        ui.println("Book returned. Fine: " + String.format("%.2f", fine));
    }

    // List all loans
    private static void listLoans() {
        ui.println("\n-- All Loans --");
        List<Loan> loans = library.listLoans();
        for (Loan l : loans) ui.println(l.toString());
    }

    // Member loan history
    private static void memberLoanHistory() {
        ui.println("\n-- Member Loan History --");
        int memberId = ui.readInt("Member ID: ");
        Member m = library.getMemberById(memberId);
        if (m == null) {
            ui.println("Member not found.");
            return;
        }
        List<Loan> history = library.getLoansByMember(memberId);
        ui.println("Loan history for " + m.getName() + " (ID: " + memberId + "):");
        for (Loan l : history) ui.println(l.toString());
    }

    // Reports menu
    private static void reportsMenu() {
        boolean back = false;
        while (!back) {
            ui.printReportsMenu();
            int choice = ui.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> reportOverdueBooks();
                case 2 -> reportMostBorrowed();
                case 3 -> reportActiveMembers();
                case 4 -> back = true;
                default -> ui.println("Invalid option.");
            }
        }
    }

    // Overdue report
    private static void reportOverdueBooks() {
        ui.println("\n-- Overdue Books Report --");
        List<Loan> overdue = library.getOverdueLoans(LocalDate.now());
        if (overdue.isEmpty()) {
            ui.println("No overdue loans.");
            return;
        }
        for (Loan l : overdue) {
            Member m = library.getMemberById(l.getMemberId());
            Book b = library.getBookById(l.getBookId());
            ui.println(String.format("LoanID: %d | Member: %s (ID:%d) | Book: %s (ID:%d) | Due: %s | Days Overdue: %d",
                    l.getId(),
                    m != null ? m.getName() : "Unknown",
                    l.getMemberId(),
                    b != null ? b.getTitle() : "Unknown",
                    l.getBookId(),
                    l.getDueDate().format(DATE_FORMAT),
                    ChronoUnit.DAYS.between(l.getDueDate(), LocalDate.now())
            ));
        }
    }

    // Most borrowed books
    private static void reportMostBorrowed() {
        ui.println("\n-- Most Borrowed Books --");
        Map<Integer, Integer> counts = library.getBorrowCounts();
        if (counts.isEmpty()) {
            ui.println("No borrowing activity yet.");
            return;
        }
        // Convert to list of pairs
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue()); // descending
        int show = Math.min(10, list.size());
        for (int i = 0; i < show; i++) {
            int bookId = list.get(i).getKey();
            int count = list.get(i).getValue();
            Book book = library.getBookById(bookId);
            ui.println(String.format("%d) %s (ID:%d) - Borrowed %d time(s)",
                    i + 1,
                    book != null ? book.getTitle() : "Unknown",
                    bookId,
                    count));
        }
    }

    // Active members (by number of loans)
    private static void reportActiveMembers() {
        ui.println("\n-- Active Members --");
        Map<Integer, Integer> counts = library.getMemberLoanCounts();
        if (counts.isEmpty()) {
            ui.println("No borrowing activity yet.");
            return;
        }
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        int show = Math.min(10, list.size());
        for (int i = 0; i < show; i++) {
            int memberId = list.get(i).getKey();
            int count = list.get(i).getValue();
            Member m = library.getMemberById(memberId);
            ui.println(String.format("%d) %s (ID:%d) - %d loan(s)",
                    i + 1,
                    m != null ? m.getName() : "Unknown",
                    memberId,
                    count));
        }
    }

    // Member login or registration flow
    private static void memberLoginOrRegister() {
        ui.println("\n-- Member Access --");
        ui.println("1) Login by ID  2) Register new member  3) Back");
        int c = ui.readInt("Choice: ");
        if (c == 1) {
            int id = ui.readInt("Enter Member ID: ");
            Member m = library.getMemberById(id);
            if (m == null) {
                ui.println("Member not found.");
                return;
            }
            memberMenu(m);
        } else if (c == 2) {
            addMember();
        } else {
            return;
        }
    }

    // Member menu after login
    private static void memberMenu(Member member) {
        ui.println("\nWelcome, " + member.getName() + "!");
        boolean back = false;
        while (!back) {
            ui.printMemberOptions();
            int choice = ui.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> browseBooksAsMember(member);
                case 2 -> borrowAsMember(member);
                case 3 -> returnAsMember(member);
                case 4 -> showMemberLoans(member);
                case 5 -> back = true;
                default -> ui.println("Invalid option.");
            }
        }
    }

    // Guest browsing
    private static void browseBooksAsGuest() {
        ui.println("\n-- Browse Books (Guest) --");
        listBooks();
    }

    // Member browse
    private static void browseBooksAsMember(Member m) {
        ui.println("\n-- Browse Books --");
        listBooks();
    }

    // Member borrow
    private static void borrowAsMember(Member m) {
        ui.println("\n-- Borrow Book (Member) --");
        int bookId = ui.readInt("Book ID: ");
        Book b = library.getBookById(bookId);
        if (b == null) {
            ui.println("Book not found.");
            return;
        }
        if (b.getAvailableCopies() <= 0) {
            ui.println("No copies available.");
            return;
        }
        int days = ui.readIntDefault("Days to borrow (default 14): ", 14);
        LocalDate due = LocalDate.now().plusDays(days);
        Loan loan = library.borrowBook(m.getId(), bookId, due);
        if (loan != null) ui.println("Borrowed. Loan ID: " + loan.getId() + ", Due: " + due.format(DATE_FORMAT));
        else ui.println("Failed to borrow.");
    }

    // Member return
    private static void returnAsMember(Member member) {
        ui.println("\n-- Return Book (Member) --");
        int loanId = ui.readInt("Loan ID: ");
        Loan loan = library.getLoanById(loanId);
        if (loan == null) {
            ui.println("Loan not found.");
            return;
        }
        if (loan.getMemberId() != member.getId()) {
            ui.println("This loan does not belong to you.");
            return;
        }
        if (loan.isReturned()) {
            ui.println("Book already returned on " + loan.getReturnDate().format(DATE_FORMAT));
            return;
        }
        double fine = library.returnBook(loanId, LocalDate.now());
        ui.println("Book returned. Fine: " + String.format("%.2f", fine));
    }

    // Show member's active loans
    private static void showMemberLoans(Member m) {
        ui.println("\n-- Your Loans --");
        List<Loan> active = library.getActiveLoansByMember(m.getId());
        if (active.isEmpty()) {
            ui.println("No active loans.");
            return;
        }
        for (Loan l : active) ui.println(l.toString());
    }

    /* -----------------------------------------------------------------
     * Inner classes for models and library logic
     * ----------------------------------------------------------------- */

    // Simple console UI helper
    static class ConsoleUI {
        private final Scanner scanner = new Scanner(System.in);

        void showWelcome() {
            println("===========================================");
            println("   Welcome to the Console Library System   ");
            println("===========================================");
        }

        void showGoodbye() {
            println("\nThank you for using the Library System. Goodbye!");
        }

        void printMainMenu() {
            println("\nMAIN MENU");
            println("1) Admin Login");
            println("2) Member Access");
            println("3) Browse Books (Guest)");
            println("4) Exit");
        }

        void printAdminMenu() {
            println("\nADMIN MENU");
            println("1) Book Management");
            println("2) Member Management");
            println("3) Loan Management");
            println("4) Reports");
            println("5) Save Data");
            println("6) Back to Main Menu");
        }

        void printBookMenu() {
            println("\nBOOK MANAGEMENT");
            println("1) Add Book");
            println("2) Update Book");
            println("3) Delete Book");
            println("4) List Books");
            println("5) Search Books");
            println("6) Back");
        }

        void printMemberMenu() {
            println("\nMEMBER MANAGEMENT");
            println("1) Add Member");
            println("2) Update Member");
            println("3) Delete Member");
            println("4) List Members");
            println("5) Search Members");
            println("6) Back");
        }

        void printLoanMenu() {
            println("\nLOAN MANAGEMENT");
            println("1) Borrow Book");
            println("2) Return Book");
            println("3) List All Loans");
            println("4) Member Loan History");
            println("5) Back");
        }

        void printReportsMenu() {
            println("\nREPORTS");
            println("1) Overdue Books");
            println("2) Most Borrowed Books");
            println("3) Active Members");
            println("4) Back");
        }

        void printMemberOptions() {
            println("\nMEMBER MENU");
            println("1) Browse Books");
            println("2) Borrow Book");
            println("3) Return Book");
            println("4) My Loans");
            println("5) Back");
        }

        void println(String s) {
            System.out.println(s);
        }

        String readString(String prompt) {
            System.out.print(prompt);
            return scanner.nextLine().trim();
        }

        String readStringDefault(String prompt, String def) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return def;
            return s;
        }

        int readInt(String prompt) {
            while (true) {
                System.out.print(prompt);
                String s = scanner.nextLine().trim();
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }
        }

        int readIntDefault(String prompt, int def) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return def;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, using default: " + def);
                return def;
            }
        }
    }

    // Library holds collections and persistence
    static class Library {
        private final Map<Integer, Book> books = new HashMap<>();
        private final Map<Integer, Member> members = new HashMap<>();
        private final Map<Integer, Loan> loans = new HashMap<>();

        // Simple counters for IDs
        private int bookIdCounter = 1000;
        private int memberIdCounter = 5000;
        private int loanIdCounter = 8000;

        // In-memory stats
        private final Map<Integer, Integer> borrowCounts = new HashMap<>(); // bookId -> count
        private final Map<Integer, Integer> memberLoanCounts = new HashMap<>(); // memberId -> count

        // Business rules
        private static final double FINE_PER_DAY = 1.0; // currency units per overdue day

        /* -------------------- Persistence -------------------- */

        void loadAll() throws IOException {
            loadBooks();
            loadMembers();
            loadLoans();
        }

        void saveAll() throws IOException {
            saveBooks();
            saveMembers();
            saveLoans();
        }

        private void loadBooks() throws IOException {
            books.clear();
            Path p = Paths.get(BOOKS_FILE);
            if (!Files.exists(p)) return;
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                // CSV: id|title|author|isbn|year|totalCopies|availableCopies
                String[] parts = line.split("\\|", -1);
                try {
                    int id = Integer.parseInt(parts[0]);
                    String title = unescape(parts[1]);
                    String author = unescape(parts[2]);
                    String isbn = parts[3];
                    int year = Integer.parseInt(parts[4]);
                    int totalCopies = Integer.parseInt(parts[5]);
                    int availableCopies = Integer.parseInt(parts[6]);
                    Book b = new Book(id, title, author, isbn, year, totalCopies);
                    b.setAvailableCopies(availableCopies);
                    books.put(id, b);
                    bookIdCounter = Math.max(bookIdCounter, id + 1);
                } catch (Exception e) {
                    // skip invalid lines
                    System.out.println("Skipping invalid book line: " + line);
                }
            }
        }

        private void saveBooks() throws IOException {
            Path p = Paths.get(BOOKS_FILE);
            List<String> lines = new ArrayList<>();
            for (Book b : books.values()) {
                // id|title|author|isbn|year|totalCopies|availableCopies
                String line = String.join("|",
                        String.valueOf(b.getId()),
                        escape(b.getTitle()),
                        escape(b.getAuthor()),
                        b.getIsbn(),
                        String.valueOf(b.getPublicationYear()),
                        String.valueOf(b.getTotalCopies()),
                        String.valueOf(b.getAvailableCopies())
                );
                lines.add(line);
            }
            Files.write(p, lines);
        }

        private void loadMembers() throws IOException {
            members.clear();
            Path p = Paths.get(MEMBERS_FILE);
            if (!Files.exists(p)) return;
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                // id|name|email|phone
                String[] parts = line.split("\\|", -1);
                try {
                    int id = Integer.parseInt(parts[0]);
                    String name = unescape(parts[1]);
                    String email = unescape(parts[2]);
                    String phone = unescape(parts[3]);
                    Member m = new Member(id, name, email, phone);
                    members.put(id, m);
                    memberIdCounter = Math.max(memberIdCounter, id + 1);
                } catch (Exception e) {
                    System.out.println("Skipping invalid member line: " + line);
                }
            }
        }

        private void saveMembers() throws IOException {
            Path p = Paths.get(MEMBERS_FILE);
            List<String> lines = new ArrayList<>();
            for (Member m : members.values()) {
                String line = String.join("|",
                        String.valueOf(m.getId()),
                        escape(m.getName()),
                        escape(m.getEmail()),
                        escape(m.getPhone())
                );
                lines.add(line);
            }
            Files.write(p, lines);
        }

        private void loadLoans() throws IOException {
            loans.clear();
            borrowCounts.clear();
            memberLoanCounts.clear();
            Path p = Paths.get(LOANS_FILE);
            if (!Files.exists(p)) return;
            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                // id|memberId|bookId|loanDate|dueDate|returnDate|finePaid
                String[] parts = line.split("\\|", -1);
                try {
                    int id = Integer.parseInt(parts[0]);
                    int memberId = Integer.parseInt(parts[1]);
                    int bookId = Integer.parseInt(parts[2]);
                    LocalDate loanDate = LocalDate.parse(parts[3], DATE_FORMAT);
                    LocalDate dueDate = LocalDate.parse(parts[4], DATE_FORMAT);
                    LocalDate returnDate = parts[5].isEmpty() ? null : LocalDate.parse(parts[5], DATE_FORMAT);
                    double finePaid = parts.length > 6 && !parts[6].isEmpty() ? Double.parseDouble(parts[6]) : 0.0;
                    Loan loan = new Loan(id, memberId, bookId, loanDate, dueDate);
                    loan.setReturnDate(returnDate);
                    loan.setFinePaid(finePaid);
                    loans.put(id, loan);
                    loanIdCounter = Math.max(loanIdCounter, id + 1);

                    // rebuild stats and availability
                    borrowCounts.put(bookId, borrowCounts.getOrDefault(bookId, 0) + 1);
                    memberLoanCounts.put(memberId, memberLoanCounts.getOrDefault(memberId, 0) + 1);
                    // Adjust available copies if loan not returned
                    if (!loan.isReturned()) {
                        Book b = books.get(bookId);
                        if (b != null) b.decrementAvailable();
                    }
                } catch (Exception e) {
                    System.out.println("Skipping invalid loan line: " + line);
                }
            }
        }

        private void saveLoans() throws IOException {
            Path p = Paths.get(LOANS_FILE);
            List<String> lines = new ArrayList<>();
            for (Loan l : loans.values()) {
                // id|memberId|bookId|loanDate|dueDate|returnDate|finePaid
                String line = String.join("|",
                        String.valueOf(l.getId()),
                        String.valueOf(l.getMemberId()),
                        String.valueOf(l.getBookId()),
                        l.getLoanDate().format(DATE_FORMAT),
                        l.getDueDate().format(DATE_FORMAT),
                        l.getReturnDate() == null ? "" : l.getReturnDate().format(DATE_FORMAT),
                        String.format(Locale.ROOT, "%.2f", l.getFinePaid())
                );
                lines.add(line);
            }
            Files.write(p, lines);
        }

        /* -------------------- Book Operations -------------------- */

        synchronized int generateBookId() {
            return bookIdCounter++;
        }

        synchronized boolean addBook(Book b) {
            // Check unique ISBN
            for (Book existing : books.values()) {
                if (existing.getIsbn() != null && !existing.getIsbn().isEmpty()
                        && existing.getIsbn().equalsIgnoreCase(b.getIsbn())) {
                    return false;
                }
            }
            books.put(b.getId(), b);
            return true;
        }

        synchronized Book getBookById(int id) {
            return books.get(id);
        }

        synchronized boolean deleteBook(int id) {
            // cannot delete if any active loan exists for this book
            for (Loan l : loans.values()) {
                if (l.getBookId() == id && !l.isReturned()) return false;
            }
            return books.remove(id) != null;
        }

        synchronized List<Book> listBooks() {
            return new ArrayList<>(books.values());
        }

        synchronized List<Book> searchBooks(String term) {
            term = term.toLowerCase();
            List<Book> results = new ArrayList<>();
            for (Book b : books.values()) {
                if ((b.getTitle() != null && b.getTitle().toLowerCase().contains(term))
                        || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(term))
                        || (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(term))) {
                    results.add(b);
                }
            }
            return results;
        }

        /* -------------------- Member Operations -------------------- */

        synchronized int generateMemberId() {
            return memberIdCounter++;
        }

        synchronized void addMember(Member m) {
            members.put(m.getId(), m);
        }

        synchronized Member getMemberById(int id) {
            return members.get(id);
        }

        synchronized boolean deleteMember(int id) {
            // can delete only if no active loans
            for (Loan l : loans.values()) {
                if (l.getMemberId() == id && !l.isReturned()) return false;
            }
            return members.remove(id) != null;
        }

        synchronized List<Member> listMembers() {
            return new ArrayList<>(members.values());
        }

        synchronized List<Member> searchMembers(String term) {
            term = term.toLowerCase();
            List<Member> results = new ArrayList<>();
            for (Member m : members.values()) {
                if ((m.getName() != null && m.getName().toLowerCase().contains(term))
                        || (m.getEmail() != null && m.getEmail().toLowerCase().contains(term))
                        || (m.getPhone() != null && m.getPhone().toLowerCase().contains(term))) {
                    results.add(m);
                }
            }
            return results;
        }

        /* -------------------- Loan Operations -------------------- */

        synchronized int generateLoanId() {
            return loanIdCounter++;
        }

        synchronized Loan borrowBook(int memberId, int bookId, LocalDate dueDate) {
            // validation
            Member member = members.get(memberId);
            Book book = books.get(bookId);
            if (member == null || book == null) return null;
            if (book.getAvailableCopies() <= 0) return null;

            LocalDate loanDate = LocalDate.now();
            int loanId = generateLoanId();
            Loan loan = new Loan(loanId, memberId, bookId, loanDate, dueDate);
            loans.put(loanId, loan);

            // update stats and availability
            borrowCounts.put(bookId, borrowCounts.getOrDefault(bookId, 0) + 1);
            memberLoanCounts.put(memberId, memberLoanCounts.getOrDefault(memberId, 0) + 1);
            book.decrementAvailable();

            return loan;
        }

        synchronized double returnBook(int loanId, LocalDate returnDate) {
            Loan loan = loans.get(loanId);
            if (loan == null) return 0.0;
            if (loan.isReturned()) return loan.getFinePaid(); // already processed
            loan.setReturnDate(returnDate);
            // calculate fine if overdue
            long overdueDays = 0;
            if (returnDate.isAfter(loan.getDueDate())) {
                overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
            }
            double fine = overdueDays * FINE_PER_DAY;
            loan.setFinePaid(fine);

            // make book available again
            Book b = books.get(loan.getBookId());
            if (b != null) b.incrementAvailable();

            return fine;
        }

        synchronized Loan getLoanById(int loanId) {
            return loans.get(loanId);
        }

        synchronized List<Loan> listLoans() {
            return new ArrayList<>(loans.values());
        }

        synchronized List<Loan> getLoansByMember(int memberId) {
            List<Loan> out = new ArrayList<>();
            for (Loan l : loans.values()) if (l.getMemberId() == memberId) out.add(l);
            out.sort(Comparator.comparing(Loan::getLoanDate).reversed());
            return out;
        }

        synchronized List<Loan> getActiveLoansByMember(int memberId) {
            List<Loan> out = new ArrayList<>();
            for (Loan l : loans.values()) if (l.getMemberId() == memberId && !l.isReturned()) out.add(l);
            out.sort(Comparator.comparing(Loan::getLoanDate).reversed());
            return out;
        }

        synchronized List<Loan> getOverdueLoans(LocalDate asOfDate) {
            List<Loan> out = new ArrayList<>();
            for (Loan l : loans.values()) {
                if (!l.isReturned() && l.getDueDate().isBefore(asOfDate)) out.add(l);
            }
            out.sort(Comparator.comparing(Loan::getDueDate));
            return out;
        }

        synchronized Map<Integer, Integer> getBorrowCounts() {
            return new HashMap<>(borrowCounts);
        }

        synchronized Map<Integer, Integer> getMemberLoanCounts() {
            return new HashMap<>(memberLoanCounts);
        }

        private String escape(String s) {
            if (s == null) return "";
            return s.replace("\n", "\\n").replace("|", "\\|");
        }

        private String unescape(String s) {
            if (s == null) return "";
            return s.replace("\\n", "\n").replace("\\|", "|");
        }
    }

    /* -------------------- Model: Book -------------------- */

    static class Book {
        private final int id;
        private String title;
        private String author;
        private String isbn;
        private int publicationYear;
        private int totalCopies;
        private int availableCopies;

        Book(int id, String title, String author, String isbn, int publicationYear, int totalCopies) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.isbn = isbn;
            this.publicationYear = publicationYear;
            this.totalCopies = Math.max(0, totalCopies);
            this.availableCopies = Math.max(0, totalCopies);
        }

        int getId() { return id; }
        String getTitle() { return title; }
        String getAuthor() { return author; }
        String getIsbn() { return isbn; }
        int getPublicationYear() { return publicationYear; }
        int getTotalCopies() { return totalCopies; }
        int getAvailableCopies() { return availableCopies; }

        void setTitle(String title) { this.title = title; }
        void setAuthor(String author) { this.author = author; }
        void setIsbn(String isbn) { this.isbn = isbn; }
        void setPublicationYear(int year) { this.publicationYear = year; }
        void setTotalCopies(int total) {
            if (total < 0) return;
            int diff = total - this.totalCopies;
            this.totalCopies = total;
            this.availableCopies = Math.max(0, this.availableCopies + diff);
        }
        void setAvailableCopies(int available) { this.availableCopies = available; }

        void decrementAvailable() { if (availableCopies > 0) availableCopies--; }
        void incrementAvailable() { if (availableCopies < totalCopies) availableCopies++; }

        String toDetailedString() {
            return String.format("ID:%d | %s | Author:%s | ISBN:%s | Year:%d | Total:%d | Available:%d",
                    id, title, author, isbn, publicationYear, totalCopies, availableCopies);
        }

        @Override
        public String toString() {
            return String.format("Book[%d] %s by %s (ISBN:%s)", id, title, author, isbn);
        }
    }

    /* -------------------- Model: Member -------------------- */

    static class Member {
        private final int id;
        private String name;
        private String email;
        private String phone;

        Member(int id, String name, String email, String phone) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        int getId() { return id; }
        String getName() { return name; }
        String getEmail() { return email; }
        String getPhone() { return phone; }

        void setName(String name) { this.name = name; }
        void setEmail(String email) { this.email = email; }
        void setPhone(String phone) { this.phone = phone; }

        @Override
        public String toString() {
            return String.format("Member[%d] %s | Email:%s | Phone:%s", id, name, email, phone);
        }
    }

    /* -------------------- Model: Loan -------------------- */

    static class Loan {
        private final int id;
        private final int memberId;
        private final int bookId;
        private final LocalDate loanDate;
        private final LocalDate dueDate;
        private LocalDate returnDate;
        private double finePaid;

        Loan(int id, int memberId, int bookId, LocalDate loanDate, LocalDate dueDate) {
            this.id = id;
            this.memberId = memberId;
            this.bookId = bookId;
            this.loanDate = loanDate;
            this.dueDate = dueDate;
            this.returnDate = null;
            this.finePaid = 0.0;
        }

        int getId() { return id; }
        int getMemberId() { return memberId; }
        int getBookId() { return bookId; }
        LocalDate getLoanDate() { return loanDate; }
        LocalDate getDueDate() { return dueDate; }
        LocalDate getReturnDate() { return returnDate; }
        double getFinePaid() { return finePaid; }

        void setReturnDate(LocalDate d) { this.returnDate = d; }
        void setFinePaid(double f) { this.finePaid = f; }

        boolean isReturned() { return returnDate != null; }

        @Override
        public String toString() {
            return String.format("Loan[%d] Member:%d Book:%d LoanDate:%s Due:%s Returned:%s Fine:%.2f",
                    id, memberId, bookId,
                    loanDate.format(DATE_FORMAT),
                    dueDate.format(DATE_FORMAT),
                    returnDate == null ? "N" : returnDate.format(DATE_FORMAT),
                    finePaid);
        }
    }
}
