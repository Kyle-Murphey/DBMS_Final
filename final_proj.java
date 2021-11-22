import java.sql.Connection;
import java.sql.Statement;
import java.util.Locale;
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
    final static String QUERY_TEMPLATE_3_paint = "INSERT INTO Paint VALUES (?, ?, ?);";
    final static String QUERY_TEMPLATE_3_fit = "INSERT INTO Fit VALUES (?, ?);";
    final static String QUERY_TEMPLATE_3_cut = "INSERT INTO Cut VALUES (?, ?, ?);";

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
            "WHERE DepartmentProcessRelationship.department_id = ? AND DepartmentProcessRelationship.process_id = Process.process_id AND Process.job_no = Job.job_no " +
            "AND Job.date_end = ? AND Cut_job.job_no = Job.job_no AND Paint_job.job_no = Job.job_no AND Fit_job.job_no = Job.job_no;";

    final static String QUERY_TEMPLATE_11 = "SELECT Process.process_id, Department.department_id " +
            "FROM Process, Department, DepartmentProcessRelationship, Job " +
            "WHERE DepartmentProcessRelationship.process_id = Process.process_id " +
            "AND DepartmentProcessRelationship.department_id = Department.department_id " +
            "AND Process.assembly_id = ? AND Process.job_no = Job.job_no " +
            "ORDER BY Job.date_start ASC;";

    final static String QUERY_TEMPLATE_12 = "SELECT Cut_job.*, Paint_job.*, Fit_job.*, assembly_id " +
            "FROM Cut_job, Paint_job, Fit_job, Process, Job, DepartmentProcessRelationship " +
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
                    // Preceding nextInt, nextFloat, etc. do not consume new line characters from the user input.
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
                    // Collect the new process data from the user
                    System.out.println("Please enter process ID (e.g. 12345):");
                    final int proc_id = sc.nextInt(); // Read in the user input of process ID
                    sc.nextLine();

                    System.out.println("Please enter process data:");
                    final String proc_data = sc.nextLine(); // Read in user input of process data (white-spaces allowed).

                    System.out.println("Please enter department ID:");
                    final int proc_dept_id = sc.nextInt(); // Read in user input of department id (white-spaces allowed).
                    sc.nextLine();

                    System.out.println("Please enter process type (paint, fit, cut):");
                    final String proc_type = sc.nextLine(); // Read in user input of process type (white-spaces allowed).

                    if (proc_type.equalsIgnoreCase("PAINT")) {
                        System.out.println("Please enter paint type:");
                        final String paint_type = sc.nextLine(); // Read in user input of paint type (white-spaces allowed).

                        System.out.println("Please enter painting method:");
                        final String paint_method = sc.nextLine(); // Read in user input of paint method (white-spaces allowed).

                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_3.concat(QUERY_TEMPLATE_3_paint))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, proc_id);
                                statement.setString(2, proc_data);
                                statement.setInt(3, proc_dept_id);
                                statement.setInt(4, proc_id);
                                statement.setString(5, paint_type);
                                statement.setString(6, paint_method);
                                statement.setInt(7, proc_id);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else if (proc_type.equalsIgnoreCase("FIT")) {
                        System.out.println("Please enter fit type:");
                        final String fit_type = sc.nextLine(); // Read in user input of fit type (white-spaces allowed).


                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_3.concat(QUERY_TEMPLATE_3_fit))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, proc_id);
                                statement.setString(2, proc_data);
                                statement.setInt(3, proc_dept_id);
                                statement.setInt(4, proc_id);
                                statement.setString(5, fit_type);
                                statement.setInt(6, proc_id);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else if (proc_type.equalsIgnoreCase("CUT")) {
                        System.out.println("Please enter cut type:");
                        final String cut_type = sc.nextLine(); // Read in user input of cut type (white-spaces allowed).

                        System.out.println("Please enter machine type:");
                        final String machine_type = sc.nextLine(); // Read in user input of machine type (white-spaces allowed).

                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_3.concat(QUERY_TEMPLATE_3_cut))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, proc_id);
                                statement.setString(2, proc_data);
                                statement.setInt(3, proc_dept_id);
                                statement.setInt(4, proc_id);
                                statement.setString(5, cut_type);
                                statement.setString(6, machine_type);
                                statement.setInt(7, proc_id);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else {
                        System.out.println("Bad input.");
                        break;
                    }

                    break;
                case "4":
                    // Collect the new assembly data from the user
                    System.out.println("Please enter assembly ID (e.g. 123456):");
                    final int assembly_id = sc.nextInt(); // Read in the user input of department ID
                    sc.nextLine();

                    System.out.println("Please enter date ordered (yyyy-mm-dd):");
                    final String date_ordered = sc.nextLine(); // Read in user input of date ordered

                    System.out.println("Please enter assembly details:");
                    final String assembly_details = sc.nextLine(); // Read in user input of assembly details (white-spaces allowed).

                    System.out.println("Please enter the name of the customer:");
                    final String assembly_cust_name = sc.nextLine(); // Read in user input of customer name (white-spaces allowed).

                    System.out.println("Please enter process ID to associate with (e.g. 12345):");
                    final int assembly_proc_id = sc.nextInt(); // Read in user input of process ID
                    sc.nextLine();


                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_4)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, assembly_id);
                            statement.setString(2, date_ordered);
                            statement.setString(3, assembly_details);
                            statement.setString(4, assembly_cust_name);
                            statement.setInt(5, assembly_id);
                            statement.setInt(6, assembly_proc_id);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            final int rows_inserted = statement.executeUpdate();
                            System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                        }
                    }
                    break;
                case "5":
                    // Collect the new department data from the user
                    System.out.println("Please enter account number (e.g. 123456789):");
                    final int acc_no = sc.nextInt(); // Read in the user input of account number
                    sc.nextLine();

                    System.out.println("Please enter date established:");
                    final String date_est = sc.nextLine(); // Read in user input of date established

                    System.out.println("Please enter account type (assembly, department, process):");
                    final String acc_type = sc.nextLine(); // Read in user input of account type (white-spaces allowed).

                    if (acc_type.equalsIgnoreCase("ASSEMBLY")) {
                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_5.concat(QUERY_TEMPLATE_5_assembly))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, acc_no);
                                statement.setString(2, date_est);
                                statement.setInt(3, acc_no);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else if (acc_type.equalsIgnoreCase("DEPARTMENT")) {
                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_5.concat(QUERY_TEMPLATE_5_department))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, acc_no);
                                statement.setString(2, date_est);
                                statement.setInt(3, acc_no);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else if (acc_type.equalsIgnoreCase("PROCESS")) {
                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_5.concat(QUERY_TEMPLATE_5_process))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, acc_no);
                                statement.setString(2, date_est);
                                statement.setInt(3, acc_no);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else {
                        System.out.println("Bad input.");
                        break;
                    }
                    break;
                case "6":
                    // Collect the new job data from the user
                    System.out.println("Please enter job number (e.g. 12345678):");
                    final int job_no = sc.nextInt(); // Read in the user input of job number
                    sc.nextLine();

                    System.out.println("Please enter starting date for job:");
                    final String date_start = sc.nextLine(); // Read in user input of starting date

                    System.out.println("Please enter process ID for the job (e.g. 12345):");
                    final int job_proc_id = sc.nextInt(); // Read in the user input of process ID
                    sc.nextLine();

                    System.out.println("Please enter assembly ID for the job (e.g. 123456):");
                    final int job_assembly_id = sc.nextInt(); // Read in the user input of assembly ID
                    sc.nextLine();


                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_6)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, job_no);
                            statement.setString(2, date_start);
                            statement.setInt(3, job_no);
                            statement.setInt(4, job_proc_id);
                            statement.setInt(5, job_assembly_id);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            final int rows_inserted = statement.executeUpdate();
                            System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                        }
                    }
                    break;
                case "7":
                    // Collect the new job data from the user
                    System.out.println("Please enter job number (e.g. 12345678):");
                    final int job_no_end = sc.nextInt(); // Read in the user input of job number
                    sc.nextLine();

                    System.out.println("Please enter ending date for job:");
                    final String date_end = sc.nextLine(); // Read in user input of ending date

                    System.out.println("Please enter job type (cut, paint, fit):");
                    final String job_type = sc.nextLine(); // Read in user input of job type

                    if (job_type.equalsIgnoreCase("CUT")) {
                        System.out.println("Please enter machine type:");
                        final String job_machine_type = sc.nextLine(); // Read in user input of machine type

                        System.out.println("Please enter the how long the machine was in use (h:mm):");
                        final String job_machine_time = sc.nextLine(); // Read in user input of machine time use

                        System.out.println("Please enter material used:");
                        final String job_material = sc.nextLine(); // Read in user input of material

                        System.out.println("Please enter labor time (e.g. 8.57):");
                        final Float job_labor = sc.nextFloat(); // Read in user input of labor
                        sc.nextLine();

                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_7.concat(QUERY_TEMPLATE_7_cut))) {
                                // Populate the query template with the data collected from the user
                                statement.setString(1, date_end);
                                statement.setInt(2, job_no_end);
                                statement.setString(3, job_machine_type);
                                statement.setString(4, job_machine_time);
                                statement.setString(5, job_material);
                                statement.setFloat(6, job_labor);
                                statement.setInt(7, job_no_end);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else if (job_type.equalsIgnoreCase("PAINT")) {
                        System.out.println("Please enter color:");
                        final String job_color = sc.nextLine(); // Read in user input of color

                        System.out.println("Please enter the volume:");
                        final Float job_volume = sc.nextFloat(); // Read in user input of volume
                        sc.nextLine();

                        System.out.println("Please enter labor time (e.g. 8.57):");
                        final Float job_labor = sc.nextFloat(); // Read in user input of labor
                        sc.nextLine();

                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_7.concat(QUERY_TEMPLATE_7_paint))) {
                                // Populate the query template with the data collected from the user
                                statement.setString(1, date_end);
                                statement.setInt(2, job_no_end);
                                statement.setString(3, job_color);
                                statement.setFloat(4, job_volume);
                                statement.setFloat(5, job_labor);
                                statement.setInt(6, job_no_end);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else if (job_type.equalsIgnoreCase("FIT")) {
                        System.out.println("Please enter labor time (e.g. 8.57):");
                        final Float job_labor = sc.nextFloat(); // Read in user input of labor
                        sc.nextLine();

                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_7.concat(QUERY_TEMPLATE_7_fit))) {
                                // Populate the query template with the data collected from the user
                                statement.setString(1, date_end);
                                statement.setInt(2, job_no_end);
                                statement.setFloat(3, job_labor);
                                statement.setInt(4, job_no_end);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else {
                        System.out.println("Bad input.");
                        break;
                    }

                    break;
                case "8":
                    // Collect the new transaction data from the user
                    System.out.println("Please enter transaction number (e.g. 1234567890):");
                    final int trans_no = sc.nextInt(); // Read in the user input of transaction number
                    sc.nextLine();

                    System.out.println("Please enter the cost:");
                    final Float cost = sc.nextFloat(); // Read in user input of cost
                    sc.nextLine();

                    System.out.println("Please enter job number (e.g. 12345):");
                    final int trans_job_no = sc.nextInt(); // Read in the user input of job number
                    sc.nextLine();

                    System.out.println("Please enter account number for the transaction (e.g. 123456789):");
                    final int trans_acc_no = sc.nextInt(); // Read in the user input of account number
                    sc.nextLine();

                    System.out.println("Please account type (assembly, department, process):");
                    final String trans_type = sc.nextLine(); // Read in user input of transaction type

                    if (trans_type.equalsIgnoreCase("ASSEMBLY")) {
                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_8.concat(QUERY_TEMPLATE_8_assembly))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, trans_no);
                                statement.setFloat(2, cost);
                                statement.setInt(3, trans_job_no);
                                statement.setInt(4, trans_acc_no);
                                statement.setInt(5, trans_no);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else if (trans_type.equalsIgnoreCase("DEPARTMENT")) {
                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_8.concat(QUERY_TEMPLATE_8_department))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, trans_no);
                                statement.setFloat(2, cost);
                                statement.setInt(3, trans_job_no);
                                statement.setInt(4, trans_acc_no);
                                statement.setInt(5, trans_no);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else if (trans_type.equalsIgnoreCase("PROCESS")) {
                        System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                        try (final Connection connection = DriverManager.getConnection(URL)) {
                            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_8.concat(QUERY_TEMPLATE_8_process))) {
                                // Populate the query template with the data collected from the user
                                statement.setInt(1, trans_no);
                                statement.setFloat(2, cost);
                                statement.setInt(3, trans_job_no);
                                statement.setInt(4, trans_acc_no);
                                statement.setInt(5, trans_no);
                                System.out.println("Dispatching the query..."); // Actually execute the populated query

                                final int rows_inserted = statement.executeUpdate();
                                System.out.println(String.format("Done. %d rows inserted.", rows_inserted));
                            }
                        }
                    }
                    else {
                        System.out.println("Bad input.");
                        break;
                    }
                    break;
                case "9":
                    // Display costs from a given assembly
                    System.out.println("Please enter assembly ID (e.g. 123456):");
                    final int assembly_id_search = sc.nextInt(); // Read in the user input of assembly ID
                    sc.nextLine();

                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_9)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, assembly_id_search);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            System.out.print(String.format("Total cost on assembly %d: ", assembly_id_search));
                            ResultSet process_result = statement.executeQuery();
                            while (process_result.next())
                            {
                                System.out.println(String.format("%s\n", process_result.getString(1)));
                            }
                        }
                    }
                    break;
                case "10":
                    // Display labor from a department
                    System.out.println("Please enter department ID (e.g. 12345):");
                    final int department_id_search = sc.nextInt(); // Read in the user input of department ID
                    sc.nextLine();

                    System.out.println("Please enter the date job finished (yyyy-mm-dd):");
                    final String department_date_search = sc.nextLine(); // Read in the user input of finished date

                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_10)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, department_id_search);
                            statement.setString(2, department_date_search);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            System.out.print(String.format("Labor in department %d on %s: ", department_id_search, department_date_search));
                            ResultSet process_result = statement.executeQuery();
                            while (process_result.next())
                            {
                                System.out.println(String.format("%s\n", process_result.getString(1)));
                            }
                        }
                    }
                    break;
                case "11":
                    // Display process through an assembly has gone through
                    System.out.println("Please enter assembly ID (e.g. 123456):");
                    final int proc_assembly_search = sc.nextInt(); // Read in the user input of assembly ID
                    sc.nextLine();

                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_11)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, proc_assembly_search);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            System.out.println("| Process ID | Department ID |");
                            ResultSet process_result = statement.executeQuery();
                            while (process_result.next())
                            {
                                System.out.println(String.format("%s, %s\n", process_result.getString(1), process_result.getString(2)));
                            }
                        }
                    }
                    break;
                case "12":
                    // Display jobs completed on certain date
                    System.out.println("Please enter department ID (e.g. 12345):");
                    final int department_id_end_search = sc.nextInt(); // Read in the user input of department ID
                    sc.nextLine();

                    System.out.println("Please enter the date job finished (yyyy-mm-dd):");
                    final String department_job_date_search = sc.nextLine(); // Read in the user input of finished date

                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_12)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, department_id_end_search);
                            statement.setString(2, department_job_date_search);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            System.out.println(String.format("Jobs completed during %s in department %d: ", department_job_date_search, department_id_end_search));
                            System.out.println("| Cut job... | Paint job... | Fit job... | job ID | assembly ID |");
                            ResultSet process_result = statement.executeQuery();
                            while (process_result.next())
                            {
                                System.out.println(String.format(" | %s, %s, %s, %s, %s |, %s, %s, %s, %s |, %s, %s, | %s\n",
                                        process_result.getString(1),
                                        process_result.getString(2),
                                        process_result.getString(3),
                                        process_result.getString(4),
                                        process_result.getString(5),
                                        process_result.getString(6),
                                        process_result.getString(7),
                                        process_result.getString(8),
                                        process_result.getString(9),
                                        process_result.getString(10),
                                        process_result.getString(11),
                                        process_result.getString(12)));
                            }
                        }
                    }
                    break;
                case "13":
                    // Display customers in category range
                    System.out.println("Please enter lower-bound customer range (1-10):");
                    final int customer_lrange_search = sc.nextInt(); // Read in the user input of lower customer category
                    sc.nextLine();

                    System.out.println("Please enter upper-bound customer range (1-10):");
                    final int customer_urange_search = sc.nextInt(); // Read in the user input of upper customer category
                    sc.nextLine();

                    if (customer_urange_search < customer_lrange_search) {
                        System.out.println("invalid range");
                        break;
                    }

                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_13)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, customer_urange_search);
                            statement.setInt(2, customer_lrange_search);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            System.out.println(String.format("Customers within %d and %d: ", customer_lrange_search, customer_urange_search));
                            ResultSet process_result = statement.executeQuery();
                            while (process_result.next())
                            {
                                System.out.println(String.format("%s, %s, %s\n",
                                        process_result.getString(1),
                                        process_result.getString(2),
                                        process_result.getString(3)));
                            }
                        }
                    }
                    break;
                case "14":
                    // Delete cut_jobs in range
                    System.out.println("Please enter lower-bound cut job number range:");
                    final int job_lrange_search = sc.nextInt(); // Read in the user input of lower number range
                    sc.nextLine();

                    System.out.println("Please enter upper-bound cut job number range:");
                    final int job_urange_search = sc.nextInt(); // Read in the user input of upper number range
                    sc.nextLine();

                    if (job_urange_search < job_lrange_search) {
                        System.out.println("invalid range");
                        break;
                    }

                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_14)) {
                            // Populate the query template with the data collected from the user
                            statement.setInt(1, job_urange_search);
                            statement.setInt(2, job_lrange_search);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            final int rows_deleted = statement.executeUpdate();
                            System.out.println(String.format("Done. %d rows deleted.", rows_deleted));
                        }
                    }
                    break;
                case "15":
                    // update color on paint job
                    System.out.println("Please enter job number (e.g. 12345):");
                    final int paint_job_update = sc.nextInt(); // Read in the user input of job number
                    sc.nextLine();

                    System.out.println("Please enter color to change to:");
                    final String update_color = sc.nextLine(); // Read in the user input of color


                    System.out.println("Connecting to the database..."); // Get a database connection and prepare a query statement
                    try (final Connection connection = DriverManager.getConnection(URL)) {
                        try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_15)) {
                            // Populate the query template with the data collected from the user
                            statement.setString(1, update_color);
                            statement.setInt(2, paint_job_update);
                            System.out.println("Dispatching the query..."); // Actually execute the populated query

                            final int rows_updated = statement.executeUpdate();
                            System.out.println(String.format("Done. %d rows updated.", rows_updated));
                        }
                    }
                    break;
                case "16":
                    break;
                case "17":
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