import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class final_proj {
    // Database credentials
    final static String HOSTNAME = "murp0004-sql-server.database.windows.net";
    final static String DBNAME = "cs-dsa-4513-sql-db";
    final static String USERNAME = "murp0004";
    final static String PASSWORD = "Ninja25543&";
    // Database connection string
    final static String URL = String.format("jdbc:sqlserver://%s:1433;" +
                    "database=%s;" +
                    "user=%s;" +
                    "password=%s;" +
                    "encrypt=true;" +
                    "trustServerCertificate=false;" +
                    "hostNameInCertificate=*.database.windows.net;" +
                    "loginTimeout=30;"
            ,HOSTNAME, DBNAME, USERNAME, PASSWORD);



    // Queries
    final static String QUERY_TEMPLATE_1 = "INSERT INTO Customer(name, address, category) " +
            "VALUES (?, ?, ?);";

    final static String QUERY_TEMPLATE_2 = "INSERT INTO Department(department_id, department_data) " +
            "VALUES (?, ?);";

    final static String QUERY_TEMPLATE_3 = "INSERT INTO Process(process_id, process_data) " +
            "VALUES (?, ?); " +
            "INSERT INTO DepartmentProcessRelationship(department_id, process_id) " +
            "SELECT department_id, process_id FROM Department, Process " +
            "WHERE department_id = ? AND process_id = ?; ";
    final static String QUERY_TEMPLATE_3_paint = "INSERT INTO Paint VALUES ('?', '?', ?);";
    final static String QUERY_TEMPLATE_3_fit = "INSERT INTO Fit VALUES ('?', '?');";
    final static String QUERY_TEMPLATE_3_cut = "INSERT INTO Cut VALUES ('?', '?', ?);";

    final static String QUERY_TEMPLATE_4 = "INSERT INTO Assembly VALUES (?, ?, ?, ?); " +
            "UPDATE Process SET assembly_id = ? WHERE process_id = ?;";

    final static String QUERY_TEMPLATE_5 = "INSERT INTO Account VALUES (?, ?); ";
    final static String QUERY_TEMPLATE_5_assembly = "INSERT INTO Assembly_account(acc_no) VALUES (?);";
    final static String QUERY_TEMPLATE_5_department = "INSERT INTO Department_account(acc_no) VALUES (?);";
    final static String QUERY_TEMPLATE_5_process = "INSERT INTO Process_account(acc_no) VALUES (?);";

    final static String QUERY_TEMPLATE_6 = "INSERT INTO Job(job_no, date_start) VALUES (?, ?); " +
            "UPDATE Process SET job_no = ? WHERE process_id = ? AND assembly_id = ?;";

    final static String QUERY_TEMPLATE_7 = "UPDATE Job SET date_end = ? WHERE job_no = ?; ";
    final static String QUERY_TEMPLATE_7_cut = "INSERT INTO Cut_job VALUES (?, ?, ?, ?, ?);";
    final static String QUERY_TEMPLATE_7_paint = "INSERT INTO Paint_job VALUES (?, ?, ?, ?);";
    final static String QUERY_TEMPLATE_7_fit = "INSERT INTO Fit_job VALUES (?, ?);";

    final static String QUERY_TEMPLATE_8 = "INSERT INTO Transactions VALUES (?, ?, ?, ?); ";
    final static String QUERY_TEMPLATE_8_assembly = "UPDATE Assembly_account SET details_1 = ISNULL(details_1, 0) + cost " +
            "FROM Transactions WHERE transaction_no = ?;";
    final static String QUERY_TEMPLATE_8_department = "UPDATE Department_account SET details_2 = ISNULL(details_2, 0) + cost " +
            "FROM Transactions WHERE transaction_no = ?;";
    final static String QUERY_TEMPLATE_8_process = "UPDATE Process_account SET details_3 = ISNULL(details_3, 0) + cost " +
            "FROM Transactions WHERE transaction_no = ?;";

    final static String QUERY_TEMPLATE_9 = "SELECT details_1 FROM Assembly_account, Transactions, Process " +
            "WHERE Process.assembly_id = ? AND Transactions.job_no = Process.job_no AND Assembly_account.acc_no = Transactions.acc_no;";

    final static String QUERY_TEMPLATE_10 = "SELECT ISNULL(Cut_job.labor_time, 0.0) + ISNULL(Paint_job.labor_time, 0.0) + ISNULL(Fit_job.labor_time, 0.0) " +
            "FROM Cut_job, Paint_job, Fit_job, DepartmentProcessRelationship, Process, Job " +
            "WHERE department_id = ? AND DepartmentProcessRelationship.process_id = Process.process_id AND Process.job_no = Job.job_no " +
            "AND Job.date_end = ? AND Cut_job.job_no = Job.job_no AND Paint_job.job_no = Job.job_no AND Fit_job.job_no = Job.job_no;";

    final static String QUERY_TEMPLATE_11 = "SELECT Process.process_id, Department.department_id " +
            "FROM Process, Department, DepartmentProcessRelationship, Job " +
            "WHERE DepartmentProcessRelationship.process_id = Process.process_id " +
            "AND DepartmentProcessRelationship.department_id = Department.department_id " +
            "AND Process.assembly_id = ? AND Process.job_no = Job.job_no " +
            "ORDER BY Job.date_start ASC;";

    final static String QUERY_TEMPLATE_12 = "SELECT Cut_job.*, Paint_job.*, Fit_job.*, assembly_id " +
            "FROM Cut_job, Paint_job, Fit_job, Process, Job, DepartmentProcessRelationship, Department " +
            "WHERE DepartmentProcessRelationship.department_id = ? AND DepartmentProcessRelationship.process_id = Process.process_id " +
            "AND Process.job_no = Job.job_no AND Job.date_end = ? " +
            "ORDER BY Job.job_no;";

    final static String QUERY_TEMPLATE_13 = "SELECT * FROM Customer WHERE category <= ? AND category >= ? ORDER BY name;";

    final static String QUERY_TEMPLATE_14 = "DELETE FROM Cut_job WHERE job_no <= ? AND job_no >= ?;";

    final static String QUERY_TEMPLATE_15 = "UPDATE Paint_job SET color = ? WHERE job_no = ?;";



    // User input prompt//
    final static String PROMPT = "\nPlease select one of the options below: \n" +
            "1) Enter a new customer; \n" +
            "2) Enter a new department; \n" +
            "3) Enter a new process with info and its department; \n" +
            "4) Enter a new assembly and associate it with a process; \n" +
            "5) Create a new account and associate it with process, assembly, or department; \n" +
            "6) Enter a new job; \n" +
            "7) Enter job completion; \n" +
            "8) Enter transaction; \n" +
            "9) Retrieve total cost incurred on an assembly; \n" +
            "10) Retrieve labor within a department at specified date; \n" +
            "11) Retrieve processes a specified assembly has passed through and the department overseeing; \n" +
            "12) Retrieve jobs completed during a specified date in a specified department; \n" +
            "13) Retrieve customers within a specified category range; \n" +
            "14) Delete all cut_jobs within a specified job range; \n" +
            "15) Change the color of a paint job; \n" +
            "16) Import: enter new customers from a data file; \n" +
            "17) Export: Retrieve the customers; \n" +
            "18) Exit!";



    public static void main(String[] args) throws SQLException {
        System.out.println("WELCOME TO THE JOB-SHOP ACCOUNTING DATABASE SYSTEM");
        final Scanner sc = new Scanner(System.in); // Scanner is used to collect the userinput
        String option = "";// Initialize user option selection as nothing

        while (!option.equals("18")) {
            // Ask user for options until option 18 is selected
            System.out.println(PROMPT); // Print the available options
            option = sc.next(); // Read in the user option selection

            switch (option) {
                // Switch between different options
                case "1": // Insert a new customer
                    // Collect the new customer data from the user
                    sc.nextLine();
                    System.out.println("Please enter customer name:");
                    final String cust_name = sc.nextLine(); // Read in the user input of customer name

                    System.out.println("Please enter customer address (e.g. 1200 South Lane):");
                    // Preceding nextInt, nextFloar, etc. do not consume new line characters from the user input.
                    // We call nextLine to consume that newline character, so that subsequent nextLine doesn't return nothing.
                    //sc.nextLine();
                    final String cust_address = sc.nextLine(); // Read in user input of customer address (white-spaces allowed).

                    System.out.println("Please enter customer category (1-10):"); // No need to call nextLine extra time here, because the preceding nextLine consumed the newline character.
                    final int cust_category = sc.nextInt(); // Read in user input of customer category
                    sc.nextLine();


                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_1)) {
                            // Populate the query template with the data collected from the user
                            statement.setString(1, cust_name);
                            statement.setString(2, cust_address);
                            statement.setInt(3, cust_category);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            final int rows_inserted = statement.executeUpdate();
                            System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                        }
                    }
                    break;
                case "2":
                    // Collect the new department data from the user
                    System.out.println("Please enter department ID (e.g. 1234567):");
                    final int dept_id = sc.nextInt(); // Read in the user input of department ID
                    sc.nextLine();

                    System.out.println("Please enter department data:");
                    final String dept_data = sc.nextLine(); // Read in user input of department data (white-spaces allowed).


                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_2)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, dept_id);
                            statement.setString(2, dept_data);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            final int rows_inserted = statement.executeUpdate();
                            System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                        }
                    }
                    break;
                case "3":
                    System.out.println("Connecting to the database...");
                    // Get the database connection, create statement and execute it right away, as no user input need be collected
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        System.out.println("Dispatching the query...");

                        try (final Statement statement = connection.createStatement();
                             final ResultSet resultSet = statement.executeQuery(QUERY_TEMPLATE_2)) {
                            System.out.println("Contents of the Student table:");
                            System.out.println("ID | first name |last name | GPA | major | classification ");

                            // Unpack the tuples returned by the database and print them out to the user
                            while (resultSet.next()) {
                                System.out.println(String.format("%s | %s | %s | %s | %s | %s ",
                                        resultSet.getString(1),
                                        resultSet.getString(2),
                                        resultSet.getString(3),
                                        resultSet.getString(4),
                                        resultSet.getString(5),
                                        resultSet.getString(6)));
                            }
                        }
                    }
                    break;
                case "18": // Do nothing, the while loop will terminate upon the next iteration
                    System.out.println("Exiting! Good-buy!");
                    break;
                default: // Unrecognized option, re-prompt the user for the correct one
                    System.out.println(String.format("Unrecognized option: %s\n" + "Please try again!", option));
                    break;
            }
        }
        sc.close(); // Close the scanner before exiting the application
    }
}