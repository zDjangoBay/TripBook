# TripBook Reservations Test Scripts

This directory contains organized test scripts for the TripBook reservations system.

## 📁 Directory Structure

```
app/src/test/scripts/reservations/
├── README.md                    # This file
├── hotel/                       # Hotel booking tests
│   └── run_hotel_dao_tests.sh   # Hotel DAO test runner
├── flight/                      # Flight booking tests (future)
├── restaurant/                  # Restaurant booking tests (future)
└── activity/                    # Activity booking tests (future)
```

## 🏨 Hotel Tests

### Hotel DAO Tests
**Location**: `hotel/run_hotel_dao_tests.sh`

**What it tests**:
- Hotel entity to domain model conversion
- Domain model to hotel entity conversion  
- Hotel amenities list functionality
- Room availability checking logic

**How to run**:
```bash
# From TripBook root directory
./app/src/test/scripts/reservations/hotel/run_hotel_dao_tests.sh
```

**Test Coverage**:
- ✅ `hotelEntity_toDomainModel_convertsCorrectly`
- ✅ `hotelEntity_fromDomainModel_convertsCorrectly`
- ✅ `hotelEntity_getAmenitiesList_worksCorrectly`
- ✅ `hotelEntity_hasAvailableRooms_worksCorrectly`

## 🚀 Quick Start

1. **Run all hotel tests**:
   ```bash
   ./app/src/test/scripts/reservations/hotel/run_hotel_dao_tests.sh
   ```

2. **View test results**: The script automatically opens the HTML test report in your browser

3. **Check test output**: Detailed console output shows pass/fail status for each test

## 📊 Test Results

The scripts generate comprehensive test reports including:
- Total number of tests run
- Pass/fail counts
- Execution time for each test
- Detailed error messages (if any)
- HTML reports with visual summaries

## 🔧 Technical Details

### Build System
- Uses **KSP (Kotlin Symbol Processing)** instead of KAPT for faster builds
- Room database with SQLite backend
- Kotlin coroutines for async operations

### Test Framework
- JUnit 4 for unit testing
- Robolectric for Android framework testing
- Mockito for mocking dependencies

### Performance
- Hotel DAO tests complete in ~0.033 seconds
- Full build with KSP: ~2-3 minutes (vs 18+ minutes with KAPT)

## 🐛 Troubleshooting

### Common Issues

1. **Build hanging at KSP phase**:
   - Solution: Already fixed by migrating from KAPT to KSP

2. **"Please run from TripBook root directory" error**:
   - Solution: Make sure you're in the project root, not in the scripts folder

3. **Permission denied**:
   - Solution: Make scripts executable with `chmod +x script_name.sh`

### Getting Help

If tests fail:
1. Check the console output for specific error messages
2. Review the HTML test report for detailed failure information
3. Ensure all dependencies are properly installed
4. Try cleaning the project: `./gradlew clean`

## 🎯 Future Enhancements

- [ ] Flight booking DAO tests
- [ ] Restaurant reservation DAO tests  
- [ ] Activity booking DAO tests
- [ ] Integration tests for complete booking flows
- [ ] Performance benchmarking tests
- [ ] UI automation tests

## 📝 Contributing

When adding new test scripts:
1. Follow the existing naming convention
2. Include colored output for better readability
3. Generate both console and HTML reports
4. Update this README with new test information
5. Ensure scripts are executable (`chmod +x`)

---

**Last Updated**: $(date +%Y-%m-%d)  
**Team**: TripBook Development Team  
**Status**: ✅ All hotel DAO tests passing
