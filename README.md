# 🛍️ DemoShoppingApp

A **demo Android shopping app** built with **Jetpack Compose**, integrated with **Stripe** for secure payments.  
The app communicates with a **Spring Boot backend** that generates **PaymentIntents** and provides the **client secret** required for confirming payments via Stripe.  

---

## ✨ Features

### 🛒 Shopping Functionality
- **Browse Products** – View a list of available products with details.  
- **Product Details** – See full information, price, and description.  
- **Add to Cart** – Add items to your shopping cart.  
- **Remove from Cart** – Delete products from your cart.  
- **Update Cart** – Increase or decrease product quantities.  

### 💳 Stripe Payment Integration
- Integrated **Stripe Android SDK** for smooth and secure payments.  
- **Spring Boot backend** creates **PaymentIntents** and returns the **client secret**.  
- Supports **credit/debit card payments** via Stripe’s **CardInputWidget** or custom UI.  
- **Billing Address Form** – Auto-filled from user profile and editable before checkout.  
- **Payment Status Handling:**
  - ✅ **Payment Success** → Navigate to success screen.  
  - ❌ **Payment Failed** → Show failed payment screen.  
  - ⚪ **Payment Canceled** → Properly handled for clean flow.  

### 👤 User Authentication & Profile
- **Sign Up / Register** – Create a new account.  
- **Login** – Authenticate and access personalized data.  
- **User Profile** – Store and manage user info including billing address for quick checkout.  

### 🧾 Cart & Checkout Flow
- View and manage your cart anytime.  
- Update quantities or remove items.  
- Clear the entire cart with one tap.  
- Tap **Checkout** to confirm billing details and complete secure payment.  

---

## 🏗️ Architecture

The app follows a clean **MVVM architecture**:

| Layer | Description |
|-------|--------------|
| **UI Layer** | Built with **Jetpack Compose** for modern declarative UIs. |
| **ViewModel Layer** | Manages UI state and interacts with repositories. |
| **Repository Layer** | Handles data operations and API communication. |
| **Backend** | A **Spring Boot** server that creates Stripe **PaymentIntents** and returns client secrets. |

---

## 🧰 Tech Stack

### **Frontend (Android)**
- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Navigation:** Jetpack Navigation Component  
- **Payments:** [Stripe Android SDK](https://stripe.com/docs/payments/accept-a-payment?platform=android)  
- **Networking:** Retrofit + Coroutines  
- **State Management:** ViewModel + LiveData / StateFlow  


### **Backend (Spring Boot)**
- **Framework:** Spring Boot  
- **Payment API:** [Stripe Java SDK](https://github.com/stripe/stripe-java).
- **Main Endpoint:**
  ```http
  POST /create-payment-intent
  ```
---

## **Screenshots**

### 🔐 Login and Register
<p align="left">
  <img src="ScreenShots/login.jpg" width="250" />
  <img src="ScreenShots/register.jpg" width="250" />
</p>

### 🛍️ Product Listing
<p align="left">
  <img src="ScreenShots/products.jpg" width="250" />
</p>

### 📦 Product Preview
<p align="left">
  <img src="ScreenShots/product_prev.jpg" width="250" />
</p>

### 🛒 Cart
<p align="left">
  <img src="ScreenShots/cart_empty.jpg" width="250" />
  <img src="ScreenShots/cart_filled.jpg" width="250" />
</p>

### 💳 Checkout / Billing
<p align="left">
  <img src="ScreenShots/billing.jpg" width="250" />
</p>

### ✅ Payment Success & ❌ Failed
<p align="left">
  <img src="ScreenShots/payment_success.jpg" width="250" />
  <img src="ScreenShots/payment_failed.jpg" width="250" />
</p>

### 👤 Profile
<p align="left">
  <img src="ScreenShots/profile.jpg" width="250" />
</p>

---


