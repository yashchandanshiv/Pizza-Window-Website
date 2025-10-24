// ============================================
// API CONFIGURATION
// ============================================
const API_URL = "http://localhost:8080/api";
let authToken = localStorage.getItem("authToken");
let currentUser = JSON.parse(localStorage.getItem("currentUser"));
let isAdmin = localStorage.getItem("isAdmin") === "true";
let cart = JSON.parse(localStorage.getItem("cart")) || [];

// ============================================
// AUTHENTICATION FUNCTIONS
// ============================================

// User Registration
async function registerUser(fullName, email, password, phone, address) {
  try {
    const response = await fetch(`${API_URL}/users/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ fullName, email, password, phone, address }),
    });

    const data = await response.json();

    if (data.success) {
      alert("Registration successful! Please login.");
      window.location.href = "Login.html";
      return true;
    } else {
      alert(data.message || "Registration failed");
      return false;
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Registration failed. Please try again.");
    return false;
  }
}

// User Login
async function loginUser(email, password) {
  try {
    const response = await fetch(`${API_URL}/users/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();

    if (data.success) {
      localStorage.setItem("authToken", data.token);
      localStorage.setItem("currentUser", JSON.stringify(data.user));
      alert("Login successful!");
      window.location.href = "Home.html";
      return true;
    } else {
      alert(data.message || "Invalid credentials");
      return false;
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Login failed. Please try again.");
    return false;
  }
}

// Admin Login
async function loginAdmin(email, password) {
  try {
    const response = await fetch(`${API_URL}/admin/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();

    if (data.success) {
      localStorage.setItem("authToken", data.token);
      localStorage.setItem("currentUser", JSON.stringify(data.admin));
      localStorage.setItem("isAdmin", "true");
      alert("Admin login successful!");
      window.location.href = "Admin_Page.html";
      return true;
    } else {
      alert(data.message || "Invalid admin credentials");
      return false;
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Admin login failed. Please try again.");
    return false;
  }
}

// Logout
function logout() {
  localStorage.removeItem("authToken");
  localStorage.removeItem("currentUser");
  localStorage.removeItem("isAdmin");
  localStorage.removeItem("cart");
  alert("Logged out successfully!");
  window.location.href = "Home.html";
}

// Check if user is logged in
function isLoggedIn() {
  return localStorage.getItem("authToken") !== null;
}

// Get current user
function getCurrentUser() {
  return JSON.parse(localStorage.getItem("currentUser"));
}

// ============================================
// PRODUCT FUNCTIONS
// ============================================

// Get All Products
async function getAllProducts() {
  try {
    const response = await fetch(`${API_URL}/products/all`);
    return await response.json();
  } catch (error) {
    console.error("Error fetching products:", error);
    return [];
  }
}

// Get Available Products
async function getAvailableProducts() {
  try {
    const response = await fetch(`${API_URL}/products/available`);
    return await response.json();
  } catch (error) {
    console.error("Error fetching products:", error);
    return [];
  }
}

// Get Products by Category
async function getProductsByCategory(category) {
  try {
    const response = await fetch(`${API_URL}/products/category/${category}`);
    return await response.json();
  } catch (error) {
    console.error("Error fetching products:", error);
    return [];
  }
}

// Get Product by ID
async function getProductById(id) {
  try {
    const response = await fetch(`${API_URL}/products/${id}`);
    return await response.json();
  } catch (error) {
    console.error("Error fetching product:", error);
    return null;
  }
}

// Create Product (Admin only)
async function createProduct(productData) {
  if (!authToken) {
    alert("Please login as admin");
    return null;
  }

  try {
    const response = await fetch(`${API_URL}/products/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(productData),
    });

    return await response.json();
  } catch (error) {
    console.error("Error creating product:", error);
    alert("Failed to create product");
    return null;
  }
}

// Update Product (Admin only)
async function updateProduct(id, productData) {
  if (!authToken) {
    alert("Please login as admin");
    return null;
  }

  try {
    const response = await fetch(`${API_URL}/products/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify(productData),
    });

    return await response.json();
  } catch (error) {
    console.error("Error updating product:", error);
    alert("Failed to update product");
    return null;
  }
}

// Delete Product (Admin only)
async function deleteProduct(id) {
  if (!authToken) {
    alert("Please login as admin");
    return false;
  }

  try {
    const response = await fetch(`${API_URL}/products/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    return response.ok;
  } catch (error) {
    console.error("Error deleting product:", error);
    alert("Failed to delete product");
    return false;
  }
}

// ============================================
// CART FUNCTIONS
// ============================================

// Add to Cart
function addToCart(productId, productName, price) {
  if (!isLoggedIn()) {
    alert("Please login to add items to cart");
    window.location.href = "Login.html";
    return;
  }

  const existingItem = cart.find((item) => item.productId === productId);

  if (existingItem) {
    existingItem.quantity++;
  } else {
    cart.push({
      productId: parseInt(productId),
      productName: productName,
      price: parseFloat(price),
      quantity: 1,
    });
  }

  localStorage.setItem("cart", JSON.stringify(cart));
  updateCartCount();
  alert(`${productName} added to cart!`);
}

// Remove from Cart
function removeFromCart(productId) {
  cart = cart.filter((item) => item.productId !== parseInt(productId));
  localStorage.setItem("cart", JSON.stringify(cart));
  updateCartCount();
}

// Update Cart Quantity
function updateCartQuantity(productId, quantity) {
  const item = cart.find((item) => item.productId === parseInt(productId));

  if (item) {
    item.quantity = parseInt(quantity);

    if (item.quantity <= 0) {
      removeFromCart(productId);
    } else {
      localStorage.setItem("cart", JSON.stringify(cart));
    }
    updateCartCount();
  }
}

// Clear Cart
function clearCart() {
  cart = [];
  localStorage.setItem("cart", JSON.stringify(cart));
  updateCartCount();
}

// Get Cart
function getCart() {
  return cart;
}

// Get Cart Total
function getCartTotal() {
  return cart.reduce((total, item) => total + item.price * item.quantity, 0);
}

// Get Cart Count
function getCartCount() {
  return cart.reduce((count, item) => count + item.quantity, 0);
}

// Update Cart Count Display
function updateCartCount() {
  const countElements = document.querySelectorAll(".cart-count");
  const count = getCartCount();
  countElements.forEach((el) => {
    el.textContent = count;
  });
}

// ============================================
// ORDER FUNCTIONS
// ============================================

// Create Order
async function createOrder(
  productId,
  quantity,
  deliveryAddress,
  phoneNumber,
  paymentMethod
) {
  if (!isLoggedIn()) {
    alert("Please login to place order");
    return null;
  }

  const user = getCurrentUser();

  try {
    const response = await fetch(`${API_URL}/orders/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({
        userId: user.id,
        productId: productId,
        quantity: quantity,
        deliveryAddress: deliveryAddress,
        phoneNumber: phoneNumber,
        paymentMethod: paymentMethod,
      }),
    });

    return await response.json();
  } catch (error) {
    console.error("Error creating order:", error);
    alert("Failed to create order");
    return null;
  }
}

// Place Order for all cart items
async function placeOrderFromCart(deliveryAddress, phoneNumber, paymentMethod) {
  if (cart.length === 0) {
    alert("Your cart is empty");
    return false;
  }

  if (!isLoggedIn()) {
    alert("Please login to place order");
    window.location.href = "Login.html";
    return false;
  }

  const user = getCurrentUser();
  const orders = [];

  for (const item of cart) {
    const order = await createOrder(
      item.productId,
      item.quantity,
      deliveryAddress,
      phoneNumber,
      paymentMethod
    );

    if (order) {
      orders.push(order);
    }
  }

  if (orders.length > 0) {
    if (paymentMethod === "ONLINE" && orders[0]) {
      // Initiate payment for first order (you can modify this logic)
      await initiatePayment(orders[0].id);
    } else {
      alert("Order placed successfully! Payment on delivery.");
      clearCart();
      window.location.href = "Order_success.html";
    }
    return true;
  } else {
    alert("Failed to place order");
    return false;
  }
}

// Get User Orders
async function getUserOrders() {
  if (!isLoggedIn()) {
    return [];
  }

  const user = getCurrentUser();

  try {
    const response = await fetch(`${API_URL}/orders/user/${user.id}`, {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    return await response.json();
  } catch (error) {
    console.error("Error fetching orders:", error);
    return [];
  }
}

// Get All Orders (Admin)
async function getAllOrders() {
  if (!isLoggedIn() || !isAdmin) {
    return [];
  }

  try {
    const response = await fetch(`${API_URL}/orders/all`, {
      headers: {
        Authorization: `Bearer ${authToken}`,
      },
    });

    return await response.json();
  } catch (error) {
    console.error("Error fetching orders:", error);
    return [];
  }
}

// Update Order Status (Admin)
async function updateOrderStatus(orderId, status) {
  if (!isLoggedIn() || !isAdmin) {
    alert("Admin access required");
    return false;
  }

  try {
    const response = await fetch(`${API_URL}/orders/${orderId}/status`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({ status: status }),
    });

    return await response.json();
  } catch (error) {
    console.error("Error updating order:", error);
    alert("Failed to update order status");
    return null;
  }
}

// ============================================
// PAYMENT FUNCTIONS (RAZORPAY)
// ============================================

// Initiate Payment
async function initiatePayment(orderId) {
  try {
    const response = await fetch(`${API_URL}/payment/create-order`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({ orderId: orderId }),
    });

    const data = await response.json();

    if (data.success) {
      openRazorpayCheckout(data);
    } else {
      alert("Payment initiation failed: " + data.message);
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Payment initiation failed");
  }
}

// Open Razorpay Checkout
function openRazorpayCheckout(orderData) {
  const user = getCurrentUser();

  const options = {
    key: orderData.keyId,
    amount: orderData.amount,
    currency: orderData.currency,
    name: "Pizza Window",
    description: "Food Order Payment",
    order_id: orderData.orderId,
    handler: function (response) {
      verifyPayment(response);
    },
    prefill: {
      name: user.fullName,
      email: user.email,
      contact: user.phone,
    },
    theme: {
      color: "#FF6B35",
    },
    modal: {
      ondismiss: function () {
        handlePaymentFailure(orderData.orderId, "Payment cancelled by user");
      },
    },
  };

  const rzp = new Razorpay(options);

  rzp.on("payment.failed", function (response) {
    handlePaymentFailure(orderData.orderId, response.error.description);
  });

  rzp.open();
}

// Verify Payment
async function verifyPayment(response) {
  try {
    const verifyResponse = await fetch(`${API_URL}/payment/verify`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({
        razorpay_order_id: response.razorpay_order_id,
        razorpay_payment_id: response.razorpay_payment_id,
        razorpay_signature: response.razorpay_signature,
      }),
    });

    const data = await verifyResponse.json();

    if (data.success) {
      alert("Payment successful!");
      clearCart();
      window.location.href = "Order_success.html";
    } else {
      alert("Payment verification failed");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Payment verification failed");
  }
}

// Handle Payment Failure
async function handlePaymentFailure(razorpayOrderId, reason) {
  try {
    await fetch(`${API_URL}/payment/failure`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${authToken}`,
      },
      body: JSON.stringify({
        razorpay_order_id: razorpayOrderId,
        reason: reason,
      }),
    });

    alert("Payment failed: " + reason);
    window.location.href = "exception.html";
  } catch (error) {
    console.error("Error:", error);
  }
}

// ============================================
// UTILITY FUNCTIONS
// ============================================

// Format Price
function formatPrice(price) {
  return "₹" + parseFloat(price).toFixed(2);
}

// Format Date
function formatDate(dateString) {
  const date = new Date(dateString);
  return (
    date.toLocaleDateString("en-IN") + " " + date.toLocaleTimeString("en-IN")
  );
}

// ============================================
// INITIALIZATION
// ============================================

// Initialize on page load
document.addEventListener("DOMContentLoaded", function () {
  // Update cart count
  updateCartCount();

  // Load user info if logged in
  if (isLoggedIn()) {
    const user = getCurrentUser();
    const userNameElements = document.querySelectorAll(".user-name");
    userNameElements.forEach((el) => {
      el.textContent = user.fullName;
    });
  }
});
