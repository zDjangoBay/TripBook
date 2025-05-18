// Flutter implementation of Review data model
import 'package:flutter/foundation.dart';

/// Model class for a review
class Review {
  final String id;
  final String companyId;
  final String userId;
  final int rating;
  final String comment;
  final List<String> photos;
  final int helpfulCount;
  final DateTime createdAt;
  final DateTime updatedAt;
  
  const Review({
    required this.id,
    required this.companyId,
    required this.userId,
    required this.rating,
    required this.comment,
    this.photos = const [],
    this.helpfulCount = 0,
    required this.createdAt,
    required this.updatedAt,
  });
  
  // Factory constructor from JSON
  factory Review.fromJson(Map<String, dynamic> json) {
    return Review(
      id: json['id'],
      companyId: json['companyId'],
      userId: json['userId'],
      rating: json['rating'],
      comment: json['comment'],
      photos: List<String>.from(json['photos'] ?? []),
      helpfulCount: json['helpfulCount'] ?? 0,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }
  
  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'companyId': companyId,
      'userId': userId,
      'rating': rating,
      'comment': comment,
      'photos': photos,
      'helpfulCount': helpfulCount,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }
  
  // Check if review is recent (within 7 days)
  bool get isRecent {
    final now = DateTime.now();
    final difference = now.difference(createdAt);
    return difference.inDays <= 7;
  }
}

/// Model class for review summary statistics
class ReviewSummary {
  final String companyId;
  final double averageRating;
  final int reviewCount;
  final Map<int, int> ratingDistribution;
  
  const ReviewSummary({
    required this.companyId,
    required this.averageRating,
    required this.reviewCount,
    required this.ratingDistribution,
  });
  
  // Factory constructor from JSON
  factory ReviewSummary.fromJson(Map<String, dynamic> json) {
    return ReviewSummary(
      companyId: json['companyId'],
      averageRating: json['averageRating'],
      reviewCount: json['reviewCount'],
      ratingDistribution: Map<int, int>.from(json['ratingDistribution'] ?? {}),
    );
  }
  
  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'companyId': companyId,
      'averageRating': averageRating,
      'reviewCount': reviewCount,
      'ratingDistribution': ratingDistribution,
    };
  }
  
  // Get percentage for a specific rating (1-5)
  double getRatingPercentage(int rating) {
    if (reviewCount == 0) return 0.0;
    final count = ratingDistribution[rating] ?? 0;
    return (count / reviewCount) * 100;
  }
  
  // Format average rating as a string with appropriate decimal places
  String get formattedRating {
    return averageRating.toStringAsFixed(1);
  }
}