"use client"

import React from "react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

interface SeatSelectionProps {
  onSeatSelect: (seatNumber: string) => void
  selectedSeat: string
}

export default function SeatSelection({ onSeatSelect, selectedSeat }: SeatSelectionProps) {
  // Seats that are already booked
  const bookedSeats = ["A1", "A4", "B2", "C3", "D1", "D4"]

  const rows = ["A", "B", "C", "D"]
  const columns = [1, 2, 3, 4]

  return (
    <div className="space-y-6">
      <div className="flex justify-center mb-6">
        <div className="w-20 h-10 bg-slate-700 rounded-t-xl flex items-center justify-center text-white text-sm">
          Driver
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 max-w-xs mx-auto">
        {rows.map((row) => (
          <React.Fragment key={row}>
            {columns.map((col) => {
              const seatNumber = `${row}${col}`
              const isBooked = bookedSeats.includes(seatNumber)
              const isSelected = selectedSeat === seatNumber

              return (
                <Button
                  key={seatNumber}
                  variant={isSelected ? "default" : "outline"}
                  size="sm"
                  className={cn(
                    "h-12 w-12 rounded-md",
                    isBooked && "bg-slate-200 text-slate-400 hover:bg-slate-200 cursor-not-allowed",
                    isSelected && "bg-primary text-primary-foreground",
                  )}
                  disabled={isBooked}
                  onClick={() => onSeatSelect(seatNumber)}
                >
                  {seatNumber}
                </Button>
              )
            })}
          </React.Fragment>
        ))}
      </div>

      <div className="flex justify-center gap-6 text-sm">
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded-sm bg-primary"></div>
          <span>Selected</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded-sm border border-input"></div>
          <span>Available</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 rounded-sm bg-slate-200"></div>
          <span>Booked</span>
        </div>
      </div>
    </div>
  )
}
