// src/screens/MapScreen.js
import React from 'react';
import { View, StyleSheet } from 'react-native';
import MapView, { Marker } from 'react-native-maps';

const MapScreen = () => {
  return (
    <View style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={{
          latitude: 3.8480,  // Yaoundé coordinates
          longitude: 11.5021,
          latitudeDelta: 1.5,
          longitudeDelta: 1.5,
        }}>
        <Marker
          coordinate={{ latitude: 3.8480, longitude: 11.5021 }}
          title="Yaoundé"
          pinColor="#00796b"
        />
        <Marker
          coordinate={{ latitude: 4.1586, longitude: 9.2417 }}
          title="Buea"
          pinColor="#00796b"
        />
      </MapView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#e8f5e9',
  },
  map: {
    flex: 1,
  },
});

export default MapScreen;