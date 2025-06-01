// src/main/reactnative/components/CatalogueScreen.js
import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, FlatList, StyleSheet, TouchableOpacity } from 'react-native';

const CatalogueScreen = () => {
  const [companies, setCompanies] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState('name'); // default sort by name

  // Mock data - replace with API calls to your Kotlin backend
  useEffect(() => {
    const mockCompanies = [
      { id: 1, name: 'Adventure Travel', type: 'Travel Agency', dateAdded: '2023-01-15' },
      { id: 2, name: 'Business Flights Inc', type: 'Airline', dateAdded: '2023-03-22' },
      { id: 3, name: 'Luxury Stays', type: 'Hotel Chain', dateAdded: '2023-02-10' },
    ];
    setCompanies(mockCompanies);
  }, []);

  const filteredCompanies = companies.filter(company =>
    company.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    company.type.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const sortedCompanies = [...filteredCompanies].sort((a, b) => {
    if (sortBy === 'name') return a.name.localeCompare(b.name);
    if (sortBy === 'date') return new Date(b.dateAdded) - new Date(a.dateAdded);
    if (sortBy === 'type') return a.type.localeCompare(b.type);
    return 0;
  });

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.searchInput}
        placeholder="Search companies..."
        value={searchQuery}
        onChangeText={setSearchQuery}
      />
      
      <View style={styles.sortButtons}>
        <TouchableOpacity onPress={() => setSortBy('name')} style={styles.sortButton}>
          <Text>Sort by Name</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => setSortBy('date')} style={styles.sortButton}>
          <Text>Sort by Date</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => setSortBy('type')} style={styles.sortButton}>
          <Text>Sort by Type</Text>
        </TouchableOpacity>
      </View>
      
      <FlatList
        data={sortedCompanies}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.companyItem}>
            <Text style={styles.companyName}>{item.name}</Text>
            <Text style={styles.companyType}>{item.type}</Text>
            <Text style={styles.companyDate}>Added: {item.dateAdded}</Text>
          </View>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  searchInput: {
    height: 40,
    borderColor: 'gray',
    borderWidth: 1,
    padding: 10,
    marginBottom: 16,
    borderRadius: 8,
  },
  sortButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  sortButton: {
    padding: 10,
    backgroundColor: '#e0e0e0',
    borderRadius: 8,
  },
  companyItem: {
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  companyName: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  companyType: {
    color: '#666',
  },
  companyDate: {
    color: '#999',
    fontSize: 12,
  },
});

export default CatalogueScreen;