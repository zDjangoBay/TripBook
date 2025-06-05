#!/bin/bash

# Hotel DAO Test Script for TripBook Reservations
# This script runs all Hotel DAO related tests and generates reports
# Author: TripBook Development Team
# Date: $(date +%Y-%m-%d)

echo "🏨 TripBook Hotel DAO Test Suite"
echo "================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "app/build.gradle.kts" ]; then
    print_error "Please run this script from the TripBook root directory"
    exit 1
fi

print_status "Starting Hotel DAO tests..."

# Clean previous test results
print_status "Cleaning previous test results..."
./gradlew clean > /dev/null 2>&1

# Run Hotel DAO specific tests
print_status "Running Hotel DAO tests..."
echo ""

# Run the tests with detailed output
./gradlew testDebugUnitTest --tests "*HotelDaoTest*" --no-daemon

# Check if tests passed
if [ $? -eq 0 ]; then
    print_success "All Hotel DAO tests passed! ✅"
    echo ""
    
    # Display test results summary
    if [ -f "app/build/test-results/testDebugUnitTest/TEST-com.android.tripbook.data.database.dao.HotelDaoTest.xml" ]; then
        print_status "Test Results Summary:"
        echo "====================="
        
        # Extract test information from XML
        TESTS=$(grep -o 'tests="[0-9]*"' app/build/test-results/testDebugUnitTest/TEST-com.android.tripbook.data.database.dao.HotelDaoTest.xml | cut -d'"' -f2)
        FAILURES=$(grep -o 'failures="[0-9]*"' app/build/test-results/testDebugUnitTest/TEST-com.android.tripbook.data.database.dao.HotelDaoTest.xml | cut -d'"' -f2)
        ERRORS=$(grep -o 'errors="[0-9]*"' app/build/test-results/testDebugUnitTest/TEST-com.android.tripbook.data.database.dao.HotelDaoTest.xml | cut -d'"' -f2)
        TIME=$(grep -o 'time="[0-9.]*"' app/build/test-results/testDebugUnitTest/TEST-com.android.tripbook.data.database.dao.HotelDaoTest.xml | head -1 | cut -d'"' -f2)
        
        echo "📊 Total Tests: $TESTS"
        echo "✅ Passed: $((TESTS - FAILURES - ERRORS))"
        echo "❌ Failed: $FAILURES"
        echo "⚠️  Errors: $ERRORS"
        echo "⏱️  Execution Time: ${TIME}s"
        echo ""
        
        # List individual test cases
        print_status "Individual Test Cases:"
        echo "======================"
        grep 'testcase name=' app/build/test-results/testDebugUnitTest/TEST-com.android.tripbook.data.database.dao.HotelDaoTest.xml | while read line; do
            TEST_NAME=$(echo $line | grep -o 'name="[^"]*"' | cut -d'"' -f2)
            TEST_TIME=$(echo $line | grep -o 'time="[^"]*"' | cut -d'"' -f2)
            echo "  ✅ $TEST_NAME (${TEST_TIME}s)"
        done
        echo ""
    fi
    
    # Open test report if available
    if [ -f "app/build/reports/tests/testDebugUnitTest/index.html" ]; then
        print_status "Test report generated at: app/build/reports/tests/testDebugUnitTest/index.html"
        print_status "Opening test report in browser..."
        
        # Try to open in browser (works on most Linux systems)
        if command -v xdg-open > /dev/null; then
            xdg-open "app/build/reports/tests/testDebugUnitTest/index.html" 2>/dev/null &
        elif command -v firefox > /dev/null; then
            firefox "app/build/reports/tests/testDebugUnitTest/index.html" 2>/dev/null &
        elif command -v google-chrome > /dev/null; then
            google-chrome "app/build/reports/tests/testDebugUnitTest/index.html" 2>/dev/null &
        else
            print_warning "Could not open browser automatically. Please open the report manually."
        fi
    fi
    
else
    print_error "Hotel DAO tests failed! ❌"
    echo ""
    print_status "Check the output above for details on what went wrong."
    exit 1
fi

echo ""
print_success "Hotel DAO test execution completed!"
echo "🎉 All systems go for hotel booking functionality!"
