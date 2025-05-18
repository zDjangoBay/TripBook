// Flutter service for review-related API calls
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/review.dart';
import '../utils/api_config.dart';

/// Service class for review-related API operations
class ReviewService {
  final http.Client _httpClient;
  final String _baseUrl;
  
  ReviewService({
    http.Client? httpClient,
    String? baseUrl,
  }) : _httpClient = httpClient ?? http.Client(),
       _baseUrl = baseUrl ?? ApiConfig.baseUrl;
  
  /// Get reviews for a company
  Future<List<Review>> getReviewsByCompany(String companyId) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/reviews/company/$companyId'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Review.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load reviews: ${response.body}');
    }
  }
  
  /// Get review by ID
  Future<Review> getReviewById(String id) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/reviews/$id'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      return Review.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to load review: ${response.body}');
    }
  }
  
  /// Submit a new review
  Future<Review> createReview(Review review) async {
    final response = await _httpClient.post(
      Uri.parse('$_baseUrl/api/reviews'),
      headers: ApiConfig.headers,
      body: json.encode(review.toJson()),
    );
    
    if (response.statusCode == 201) {
      return Review.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to create review: ${response.body}');
    }
  }
  
  /// Update an existing review
  Future<Review> updateReview(String id, Review review) async {
    final response = await _httpClient.put(
      Uri.parse('$_baseUrl/api/reviews/$id'),
      headers: ApiConfig.headers,
      body: json.encode(review.toJson()),
    );
    
    if (response.statusCode == 200) {
      return Review.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to update review: ${response.body}');
    }
  }
  
  /// Delete a review
  Future<bool> deleteReview(String id) async {
    final response = await _httpClient.delete(
      Uri.parse('$_baseUrl/api/reviews/$id'),
      headers: ApiConfig.headers,
    );
    
    return response.statusCode == 204;
  }
  
  /// Mark a review as helpful
  Future<bool> markReviewAsHelpful(String reviewId, String userId) async {
    final response = await _httpClient.post(
      Uri.parse('$_baseUrl/api/reviews/$reviewId/helpful?userId=$userId'),
      headers: ApiConfig.headers,
    );
    
    return response.statusCode == 200;
  }
  
  /// Get review summary for a company
  Future<ReviewSummary> getReviewSummary(String companyId) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/reviews/summary/$companyId'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      return ReviewSummary.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to load review summary: ${response.body}');
    }
  }
  
  /// Get reviews by a user
  Future<List<Review>> getReviewsByUser(String userId) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/reviews/user/$userId'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Review.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load user reviews: ${response.body}');
    }
  }
}