package org.example;

import java.sql.*;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class IIITBLINKIT {

    private static Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3006/IIITBLINKIT", "root", "dhruv");

    }

    private static boolean userExists(Connection connection, String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to check user existence: " + e.getMessage());
            return false;
        }
    }

    private static void viewFeedback(Connection connection, int userId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT order_id, product_rating, product_feedback, delivery_rating, delivery_feedback FROM feedback WHERE user_id = ?")) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                int productRating = resultSet.getInt("product_rating");
                String productFeedback = resultSet.getString("product_feedback");
                int deliveryRating = resultSet.getInt("delivery_rating");
                String deliveryFeedback = resultSet.getString("delivery_feedback");
                System.out.println("Order ID: " + orderId +
                        ", Product Rating: " + productRating +
                        ", Product Feedback: " + productFeedback +
                        ", Delivery Rating: " + deliveryRating +
                        ", Delivery Feedback: " + deliveryFeedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to retrieve feedback: " + e.getMessage());
        }
    }


    private static void insertUser(Connection connection, String username, String email, String age, String password, String phoneNum) {
        if (phoneNum.length() != 10) {
            System.out.println("Error: Phone number should be 10 digits long");
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO users (username, email, age, U_password, phone_num) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, age);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, phoneNum);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Success: User registered successfully");
            } else {
                System.out.println("Error: Failed to register user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to register user: " + e.getMessage());
        }
    }


    private static void viewUsers(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String age = resultSet.getString("age");
                String phoneNum = resultSet.getString("phone_num");
                System.out.println("Username: " + username + ", Email: " + email + ", Age: " + age + ", Phone Number: " + phoneNum);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to retrieve users: " + e.getMessage());
        }
    }

    private static void viewAdmins(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM admin");
            while (resultSet.next()) {
                String adminId = resultSet.getString("admin_id");
                String manage_warehouse = resultSet.getString("manage_warehouse");
                System.out.println("Admin ID: " + adminId + ", manage_warehouse: " + manage_warehouse);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to retrieve admins: " + e.getMessage());
        }
    }


    private static void viewOrders(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM orders");
            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                int userId = resultSet.getInt("user_id");
                int cartId = resultSet.getInt("cart_id");
                String orderType = resultSet.getString("order_type");
                Date orderDate = resultSet.getDate("order_date");
                double totalAmount = resultSet.getDouble("total_amount");
                System.out.println("Order ID: " + orderId + ", User ID: " + userId + ", Cart ID: " + cartId + ", Order Type: " + orderType + ", Order Date: " + orderDate + ", Total Amount: " + totalAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to retrieve orders: " + e.getMessage());
        }
    }

    private static void viewCart(Connection connection, int userId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT c.product_id, w.product_description, w.price_after_discount, c.quantity FROM cart c " +
                        "INNER JOIN warehouse w ON c.product_id = w.product_id " +
                        "WHERE c.user_id = ?")) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            double total = 0;
            System.out.println("Product ID | Product Name | Price | Quantity | Total");
            System.out.println("---------------------------------------------------");
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("product_description");
                double price = resultSet.getDouble("price_after_discount");
                int quantity = resultSet.getInt("quantity");
                double subtotal = price * quantity;
                total += subtotal;
                System.out.println(productId + " | " + productName + " | $" + price + " | " + quantity + " | $" + subtotal);
            }
            System.out.println("---------------------------------------------------");
            System.out.println("Total Price: $" + total);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to retrieve cart: " + e.getMessage());
        }
    }



    private static void addCart(Connection connection, int userId, int productId, int quantity) {
        try {
            // Check if the product exists in the warehouse
            PreparedStatement checkStatement = connection.prepareStatement(
                    "SELECT quantity FROM warehouse WHERE product_id = ?");
            checkStatement.setInt(1, productId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                int availableQuantity = resultSet.getInt("quantity");
                if (availableQuantity >= quantity) {
                    // Sufficient quantity available, proceed with adding to cart
                    PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE warehouse SET quantity = quantity - ? WHERE product_id = ?");
                    updateStatement.setInt(1, quantity);
                    updateStatement.setInt(2, productId);
                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        PreparedStatement insertStatement = connection.prepareStatement(
                                "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)");
                        insertStatement.setInt(1, userId);
                        insertStatement.setInt(2, productId);
                        insertStatement.setInt(3, quantity);
                        int cartRowsAffected = insertStatement.executeUpdate();

                        if (cartRowsAffected > 0) {
                            System.out.println("Product added to cart successfully.");
                        } else {
                            System.out.println("Error: Failed to add product to cart.");
                        }
                    } else {
                        System.out.println("Error: Failed to update warehouse quantity.");
                    }
                } else {
                    // Insufficient quantity available in the warehouse
                    System.out.println("Sorry, we have only " + availableQuantity + " quantity of the product.");
                }
            } else {
                System.out.println("Error: Product not found in the warehouse.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void orderCart(Connection connection, int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ordering cart for user: " + userId);
        System.out.println("Are you sure you want to proceed with the order? (yes/no)");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            try (PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE cart SET ordered = ? WHERE user_id = ?")) {
                updateStatement.setBoolean(1, true);
                updateStatement.setInt(2, userId);
                int rowsUpdated = updateStatement.executeUpdate();
                System.out.println(rowsUpdated + " item(s) ordered successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error: Failed to update cart: " + e.getMessage());
            }

            System.out.println("Payment processed successfully.");

        } else {
            System.out.println("Order cancelled.");
        }
    }



    public static void viewPaymentInfo(Connection connection, int userId) {
        String query = "SELECT * FROM payment WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Payment Information for User ID " + userId + ":");
            System.out.println("Payment ID | Order ID | Payment Mode | Total Payment | Offers");
            System.out.println("-----------------------------------------------");

            while (resultSet.next()) {
                int paymentId = resultSet.getInt("payment_id");
                int orderId = resultSet.getInt("order_id");
                String paymentMode = resultSet.getString("payment_mode");
                double totalPayment = resultSet.getDouble("total_payment");
                int offers = resultSet.getInt("offers");

                System.out.println(paymentId + " | " + orderId + " | " + paymentMode + " | $" + totalPayment + " | " + offers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static int loginAttempts = 0;

    private static boolean validateUser(Connection connection, String username, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND U_password = ?")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to validate user: " + e.getMessage());
            return false;
        }
    }

    private static int getUserId(Connection connection, String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT user_id FROM users WHERE username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to get user ID: " + e.getMessage());
        }
        return -1;
    }
    private static boolean adminLogin(Connection connection, String adminId, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM admin WHERE admin_id = ? AND Password = ?")) {
            preparedStatement.setString(1, adminId);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to log in as admin: " + e.getMessage());
            return false;
        }
    }
    private static void giveFeedback(Connection connection, int userId, int orderId, int productRating, String productFeedback, int deliveryRating, String deliveryFeedback) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO feedback (user_id, order_id, product_rating, product_feedback, delivery_rating, delivery_feedback) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.setInt(3, productRating);
            preparedStatement.setString(4, productFeedback);
            preparedStatement.setInt(5, deliveryRating);
            preparedStatement.setString(6, deliveryFeedback);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Feedback submitted successfully.");
            } else {
                System.out.println("Error: Failed to submit feedback.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to submit feedback: " + e.getMessage());
        }
    }
    private static void clearCart(Connection connection, int userId) {
        try {
            // Prompt for confirmation
            System.out.print("Do you really want to clear the cart? (Y/N): ");
            Scanner scanner = new Scanner(System.in);
            String confirm = scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("Y")) {
                // SQL query to delete rows from the cart table for the specified user
                String deleteQuery = "DELETE FROM cart WHERE user_id = ?";

                // Create a prepared statement
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, userId);

                // Execute the delete query
                int rowsAffected = preparedStatement.executeUpdate();

                // Check if any rows were affected
                if (rowsAffected > 0) {
                    System.out.println("Cart cleared successfully.");
                } else {
                    System.out.println("No items found in the cart for the specified user.");
                }
            } else {
                System.out.println("Operation cancelled. Cart is not cleared.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to clear cart: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error: MySQL JDBC Driver not found");
            return;
        }

        try (Connection connection = IIITBLINKIT.connectToDatabase()) {
            if (connection != null) {
                System.out.println("Connected to the database");
            } else {
                System.out.println("Failed to make connection");
                return;
            }

            Scanner scanner = new Scanner(System.in);
            int option;

            do {
                System.out.println("Choose an option:");
                System.out.println("1. Admin Login");
                System.out.println("2. User Login");
                System.out.println("3. User Sign Up");
                System.out.println("0. Exit");

                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        // Admin Login
                        System.out.print("Enter admin ID: ");
                        String adminId = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String adminPassword = scanner.nextLine();
                        if (adminLogin(connection, adminId, adminPassword)) {
                            int adminOption;
                            do {
                                System.out.println("Admin Panel:");
                                System.out.println("1. View Users");
                                System.out.println("2. View Admins");
                                System.out.println("3. View Orders");
                                System.out.println("4. View Feedback");
                                System.out.println("5. View Payment Details");
                                System.out.println("6. Exit");
                                adminOption = scanner.nextInt();
                                scanner.nextLine();
                                switch (adminOption) {
                                    case 1:
                                        viewUsers(connection);
                                        break;
                                    case 2:
                                        viewAdmins(connection);
                                        break;
                                    case 3:
                                        viewOrders(connection);
                                        break;
                                    case 4:
                                        System.out.print("Enter user ID: ");
                                        int feedbackUserId = scanner.nextInt();
                                        viewFeedback(connection, feedbackUserId);
                                        break;
                                    case 5:
                                        System.out.println("Enter User Id:");
                                        int userId = scanner.nextInt();
                                        viewPaymentInfo(connection, userId);
                                        break;
                                    case 6:
                                        System.out.println("Exiting Admin Panel...");
                                        break;
                                    default:
                                        System.out.println("Invalid option, please choose again");
                                }
                            } while (adminOption != 6);
                        } else {
                            System.out.println("Error: Incorrect admin ID or password");
                        }
                        break;

                    case 2:
                        // User Login
                        while (loginAttempts < 3) {
                            System.out.print("Enter username: ");
                            String usernameInput = scanner.nextLine();
                            System.out.print("Enter password: ");
                            String passwordInput = scanner.nextLine();

                            int loggedInUserId = getUserId(connection, usernameInput);

                            if (validateUser(connection, usernameInput, passwordInput)) {
                                loginAttempts = 0;

                                int userOption;
                                do {
                                    System.out.println("User Panel:");
                                    System.out.println("1. View Products");
                                    System.out.println("2. Add Cart ");
                                    System.out.println("3. View Cart");
                                    System.out.println("4. Clear Cart");
                                    System.out.println("5. Place Order");
                                    System.out.println("6. View Payment Details");
                                    System.out.println("7. Give Feedback");
                                    System.out.println("8. Exit");
                                    userOption = scanner.nextInt();
                                    scanner.nextLine();
                                    switch (userOption) {
                                        case 1:
                                            // view products
                                            try (Statement statement = connection.createStatement()) {
                                                ResultSet resultSet = statement.executeQuery(
                                                        "SELECT product_id, product_description, price_after_discount FROM warehouse");
                                                System.out.println("Product ID | Product Name | Price");
                                                System.out.println("----------------------------------");
                                                while (resultSet.next()) {
                                                    int productId = resultSet.getInt("product_id");
                                                    String productName = resultSet.getString("product_description");
                                                    double price = resultSet.getDouble("price_after_discount");
                                                    System.out.println(productId + " | " + productName + " | $" + price);
                                                }
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                                System.out.println("Error: Failed to retrieve products: " + e.getMessage());
                                            }
                                            break;
                                        case 2:
                                            System.out.println("Add Item to Cart");
                                            System.out.print("Enter Product ID: ");
                                            int productId = scanner.nextInt();
                                            System.out.print("Enter Quantity: ");
                                            int quantity = scanner.nextInt();
                                            addCart(connection, loggedInUserId, productId, quantity);
                                            break;

                                        case 3:
                                            viewCart(connection, loggedInUserId);
                                            break;

                                        case 4:
                                            clearCart(connection, loggedInUserId);
                                            break;

                                        case 5:
                                            orderCart(connection,loggedInUserId);
                                            break;
                                        case 6:
                                            viewPaymentInfo(connection, loggedInUserId);
                                            break;

                                        case 7:
                                            System.out.println("Give Feedback:");
                                            System.out.print("Enter Order ID: ");
                                            int orderId = scanner.nextInt();
                                            scanner.nextLine();
                                            System.out.print("Rate the product (1-5): ");
                                            int productRating = scanner.nextInt();
                                            scanner.nextLine();
                                            System.out.print("Enter product feedback: ");
                                            String productFeedback = scanner.nextLine();
                                            System.out.print("Rate the delivery (1-5): ");
                                            int deliveryRating = scanner.nextInt();
                                            scanner.nextLine();
                                            System.out.print("Enter delivery feedback: ");
                                            String deliveryFeedback = scanner.nextLine();
                                            giveFeedback(connection, loggedInUserId, orderId, productRating, productFeedback, deliveryRating, deliveryFeedback);
                                            System.out.println("Feedback added successfully.");
                                            break;

                                        case 8:
                                            System.out.println("Exiting User Panel...");
                                            break;
                                        default:
                                            System.out.println("Invalid option, please choose again");
                                    }
                                } while (userOption != 8);
                            } else {
                                System.out.println("Error: Invalid username or password");
                                loginAttempts++;
                                if (loginAttempts == 3) {
                                    System.out.println("Error: Maximum login attempts reached. Terminating...");
                                    return;
                                }
                            }
                        }
                        break;



                    case 3:
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();
                        if (userExists(connection, username)) {
                            System.out.println("Error: User already exists");
                            break;
                        }
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter age: ");
                        String age = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        System.out.print("Enter phone number: ");
                        String phoneNum = scanner.nextLine();
                        insertUser(connection, username, email, age, password, phoneNum);
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid option, please choose again");
                }
            } while (option != 0);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to connect to database: " + e.getMessage());
        }
    }
}