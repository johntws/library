# Library Management System

# Overview

This API allows you to manage a library system, enabling book registration, member management, and book loans and returns.

# Assumption

- Only considering ISBN10 for validation
- 1 email can only be used to register 1 for member
- A loan contains:
  - book
  - member
  - issue date
  - return date
  - due date
- A book cannot be borrowed by a someone who is not a member
- A loan must contains a valid book
- A loaned book, can only be returned by the same member that loaned it
- Cannot return a book that was already returned
- Cannot return a book that doesn’t exist in the system
- All tables must have auditing info. E.g. createdBy, createdDate, etc…

# Database

MySQL was chosen as the database for the project due to several key reasons. First, the project does not require the processing of big data, making MySQL's efficiency and performance sufficient for the needs of the system. Additionally, MySQL supports ACID (Atomicity, Consistency, Isolation, Durability) transactions, ensuring reliable and consistent data handling, which is crucial for maintaining data integrity in a library management system. Finally, since the requirements and data structure of the system do not frequently change, MySQL's stability and schema-based structure are well-suited for this environment, providing a dependable and straightforward database solution.

# Limitations & Future Work

## Limitations

- unable to update & delete books
- unable to update & delete members
- unable to update & delete loans
- no authentication for admin operations

## Future Work

- add CRUD operations for the limitation mentioned above
- delete operations should be done using soft delete
- add authentication for admin operations
- calculate fines for late returns
- inventory management
- report generation
- improve global exception handling
- have a base response format that includes response code, response message, & error list

# Docker

## Dockerfile

```docker
# Stage 1: Build the application
FROM maven:3.9.8-amazoncorretto-17-debian AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-oracle
WORKDIR /app
COPY --from=build /app/target/library-0.0.1-SNAPSHOT.jar /app/library-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/library-0.0.1-SNAPSHOT.jar"]

```

## Usage

```
sudo docker build -t lms .
docker run -p 8080:8080 lms -d
```

# Base URL

`http://localhost:8080/`

# Authentication

Authentication is not required for these endpoints (or specify if there is any).

# API

## POST - `/books`

Allows book registration

### Request

| Field  | Type  | Mandatory  | Description  |
| --- | --- | --- | --- |
| title  | String  | Yes  | The title of the book.  |
| author  | String  | Yes  | The author of the book.  |
| isbn  | String  | Yes  | The International Standard Book Number. |

```json
{
    "isbn": "0545162076",
    "author": "milk",
    "title": "title 1"
}
```

### Response

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| copyId | String | Yes | The id of the copy. |
| author | String | Yes | The author of the book. |
| isbn | String | Yes | The International Standard Book Number. |
| title | String | Yes | The title of the book. |
| borrowed | boolean | Yes | Borrowed status of the book. |

```json
{
    "copyId": 1,
    "isbn": "0545162076",
    "title": "title 1",
    "author": "milk",
    "borrowed": false
}
```

## GET - `/books`

Retrieve list of books

### RequestParams

| Field  | Type  | Mandatory  | Description  |
| --- | --- | --- | --- |
| title  | String  | No  | Filter by title.  |
| author  | String  | No  | Filter by author.  |
| isbn  | String  | No  | Filter by ISBN.  |
| pageSize  | Integer  | Yes  | Default = 10  |
| pageNo  | Integer  | Yes  | Default = 1  |

### Response

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| totalPages | Integer | Yes | Total available pages |
| currentPage | Integer | Yes | Current page |
| bookList | List | Yes | List of books |
| isbn | String | Yes | The International Standard Book Number. |
| title | String | Yes | The title of the book |
| author | String | Yes | The author of the book |
| copies | List<Copy> | Yes | List of book copies |

### Copy

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| copyId | Integer | Yes | The id of the book copy |
| borrowed | boolean | Yes | The status of the book |

```json
{
    "totalPages": 1,
    "currentPage": 1,
    "bookList": [
        {
            "copies": [
                {
                    "copyId": 1,
                    "borrowed": false
                },
                {
                    "copyId": 2,
                    "borrowed": false
                }
            ],
            "isbn": "0545162076",
            "title": "title 1",
            "author": "gg martin"
        },
        {
            "copies": [
                {
                    "id": 3,
                    "borrowed": false
                }
            ],
            "isbn": "222222222",
            "title": "title 2",
            "author": "aa forest"
        }
    ]
}
```

