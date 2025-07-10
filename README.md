
# E-Waste Management System (Java Swing Desktop App)

This is a Java Swing-based **E-Waste Management System** that helps users submit e-waste pickup requests, track them, and update personal profiles. An admin panel allows request and user management. Data is persisted using simple text files.

---

## 🚀 Features

### 👤 User Side
- **Login/Register** with email and password.
- **Submit Requests** for pickup of e-waste like Mobiles, Laptops, TVs, etc.
- **Track Requests** in a report table (status: Pending, Picked Up, Complete).
- **Edit Profile** including phone, address, and password.
- **Session Management** using basic UI navigation.

### 🛠️ Admin Side
- **Admin Login** (`admin/admin` credentials).
- **Manage Requests** with status updates.
- **View & Delete Users**, including their associated requests.

---
 
---

## 🧪 Sample Credentials (For Testing)

- **Admin**
  ```
  Email: admin
  Password: admin
  ```

- **Sample User Entry (users.txt):**
  ```
  U1234,john@example.com,John Doe,9876543210,password123,123 Main Street
  ```
 ---

## 📌 Notes

- The application uses **plain text files** for storage, no database is required.
- Data format validation is minimal – extend it for production use.
- Keep backup of `users.txt` and `requests.txt` to preserve data.

---

## 🧑‍💻 Contributing

Feel free to fork this project and open pull requests for enhancements like:
- Session timeout
- Rating system
- Database integration (MySQL, SQLite)
- Improved UI/UX

---
 
## 📸 Screenshot Preview

<img width="781" height="404" alt="image" src="https://github.com/user-attachments/assets/4266896a-de49-4d65-806c-244e765f12b7" />

---

 
