import 'package:flutter/material.dart';
import '../models/company.dart';
import '../services/company_service.dart';
import '../widgets/company_card.dart';

/// Main screen for displaying the company catalog
class CompanyCatalogScreen extends StatefulWidget {
  const CompanyCatalogScreen({Key? key}) : super(key: key);

  @override
  _CompanyCatalogScreenState createState() => _CompanyCatalogScreenState();
}

class _CompanyCatalogScreenState extends State<CompanyCatalogScreen> {
  final CompanyService _companyService = CompanyService();
  late Future<List<Company>> _companiesFuture;
  late TextEditingController _searchController;
  String _searchQuery = '';
  ServiceCategory? _selectedCategory;
  bool _showingFeatured = false;
  bool _showingTopRated = false;
  
  @override
  void initState() {
    super.initState();
    _searchController = TextEditingController();
    _loadCompanies();
  }
  
  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }
  
  void _loadCompanies() {
    setState(() {
      if (_searchQuery.isNotEmpty) {
        _companiesFuture = _companyService.searchCompanies(_searchQuery);
      } else if (_selectedCategory != null) {
        _companiesFuture = _companyService.getCompaniesByCategory(_selectedCategory!);
      } else if (_showingFeatured) {
        _companiesFuture = _companyService.getFeaturedCompanies();
      } else if (_showingTopRated) {
        _companiesFuture = _companyService.getTopRatedCompanies();
      } else {
        _companiesFuture = _companyService.getAllCompanies();
      }
    });
  }
  
  void _resetFilters() {
    setState(() {
      _searchQuery = '';
      _searchController.clear();
      _selectedCategory = null;
      _showingFeatured = false;
      _showingTopRated = false;
      _loadCompanies();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Travel Companies'),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: _showFilterDialog,
          ),
        ],
      ),
      body: Column(
        children: [
          // Search bar
          Padding(
            padding: const EdgeInsets.all(16),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: 'Search companies...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: _searchQuery.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          _searchController.clear();
                          setState(() {
                            _searchQuery = '';
                            _loadCompanies();
                          });
                        },
                      )
                    : null,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide.none,
                ),
                filled: true,
                fillColor: Colors.grey[200],
              ),
              onSubmitted: (value) {
                setState(() {
                  _searchQuery = value;
                  _selectedCategory = null;
                  _showingFeatured = false;
                  _showingTopRated = false;
                  _loadCompanies();
                });
              },
            ),
          ),
          
          // Active filters
          if (_selectedCategory != null || _showingFeatured || _showingTopRated)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Row(
                children: [
                  const Text(
                    'Filters:',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(width: 8),
                  if (_selectedCategory != null)
                    Chip(
                      label: Text(_selectedCategory!.title),
                      onDeleted: () {
                        setState(() {
                          _selectedCategory = null;
                          _loadCompanies();
                        });
                      },
                    ),
                  if (_showingFeatured)
                    Chip(
                      label: const Text('Featured'),
                      onDeleted: () {
                        setState(() {
                          _showingFeatured = false;
                          _loadCompanies();
                        });
                      },
                    ),
                  if (_showingTopRated)
                    Chip(
                      label: const Text('Top Rated'),
                      onDeleted: () {
                        setState(() {
                          _showingTopRated = false;
                          _loadCompanies();
                        });
                      },
                    ),
                  const Spacer(),
                  TextButton(
                    onPressed: _resetFilters,
                    child: const Text('Reset All'),
                  ),
                ],
              ),
            ),
          
          // Company list
          Expanded(
            child: FutureBuilder<List<Company>>(
              future: _companiesFuture,
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
                          'Error loading companies: ${snapshot.error}',
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(height: 16),
                        ElevatedButton(
                          onPressed: _loadCompanies,
                          child: const Text('Retry'),
                        ),
                      ],
                    ),
                  );
                } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.search_off,
                          color: Colors.grey[400],
                          size: 60,
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'No companies found',
                          style: TextStyle(
                            fontSize: 18,
                            color: Colors.grey[600],
                          ),
                        ),
                        if (_searchQuery.isNotEmpty ||
                            _selectedCategory != null ||
                            _showingFeatured ||
                            _showingTopRated)
                          TextButton(
                            onPressed: _resetFilters,
                            child: const Text('Clear filters'),
                          ),
                      ],
                    ),
                  );
                }
                
                return RefreshIndicator(
                  onRefresh: () async {
                    _loadCompanies();
                  },
                  child: ListView.builder(
                    padding: const EdgeInsets.only(top: 8, bottom: 24),
                    itemCount: snapshot.data!.length,
                    itemBuilder: (context, index) {
                      return CompanyCard(company: snapshot.data![index]);
                    },
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
  
  void _showFilterDialog() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (BuildContext context) {
        return StatefulBuilder(
          builder: (context, setModalState) {
            return Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Center(
                    child: Text(
                      'Filter Companies',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  const Text(
                    'Service Categories',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: ServiceCategory.values.map((category) {
                      final isSelected = _selectedCategory == category;
                      return FilterChip(
                        label: Text(category.title),
                        selected: isSelected,
                        onSelected: (selected) {
                          setModalState(() {
                            if (selected) {
                              _selectedCategory = category;
                            } else {
                              _selectedCategory = null;
                            }
                          });
                        },
                      );
                    }).toList(),
                  ),
                  const SizedBox(height: 16),
                  const Text(
                    'Specials',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      FilterChip(
                        label: const Text('Featured'),
                        selected: _showingFeatured,
                        onSelected: (selected) {
                          setModalState(() {
                            _showingFeatured = selected;
                            if (selected) {
                              _showingTopRated = false;
                            }
                          });
                        },
                      ),
                      const SizedBox(width: 8),
                      FilterChip(
                        label: const Text('Top Rated'),
                        selected: _showingTopRated,
                        onSelected: (selected) {
                          setModalState(() {
                            _showingTopRated = selected;
                            if (selected) {
                              _showingFeatured = false;
                            }
                          });
                        },
                      ),
                    ],
                  ),
                  const SizedBox(height: 24),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      TextButton(
                        onPressed: () {
                          Navigator.pop(context);
                        },
                        child: const Text('Cancel'),
                      ),
                      const SizedBox(width: 16),
                      ElevatedButton(
                        onPressed: () {
                          Navigator.pop(context);
                          _loadCompanies();
                        },
                        child: const Text('Apply Filters'),
                      ),
                    ],
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }
}