# TripBook Supabase Setup Guide

This guide will help you set up Supabase backend for the TripBook Android application to enable persistent data storage.

## Prerequisites

- Android Studio with the TripBook project
- A Supabase account (free tier available)
- Internet connection for testing

## Step 1: Create Supabase Project

1. **Sign up/Login to Supabase**
   - Go to [https://supabase.com](https://supabase.com)
   - Create an account or login to existing account

2. **Create New Project**
   - Click "New Project"
   - Choose your organization
   - Enter project name: `tripbook-android`
   - Enter database password (save this securely)
   - Select region closest to your users
   - Click "Create new project"

3. **Wait for Setup**
   - Project creation takes 1-2 minutes
   - You'll be redirected to the project dashboard

## Step 2: Configure Database Schema

1. **Open SQL Editor**
   - In your Supabase dashboard, go to "SQL Editor"
   - Click "New query"

2. **Execute Schema Script**
   - Copy the entire content from `SUPABASE_SCHEMA.sql` file
   - Paste it into the SQL editor
   - Click "Run" to execute the script
   - Verify tables are created in the "Table Editor"

3. **Verify Tables Created**
   - Go to "Table Editor" in the sidebar
   - You should see two tables:
     - `trips` - Main trips table
     - `travel_companions` - Companions linked to trips

## Step 3: Get Project Credentials

1. **Find Project URL**
   - Go to "Settings" → "API"
   - Copy the "Project URL" (looks like: `https://your-project-id.supabase.co`)

2. **Get Anonymous Key**
   - In the same API settings page
   - Copy the "anon public" key (starts with `eyJ...`)

3. **Update Android App**
   - Open `app/src/main/java/com/android/tripbook/data/SupabaseConfig.kt`
   - Replace the placeholder values:

```kotlin
private const val SUPABASE_URL = "https://your-project-id.supabase.co"
private const val SUPABASE_ANON_KEY = "your-anon-key-here"
```

## Step 4: Test the Integration

1. **Build and Run App**
   - Sync project in Android Studio
   - Build and run the app on device/emulator

2. **Test Trip Creation**
   - Create a new trip through the app
   - Check if it appears in the trip list
   - Verify data in Supabase dashboard

3. **Test Data Persistence**
   - Close and restart the app
   - Verify trips are still visible
   - Check Supabase "Table Editor" to see stored data

## Step 5: Verify Database Content

1. **Check Trips Table**
   - Go to Supabase "Table Editor"
   - Select "trips" table
   - Verify your created trips appear here

2. **Check Companions Table**
   - Select "travel_companions" table
   - Verify companions are linked to correct trip IDs

## Troubleshooting

### Common Issues

1. **Build Errors**
   - Ensure all dependencies are synced
   - Check internet connection
   - Verify Kotlin serialization plugin is applied

2. **Connection Errors**
   - Verify SUPABASE_URL and SUPABASE_ANON_KEY are correct
   - Check device/emulator has internet access
   - Ensure Supabase project is active

3. **Data Not Appearing**
   - Check Android Studio Logcat for error messages
   - Verify database schema was created correctly
   - Test with simple trip creation first

4. **Authentication Errors**
   - Ensure Row Level Security policies are set correctly
   - Check if anon key has proper permissions

### Debug Steps

1. **Check Logs**
   ```
   adb logcat | grep "SupabaseTripRepository"
   ```

2. **Verify Network Requests**
   - Use Android Studio Network Inspector
   - Check if requests are reaching Supabase

3. **Test Database Directly**
   - Use Supabase SQL Editor to query data
   - Verify tables have correct structure

## Security Considerations

### Current Setup (Development)
- Uses anonymous access for simplicity
- Suitable for development and testing
- All users can read/write all data

### Production Recommendations
1. **Implement Authentication**
   - Add user authentication with Supabase Auth
   - Restrict data access to authenticated users

2. **Update RLS Policies**
   - Modify Row Level Security policies
   - Ensure users can only access their own trips

3. **Environment Variables**
   - Move credentials to environment variables
   - Use different projects for dev/staging/production

## Advanced Features (Optional)

### Real-time Updates
- Enable real-time subscriptions for live data sync
- Multiple devices can see updates instantly

### File Storage
- Use Supabase Storage for trip photos
- Implement image upload/download functionality

### Offline Support
- Implement local caching with Room database
- Sync with Supabase when online

## Support

### Resources
- [Supabase Documentation](https://supabase.com/docs)
- [Supabase Android Guide](https://supabase.com/docs/reference/kotlin/introduction)
- [TripBook GitHub Repository](your-repo-link)

### Getting Help
1. Check Supabase dashboard logs
2. Review Android Studio Logcat
3. Consult Supabase community forums
4. Check project documentation

## Next Steps

After successful setup:
1. Test all CRUD operations (Create, Read, Update, Delete)
2. Verify data persistence across app restarts
3. Test error handling with network issues
4. Consider implementing user authentication
5. Plan for production deployment

---

**Note**: Keep your Supabase credentials secure and never commit them to public repositories. Consider using environment variables or Android's BuildConfig for production apps.
