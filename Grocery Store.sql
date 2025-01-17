CREATE DATABASE IIITBLINKIT;
USE IIITBLINKIT;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_type ENUM('registered', 'new') NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    registration_date DATE,
    U_password VARCHAR(50) NOT NULL,
    age INT,
    phone_num VARCHAR(20)
);

CREATE TABLE admin (
  admin_id INT PRIMARY KEY AUTO_INCREMENT,
  Password VARCHAR(50) NOT NULL,
  manage_warehouse VARCHAR(50) DEFAULT NULL,
  gender ENUM('Male', 'Female') NOT NULL,
  stock_check BOOLEAN NOT NULL
);  

CREATE TABLE app_login (
    login_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT ,
    admin_id INT,
    login_type ENUM('user', 'admin') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

CREATE TABLE warehouse (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    original_price INT NOT NULL,
    product_description VARCHAR(1000) NOT NULL,
    allotment_date DATE NOT NULL,
    product_category VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    offer INT DEFAULT 0,
    best_before_period INT,
    product_color VARCHAR(15),
    price_after_discount INT NOT NULL,
    admin_id INT NOT NULL,
    FOREIGN KEY (admin_id) REFERENCES admin(admin_id),
    CHECK (original_price > 0),
    CHECK (quantity >= 0),
    CHECK (price_after_discount >= 0)
);

CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    primary key (cart_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES warehouse(product_id)
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    cart_id INT NOT NULL,
    order_type VARCHAR(50) NOT NULL,
    order_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (cart_id) REFERENCES cart(cart_id)
);

CREATE TABLE payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    user_id INT NOT NULL,
    payment_mode ENUM('Credit Card', 'Debit Card', 'Bank Transfer', 'UPI', 'COD') NOT NULL,
    total_payment DECIMAL(10, 2) NOT NULL,
    offers INT DEFAULT 0,
    net_payment DECIMAL(10, 2) GENERATED ALWAYS AS (total_payment - offers) STORED,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    CHECK (total_payment > 0),
    CHECK (offers >= 0)
);

CREATE TABLE product_delivery (
    delivery_id INT PRIMARY KEY AUTO_INCREMENT,
    address VARCHAR(255) NOT NULL,
    delivery_date DATE NOT NULL,
    payment_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    contact_details VARCHAR(100) NOT NULL,
    FOREIGN KEY (payment_id) REFERENCES payment(payment_id)
);

CREATE TABLE feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_rating INT,
    product_feedback VARCHAR(100),
    delivery_rating INT,
    delivery_feedback VARCHAR(100),
    user_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);




INSERT INTO users (user_id, user_type, username, email, registration_date, U_password, age, phone_num)
VALUES
    (1, 'registered', 'Gautam', 'gautam@iiitd.com', '2024-02-12', 'password1', 30, 1234567812),
    (2, 'registered', 'Dhruv', 'dhruv@iiitd.com', '2024-02-12', 'password2', 25, 1234567813),
    (3, 'registered', 'Nikhar', 'nikhar@iiitd.com', '2024-02-12', 'password3', 35, 1234567842),
    (4, 'new', 'Nikhil', 'nikhil@iiitd.com', '2024-02-12', 'password4', 40, 1234567852),
    (5, 'new', 'pridum', 'pridum@iiitd.com', '2024-02-12', 'password5', 28, 1234567862),
    (6, 'new', 'suresh', 'suresh@iiitd.com', '2024-02-12', 'password6', 22, 1075264872),
    (7, 'new', 'Sonal', 'sonal@iiitd.com', '2024-02-12', 'password7', 26, 1068565792),
    (8, 'new', 'Kunal', 'kunal@iiitd.com', '2024-02-12', 'password8', 32, 1028645356),
    (9, 'new', 'Sonia', 'sonia@iiitd.com', '2024-02-12', 'password9', 27, 1234098746),
    (10, 'new', 'Nishant', 'nishant@iiitd.com', '2024-02-12', 'password10', 29, 1200120012);

INSERT INTO admin (admin_id, Password, manage_warehouse, gender, stock_check)
VALUES
    (11, 'password1', 'Warehouse1', 'Male', 1),
    (12, 'password2', 'Warehouse2', 'Female', 0),
    (13, 'password3', 'Warehouse3', 'Male', 1),
    (14, 'password4', 'Warehouse4', 'Female', 1),
    (15, 'password5', 'Warehouse5', 'Male', 0),
    (16, 'password6', 'Warehouse6', 'Male', 1),
    (17, 'password7', 'Warehouse7', 'Female', 0),
    (18, 'password8', 'Warehouse8', 'Male', 1),
    (19, 'password9', 'Warehouse9', 'Female', 1),
    (20, 'password10', 'Warehouse10', 'Male', 0);

