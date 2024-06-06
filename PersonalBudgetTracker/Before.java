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

abstract class Account {
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

    public abstract void displayAccountInfo();
}

class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(String accountNumber, double initialBalance, double interestRate) {
        super(accountNumber, "Savings", initialBalance);
        this.interestRate = interestRate;
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Savings Account - Account Number: " + getAccountNumber() + ", Balance: " + getBalance() + ", Interest Rate: " + interestRate);
    }
}

class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountNumber, double initialBalance, double overdraftLimit) {
        super(accountNumber, "Checking", initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Checking Account - Account Number: " + getAccountNumber() + ", Balance: " + getBalance() + ", Overdraft Limit: " + overdraftLimit);
    }
}

abstract class Transaction {
    private Date date;
    private double amount;
    private String description;

    public Transaction(Date date, double amount, String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionDetails() {
        return "Date: " + date + ", Amount: " + amount + ", Description: " + description;
    }

    public abstract void processTransaction(Account account);
}

class IncomeTransaction extends Transaction {
    public IncomeTransaction(Date date, double amount, String description) {
        super(date, amount, description);
    }

    @Override
    public void processTransaction(Account account) {
        account.deposit(getAmount());
    }
}

class ExpenseTransaction extends Transaction {
    public ExpenseTransaction(Date date, double amount, String description) {
        super(date, amount, description);
    }

    @Override
    public void processTransaction(Account account) {
        account.withdraw(getAmount());
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
    private List<Transaction> transactions;

    public Budget(Date startDate, Date endDate, double income) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.income = income;
        this.transactions = new ArrayList<>();
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void addTransaction(Transaction transaction, Account account) {
        transaction.processTransaction(account);
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public String getBudgetSummary() {
        double totalExpenses = 0;
        for (Transaction transaction : transactions) {
            if (transaction instanceof ExpenseTransaction) {
                totalExpenses += transaction.getAmount();
            }
        }
        return "Income: " + income + ", Total Expenses: " + totalExpenses + ", Balance: " + (income - totalExpenses);
    }

    public boolean isBudgetExceeded() {
        double totalExpenses = 0;
        for (Transaction transaction : transactions) {
            if (transaction instanceof ExpenseTransaction) {
                totalExpenses += transaction.getAmount();
            }
        }
        return totalExpenses > income;
    }
}

public class Before {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Budget budget = null;
        Account account = null;

        while (true) {
            System.out.println("\n--- Personal Budget Tracker ---");
            System.out.println("1. Set Budget");
            System.out.println("2. Add Income");
            System.out.println("3. Add Expense");
            System.out.println("4. View Budget Summary");
            System.out.println("5. Create Account");
            System.out.println("6. Display Account Info");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    budget = setBudget(scanner);
                    break;
                case 2:
                    if (budget == null || account == null) {
                        System.out.println("Please set the budget and create an account first.");
                    } else {
                        addIncome(scanner, budget, account);
                    }
                    break;
                case 3:
                    if (budget == null || account == null) {
                        System.out.println("Please set the budget and create an account first.");
                    } else {
                        addExpense(scanner, budget, account);
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
                    account = createAccount(scanner);
                    break;
                case 6:
                    if (account != null) {
                        account.displayAccountInfo();
                    } else {
                        System.out.println("Please create an account first.");
                    }
                    break;
                case 7:
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

    private static void addIncome(Scanner scanner, Budget budget, Account account) {
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            Date date = DATE_FORMAT.parse(scanner.nextLine());

            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();  // Consume newline

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            Transaction income = new IncomeTransaction(date, amount, description);
            income.processTransaction(account);  // Ensure account balance is updated
            budget.setIncome(budget.getIncome() + amount);  // Ensure income is updated in the budget

            System.out.println("Income added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }

    private static void addExpense(Scanner scanner, Budget budget, Account account) {
        try {
            System.out.print("Enter date (yyyy-MM-dd): ");
            Date date = DATE_FORMAT.parse(scanner.nextLine());

            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();  // Consume newline

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            Transaction expense = new ExpenseTransaction(date, amount, description);
            expense.processTransaction(account);
            budget.addTransaction(expense, account);

            System.out.println("Expense added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }

    private static Account createAccount(Scanner scanner) {
        System.out.println("1. Create Savings Account");
        System.out.println("2. Create Checking Account");
        System.out.print("Choose account type: ");
        int accountType = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        System.out.print("Enter initial balance: ");
        double initialBalance = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        if (accountType == 1) {
            System.out.print("Enter interest rate: ");
            double interestRate = scanner.nextDouble();
            scanner.nextLine();  // Consume newline
            return new SavingsAccount(accountNumber, initialBalance, interestRate);
        } else if (accountType == 2) {
            System.out.print("Enter overdraft limit: ");
            double overdraftLimit = scanner.nextDouble();
            scanner.nextLine();  // Consume newline
            return new CheckingAccount(accountNumber, initialBalance, overdraftLimit);
        } else {
            System.out.println("Invalid account type. Please try again.");
            return null;
        }
    }
}
