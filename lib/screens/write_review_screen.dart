import 'package:flutter/material.dart';
import '../models/review.dart';
import '../services/review_service.dart';
import '../widgets/rating_bar.dart';

/// Screen for writing or editing a review
class WriteReviewScreen extends StatefulWidget {
  final String companyId;
  final Review? existingReview;
  
  const WriteReviewScreen({
    Key? key,
    required this.companyId,
    this.existingReview,
  }) : super(key: key);

  @override
  _WriteReviewScreenState createState() => _WriteReviewScreenState();
}

class _WriteReviewScreenState extends State<WriteReviewScreen> {
  final ReviewService _reviewService = ReviewService();
  final TextEditingController _commentController = TextEditingController();
  
  double _rating = 0;
  bool _isSubmitting = false;
  List<String> _photos = [];
  
  @override
  void initState() {
    super.initState();
    
    // If editing an existing review, populate the form
    if (widget.existingReview != null) {
      _rating = widget.existingReview!.rating.toDouble();
      _commentController.text = widget.existingReview!.comment;
      _photos = List.from(widget.existingReview!.photos);
    }
  }
  
  @override
  void dispose() {
    _commentController.dispose();
    super.dispose();
  }
  
  bool get _isValid => _rating > 0 && _commentController.text.trim().isNotEmpty;
  
  Future<void> _submitReview() async {
    if (!_isValid) return;
    
    setState(() {
      _isSubmitting = true;
    });
    
    try {
      final userId = 'mock-user-id'; // In a real app, this would be the current user's ID
      
      if (widget.existingReview != null) {
        // Update existing review
        final updatedReview = Review(
          id: widget.existingReview!.id,
          companyId: widget.companyId,
          userId: userId,
          rating: _rating.round(),
          comment: _commentController.text.trim(),
          photos: _photos,
          helpfulCount: widget.existingReview!.helpfulCount,
          createdAt: widget.existingReview!.createdAt,
          updatedAt: DateTime.now(),
        );
        
        await _reviewService.updateReview(widget.existingReview!.id, updatedReview);
      } else {
        // Create new review
        final review = Review(
          id: 'temp-id', // This will be replaced by the backend
          companyId: widget.companyId,
          userId: userId,
          rating: _rating.round(),
          comment: _commentController.text.trim(),
          photos: _photos,
          createdAt: DateTime.now(),
          updatedAt: DateTime.now(),
        );
        
        await _reviewService.createReview(review);
      }
      
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(widget.existingReview != null
              ? 'Review updated successfully'
              : 'Review submitted successfully'),
        ),
      );
      
      Navigator.pop(context, true);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Error: $e'),
          backgroundColor: Colors.red,
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          _isSubmitting = false;
        });
      }
    }
  }
  
  void _addPhoto() {
    // In a real app, this would open the camera or gallery
    // For simplicity, we'll just add a placeholder URL
    setState(() {
      _photos.add('https://via.placeholder.com/300');
    });
  }
  
  void _removePhoto(int index) {
    setState(() {
      _photos.removeAt(index);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.existingReview != null ? 'Edit Review' : 'Write a Review'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Rating selector
            Center(
              child: Column(
                children: [
                  const Text(
                    'Rate your experience',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 16),
                  RatingBar(
                    rating: _rating,
                    size: 40,
                    isSelectable: true,
                    onRatingChanged: (rating) {
                      setState(() {
                        _rating = rating;
                      });
                    },
                  ),
                  const SizedBox(height: 8),
                  Text(
                    _getRatingDescription(),
                    style: TextStyle(
                      color: Colors.grey[600],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            
            // Comment input
            const Text(
              'Write your review',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _commentController,
              maxLines: 5,
              decoration: InputDecoration(
                hintText: 'Share your experience with this company...',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                filled: true,
                fillColor: Colors.grey[50],
              ),
              onChanged: (_) {
                setState(() {});
              },
            ),
            const SizedBox(height: 24),
            
            // Photo upload
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Add photos',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                TextButton.icon(
                  icon: const Icon(Icons.add_a_photo),
                  label: const Text('Add Photo'),
                  onPressed: _addPhoto,
                ),
              ],
            ),
            const SizedBox(height: 8),
            if (_photos.isNotEmpty)
              SizedBox(
                height: 100,
                child: ListView.builder(
                  scrollDirection: Axis.horizontal,
                  itemCount: _photos.length,
                  itemBuilder: (context, index) {
                    return Stack(
                      children: [
                        Padding(
                          padding: const EdgeInsets.only(right: 8),
                          child: ClipRRect(
                            borderRadius: BorderRadius.circular(8),
                            child: Image.network(
                              _photos[index],
                              height: 100,
                              width: 100,
                              fit: BoxFit.cover,
                            ),
                          ),
                        ),
                        Positioned(
                          top: 4,
                          right: 12,
                          child: GestureDetector(
                            onTap: () => _removePhoto(index),
                            child: Container(
                              padding: const EdgeInsets.all(4),
                              decoration: const BoxDecoration(
                                color: Colors.black54,
                                shape: BoxShape.circle,
                              ),
                              child: const Icon(
                                Icons.close,
                                color: Colors.white,
                                size: 16,
                              ),
                            ),
                          ),
                        ),
                      ],
                    );
                  },
                ),
              )
            else
              Container(
                height: 100,
                decoration: BoxDecoration(
                  color: Colors.grey[200],
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: Colors.grey[300]!),
                ),
                child: Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.add_photo_alternate,
                        size: 40,
                        color: Colors.grey[400],
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Add photos to your review',
                        style: TextStyle(
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
      bottomNavigationBar: BottomAppBar(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: ElevatedButton(
            onPressed: _isValid && !_isSubmitting ? _submitReview : null,
            style: ElevatedButton.styleFrom(
              padding: const EdgeInsets.symmetric(vertical: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: _isSubmitting
                ? const CircularProgressIndicator(
                    color: Colors.white,
                  )
                : Text(
                    widget.existingReview != null ? 'Update Review' : 'Submit Review',
                    style: const TextStyle(fontSize: 16),
                  ),
          ),
        ),
      ),
    );
  }
  
  String _getRatingDescription() {
    if (_rating <= 0) return 'Tap to rate';
    if (_rating <= 1) return 'Poor';
    if (_rating <= 2) return 'Below average';
    if (_rating <= 3) return 'Average';
    if (_rating <= 4) return 'Good';
    return 'Excellent';
  }
}