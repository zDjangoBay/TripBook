// src/components/BusCard.js
import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Alert, NativeModules } from 'react-native';

const { TripCatalogActivity } = NativeModules;

const BusCard = ({ bus }) => {
  const handleBook = () => {
    Alert.alert(
      'Confirm Booking',
      `Book ${bus.operator} for ${bus.price}?`,
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'Confirm',
          onPress: () => {
            TripCatalogActivity.bookBusTicket(bus.id)
              .then((success) => {
                Alert.alert(
                  success ? 'Booking Confirmed!' : 'Sold Out',
                  success 
                    ? `Your ticket for ${bus.operator} is confirmed`
                    : 'Please select another bus',
                  [{ text: 'OK' }]
                );
              })
              .catch((error) => {
                Alert.alert('Error', 'Failed to book ticket. Please try again.');
              });
          },
        },
      ]
    );
  };

  return (
    <View style={styles.card}>
      <Text style={styles.operator}>{bus.operator}</Text>
      <View style={styles.timeContainer}>
        <Text style={styles.time}>{bus.departure}</Text>
        <Text style={styles.duration}>⏳ {bus.duration}</Text>
        <Text style={styles.time}>{bus.arrival}</Text>
      </View>
      <Text style={styles.price}>{bus.price}</Text>
      <TouchableOpacity style={styles.bookButton} onPress={handleBook}>
        <Text style={styles.bookButtonText}>Book Now</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#ffffff',
    padding: 15,
    borderRadius: 8,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: '#b0bec5',
  },
  operator: {
    fontSize: 16,
    color: '#00796b',
    fontWeight: 'bold',
    marginBottom: 10,
  },
  timeContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  time: {
    fontSize: 14,
    color: '#263238',
  },
  duration: {
    fontSize: 14,
    color: '#78909c',
  },
  price: {
    fontSize: 18,
    color: '#00796b',
    fontWeight: 'bold',
    marginBottom: 10,
  },
  bookButton: {
    backgroundColor: '#00796b',
    padding: 10,
    borderRadius: 5,
    alignItems: 'center',
  },
  bookButtonText: {
    color: '#ffffff',
    fontWeight: 'bold',
  },
});

export default BusCard;