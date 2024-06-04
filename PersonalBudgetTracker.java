import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParseException;

class User {
    private String username;
    private String password;
    private List<Account> accounts;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    public Account getAccount(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }
}

class Account {
    private String accountNumber;
    private String accountType;
    private double balance;

    public Account(String accountNumber, String accountType, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = initialBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }
}

class Transaction {
    private Date date;
    private double amount;
    private String description;
    private Account account;

    public Transaction(Date date, double amount, String description, Account account) {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.account = account;
    }

    public double getAmount() {
        return amount;
    }

    public void recordTransaction() {
        if (amount < 0) {
            account.withdraw(-amount);
        } else {
            account.deposit(amount);
        }
    }

    public String getTransactionDetails() {
        return "Date: " + date + ", Amount: " + amount + ", Description: " + description + ", Account: " + account.getAccountNumber();
    }
}

class Category {
    private String name;
    private List<Transaction> expenses;

    public Category(String name) {
        this.name = name;
        this.expenses = new ArrayList<>();
    }

    public void addExpense(Transaction transaction) {
        expenses.add(transaction);
    }

    public void removeExpense(Transaction transaction) {
        expenses.remove(transaction);
    }

    public List<Transaction> getExpenses() {
        return expenses;
    }
}

class Budget {
    private Date startDate;
    private Date endDate;
    private double income;
    private List<Transaction> expenses;

    public Budget(Date startDate, Date endDate, double income) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.income = income;
        this.expenses = new ArrayList<>();
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void addExpense(Transaction transaction) {
        expenses.add(transaction);
    }

    public void removeExpense(Transaction transaction) {
        expenses.remove(transaction);
    }

    public String getBudgetSummary() {
        double totalExpenses = 0;
        for (Transaction transaction : expenses) {
            totalExpenses += transaction.getAmount();
        }
        return "Income: " + income + ", Total Expenses: " + totalExpenses + ", Balance: " + (income + totalExpenses);
    }

    public boolean isBudgetExceeded() {
        double totalExpenses = 0;
        for (Transaction transaction : expenses) {
            totalExpenses += transaction.getAmount();
        }
        return totalExpenses > income;
    }
}

public class PersonalBudgetTracker {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Budget budget = null;

        while (true) {
            System.out.println("\n--- Personal Budget Tracker ---");
            System.out.println("1. Set Budget");
            System.out.println("2. Add Income");
            System.out.println("3. Add Expense");
            System.out.println("4. View Budget Summary");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    budget = setBudget(scanner);
                    break;
                case 2:
                    if (budget == null) {
                        System.out.println("Please set the budget first.");
                    } else {
                        addIncome(scanner, budget);
                    }
                    break;
                case 3:
                    if (budget == null) {
                        System.out.println("Please set the budget first.");
                    } else {
                        addExpense(scanner, budget);
                        if (budget.isBudgetExceeded()) {
                            System.out.println("Warning: You have exceeded your budget!");
                        }
                    }
                    break;
                case 4:
                    if (budget == null) {
                        System.out.println("Please set the budget first.");
                    } else {
                        System.out.println(budget.getBudgetSummary());
                    }
                    break;
                case 5:
                    System.out.println("Exiting the program. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static Budget setBudget(Scanner scanner) {
        try {
            System.out.print("Enter start date (yyyy-MM-dd): ");
            Date startDate = DATE_FORMAT.parse(scanner.nextLine());

            System.out.print("Enter end date (yyyy-MM-dd): ");
            Date endDate = DATE_FORMAT.parse(scanner.nextLine());

            System.out.print("Enter income: ");
            double income = scanner.nextDouble();
            scanner.nextLine();  // Consume newline

            Budget budget = new Budget(startDate, endDate, income);
            System.out.println("Budget set successfully.");
            return budget;
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
            return null;
        }
    }

    private static void addIncome(Scanner scanner, Budget budget) {
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            Date date = DATE_FORMAT.parse(scanner.nextLine());

            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();  // Consume newline

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            Account account = new Account("000000", "General", 0.0);
            Transaction income = new Transaction(date, amount, description, account);
            income.recordTransaction();
            budget.addExpense(income);

            System.out.println("Income added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }

    private static void addExpense(Scanner scanner, Budget budget) {
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            Date date = DATE_FORMAT.parse(scanner.nextLine());

            System.out.print("Enter amount: ");
            double amount = -scanner.nextDouble();  // Negative for expense
            scanner.nextLine();  // Consume newline

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            Account account = new Account("000000", "General", 0.0);
            Transaction expense = new Transaction(date, amount, description, account);
            expense.recordTransaction();
            budget.addExpense(expense);

            System.out.println("Expense added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }
}
