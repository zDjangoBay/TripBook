import 'package:flutter/material.dart';
import '../models/review.dart';
import 'rating_bar.dart';

/// Widget for displaying a review card
class ReviewCard extends StatelessWidget {
  final Review review;
  final Function(String)? onMarkHelpful;
  final String? currentUserId;
  
  const ReviewCard({
    Key? key,
    required this.review,
    this.onMarkHelpful,
    this.currentUserId,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8, horizontal: 0),
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // User info and rating
            Row(
              children: [
                const CircleAvatar(
                  backgroundImage: AssetImage('assets/images/default_avatar.png'),
                  radius: 16,
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'User', // Would be replaced with actual username
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        '${_formatDate(review.createdAt)}${review.isRecent ? " · Recent" : ""}',
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                ),
                RatingBar(
                  rating: review.rating.toDouble(),
                  size: 16,
                ),
              ],
            ),
            
            // Review content
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 12),
              child: Text(review.comment),
            ),
            
            // Review photos
            if (review.photos.isNotEmpty)
              SizedBox(
                height: 80,
                child: ListView.builder(
                  scrollDirection: Axis.horizontal,
                  itemCount: review.photos.length,
                  itemBuilder: (context, index) {
                    return Padding(
                      padding: const EdgeInsets.only(right: 8),
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(8),
                        child: Image.network(
                          review.photos[index],
                          height: 80,
                          width: 80,
                          fit: BoxFit.cover,
                        ),
                      ),
                    );
                  },
                ),
              ),
            
            // Helpful button
            if (onMarkHelpful != null && currentUserId != null && currentUserId != review.userId)
              Padding(
                padding: const EdgeInsets.only(top: 12),
                child: Row(
                  children: [
                    TextButton.icon(
                      onPressed: () => onMarkHelpful!(review.id),
                      icon: const Icon(Icons.thumb_up_alt_outlined, size: 16),
                      label: const Text('Helpful'),
                      style: TextButton.styleFrom(
                        foregroundColor: Colors.grey[700],
                        padding: const EdgeInsets.symmetric(horizontal: 12),
                        minimumSize: Size.zero,
                        tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                      ),
                    ),
                    if (review.helpfulCount > 0)
                      Padding(
                        padding: const EdgeInsets.only(left: 8),
                        child: Text(
                          '${review.helpfulCount} ${review.helpfulCount == 1 ? 'person' : 'people'} found this helpful',
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.grey[600],
                          ),
                        ),
                      ),
                  ],
                ),
              ),
          ],
        ),
      ),
    );
  }
  
  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }
}