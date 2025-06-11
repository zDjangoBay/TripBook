import React from "react";

const sampleReservations = [
  {
    destination: "Nairobi",
    timeOfKickoff: "2025-05-10T08:00:00",
    busName: "Express Bus",
    seatsAvailable: 5,
  },
  {
    destination: "Kampala",
    timeOfKickoff: "2025-05-10T10:30:00",
    busName: "Speedliner",
    seatsAvailable: 2,
  },
  {
    destination: "Dar es Salaam",
    timeOfKickoff: "2025-05-11T06:00:00",
    busName: "Coastal Express",
    seatsAvailable: 15,
  },
];

// Reservation Card Component
const ReservationCard = ({ reservation }) => {
  return (
    <div style={styles.card}>
      <h3>Destination: {reservation.destination}</h3>
      <p>Time of Kickoff: {new Date(reservation.timeOfKickoff).toLocaleString()}</p>
      <p>Bus Name: {reservation.busName}</p>
      <p>Seats Available: {reservation.seatsAvailable}</p>
    </div>
  );
};

// Reservations Screen Component
const Reservations = () => {
  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <h1>Bus Reservations</h1>
      </header>
      <main>
        {sampleReservations.map((reservation, index) => (
          <ReservationCard key={index} reservation={reservation} />
        ))}
      </main>
    </div>
  );
};

// Styles
const styles = {
  container: {
    fontFamily: "Arial, sans-serif",
    padding: "20px",
  },
  header: {
    textAlign: "center",
    marginBottom: "20px",
  },
  card: {
    border: "1px solid #ccc",
    borderRadius: "8px",
    padding: "16px",
    marginBottom: "16px",
    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
  },
};

export default Reservations;