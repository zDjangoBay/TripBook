// src/screens/TicketListScreen.js
import React, { useEffect, useState } from 'react';
import { 
  View, 
  Text, 
  FlatList, 
  StyleSheet, 
  NativeModules, 
  TouchableOpacity,
  Alert 
} from 'react-native';
import BusCard from '../components/BusCard';

const { TripCatalogActivity } = NativeModules;

const TicketListScreen = ({ navigation }) => {
  const [buses, setBuses] = useState([
    {
      id: '1',
      departure: '06:00 AM',
      arrival: '11:30 AM',
      price: 'XAF 5,000',
      duration: '5h 30m',
      operator: 'Guarantee Express',
    },
    // Add more buses...
  ]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    TripCatalogActivity.getBusSchedules()
      .then((data) => {
        setBuses(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <Text style={styles.loading}>Loading buses...</Text>;
  if (error) return <Text style={styles.error}>Error: {error}</Text>;

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Available Buses (Yaounde → Buea)</Text>
      <TouchableOpacity 
        style={styles.mapButton}
        onPress={() => navigation.navigate('Map')}
      >
        <Text style={styles.mapButtonText}>View Route Map</Text>
      </TouchableOpacity>
      <FlatList
        data={buses}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => <BusCard bus={item} />}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#e8f5e9',
    padding: 15,
  },
  header: {
    fontSize: 20,
    color: '#263238',
    fontWeight: 'bold',
    marginBottom: 15,
  },
  mapButton: {
    backgroundColor: '#00796b',
    padding: 10,
    borderRadius: 5,
    marginBottom: 15,
    alignSelf: 'flex-start',
  },
  mapButtonText: {
    color: 'white',
    fontWeight: 'bold',
  },
  loading: {
    textAlign: 'center',
    marginTop: 20,
    color: '#00796b',
  },
  error: {
    textAlign: 'center',
    marginTop: 20,
    color: '#d32f2f',
  },
});

export default TicketListScreen;