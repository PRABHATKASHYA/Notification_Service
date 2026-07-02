# Notification Service

A Spring Boot microservice that handles email notifications and consumes payment success events from Apache Kafka. The service provides REST APIs for sending emails and automatically sends payment confirmation emails when payment events are received.

## Features

- **Email Notifications**: Send emails via REST API with validation
- **Kafka Integration**: Consumes payment success events from Kafka topics
- **Automatic Payment Emails**: Sends payment confirmation emails when payment events are received
- **Dead Letter Topic (DLT)**: Failed Kafka messages are sent to DLT for later processing
- **Retry Mechanism**: Configurable retry logic with exponential backoff
- **Multi-Environment Support**: Separate configurations for dev, idev, and prod environments
- **Exception Handling**: Global exception handling for email failures
- **Docker Support**: Containerized deployment with multi-stage builds
- **CI/CD Pipeline**: Jenkins pipeline for automated builds and deployments

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.5.15
- **Spring Mail**: For email functionality
- **Spring Kafka**: For Kafka integration
- **Lombok**: For reducing boilerplate code
- **Maven**: Build and dependency management
- **Docker**: Containerization
- **Jenkins**: CI/CD

## Architecture

```
┌─────────────────┐
│   Payment       │
│   Service       │
└────────┬────────┘
         │ Kafka Event
         ↓
┌─────────────────┐
│   Kafka Topic   │
│ payment-success │
└────────┬────────┘
         │
         ↓
┌─────────────────────────────┐
│   Notification Service       │
│  ┌───────────────────────┐  │
│  │ PaymentKafkaConsumer  │  │
│  │   - Consumes events   │  │
│  │   - Sends emails      │  │
│  └───────────────────────┘  │
│  ┌───────────────────────┐  │
│  │   EmailController     │  │
│  │   - REST API endpoint │  │
│  └───────────────────────┘  │
│  ┌───────────────────────┐  │
│  │   EmailService        │  │
│  │   - Sends emails      │  │
│  └───────────────────────┘  │
└─────────────────────────────┘
         │
         ↓
┌─────────────────┐
│   SMTP Server   │
│   (Gmail/Custom)│
└─────────────────┘
```

## Project Structure

```
notification-service/
├── src/
│   ├── main/
│   │   ├── java/com/notification_service/
│   │   │   ├── config/
│   │   │   │   └── KafkaErrorHandlerConfig.java
│   │   │   ├── controller/
│   │   │   │   └── EmailController.java
│   │   │   ├── dto/
│   │   │   │   ├── EmailRequest.java
│   │   │   │   └── PaymentSuccessKafkaEvent.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── kafka/
│   │   │   │   ├── PaymentKafkaConsumer.java
│   │   │   │   └── PaymentDltConsumer.java
│   │   │   ├── service/
│   │   │   │   └── EmailService.java
│   │   │   └── NotificationServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-idev.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/com/notification_service/
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── sonar-project.properties
```

## API Endpoints

### Send Email

**Endpoint**: `POST /api/notifications/email`

**Request Body**:
```json
{
  "to": "recipient@example.com",
  "subject": "Email Subject",
  "message": "Email message content"
}
```

**Response**:
- **Status**: 200 OK
- **Body**: `"Email sent successfully"`

**Validation Rules**:
- `to`: Must be a valid email address and not blank
- `subject`: Must not be blank
- `message`: Must not be blank

**Error Response** (500 Internal Server Error):
```json
{
  "message": "Email could not be sent. Please try again later."
}
```

## Kafka Topics

### payment-success-topic
- **Purpose**: Receives payment success events from payment service
- **Consumer Group**: `notification-service-group`
- **Event Format**:
```json
{
  "transactionId": "TXN123456",
  "amount": 100.50,
  "paymentType": "CREDIT_CARD",
  "email": "customer@example.com"
}
```

### payment-success-topic-dlt
- **Purpose**: Dead Letter Topic for failed payment events
- **Consumer Group**: `notification-dlt-group`
- **Retry Configuration**: 2 retries with 5-second fixed backoff

