# Orwel - Government Policy Tracker

A comprehensive JavaFX application for tracking government policies, stances, and regulations across countries worldwide. Built for MIT Hackathon 2026.

## Features

- **Country Policy Tracking**: View comprehensive information about each country's policies, stances, and international agreements
- **Personalized Warnings**: Receive alerts about potential issues based on your profile (stocks, travel, tax, etc.)
- **Personalized News**: Get news articles tailored to your interests, location, and profile
- **General News**: Stay updated with news from around the world, filtered by region, country, or topics
- **Location-Based Services**: Automatic location detection and geocoding for location-relevant information
- **User Profile Management**: Comprehensive profile with location, interests, and preferences

## Technology Stack

- **Frontend**: JavaFX 21
- **Backend**: REST API (to be implemented by backend team)
- **Location Services**: Google Maps API / OpenStreetMap Nominatim
- **Build Tool**: Maven
- **HTTP Client**: OkHttp
- **JSON Processing**: Gson

## Project Structure

```
orwel-frontend/
├── src/
│   ├── main/
│   │   ├── java/com/orwel/
│   │   │   ├── Main.java                    # Application entry point
│   │   │   ├── config/                      # Configuration
│   │   │   ├── controller/                  # FXML controllers
│   │   │   ├── model/                       # Data models/DTOs
│   │   │   ├── service/                     # API and location services
│   │   │   └── util/                        # Utility classes
│   │   └── resources/
│   │       └── fxml/                         # FXML UI files
│   └── test/                                 # Test files
├── pom.xml                                   # Maven configuration
├── config.properties                         # Application configuration (created on first run)
├── API_ENDPOINTS.md                          # REST API endpoint documentation
└── README.md                                 # This file
```

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Internet connection (for API calls and location services)

### Configuration

1. **Clone the repository** (if applicable)

2. **Configure API Settings**

   Create or edit `config.properties` in the project root:
   ```properties
   api.base.url=http://localhost:8080/api
   location.api.key=YOUR_GOOGLE_MAPS_API_KEY
   location.api.provider=google
   news.api.key=YOUR_NEWS_API_KEY
   ```

   **Note**: 
   - For Google Maps API: Get your API key from [Google Cloud Console](https://console.cloud.google.com/)
   - For OpenStreetMap (free, no key required): Set `location.api.provider=openstreetmap`
   - The application will create a default `config.properties` file on first run

3. **Build the project**
   ```bash
   mvn clean compile
   ```

4. **Run the application**
   ```bash
   mvn javafx:run
   ```
   
   Or run the Main class directly from your IDE.

## API Endpoints

See [API_ENDPOINTS.md](API_ENDPOINTS.md) for complete REST API endpoint documentation.

The backend team needs to implement the following endpoints:

- **Authentication**: `/api/auth/login`, `/api/auth/register`
- **User**: `/api/users/me` (GET, PUT)
- **Countries**: `/api/countries`, `/api/countries/{code}`, `/api/countries/{code}/warnings`
- **News**: `/api/news/personalized`, `/api/news/general`, `/api/news/country/{code}`, `/api/news/region/{region}`, `/api/news/search`

## Usage

1. **Registration/Login**: Create an account or login with existing credentials
2. **Complete Profile**: Fill in your profile including location, interests, and preferences
3. **Explore Countries**: Browse countries and view their policies and stances
4. **View Warnings**: Check personalized warnings for countries you're interested in
5. **Read News**: Browse personalized or general news, filtered by region or country
6. **Update Profile**: Keep your profile updated for better personalization

## Location API Configuration

### Google Maps API (Recommended)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable "Geocoding API"
4. Create credentials (API Key)
5. Add the API key to `config.properties`

### OpenStreetMap Nominatim (Free Alternative)

- No API key required
- Set `location.api.provider=openstreetmap` in `config.properties`
- Note: Has rate limiting (1 request per second recommended)

## Development

### Running Tests
```bash
mvn test
```

### Building JAR
```bash
mvn clean package
```

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic

## Troubleshooting

### Application won't start
- Check Java version: `java -version` (should be 17+)
- Verify Maven installation: `mvn -version`
- Check `config.properties` exists and has correct values

### API Connection Errors
- Verify backend server is running
- Check `api.base.url` in `config.properties`
- Ensure network connectivity

### Location API Errors
- Verify API key is correct (for Google Maps)
- Check API quotas/limits
- Try switching to OpenStreetMap provider

## Contributing

This is a hackathon project. For contributions:
1. Follow the existing code structure
2. Maintain consistent styling
3. Update documentation as needed
4. Test your changes thoroughly

## License

See LICENSE file for details.

## Team

Built for MIT Hackathon 2026.

## Support

For issues or questions, contact the development team.
