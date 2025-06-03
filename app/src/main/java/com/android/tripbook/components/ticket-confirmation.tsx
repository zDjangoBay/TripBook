"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { CalendarIcon, Bus, Check } from "lucide-react"

interface TicketConfirmationProps {
  formData: {
    from: string
    to: string
    date: string
    time: string
    passengers: string
    selectedSeat: string
  }
  onBack: () => void
}

export default function TicketConfirmation({ formData, onBack }: TicketConfirmationProps) {
  const [paymentMethod, setPaymentMethod] = useState("card")
  const [isProcessing, setIsProcessing] = useState(false)
  const [isConfirmed, setIsConfirmed] = useState(false)

  const handlePayment = () => {
    setIsProcessing(true)

    // Simulate payment processing
    setTimeout(() => {
      setIsProcessing(false)
      setIsConfirmed(true)
    }, 2000)
  }

  const formatLocation = (location: string) => {
    switch (location) {
      case "new-york":
        return "New York"
      case "boston":
        return "Boston"
      case "washington":
        return "Washington DC"
      case "philadelphia":
        return "Philadelphia"
      default:
        return location
    }
  }

  if (isConfirmed) {
    return (
      <Card>
        <CardHeader className="text-center">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-green-100">
            <Check className="h-6 w-6 text-green-600" />
          </div>
          <CardTitle>Booking Confirmed!</CardTitle>
          <CardDescription>Your ticket has been booked successfully</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="rounded-lg border p-4 mb-4">
            <div className="flex justify-between items-center mb-4">
              <div className="font-semibold text-lg">Ticket #BG{Math.floor(Math.random() * 10000)}</div>
              <Bus className="h-5 w-5 text-primary" />
            </div>

            <div className="space-y-4">
              <div className="flex items-center gap-3">
                <div className="flex flex-col items-center">
                  <div className="w-3 h-3 rounded-full bg-primary"></div>
                  <div className="w-0.5 h-10 bg-slate-200"></div>
                  <div className="w-3 h-3 rounded-full bg-slate-400"></div>
                </div>
                <div className="flex-1">
                  <div className="flex justify-between">
                    <div>
                      <div className="font-medium">{formatLocation(formData.from)}</div>
                      <div className="text-sm text-muted-foreground">{formData.time}</div>
                    </div>
                    <div className="text-right">
                      <div className="text-sm text-muted-foreground">From</div>
                      <div className="font-medium">Terminal A</div>
                    </div>
                  </div>
                  <div className="my-2 text-xs text-muted-foreground">2h 30m</div>
                  <div className="flex justify-between">
                    <div>
                      <div className="font-medium">{formatLocation(formData.to)}</div>
                      <div className="text-sm text-muted-foreground">
                        {formData.time.split(":")[0] > "12"
                          ? `${Number.parseInt(formData.time.split(":")[0]) + 2}:${formData.time.split(":")[1]} PM`
                          : `${Number.parseInt(formData.time.split(":")[0]) + 2}:${formData.time.split(":")[1]} AM`}
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-sm text-muted-foreground">To</div>
                      <div className="font-medium">Terminal B</div>
                    </div>
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-2 pt-2 border-t">
                <div>
                  <div className="text-xs text-muted-foreground">Date</div>
                  <div className="font-medium">{formData.date}</div>
                </div>
                <div>
                  <div className="text-xs text-muted-foreground">Seat</div>
                  <div className="font-medium">{formData.selectedSeat}</div>
                </div>
                <div>
                  <div className="text-xs text-muted-foreground">Passenger</div>
                  <div className="font-medium">{formData.passengers}</div>
                </div>
              </div>
            </div>
          </div>

          <div className="flex justify-between text-sm mb-1">
            <span>Ticket Price</span>
            <span>$45.00</span>
          </div>
          <div className="flex justify-between text-sm mb-1">
            <span>Service Fee</span>
            <span>$2.50</span>
          </div>
          <div className="flex justify-between font-medium pt-2 border-t">
            <span>Total</span>
            <span>$47.50</span>
          </div>
        </CardContent>
        <CardFooter className="flex-col gap-2">
          <Button className="w-full">Download Ticket</Button>
          <Button variant="outline" className="w-full">
            Back to Home
          </Button>
        </CardFooter>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Payment & Confirmation</CardTitle>
        <CardDescription>Complete your booking by making payment</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="p-3 bg-slate-100 rounded-lg mb-2">
          <div className="flex justify-between items-center mb-2">
            <div className="font-medium">
              {formatLocation(formData.from)} to {formatLocation(formData.to)}
            </div>
            <Bus className="h-5 w-5 text-primary" />
          </div>
          <div className="text-sm text-muted-foreground flex items-center gap-1 mb-2">
            <CalendarIcon className="h-3 w-3" /> {formData.date} • {formData.time}
          </div>
          <div className="flex justify-between text-sm">
            <span>Seat {formData.selectedSeat}</span>
            <span>{formData.passengers} Passenger</span>
          </div>
        </div>

        <div className="space-y-2">
          <Label>Passenger Details</Label>
          <div className="grid gap-2">
            <Input placeholder="Full Name" />
            <Input placeholder="Email" type="email" />
            <Input placeholder="Phone Number" type="tel" />
          </div>
        </div>

        <div className="space-y-2">
          <Label>Payment Method</Label>
          <Tabs value={paymentMethod} onValueChange={setPaymentMethod}>
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="card">Credit Card</TabsTrigger>
              <TabsTrigger value="paypal">PayPal</TabsTrigger>
            </TabsList>
            <TabsContent value="card" className="space-y-2 pt-2">
              <Input placeholder="Card Number" />
              <div className="grid grid-cols-2 gap-2">
                <Input placeholder="MM/YY" />
                <Input placeholder="CVC" />
              </div>
              <Input placeholder="Cardholder Name" />
            </TabsContent>
            <TabsContent value="paypal" className="pt-2">
              <div className="text-center p-4 border rounded-md">
                <p className="mb-2">You'll be redirected to PayPal to complete payment</p>
                <Button variant="outline" className="w-full">
                  Continue with PayPal
                </Button>
              </div>
            </TabsContent>
          </Tabs>
        </div>

        <div className="space-y-1 pt-2">
          <div className="flex justify-between text-sm">
            <span>Ticket Price</span>
            <span>$45.00</span>
          </div>
          <div className="flex justify-between text-sm">
            <span>Service Fee</span>
            <span>$2.50</span>
          </div>
          <div className="flex justify-between font-medium pt-2 border-t">
            <span>Total</span>
            <span>$47.50</span>
          </div>
        </div>
      </CardContent>
      <CardFooter className="flex-col gap-2">
        <Button className="w-full" onClick={handlePayment} disabled={isProcessing}>
          {isProcessing ? "Processing..." : "Pay & Confirm Booking"}
        </Button>
        <Button variant="outline" className="w-full" onClick={onBack} disabled={isProcessing}>
          Back
        </Button>
      </CardFooter>
    </Card>
  )
}
