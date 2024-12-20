# EasyShop
**EasyShop** is a modern and scalable e-commerce platform built with a
**Spring Boot** backend and a lightweight **HTML, CSS, and JavaScript** frontend.
The application provides a seamless user experience with advanced filtering,
rate-limiting, and efficient caching mechanisms. It supports user-specific features
like profile management, shopping cart functionality, and a secure checkout system.

## Table of Contents
1. [Getting Started](#getting-started)
2. [Features](#features)
   1. [Home Page](#dynamic-home-page)
   2. [Login](#login-feature)
   3. [Profile](#profile-management)
   4. [Shopping Cart](#shopping-cart)
   5. [Checkout](#checkout)
   6. [Rate Limiting](#rate-limiting)
3. [Code Highlight](#code-highlight-efficient-pagination-with-lru-caching)
4. [Future Improvements](#future-improvements)
## Getting Started
### Prerequisites
- Java 17 or later
- Relational database (MySql, PostgreSql, etc)

### Setup
1. Clone the repository.
2. Set up the backend:
   1. Use a relational database.
   2. Run the provided schema and scripts to set up tables
   3. Configure the database connection in `application.properties`.
   4. Run the SpringBoot application
3. Set up the frontend:
   1. Open the `index.html` file in a browser
   2. Ensure the backend API URL is correctly configured in the JavaScript files.

## Features

### Dynamic Home Page
![Home Page](/ReadMeImages/Home.png)
- Displays products with filters for category, price range, and color.
- Implements paginated views to enhance browsing.
- Pagination:
  - Products are paginated for better navigation.
  - Prefetches pages for smoother navigation and faster load times.
- Caching:
  - Reduces redundant API calls using an efficient Least Recently Used (LRU) caching algorithm.

### Login Feature
![Login Pop Up](/ReadMeImages/Login%20Pop%20Up.png)
- Enables user-specific features after login:
  - Adding products to the shopping cart.
  - Accessing the cart and profile sections.

### Profile Management
![Profile](/ReadMeImages/Profile.png)
- View and edit personal information like name, email, and address.
- All data is securely fetched and updated via the backend API.

### Shopping Cart
![Cart](/ReadMeImages/Cart.png)
- Users can add, view, and modify items in the cart.
- Automatically calculates total price.

### Checkout
- Creates orders using the user's profile and cart details.
- Integrates backend tables (`orders` and `order_line_items`) for robust order management.

### Rate Limiting
- A custom-built filter manages API call limits based on user roles:
    - **GUEST**: Limited access for unauthenticated users.
    - **USER**: Standard limits for logged-in users.
    - **ADMIN**: Higher limits for administrative tasks.

## Code Highlight: Efficient Pagination with LRU Caching

One of the most impactful features of EasyShop is its efficient pagination system, designed to enhance
user experience by reducing redundant API calls.

### **Why It’s Significant**
- **Optimization**: Reduces server load and improves response times for paginated product data.
- **Prefetching**: Caches upcoming pages to ensure smooth navigation.
- **Scalability**: Ensures the application can handle large datasets effectively.

### **The Code**

```javascript
class LRUCache {
    constructor(maxSize) {
        this.maxSize = maxSize;
        this.cache = new Map();
    }

    get(key) {
        if (!this.cache.has(key)) return null;

        const value = this.cache.get(key);

        this.cache.delete(key);
        this.cache.set(key, value);
        return value;
    }

    set(key, value) {
        if (this.cache.has(key)) {
            this.cache.delete(key);
        }

        if (this.cache.size == this.maxSize) {
            const firstKey = this.cache.keys().next().value;
            this.cache.delete(firstKey);
        }

         this.cache.set(key, value);

    }
}
```

## Future Improvements
- **OAuth2 Integration**: Strengthen user authentication with industry-standard security practices.
- **Product Search**: Add a search bar for users to quickly find products by name or description.
- **Wishlist**: Enable users to save products for future purchases.
- **Enhanced API Rate Limit Handling**: Update the client-side to handle `TOO_MANY_REQUESTS` responses more effectively
- **Optimized Prefetching**: Improve the caching system to dynamically adjust prefetching.
- **Admin Dashboard**: Introduce analytics and controls for managing products, users, and orders.

[Back to Top](#easyshop)
