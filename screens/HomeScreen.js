
import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

const HomeScreen = ({ navigation }) => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>TripBook</Text>
      <View style={styles.routeBox}>
        <Text style={styles.routeText}>Yaounde → Buea</Text>
      </View>
      <TouchableOpacity 
        style={styles.button}
        onPress={() => navigation.navigate('TicketList')}
      >
        <Text style={styles.buttonText}>Find Buses</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#e8f5e9', // Light gray-green
    padding: 20,
    justifyContent: 'center',
  },
  title: {
    fontSize: 24,
    color: '#263238', // Dark gray
    fontWeight: 'bold',
    marginBottom: 30,
    textAlign: 'center',
  },
  routeBox: {
    backgroundColor: '#ffffff',
    padding: 15,
    borderRadius: 8,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#b0bec5',
  },
  routeText: {
    fontSize: 18,
    color: '#00796b', // Dark teal
    textAlign: 'center',
  },
  button: {
    backgroundColor: '#00796b', // Dark teal
    padding: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default HomeScreen;