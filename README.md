# TripBook

TripBook: A mobile social network for travelers exploring Africa & beyond. Share stories, photos, and tips, rate travel agencies, and connect with adventurers. Community-driven platform to discover hidden gems, promote tourism, and ensure safer journeys. Built with React Native, Node.js & geolocation APIs. Contributions welcome! 🌍✨

## Payment Module Features Implemented

- **Card Validation:**
  - Added card brand detection (Visa, Mastercard, American Express, Discover)
  - Implemented card number validation based on IIN (Issuer Identification Number)
  - Added card number input field for credit/debit card payments

- **Mobile Money Validation:**
  - Added phone number validation for Orange Money and MTN Mobile Money
  - Added mobile number input field with phone keyboard type
  - Added country code validation for mobile money payments

- **Database Integration:**
  - Created `PaymentTransactionEntity` for Room database
  - Added `PaymentDao` with queries for:
    - All transactions
    - Transactions by status
    - Transactions by payment method
    - Transactions by date range
    - Total amount by status
    - Transaction count by status

- **UI Enhancements:**
  - Added conditional input fields based on payment method
  - Added validation for card numbers and mobile numbers
  - Improved error handling and user feedback
  - Added proper keyboard types for inputs

### Payment Flow

1. User selects payment method:
   - For card payments: Enter card number (validated for card brand)
   - For mobile money: Enter mobile number (validated with country code)
   - For bank transfer: No additional info needed

2. Payment processing:
   - Validates card numbers for credit/debit cards
   - Validates mobile numbers for Orange Money/MTN Mobile Money
   - Processes payment through `MockPaymentProcessor`
   - Saves transaction to database

3. Success screen:
   - Shows transaction details
   - Includes card brand or mobile money info
   - Provides back to home navigation

### Admin Database Features

- View all transactions
- Filter by payment method (card, Orange Money, MTN Mobile Money)
- Filter by date range
- Get total amounts processed
- Track transaction counts
