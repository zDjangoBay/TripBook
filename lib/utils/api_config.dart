// Configuration for API connections
class ApiConfig {
  // Base URL for API requests
  static String baseUrl = 'https://api.tripbook.com';
  
  // Default headers for API requests
  static Map<String, String> headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };
  
  // Set the authorization token
  static void setAuthToken(String token) {
    headers['Authorization'] = 'Bearer $token';
  }
  
  // Clear the authorization token
  static void clearAuthToken() {
    headers.remove('Authorization');
  }
}