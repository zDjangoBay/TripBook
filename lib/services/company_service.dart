// Flutter service for company-related API calls
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/company.dart';
import '../utils/api_config.dart';

/// Service class for company-related API operations
class CompanyService {
  final http.Client _httpClient;
  final String _baseUrl;
  
  CompanyService({
    http.Client? httpClient,
    String? baseUrl,
  }) : _httpClient = httpClient ?? http.Client(),
       _baseUrl = baseUrl ?? ApiConfig.baseUrl;
  
  /// Get all companies
  Future<List<Company>> getAllCompanies() async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/companies'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Company.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load companies: ${response.body}');
    }
  }
  
  /// Get company by ID
  Future<Company> getCompanyById(String id) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/companies/$id'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      return Company.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to load company: ${response.body}');
    }
  }
  
  /// Search companies by name
  Future<List<Company>> searchCompanies(String query) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/companies/search?q=$query'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Company.fromJson(json)).toList();
    } else {
      throw Exception('Failed to search companies: ${response.body}');
    }
  }
  
  /// Filter companies by location
  Future<List<Company>> getCompaniesByLocation({
    String? country,
    String? city,
  }) async {
    final queryParams = <String, String>{};
    if (country != null) queryParams['country'] = country;
    if (city != null) queryParams['city'] = city;
    
    final uri = Uri.parse('$_baseUrl/api/companies/location')
        .replace(queryParameters: queryParams);
    
    final response = await _httpClient.get(
      uri,
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Company.fromJson(json)).toList();
    } else {
      throw Exception('Failed to filter companies by location: ${response.body}');
    }
  }
  
  /// Filter companies by service category
  Future<List<Company>> getCompaniesByCategory(ServiceCategory category) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/companies/category/${category.name.toUpperCase()}'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Company.fromJson(json)).toList();
    } else {
      throw Exception('Failed to filter companies by category: ${response.body}');
    }
  }
  
  /// Get featured companies
  Future<List<Company>> getFeaturedCompanies({int limit = 10}) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/companies/featured?limit=$limit'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Company.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load featured companies: ${response.body}');
    }
  }
  
  /// Get top-rated companies
  Future<List<Company>> getTopRatedCompanies({
    int minReviews = 5,
    int limit = 10,
  }) async {
    final response = await _httpClient.get(
      Uri.parse('$_baseUrl/api/companies/top-rated?minReviews=$minReviews&limit=$limit'),
      headers: ApiConfig.headers,
    );
    
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((json) => Company.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load top-rated companies: ${response.body}');
    }
  }
}