INSERT INTO app_login (login_id, user_id, admin_id, login_type)
VALUES
    (1, 1, NULL, 'user'),
    (2, 2, NULL, 'user'),
    (3, 3, NULL, 'user'),
    (4, 4, NULL, 'user'),
    (5, 5, NULL, 'user'),
    (6, 6, NULL, 'user'),
    (7, 7, NULL, 'user'),
    (8, 8, NULL, 'user'),
    (9, 9, NULL, 'user'),
    (10, 10, NULL, 'user'),
    (11, NULL, 11, 'admin'),
    (12, NULL, 12, 'admin'),
    (13, NULL, 13, 'admin'),
    (14, NULL, 14, 'admin'),
    (15, NULL, 15, 'admin'),
    (16, NULL, 16, 'admin'),
    (17, NULL, 17, 'admin'),
    (18, NULL, 18, 'admin'),
    (19, NULL, 19, 'admin'),
    (20, NULL, 20, 'admin');

INSERT INTO warehouse (original_price, product_id, product_description, allotment_date, product_category, quantity, offer, best_before_period, product_color, price_after_discount, admin_id)
VALUES
    (300, 21, 'Tresseme Shampoo', '2024-02-01', 'Hair Care', 1000, 10, 365, 'Multi Color', 290, 11),
    (30, 22, 'Table Cloth', '2024-02-02', 'Footwear', 50, 0, 180, 'Multi Color', 30, 12),
    (20, 23, 'Mangladeep Dhoop', '2024-02-03', 'Puja Samagri', 200, 0, 730, NULL, 20, 13),
    (100, 24, 'Kaju Biscuit', '2024-02-04', 'Biscuits', 30, 5, 365, 'Black', 95, 14),
    (55, 25, 'Notebook', '2024-02-05', 'Stationery', 150, 0, 180, NULL, 55, 15),
    (40, 26, 'Kitchen Blender', '2024-02-06', 'Appliances', 20, 0, 365, 'Red', 40, 16),
    (80, 27, 'Milk (2L)', '2024-02-07', 'Dairy Product', 10, 5, 365, NULL, 75, 17),
    (250, 28, 'Connecting Cable', '2024-02-08', 'Computer Accessories', 50, 0, 365, 'Black', 250, 18),
    (100, 29, 'LED Bulb 0 Watt', '2024-02-09', 'Home Appliance', 40, 0, 730, 'White', 100, 19),
    (35, 30, 'Pocket-Perfume', '2024-02-10', 'Accessories', 25, 0, 365, 'Black', 35, 20);
    
INSERT INTO cart (cart_id, user_id, product_id, quantity)
VALUES
    (1001, 1, 21, 2),
    (1002, 2, 22, 1),
    (1003, 3, 23, 3),
    (1004, 4, 24, 2),
    (1005, 5, 25, 5),
    (1006, 6, 26, 1),
    (1007, 7, 27, 2),
    (1008, 8, 28, 1),
    (1009, 9, 29, 3),
    (1010, 10, 30, 4);

INSERT INTO orders (order_id, user_id, cart_id, order_type, order_date, total_amount)
VALUES
    (10011, 1, 1001, 'Online', '2024-03-01', 370),
    (10012, 2, 1002, 'Online', '2024-03-02', 150),
    (10013, 3, 1003, 'Online', '2024-03-03', 195),
    (10014, 4, 1004, 'Online', '2024-03-04', 220),
    (10015, 5, 1005, 'Online', '2024-03-05', 200),
    (10016, 6, 1006, 'Online', '2024-03-06', 500),
    (10017, 7, 1007, 'Online', '2024-03-07', 50),
    (10018, 8, 1008, 'Online', '2024-03-08', 290),
    (10019, 9, 1009, 'Online', '2024-03-09', 225),
    (10020, 10, 1010, 'Online', '2024-03-10', 60);

INSERT INTO payment (payment_id, order_id, user_id, payment_mode, total_payment, offers)
VALUES
    (211, 10011, 1, 'Credit Card', 370, 0),
    (212, 10012, 2, 'Debit Card', 150, 0),
    (213, 10013, 3, 'Bank Transfer', 195, 0),
    (214, 10014, 4, 'UPI', 220, 0),
    (215, 10015, 5, 'COD', 200, 0),
    (216, 10016, 6, 'Credit Card', 500, 0),
    (217, 10017, 7, 'Debit Card', 50, 0),
    (218, 10018, 8, 'Bank Transfer', 290, 0),
    (219, 10019, 9, 'UPI', 225, 0),
    (220, 10020, 10, 'COD', 60, 0);

INSERT INTO product_delivery (delivery_id, address, delivery_date, payment_id, status, contact_details)
VALUES
    (4001, 'Flat No. 101, Delhi', '2024-03-10', 211, 'Delivered', '1234567812'),
    (4002, 'House No. 102, Ghaziabad', '2024-03-11', 212, 'In Transit', '1234567813'),
    (4003, 'Flat No. 201, Harkesh Nagar Okhla', '2024-03-12', 213, 'Pending', '1234567842'),
    (4004, 'Plot No. 302, Govindpuri', '2024-03-13', 214, 'Delivered', '1234567852'),
    (4005, 'House No. 403, Harkesh Nagar', '2024-03-14', 215, 'Cancelled', '1234567862'),
    (4006, 'Flat No. 501, Kalkaji Mandir', '2024-03-15', 216, 'Delivered', '1075264872'),
    (4007, 'House No. 602, Delhi', '2024-03-16', 217, 'In Transit', '1068565792'),
    (4008, 'Flat No. 701, Govindpuri', '2024-03-17', 218, 'Pending', '1028645356'),
    (4009, 'Plot No. 802, Nehru Place', '2024-03-18', 219, 'Delivered', '1234098746'),
    (4010, 'House No. 903, Lajpat Nagar', '2024-03-19', 220, 'Cancelled', '1200120012');
    
