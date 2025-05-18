// Flutter implementation of Company data model
import 'package:flutter/foundation.dart';

/// Enum representing categories of services that travel companies can offer.
enum ServiceCategory {
  accommodation,
  transportation,
  tours,
  activities,
  foodAndDining,
  guideServices,
  equipmentRental,
  photography,
  other
}

/// Extension on ServiceCategory to provide human-readable titles
extension ServiceCategoryExt on ServiceCategory {
  String get title {
    switch (this) {
      case ServiceCategory.accommodation:
        return 'Accommodation';
      case ServiceCategory.transportation:
        return 'Transportation';
      case ServiceCategory.tours:
        return 'Tours';
      case ServiceCategory.activities:
        return 'Activities';
      case ServiceCategory.foodAndDining:
        return 'Food & Dining';
      case ServiceCategory.guideServices:
        return 'Guide Services';
      case ServiceCategory.equipmentRental:
        return 'Equipment Rental';
      case ServiceCategory.photography:
        return 'Photography';
      case ServiceCategory.other:
        return 'Other';
    }
  }
  
  String get icon {
    switch (this) {
      case ServiceCategory.accommodation:
        return 'assets/icons/accommodation.png';
      case ServiceCategory.transportation:
        return 'assets/icons/transportation.png';
      case ServiceCategory.tours:
        return 'assets/icons/tours.png';
      case ServiceCategory.activities:
        return 'assets/icons/activities.png';
      case ServiceCategory.foodAndDining:
        return 'assets/icons/food.png';
      case ServiceCategory.guideServices:
        return 'assets/icons/guide.png';
      case ServiceCategory.equipmentRental:
        return 'assets/icons/equipment.png';
      case ServiceCategory.photography:
        return 'assets/icons/photography.png';
      case ServiceCategory.other:
        return 'assets/icons/other.png';
    }
  }
}

/// Model class for a physical location
class Location {
  final String address;
  final String city;
  final String country;
  final double latitude;
  final double longitude;
  
  const Location({
    required this.address,
    required this.city,
    required this.country,
    required this.latitude,
    required this.longitude,
  });
  
  // Factory constructor from JSON
  factory Location.fromJson(Map<String, dynamic> json) {
    return Location(
      address: json['address'],
      city: json['city'],
      country: json['country'],
      latitude: json['latitude'],
      longitude: json['longitude'],
    );
  }
  
  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'address': address,
      'city': city,
      'country': country,
      'latitude': latitude,
      'longitude': longitude,
    };
  }
  
  @override
  String toString() {
    return '$address, $city, $country';
  }
  
  // For map display formatting
  String get mapDisplay => '$city, $country';
}

/// Model class for contact information
class ContactInfo {
  final String email;
  final String phone;
  final String? website;
  final Map<String, String> socialMedia;
  
  const ContactInfo({
    required this.email,
    required this.phone,
    this.website,
    this.socialMedia = const {},
  });
  
  // Factory constructor from JSON
  factory ContactInfo.fromJson(Map<String, dynamic> json) {
    return ContactInfo(
      email: json['email'],
      phone: json['phone'],
      website: json['website'],
      socialMedia: Map<String, String>.from(json['socialMedia'] ?? {}),
    );
  }
  
  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'email': email,
      'phone': phone,
      'website': website,
      'socialMedia': socialMedia,
    };
  }
}

/// Model class for a service offered by a company
class Service {
  final String id;
  final String name;
  final String description;
  final ServiceCategory category;
  final double? price;
  final String? currencyCode;
  
  const Service({
    required this.id,
    required this.name,
    required this.description,
    required this.category,
    this.price,
    this.currencyCode,
  });
  
  // Factory constructor from JSON
  factory Service.fromJson(Map<String, dynamic> json) {
    return Service(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      category: ServiceCategory.values.firstWhere(
        (cat) => cat.name.toUpperCase() == json['category'],
        orElse: () => ServiceCategory.other,
      ),
      price: json['price'],
      currencyCode: json['currencyCode'],
    );
  }
  
  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'category': category.name.toUpperCase(),
      'price': price,
      'currencyCode': currencyCode,
    };
  }
  
  // Format price with currency
  String? get formattedPrice {
    if (price == null || currencyCode == null) return null;
    return '$currencyCode ${price!.toStringAsFixed(2)}';
  }
}

/// Main model class for a company
class Company {
  final String id;
  final String name;
  final String description;
  final String logo;
  final String coverImage;
  final Location location;
  final ContactInfo contactInfo;
  final List<Service> services;
  final double rating;
  final int reviewCount;
  final bool verified;
  final bool featured;
  final DateTime createdAt;
  final DateTime updatedAt;
  
  const Company({
    required this.id,
    required this.name,
    required this.description,
    required this.logo,
    required this.coverImage,
    required this.location,
    required this.contactInfo,
    required this.services,
    this.rating = 0.0,
    this.reviewCount = 0,
    this.verified = false,
    this.featured = false,
    required this.createdAt,
    required this.updatedAt,
  });
  
  // Factory constructor from JSON
  factory Company.fromJson(Map<String, dynamic> json) {
    return Company(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      logo: json['logo'],
      coverImage: json['coverImage'],
      location: Location.fromJson(json['location']),
      contactInfo: ContactInfo.fromJson(json['contactInfo']),
      services: (json['services'] as List)
          .map((service) => Service.fromJson(service))
          .toList(),
      rating: json['rating'] ?? 0.0,
      reviewCount: json['reviewCount'] ?? 0,
      verified: json['verified'] ?? false,
      featured: json['featured'] ?? false,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }
  
  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'logo': logo,
      'coverImage': coverImage,
      'location': location.toJson(),
      'contactInfo': contactInfo.toJson(),
      'services': services.map((service) => service.toJson()).toList(),
      'rating': rating,
      'reviewCount': reviewCount,
      'verified': verified,
      'featured': featured,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }
  
  // Helper to get a list of unique service categories
  List<ServiceCategory> get uniqueServiceCategories {
    return services
        .map((service) => service.category)
        .toSet()
        .toList();
  }
  
  // Format rating as a string with appropriate decimal places
  String get formattedRating {
    return rating.toStringAsFixed(1);
  }
}