## Configuration

### Environment Variables

#### Mail Configuration
- `MAIL_USERNAME`: SMTP username (e.g., Gmail email)
- `MAIL_PASSWORD`: SMTP password or app-specific password
- `MAIL_HOST`: SMTP server host (prod only)
- `MAIL_PORT`: SMTP server port (prod only)

#### Kafka Configuration
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers (e.g., `localhost:9092`)

#### Spring Configuration
- `SPRING_PROFILES_ACTIVE`: Active profile (`dev`, `idev`, or `prod`)

### Application Profiles

#### Dev Profile
- Port: 8084
- SMTP: Gmail (smtp.gmail.com:587)
- Kafka: Configured via environment variable

#### IDev Profile
- Port: 8084
- SMTP: Gmail (smtp.gmail.com:587)
- Kafka: Configured via environment variable

#### Prod Profile
- Port: 8084
- SMTP: Configured via environment variables
- Kafka: Configured via environment variables

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Access to SMTP server (Gmail or custom)
- Apache Kafka (for event consumption)

### Local Development

1. **Clone the repository**
```bash
git clone <repository-url>
cd notification-service
```

2. **Configure environment variables**
```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

3. **Run the application**
```bash
./mvnw spring-boot:run
```

Or with specific profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=idev
```

### Using Maven Wrapper
```bash
# Build the project
./mvnw clean install

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run
```

## Docker Deployment

### Build Docker Image
```bash
docker build -t notification-service:latest .
```

### Build with specific profile
```bash
docker build --build-arg SPRING_PROFILES_ACTIVE=prod -t notification-service:prod .
```

### Run Docker Container
```bash
docker run -d \
  -p 8084:8084 \
  -e MAIL_USERNAME=your-email@gmail.com \
  -e MAIL_PASSWORD=your-app-password \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e SPRING_PROFILES_ACTIVE=prod \
  notification-service:latest
```

### Docker Compose Example
```yaml
version: '3.8'
services:
  notification-service:
    build: .
    ports:
      - "8084:8084"
    environment:
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - kafka
```

## Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=EmailControllerTest
```

### Test Coverage
The project includes tests for:
- EmailController
- EmailService
- PaymentKafkaConsumer
- PaymentDltConsumer

## CI/CD Pipeline

The Jenkinsfile includes:
- Build from source
- Run tests
- SonarQube analysis
- Docker image build
- Deployment to target environment

### Jenkins Pipeline Stages
1. **Checkout**: Clone the repository
2. **Build**: Maven build with tests
3. **SonarQube**: Code quality analysis
4. **Docker Build**: Build Docker image
5. **Deploy**: Deploy to target environment

## Special Features

### Kafka Retry and DLT Flow
- When a payment event fails to process, the service retries 2 times with 5-second intervals
- After all retries are exhausted, the message is sent to the Dead Letter Topic (DLT)
- The DLT consumer logs the failed message for manual inspection
- Test case: Email `fail@test.com` triggers the retry and DLT flow for testing purposes

### Email Configuration
- Uses Gmail SMTP by default for dev/idev environments
- Configurable SMTP for production
- Connection timeout: 5000ms
- Read timeout: 3000ms
- Write timeout: 5000ms

## Troubleshooting

### Email Not Sending
- Verify SMTP credentials are correct
- Check if app-specific password is used for Gmail
- Ensure network connectivity to SMTP server
- Check application logs for detailed error messages

### Kafka Consumer Not Receiving Messages
- Verify Kafka bootstrap servers configuration
- Check if Kafka topic exists
- Ensure consumer group ID is correct
- Verify network connectivity to Kafka cluster

### Build Failures
- Ensure Java 17 is installed and configured
- Check Maven dependencies in `pom.xml`
- Verify network connectivity for Maven repository access

## Contributing

1. Create a feature branch
2. Make your changes
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

[Add your license information here]

## Contact

For questions or support, please contact [your contact information].