## POST - `/members`

Allows member registration

### Request

| Field  | Type  | Mandatory  | Description  |
| --- | --- | --- | --- |
| name  | String  | Yes  | The name of the member.  |
| email  | String  | Yes  | The email address of the member.  |

```json
{
    "name": "tester",
    "email": "tester@gmail.com"
}
```

### Response

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| name | String | Yes | The name of the member. |
| email | String | Yes | The email address of the member. |
| memberId | Integer | yes | The id of the member |

```json
{
    "memberId": 1,
    "name": "tester",
    "email": "tester@gmail.com"
}
```

## POST - `/members/{id}/loan`

Allows member to loan books

### Request

| Field  | Type  | Mandatory  | Description  |
| --- | --- | --- | --- |
| copyId | Integer  | Yes  | The ID of the book to be loaned.  |
| email  | String  | Yes  | Specifies user to loan  |
| dueDate  | String  | Yes  | The due date for returning the book. (YYYY-MM-DD)  |

```json
{
    "copyId": "1",
    "email": "test@gmail.com",
    "dueDate": "2024-08-22"
}
```

### Response

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| loanId | Integer | Yes | Id of the loan. |
| book | Book | Yes | Book details. |
| dueDate | String | Yes | The due date for returning the book. (YYYY-MM-DD) |
| issueDate | String | Yes | The issue date for the book. (YYYY-MM-DD) |
| borrower | Member | Yes | Member info. |

### Book

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| isbn | Integer | Yes | Id of the loan. |
| title | Book | Yes | Book details. |
| author | String | Yes | The due date for returning the book. (YYYY-MM-DD) |
| copyId | String | Yes | The issue date for the book. (YYYY-MM-DD) |

### Member

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| memberId | Integer | Yes | Id of the member |
| name | String | Yes | Name of the member |
| email | String | Yes | Email of the member |

```json
{
    "loanId": 1,
    "book": {
        "isbn": "0545162076",
        "title": "title 1",
        "author": "gg martin",
        "copyId": 1
    },
    "member": {
        "memberId": 1,
        "name": "test",
        "email": "test@gmail.com"
    },
    "issueDate": "2024-08-16",
    "dueDate": "2024-08-22"
}
```

## POST - `/members/{id}/return`

Allows member to loan books

### Request

| Field  | Type  | Mandatory  | Description  |
| --- | --- | --- | --- |
| copyId | Integer  | Yes  | The ID of the book copy to be loaned.  |
| email  | String  | Yes  | Specifies user to loan  |

```json
{
    "copyId": "1",
    "email": "jonathan@gmail.com"
}
```

### Response

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| loanId | Integer | Yes | Id of the loan. |
| book | Book | Yes | Book details. |
| dueDate | String | Yes | The due date for the book. (YYYY-MM-DD) |
| issueDate | String | Yes | The issue date for the book. (YYYY-MM-DD) |
| borrower | Member | Yes | Member info. |
| returnDate | String | Yes | The return date of the book. (YYYY-MM-DD) |

### Book

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| isbn | Integer | Yes | Id of the loan. |
| title | Book | Yes | Book details. |
| author | String | Yes | The due date for returning the book. (YYYY-MM-DD) |
| copyId | String | Yes | The issue date for the book. (YYYY-MM-DD) |

### Member

| Field | Type | Mandatory | Description |
| --- | --- | --- | --- |
| memberId | Integer | Yes | Id of the member |
| name | String | Yes | Name of the member |
| email | String | Yes | Email of the member |

```json
{
  "loanId": 1,
  "book": {
    "isbn": "0545162076",
    "title": "title 1",
    "author": "aa martion",
    "copyId": 1
  },
  "member": {
    "memberId": 1,
    "name": "test",
    "email": "test@gmail.com"
  },
  "issueDate": "2024-08-16",
  "dueDate": "2024-08-22",
  "returnDate": "2024-08-17"
}
```