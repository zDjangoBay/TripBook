import 'package:flutter/material.dart';

/// Custom rating bar widget for display and selection
class RatingBar extends StatelessWidget {
  final double rating;
  final double size;
  final bool isSelectable;
  final ValueChanged<double>? onRatingChanged;
  
  const RatingBar({
    Key? key,
    required this.rating,
    this.size = 24,
    this.isSelectable = false,
    this.onRatingChanged,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: List.generate(5, (index) {
        return GestureDetector(
          onTap: isSelectable
              ? () => onRatingChanged?.call(index + 1.0)
              : null,
          child: Icon(
            index < rating.floor()
                ? Icons.star
                : (index == rating.floor() && rating % 1 > 0)
                    ? Icons.star_half
                    : Icons.star_border,
            color: Colors.amber,
            size: size,
          ),
        );
      }),
    );
  }
}

/// Widget for displaying rating distribution with progress bars
class RatingDistribution extends StatelessWidget {
  final Map<int, int> distribution;
  final int totalCount;
  
  const RatingDistribution({
    Key? key,
    required this.distribution,
    required this.totalCount,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: List.generate(5, (index) {
        final starNumber = 5 - index;
        final count = distribution[starNumber] ?? 0;
        final percent = totalCount > 0 ? (count / totalCount) : 0.0;
        
        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 2),
          child: Row(
            children: [
              Text(
                '$starNumber',
                style: const TextStyle(fontSize: 12),
              ),
              const SizedBox(width: 4),
              const Icon(Icons.star, size: 12, color: Colors.amber),
              const SizedBox(width: 8),
              Expanded(
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(4),
                  child: LinearProgressIndicator(
                    value: percent,
                    backgroundColor: Colors.grey[200],
                    minHeight: 8,
                    color: Colors.amber,
                  ),
                ),
              ),
              const SizedBox(width: 8),
              SizedBox(
                width: 32,
                child: Text(
                  count.toString(),
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey[600],
                  ),
                  textAlign: TextAlign.right,
                ),
              ),
            ],
          ),
        );
      }),
    );
  }
}