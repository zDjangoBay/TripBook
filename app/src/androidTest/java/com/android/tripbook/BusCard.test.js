// src/components/__tests__/BusCard.test.js
import React from 'react';
import { render } from '@testing-library/react-native';
import BusCard from '../BusCard';

test('renders bus operator and price', () => {
  const bus = {
    id: '1',
    operator: 'Guarantee Express',
    price: 'XAF 5,000',
  };
  const { getByText } = render(<BusCard bus={bus} />);
  expect(getByText('Guarantee Express')).toBeTruthy();
  expect(getByText('XAF 5,000')).toBeTruthy();
});