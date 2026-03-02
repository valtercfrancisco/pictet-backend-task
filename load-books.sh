#!/bin/bash

# Script to load book JSON files into PostgreSQL database
# Usage: ./load-books.sh

set -e

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-backend_db}"
DB_USER="${DB_USER:-backend_user}"
DB_PASSWORD="${DB_PASSWORD:-backend_password}"

echo "Loading books into PostgreSQL database..."

load_book() {
    local json_file=$1
    local filename=$(basename "$json_file")
    
    echo "Processing: $filename"

    local json_content=$(cat "$json_file")

    local title=$(echo "$json_content" | jq -r '.title')
    local author=$(echo "$json_content" | jq -r '.author')
    local difficulty=$(echo "$json_content" | jq -r '.difficulty')
    
    # Insert book and get the book_id
    local book_id=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -A -c \
        "INSERT INTO books (title, author, difficulty) VALUES ('$title', '$author', '$difficulty') RETURNING id;" 2>&1 | grep -E '^[0-9]+$' | head -1)

    if [ -z "$book_id" ]; then
        echo "  ERROR: Failed to insert book '$title'"
        return 1
    fi

    echo "  Created book with ID: $book_id"
    
    # Extract and insert sections
    local sections=$(echo "$json_content" | jq -c '.sections[]')

    while IFS= read -r section; do
        local section_id=$(echo "$section" | jq -r '.id')
        local section_text=$(echo "$section" | jq -r '.text' | sed "s/'/''/g")
        local section_type=$(echo "$section" | jq -r '.type')
        local options=$(echo "$section" | jq -c '.options // []' | sed "s/'/''/g")

        PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -q -c \
            "INSERT INTO sections (book_id, id, text, type, options) VALUES ($book_id, $section_id, E'$section_text', '$section_type', E'$options'::jsonb);"

    done <<< "$sections"
    
    echo "  Loaded ${title} successfully!"
}

if ! command -v jq &> /dev/null; then
    echo "Error: jq is required but not installed. Please install jq first."
    echo "  macOS: brew install jq"
    echo "  Ubuntu/Debian: sudo apt-get install jq"
    exit 1
fi

if ! command -v psql &> /dev/null; then
    echo "Error: psql is required but not installed. Please install PostgreSQL client."
    exit 1
fi

echo "Waiting for database to be ready..."
for i in {1..30}; do
    if PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c '\q' 2>/dev/null; then
        echo "Database is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Error: Database is not ready after 30 seconds"
        exit 1
    fi
    sleep 1
done

# Check if books already exist
book_count=$(PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM books;" | xargs)

if [ "$book_count" -gt 0 ]; then
    echo "Books already exist in database (count: $book_count). Skipping load."
    echo "To reload, first run: PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c 'TRUNCATE books CASCADE;'"
    exit 0
fi

# Load all JSON files from books directory
for json_file in books/*.json; do
    if [ -f "$json_file" ] && [ -s "$json_file" ]; then
        load_book "$json_file"
    elif [ -f "$json_file" ]; then
        echo "Skipping empty file: $(basename "$json_file")"
    fi
done

echo ""
echo "All books loaded successfully!"

