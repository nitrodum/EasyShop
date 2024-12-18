# EasyShop
Easy shop is a feature-rich e-commerce web application. The backend is built using
Spring, and the frontend is developed with HTML, CSS, and Javascript. The frontend
interacts and fetches data from the backend by calling API endpoints. Users can
view products, filter products, add products to cart, view and edit their profile,
view their cart, and checkout.

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
### Home Page
- Displays a list of products fetched from the backend API.
- Filtering Options:
  - Category
  - Minimum Price
  - Maximum Price
  - Color
- Pagination:
  - Products are paginated for better navigation.
  - Prefetching and caching implemented using an LRU cache to optimize performance.
- Caching
  - Reduces redundant API calls and enhances user experience.

### Login Feature
- Enables user-specific features after login:
  - Adding products to the shopping cart.
  - Accessing the cart and profile sections.

### Rate Limiting
- A backend filter acts as a rate limter.
- Different rate limits are implemented based on user roles:
  - GUEST - A user that is not logged in
  - USER - A logged in user
  - ADMIN - A logged in admin

### Profile Management
- Accessed through the navigation bar
- Users can:
  - View profile details fetched from the backend API
  - Update details such as name, email, and address

### Shopping Cart
- Users can: 
  - View items added to their cart.
  - See the quantity of each product and the price of items.
  - View the total price of the order
  - Proceed to checkout.
- Data is fetched via an API call

### Checkout
- Users can place an order from the cart page.
- Sends a POST request to the API to:
  - Create an order in the `orders` table.
  - Build the items in the `order_line_items` table using cart details
  - Clear the shopping
- This checkout uses the data stored in `profiles` for details such as the address.