INSERT INTO feedback (feedback_id, order_id, product_rating, product_feedback, delivery_rating, delivery_feedback, user_id)
VALUES
    (11, 10011, 4, 'Good product quality', 4, 'Timely delivery', 1),
    (12, 10012, 5, 'Excellent product, highly recommended', 5, 'Great service', 2),
    (13, 10013, 3, 'Average product quality', 3, 'Delivery delayed', 3),
    (14, 10014, 2, 'Poor product quality', 2, 'Delivery never arrived', 4),
    (15, 10015, 1, 'Very bad product, waste of money', 1, 'Delivery damaged', 5),
    (16, 10016, 4, 'Satisfactory product', 4, 'Timely delivery', 6),
    (17, 10017, 5, 'Outstanding product, loved it!', 5, 'Excellent service', 7),
    (18, 10018, 3, 'Product was okay, could be better', 3, 'Delivery delayed', 8),
    (19, 10019, 8, 'Nice Products', 2, 'Delivery is so fast', 9),
    (20, 10020, 10, 'Good Experience, enjoying the product', 1, 'Delivery speed was ok', 10);
    

ALTER TABLE cart ADD COLUMN ordered BOOLEAN DEFAULT FALSE;


-- Total Users
Select * from users;

-- Total new users
Select * from users where user_type = 'new';

-- Total registered users
Select * from users where user_type = 'registered';

-- Total admins
Select * from admin;

-- Total Loged_in Users/Admin
Select * from app_login;

-- Sign up
INSERT INTO users (user_id, user_type, username, email, registration_date, U_password, age, phone_num)
VALUES
    (600, 'new', 'Dharmesh', 'dharmesh@iiitd.com', '2024-02-12', 'password24', 30, 1234567898);
    
-- Log-In Of New User
INSERT INTO app_login (login_id, user_id, admin_id, login_type)
VALUES
    (21, 600, NULL, 'user');

-- All Products
Select * from warehouse;

-- Carts
Select * from cart;

-- Orders
Select * from orders;

-- All Payments
Select * from payment;

-- Total Sales_Payment till now
Select SUM(total_payment) from payment;

-- All Feedbacks
Select * from feedback;

-- Product with rating more than 4 (average)
Select * from feedback where product_rating > 4;

-- Delivery with rating more than 4 (average)
Select * from feedback where delivery_rating > 4;

-- Combined Rating of more than 4 (average)
Select * from feedback where delivery_rating > 4 and product_rating > 4 ;

-- Updating Product
UPDATE warehouse
SET product_description = 'Premium Shampoo'
WHERE product_id = 21;

-- Total Current Users Feedback
SELECT users.username, feedback.product_feedback
FROM users
JOIN feedback ON users.user_id = feedback.user_id;




-- Removing a product
Delete from warehouse
where quantity < 1;


-- Total Sales of each product
SELECT warehouse.product_category, SUM(payment.total_payment) AS total_spent
FROM warehouse
JOIN cart ON warehouse.product_id = cart.product_id
JOIN orders ON cart.cart_id = orders.cart_id
JOIN payment ON orders.order_id = payment.order_id
GROUP BY warehouse.product_category;

-- Table of total consumers of app (both admin and users) through union
SELECT username AS name, 'User' AS login_type
FROM users
UNION
SELECT manage_warehouse AS name, 'Admin' AS login_type
FROM admin;

-- users with same user id and login id
SELECT username
FROM users
JOIN app_login ON users.user_id = app_login.user_id
JOIN admin ON app_login.admin_id = admin.admin_id;

-- Users who have not ordered anything yet
SELECT username
FROM users
WHERE user_id NOT IN (SELECT DISTINCT user_id FROM orders);

-- Users with their order_id and their total bill
SELECT users.username, orders.order_id, orders.total_amount
FROM users
LEFT JOIN orders ON users.user_id = orders.user_id;

-- Average rating of a user for their lifetime orders
SELECT feedback.user_id, AVG(feedback.product_rating) AS avg_product_rating, AVG(feedback.delivery_rating) AS avg_delivery_rating
FROM feedback
GROUP BY feedback.user_id;

-- Product with lesser quantity then total average quantity of stock
SELECT *
FROM warehouse
WHERE quantity < (SELECT AVG(quantity) FROM warehouse);


-- Setting Up price and discounts
UPDATE warehouse
SET price_after_discount = original_price * 0.9
WHERE original_price > 100;

UPDATE warehouse
SET price_after_discount = original_price + 20
WHERE original_price <= 100;

UPDATE warehouse
SET offer = offer + 20
WHERE offer < 10;