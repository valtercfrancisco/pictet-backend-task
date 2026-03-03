# Adventure Book API

A REST API for browsing and playing through interactive adventure books.

## Prerequisites

- Docker
- Docker Compose
- curl (for testing)
- jq (optional, for pretty JSON output)

## Running the Application

1. **Start the application:**
```bash
docker-compose up --build -d
```

2. **Wait for the application to start**

3. **Stop the application:**
```bash
docker-compose down
```

## API Endpoints

### Book Management

#### Get all books
```bash
curl -s http://localhost:8080/v1/books | jq '.'
```

#### Get a specific book
```bash
curl -s http://localhost:8080/v1/books/1 | jq '.'
```

#### Search books by title
```bash
curl -s "http://localhost:8080/v1/books/search?title=Crystal" | jq '.'
```

#### Search books by category
```bash
curl -s "http://localhost:8080/v1/books/search/category?category=FANTASY" | jq '.'
```

#### Add category to a book
```bash
curl -s -X POST "http://localhost:8080/v1/books/1/categories?category=HORROR" | jq '.'
```

#### Remove category from a book
```bash
curl -s -X DELETE "http://localhost:8080/v1/books/1/categories?category=HORROR" | jq '.'
```

### Reading Sessions (Playing Books)

#### Start a new reading session
```bash
curl -s -X POST http://localhost:8080/v1/reading/book/3/read | jq '.'
```

Response:
```json
{
  "sessionId": 1,
  "bookId": 3,
  "bookTitle": "The Prisoner",
  "health": 10,
  "currentSection": {
    "sectionId": 1,
    "text": "You wake up in what seems to be a dark prison cell...",
    "type": "BEGIN",
    "options": [
      {
        "description": "You try to open the door",
        "gotoId": 500,
        "consequence": null
      },
      {
        "description": "You look under the bed",
        "gotoId": 20,
        "consequence": null
      }
    ],
    "consequence": null
  }
}
```

#### Get current session state
```bash
curl -s http://localhost:8080/v1/reading/session/1 | jq '.'
```

#### Make a choice (continue reading)
```bash
# Choose option at index 1 (second option)
curl -s -X POST http://localhost:8080/v1/reading/session/1/choose/1 | jq '.'
```

Response with consequence:
```json
{
  "sessionId": 1,
  "bookId": 3,
  "bookTitle": "The Prisoner",
  "health": 4,
  "currentSection": {
    "sectionId": 30,
    "text": "You found what seems to be a door key.",
    "type": "NODE",
    "options": [...],
    "consequence": {
      "type": "LOSE_HEALTH",
      "value": "6",
      "text": "As you move your hands left and right under the bed, you cut yourself on a rusty nail.",
      "previousHealth": 10,
      "currentHealth": 4
    }
  }
}
```

## Complete Playthrough Example

```bash
# 1. Start reading session
SESSION_ID=$(curl -s -X POST http://localhost:8080/v1/reading/book/3/read | jq -r '.sessionId')
echo "Session ID: $SESSION_ID"

# 2. Choose option 1: "You look under the bed"
curl -s -X POST http://localhost:8080/v1/reading/session/$SESSION_ID/choose/1 | jq '.currentSection.text'

# 3. Choose option 0: "Try to scan the area with your hands" (loses 6 health)
curl -s -X POST http://localhost:8080/v1/reading/session/$SESSION_ID/choose/0 | jq '{health, text: .currentSection.text}'

# 4. Choose option 0: "Try to open the door with the key" (WIN!)
curl -s -X POST http://localhost:8080/v1/reading/session/$SESSION_ID/choose/0 | jq '{health, text: .currentSection.text, type: .currentSection.type}'

# 5. Try to get session (should return 404 - session deleted after END)
curl -s http://localhost:8080/v1/reading/session/$SESSION_ID | jq '.'
```

## Book Validation Rules

A book is considered invalid if:
- It has no BEGIN section or more than one BEGIN section
- It has no END section (but can have multiple END sections)
- During gameplay: A non-END section has no options (dead end)
- During gameplay: An option points to a non-existent section

## Available Categories

- FICTION
- SCIENCE
- HORROR
- ADVENTURE
- FANTASY
- MYSTERY
- THRILLER
- ROMANCE
- HISTORICAL
- BIOGRAPHY

## Available Difficulty Levels

- EASY
- MEDIUM
- HARD

## Database Access

The application uses PostgreSQL. Connection details:
- Host: localhost
- Port: 5432
- Database: backend_db
- Username: backend_user
- Password: backend_password

