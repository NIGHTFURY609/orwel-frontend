# Orwel - Investment & Stock Market Intelligence

A comprehensive JavaFX application for tracking stocks, analyzing companies, and gaining market intelligence across global financial markets. Built for MIT Hackathon 2026.

## Features

- **Stock & Company Analysis**: View comprehensive information about companies with detailed market strategies, financial positions, and investment opportunities
- **Risk Alerts**: Receive personalized alerts about potential market risks, regulatory changes, and investment impacts
- **Curated Market News**: Get financial news articles tailored to your investment interests and portfolio
- **Global Market News**: Stay updated with market news from around the world, filtered by sector, company, or financial topics
- **Location-Based Services**: Automatic location detection for market-relevant information and localized insights
- **Investor Profile Management**: Comprehensive profile with investment preferences, portfolio details, and interests

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
- **Companies**: `/api/companies`, `/api/companies/{ticker}`, `/api/companies/{ticker}/alerts`
- **News**: `/api/news/personalized`, `/api/news/market-wide`, `/api/news/company/{ticker}`, `/api/news/sector/{sector}`, `/api/news/search`

## Usage

1. **Registration/Login**: Create an account or login with existing credentials
2. **Complete Investor Profile**: Fill in your investment profile including portfolio details, interests, and risk preferences
3. **Explore Companies**: Browse companies and view their market strategies and financial positions
4. **View Risk Alerts**: Check personalized risk alerts for companies in your portfolio
5. **Read Market News**: Browse curated or market-wide news, filtered by sector or company
6. **Update Profile**: Keep your investor profile updated for better personalized insights

   
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
