import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void processTransaction(Transaction transaction) {
        if (transaction instanceof IncomeTransaction) {
            deposit(transaction.getAmount());
        } else if (transaction instanceof ExpenseTransaction) {
            withdraw(transaction.getAmount());
        }
    }
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
        account.processTransaction(transaction);
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

public class After {
    public static void main(String[] args) {
        PersonalBudgetTracker personalBudgetTracker = new PersonalBudgetTracker();
        personalBudgetTracker.start();
    }
}

class PersonalBudgetTracker {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Budget budget;
    private Account account;
    private Scanner scanner;
    private Map<Integer, Runnable> menuOptions;

    public PersonalBudgetTracker() {
        this.budget = null;
        this.account = null;
        this.scanner = new Scanner(System.in);
        this.menuOptions = new HashMap<>();
        initializeMenuOptions();
    }

    private void initializeMenuOptions() {
        menuOptions.put(1, this::setBudget);
        menuOptions.put(2, this::addIncome);
        menuOptions.put(3, this::addExpense);
        menuOptions.put(4, this::viewBudgetSummary);
        menuOptions.put(5, this::createAccount);
        menuOptions.put(6, this::displayAccountInfo);
        menuOptions.put(7, this::exitProgram);
    }

    public void start() {
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            Runnable selectedOption = menuOptions.get(choice);
            if (selectedOption != null) {
                selectedOption.run();
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Personal Budget Tracker ---");
        System.out.println("1. Set Budget");
        System.out.println("2. Add Income");
        System.out.println("3. Add Expense");
        System.out.println("4. View Budget Summary");
        System.out.println("5. Create Account");
        System.out.println("6. Display Account Info");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }

    private void setBudget() {
        try {
            Date startDate = getInputDate(scanner, "Enter start date (yyyy-MM-dd): ");
            Date endDate = getInputDate(scanner, "Enter end date (yyyy-MM-dd): ");
            double income = getInputIncome(scanner);

            budget = new Budget(startDate, endDate, income);
            System.out.println("Budget set successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }

    private static Date getInputDate(Scanner scanner, String message) throws ParseException {
        System.out.print(message);
        return DATE_FORMAT.parse(scanner.nextLine());
    }

    private static double getInputIncome(Scanner scanner) {
        System.out.print("Enter income: ");
        double income = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        return income;
    }

    private static TransactionDetails getInputTransactionDetails(Scanner scanner) throws ParseException {
        System.out.print("Enter date (yyyy-MM-dd): ");
        Date date = DATE_FORMAT.parse(scanner.nextLine());

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        return new TransactionDetails(date, amount, description);
    }

    private static class TransactionDetails {
        private Date date;
        private double amount;
        private String description;

        public TransactionDetails(Date date, double amount, String description) {
            this.date = date;
            this.amount = amount;
            this.description = description;
        }

        public Date getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }
    }

    private void addIncome() {
        if (budget == null || account == null) {
            System.out.println("Please set the budget and create an account first.");
            return;
        }
        try {
            TransactionDetails details = getInputTransactionDetails(scanner);
            Transaction income = new IncomeTransaction(details.getDate(), details.getAmount(),
                    details.getDescription());
            income.processTransaction(account);
            budget.setIncome(budget.getIncome() + details.getAmount());

            System.out.println("Income added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }

    private void addExpense() {
        if (budget == null || account == null) {
            System.out.println("Please set the budget and create an account first.");
            return;
        }
        try {
            TransactionDetails details = getInputTransactionDetails(scanner);
            Transaction expense = new ExpenseTransaction(details.getDate(), details.getAmount(),
                    details.getDescription());
            expense.processTransaction(account);
            budget.addTransaction(expense, account);

            System.out.println("Expense added successfully.");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }

    private void viewBudgetSummary() {
        if (budget == null) {
            System.out.println("Please set the budget first.");
        } else {
            System.out.println(budget.getBudgetSummary());
        }
    }

    private static String getInputString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static double getInputDouble(Scanner scanner, String prompt) {
        System.out.print(prompt);
        double value = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        return value;
    }


    private void createAccount() {
        System.out.println("1. Create Savings Account");
        System.out.println("2. Create Checking Account");
        System.out.print("Choose account type: ");
        int accountType = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String accountNumber = getInputString(scanner, "Enter account number: ");
        double initialBalance = getInputDouble(scanner, "Enter initial balance: ");

        if (accountType == 1) {
            System.out.print("Enter interest rate: ");
            double interestRate = scanner.nextDouble();
            scanner.nextLine();  // Consume newline
            account = new SavingsAccount(accountNumber, initialBalance, interestRate);
        } else if (accountType == 2) {
            System.out.print("Enter overdraft limit: ");
            double overdraftLimit = scanner.nextDouble();
            scanner.nextLine();  // Consume newline
            account = new CheckingAccount(accountNumber, initialBalance, overdraftLimit);
        } else {
            System.out.println("Invalid account type. Please try again.");
        }
    }

    private void displayAccountInfo() {
        if (account != null) {
            account.displayAccountInfo();
        } else {
            System.out.println("Please create an account first.");
        }
    }

    private void exitProgram() {
        System.out.println("Exiting the program. Goodbye!");
        scanner.close();
        scanner.nextLine();  // Consume newline
    }
}
