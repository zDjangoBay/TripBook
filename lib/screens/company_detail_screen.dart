import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import '../models/company.dart';
import '../models/review.dart';
import '../services/review_service.dart';
import '../widgets/rating_bar.dart';
import '../widgets/review_card.dart';
import 'write_review_screen.dart';

/// Screen for displaying detailed information about a company
class CompanyDetailScreen extends StatefulWidget {
  final Company company;
  
  const CompanyDetailScreen({
    Key? key,
    required this.company,
  }) : super(key: key);

  @override
  _CompanyDetailScreenState createState() => _CompanyDetailScreenState();
}

class _CompanyDetailScreenState extends State<CompanyDetailScreen> {
  final ReviewService _reviewService = ReviewService();
  late Future<List<Review>> _reviewsFuture;
  late Future<ReviewSummary> _reviewSummaryFuture;
  
  @override
  void initState() {
    super.initState();
    _loadReviews();
  }
  
  void _loadReviews() {
    setState(() {
      _reviewsFuture = _reviewService.getReviewsByCompany(widget.company.id);
      _reviewSummaryFuture = _reviewService.getReviewSummary(widget.company.id);
    });
  }
  
  Future<void> _launchUrl(String url) async {
    if (!await launchUrl(Uri.parse(url))) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Could not open the link'),
        ),
      );
    }
  }
  
  void _markReviewAsHelpful(String reviewId) async {
    // In a real app, we would use the current user's ID
    const userId = 'mock-user-id';
    
    try {
      final success = await _reviewService.markReviewAsHelpful(reviewId, userId);
      if (success) {
        _loadReviews();
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Marked as helpful'),
          ),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Error: $e'),
        ),
      );
    }
  }
  
  void _navigateToWriteReview() async {
    final result = await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => WriteReviewScreen(companyId: widget.company.id),
      ),
    );
    
    if (result == true) {
      _loadReviews();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: DefaultTabController(
        length: 3,
        child: NestedScrollView(
          headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
            return [
              // Cover image and company info
              SliverAppBar(
                expandedHeight: 200,
                floating: false,
                pinned: true,
                flexibleSpace: FlexibleSpaceBar(
                  background: Stack(
                    fit: StackFit.expand,
                    children: [
                      Image.network(
                        widget.company.coverImage,
                        fit: BoxFit.cover,
                      ),
                      Container(
                        decoration: BoxDecoration(
                          gradient: LinearGradient(
                            begin: Alignment.topCenter,
                            end: Alignment.bottomCenter,
                            colors: [
                              Colors.transparent,
                              Colors.black.withOpacity(0.7),
                            ],
                          ),
                        ),
                      ),
                      Positioned(
                        bottom: 16,
                        left: 16,
                        child: Row(
                          children: [
                            CircleAvatar(
                              radius: 24,
                              backgroundImage: NetworkImage(widget.company.logo),
                            ),
                            const SizedBox(width: 12),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Row(
                                  children: [
                                    Text(
                                      widget.company.name,
                                      style: const TextStyle(
                                        color: Colors.white,
                                        fontSize: 20,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                    if (widget.company.verified)
                                      const Padding(
                                        padding: EdgeInsets.only(left: 4),
                                        child: Icon(
                                          Icons.verified,
                                          color: Colors.blue,
                                          size: 16,
                                        ),
                                      ),
                                  ],
                                ),
                                const SizedBox(height: 4),
                                Row(
                                  children: [
                                    const Icon(
                                      Icons.star,
                                      color: Colors.amber,
                                      size: 16,
                                    ),
                                    const SizedBox(width: 4),
                                    Text(
                                      '${widget.company.formattedRating} (${widget.company.reviewCount} reviews)',
                                      style: const TextStyle(
                                        color: Colors.white,
                                        fontSize: 14,
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              
              // Tab bar
              SliverPersistentHeader(
                delegate: _SliverAppBarDelegate(
                  const TabBar(
                    labelColor: Colors.blue,
                    unselectedLabelColor: Colors.grey,
                    indicatorColor: Colors.blue,
                    tabs: [
                      Tab(text: 'ABOUT'),
                      Tab(text: 'SERVICES'),
                      Tab(text: 'REVIEWS'),
                    ],
                  ),
                ),
                pinned: true,
              ),
            ];
          },
          body: TabBarView(
            children: [
              // About tab
              _buildAboutTab(),
              
              // Services tab
              _buildServicesTab(),
              
              // Reviews tab
              _buildReviewsTab(),
            ],
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _navigateToWriteReview,
        child: const Icon(Icons.rate_review),
      ),
    );
  }
  
  Widget _buildAboutTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Description section
          const Text(
            'Description',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(widget.company.description),
          const SizedBox(height: 24),
          
          // Location section
          const Text(
            'Location',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Card(
            elevation: 2,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(widget.company.location.toString()),
                  const SizedBox(height: 12),
                  SizedBox(
                    height: 200,
                    width: double.infinity,
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(8),
                      child: const ColoredBox(
                        color: Colors.grey,
                        child: Center(
                          child: Text(
                            'Map placeholder',
                            style: TextStyle(color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 24),
          
          // Contact info section
          const Text(
            'Contact Information',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Card(
            elevation: 2,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  _buildContactItem(
                    icon: Icons.email,
                    title: 'Email',
                    value: widget.company.contactInfo.email,
                    onTap: () => _launchUrl('mailto:${widget.company.contactInfo.email}'),
                  ),
                  const Divider(),
                  _buildContactItem(
                    icon: Icons.phone,
                    title: 'Phone',
                    value: widget.company.contactInfo.phone,
                    onTap: () => _launchUrl('tel:${widget.company.contactInfo.phone}'),
                  ),
                  if (widget.company.contactInfo.website != null) ...[
                    const Divider(),
                    _buildContactItem(
                      icon: Icons.language,
                      title: 'Website',
                      value: widget.company.contactInfo.website!,
                      onTap: () => _launchUrl(widget.company.contactInfo.website!),
                    ),
                  ],
                  if (widget.company.contactInfo.socialMedia.isNotEmpty) ...[
                    const Divider(),
                    Align(
                      alignment: Alignment.centerLeft,
                      child: Wrap(
                        spacing: 16,
                        children: widget.company.contactInfo.socialMedia.entries.map((entry) {
                          IconData icon;
                          switch (entry.key.toLowerCase()) {
                            case 'facebook':
                              icon = Icons.facebook;
                              break;
                            case 'twitter':
                              icon = Icons.travel_explore;
                              break;
                            case 'instagram':
                              icon = Icons.camera_alt;
                              break;
                            default:
                              icon = Icons.link;
                          }
                          
                          return IconButton(
                            icon: Icon(icon),
                            onPressed: () => _launchUrl(entry.value),
                          );
                        }).toList(),
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
  
  Widget _buildContactItem({
    required IconData icon,
    required String title,
    required String value,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Row(
          children: [
            Icon(icon, color: Colors.blue),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.grey[600],
                    ),
                  ),
                  Text(
                    value,
                    style: const TextStyle(fontSize: 16),
                  ),
                ],
              ),
            ),
            const Icon(
              Icons.arrow_forward_ios,
              size: 16,
              color: Colors.grey,
            ),
          ],
        ),
      ),
    );
  }
  
  Widget _buildServicesTab() {
    // Group services by category
    final servicesByCategory = <ServiceCategory, List<Service>>{};
    for (final service in widget.company.services) {
      if (!servicesByCategory.containsKey(service.category)) {
        servicesByCategory[service.category] = [];
      }
      servicesByCategory[service.category]!.add(service);
    }
    
    return ListView(
      padding: const EdgeInsets.all(16),
      children: servicesByCategory.entries.map((entry) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 8),
              child: Text(
                entry.key.title,
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            ...entry.value.map((service) => _buildServiceCard(service)),
            const SizedBox(height: 16),
          ],
        );
      }).toList(),
    );
  }
  
  Widget _buildServiceCard(Service service) {
    return Card(
      elevation: 2,
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Text(
                    service.name,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                if (service.formattedPrice != null)
                  Text(
                    service.formattedPrice!,
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Colors.blue,
                    ),
                  ),
              ],
            ),
            const SizedBox(height: 8),
            Text(service.description),
          ],
        ),
      ),
    );
  }
  
  Widget _buildReviewsTab() {
    return FutureBuilder<List<dynamic>>(
      future: Future.wait([_reviewsFuture, _reviewSummaryFuture]),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(
                  Icons.error_outline,
                  color: Colors.red,
                  size: 60,
                ),
                const SizedBox(height: 16),
                Text(
                  'Error loading reviews: ${snapshot.error}',
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: _loadReviews,
                  child: const Text('Retry'),
                ),
              ],
            ),
          );
        }
        
        final List<Review> reviews = snapshot.data![0];
        final ReviewSummary summary = snapshot.data![1];
        
        return ListView(
          padding: const EdgeInsets.all(16),
          children: [
            // Summary card
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    const Text(
                      'Rating Overview',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 16),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          summary.formattedRating,
                          style: const TextStyle(
                            fontSize: 48,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(width: 16),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            RatingBar(
                              rating: summary.averageRating,
                              size: 20,
                            ),
                            const SizedBox(height: 4),
                            Text(
                              'Based on ${summary.reviewCount} reviews',
                              style: TextStyle(
                                color: Colors.grey[600],
                                fontSize: 12,
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    RatingDistribution(
                      distribution: summary.ratingDistribution,
                      totalCount: summary.reviewCount,
                    ),
                  ],
                ),
              ),
            ),
            
            // Review heading
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 16),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    'Reviews (${reviews.length})',
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  OutlinedButton.icon(
                    icon: const Icon(Icons.rate_review),
                    label: const Text('Write a Review'),
                    onPressed: _navigateToWriteReview,
                  ),
                ],
              ),
            ),
            
            // Reviews list
            if (reviews.isEmpty)
              Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.rate_review_outlined,
                      size: 60,
                      color: Colors.grey[400],
                    ),
                    const SizedBox(height: 16),
                    Text(
                      'Be the first to review',
                      style: TextStyle(
                        fontSize: 18,
                        color: Colors.grey[600],
                      ),
                    ),
                  ],
                ),
              )
            else
              ...reviews.map((review) => ReviewCard(
                    review: review,
                    onMarkHelpful: _markReviewAsHelpful,
                    currentUserId: 'mock-user-id', // In a real app, this would be the current user's ID
                  )),
          ],
        );
      },
    );
  }
}

class _SliverAppBarDelegate extends SliverPersistentHeaderDelegate {
  final TabBar tabBar;

  _SliverAppBarDelegate(this.tabBar);

  @override
  double get minExtent => tabBar.preferredSize.height;

  @override
  double get maxExtent => tabBar.preferredSize.height;

  @override
  Widget build(BuildContext context, double shrinkOffset, bool overlapsContent) {
    return Container(
      color: Colors.white,
      child: tabBar,
    );
  }

  @override
  bool shouldRebuild(_SliverAppBarDelegate oldDelegate) {
    return false;
  }
}