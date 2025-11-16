# CLAUDE.md - AI Assistant Guide for KanbanK

This document provides comprehensive guidance for AI assistants working with the KanbanK (Kreaker's Java Kanban) codebase.

## Project Overview

**KanbanK** is a full-stack Kanban board application built with Spring Boot and MongoDB. It provides drag-and-drop task management with support for multiple boards, columns, and prioritized tasks.

### Quick Facts
- **Name:** kjKanban (Kreaker's Java Kanban)
- **Group:** dev.kreaker.kjk
- **Version:** 1.0.0
- **Primary Language:** Java 17
- **Framework:** Spring Boot 3.5.4
- **Database:** MongoDB 7.0 (recently migrated from H2)
- **Build Tool:** Gradle 8.14.3
- **Container Runtime:** Podman (Docker-compatible)
- **Frontend:** Thymeleaf + Vanilla JavaScript

---

## Architecture

### Layered Architecture

The application follows a classic 3-tier Spring Boot architecture with clear separation of concerns:

```
┌─────────────────────────────────────┐
│  Controllers (REST & Web)           │
│  - @RestController (API endpoints)  │
│  - @Controller (Thymeleaf views)    │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│  Services (Business Logic)          │
│  - @Service                          │
│  - Validation & business rules      │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│  Repositories (Data Access)         │
│  - MongoRepository<T, String>       │
│  - Custom queries                    │
└─────────────────────────────────────┘
           ↓
┌─────────────────────────────────────┐
│  MongoDB (Persistence)              │
│  - Collections: boards, board_      │
│    columns, tasks                    │
└─────────────────────────────────────┘
```

### Domain Model

Three main entities with document-based relationships:

1. **Board** (`boards` collection)
   - Fields: id, name, description, createdAt, updatedAt
   - Relationships: @DBRef to List<BoardColumn>
   - Auto-creates 3 default columns on creation: "To Do", "In Progress", "Done"

2. **BoardColumn** (`board_columns` collection)
   - Fields: id, name, position, color, createdAt, updatedAt, boardId
   - Relationships: @DBRef to List<Task>
   - Position-based ordering within a board

3. **Task** (`tasks` collection)
   - Fields: id, title, description, position, priority, color, dueDate, createdAt, updatedAt, columnId
   - Priority levels: LOW, MEDIUM, HIGH, URGENT
   - Position-based ordering within a column

### Package Structure

```
dev.kreaker.kjk/
├── KjkApplication.java          # Main entry point
├── config/                       # Configuration classes
│   ├── GlobalExceptionHandler    # Centralized exception handling
│   └── JacksonConfig             # JSON serialization config
├── controller/                   # REST & Web controllers
│   ├── BoardController           # /api/boards
│   ├── BoardColumnController     # /api/columns, /api/boards/{id}/columns
│   ├── TaskController            # /api/tasks, /api/columns/{id}/tasks
│   └── WebController             # Thymeleaf view routes
├── model/                        # Domain entities
│   ├── Board
│   ├── BoardColumn
│   └── Task
├── repository/                   # Data access layer
│   ├── BoardRepository
│   ├── BoardColumnRepository
│   └── TaskRepository
└── service/                      # Business logic
    ├── BoardService
    ├── BoardColumnService
    └── TaskService
```

---

## Technology Stack

### Backend Dependencies
- **Spring Boot Starter Web** - REST API & MVC
- **Spring Boot Starter Data MongoDB** - MongoDB integration
- **Spring Boot Starter Validation** - Jakarta Bean Validation
- **Spring Boot Starter Thymeleaf** - Server-side templates
- **Spring Boot DevTools** - Hot reload during development
- **Spring Boot Starter Test** - JUnit testing framework

### Frontend Stack
- **Thymeleaf** - Server-side HTML templating
- **Vanilla JavaScript** - No frameworks (ES6+ syntax)
- **Font Awesome** - Icon library
- **CSS3** - Custom styling (no CSS frameworks)

### Infrastructure
- **MongoDB 7.0** - Document database with authentication
- **Podman/Docker** - Container runtime
- **Gradle 8.14.3** - Build automation

---

## Project Structure

```
/home/user/KanbanK/
├── build.gradle                  # Build configuration
├── gradlew & gradlew.bat        # Gradle wrapper scripts
├── gradle/                       # Gradle wrapper JAR
├── docker-compose.yml            # MongoDB container config
├── MONGODB_SETUP.md              # Database setup guide
├── README.md                     # Project documentation
└── src/
    └── main/
        ├── java/dev/kreaker/kjk/    # Java source code
        └── resources/
            ├── application.yml       # Spring configuration
            ├── static/
            │   ├── css/             # Stylesheets
            │   └── js/              # JavaScript files
            └── templates/            # Thymeleaf templates
                ├── index.html       # Dashboard
                ├── boards.html      # Board list
                ├── board-detail.html # Kanban view
                └── board-form.html   # Create/edit board
```

---

## Development Workflows

### Initial Setup

1. **Install Prerequisites:**
   ```bash
   # Java 17
   java -version  # Verify Java 17 is installed

   # Podman (or Docker)
   podman --version
   ```

2. **Start MongoDB:**
   ```bash
   # Using podman-compose (recommended)
   podman-compose up -d

   # Or direct podman command
   podman run -d --name kanban-mongodb -p 27017:27017 \
     -e MONGO_INITDB_ROOT_USERNAME=admin \
     -e MONGO_INITDB_ROOT_PASSWORD=password \
     mongo:7.0 mongod --auth
   ```

3. **Build the Application:**
   ```bash
   ./gradlew build
   ```

4. **Run the Application:**
   ```bash
   ./gradlew bootRun
   ```

5. **Access the Application:**
   - Web UI: http://localhost:8080
   - API: http://localhost:8080/api/*

### Common Development Commands

```bash
# Build project
./gradlew build

# Run application (port 8080)
./gradlew bootRun

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# Check dependencies
./gradlew dependencies

# View available tasks
./gradlew tasks
```

### Database Management

```bash
# Start MongoDB
podman-compose up -d

# Stop MongoDB
podman-compose down

# View MongoDB logs
podman-compose logs mongodb

# Connect to MongoDB shell
podman exec -it kanban-mongodb mongosh -u admin -p password --authenticationDatabase admin

# List all containers
podman ps

# Restart MongoDB
podman restart kanban-mongodb
```

### Database Configuration

**Default Credentials:**
- Username: `admin`
- Password: `password`
- Database: `kanban`
- Port: `27017`
- Auth Source: `admin`

**Connection URI:**
```
mongodb://admin:password@localhost:27017/kanban?authSource=admin
```

---

## API Reference

### Board Endpoints (`/api/boards`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/boards` | List all boards (ordered by creation date desc) |
| GET | `/api/boards/{id}` | Get board by ID |
| POST | `/api/boards` | Create new board (auto-creates default columns) |
| PUT | `/api/boards/{id}` | Update board |
| DELETE | `/api/boards/{id}` | Delete board (cascades to columns and tasks) |
| GET | `/api/boards/{id}/exists` | Check if board exists |

### Column Endpoints (`/api`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/boards/{boardId}/columns` | Get columns for board (ordered by position) |
| GET | `/api/columns/{id}` | Get column by ID |
| POST | `/api/boards/{boardId}/columns` | Create new column |
| PUT | `/api/columns/{id}` | Update column |
| DELETE | `/api/columns/{id}` | Delete column (cascades to tasks) |
| PUT | `/api/columns/{id}/move` | Move column to new position |

### Task Endpoints (`/api`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/columns/{columnId}/tasks` | Get tasks by column (ordered by position) |
| GET | `/api/boards/{boardId}/tasks` | Get all tasks for board |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/columns/{columnId}/tasks` | Create new task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| PUT | `/api/tasks/{id}/move` | Move task (drag-and-drop support) |
| GET | `/api/tasks/priority/{priority}` | Filter by priority (LOW/MEDIUM/HIGH/URGENT) |
| GET | `/api/tasks/due?start=&end=` | Filter by due date range |
| GET | `/api/boards/{boardId}/tasks/count` | Count tasks in board |

### Web Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Dashboard home |
| GET | `/boards` | List boards |
| GET | `/boards/{id}` | Board detail (Kanban view) |
| GET | `/boards/new` | Create board form |
| GET | `/boards/{id}/edit` | Edit board form |

---

## Key Conventions & Patterns

### Coding Conventions

1. **Package Naming:** `dev.kreaker.kjk.*`
2. **ID Type:** String (MongoDB ObjectId)
3. **Timestamps:** LocalDateTime (ISO-8601 format in JSON)
4. **Position Management:** Integer-based ordering (0-indexed)
5. **Validation:** Jakarta Bean Validation annotations (@NotBlank, @Size)
6. **Exception Handling:** IllegalArgumentException for business logic errors
7. **CORS:** Enabled with `@CrossOrigin(origins = "*")` on REST controllers

### Design Patterns

1. **Repository Pattern:** Data access abstraction with Spring Data MongoDB
2. **Service Layer Pattern:** Business logic separated from controllers
3. **DTO Pattern:** Inner classes for specialized requests/responses (ErrorResponse, SuccessResponse, MoveTaskRequest)
4. **Constructor Pattern:** Models use constructors with sensible defaults

### Naming Conventions

- **Entities:** Singular nouns (Board, Task, BoardColumn)
- **Collections:** Plural or descriptive (boards, tasks, board_columns)
- **Services:** EntityService (BoardService, TaskService)
- **Repositories:** EntityRepository (BoardRepository)
- **Controllers:** EntityController (BoardController)

### Data Relationships

- **Board → Columns:** One-to-many via @DBRef
- **Column → Tasks:** One-to-many via @DBRef
- **Foreign Keys:** String references (boardId, columnId)
- **Cascade Deletes:** Implemented in service layer (not database-level)

---

## Business Rules & Validation

### Board Rules
1. Board names must be unique across the system
2. Board names are required and limited to 100 characters
3. Descriptions are optional and limited to 500 characters
4. New boards auto-create 3 default columns: "To Do", "In Progress", "Done"
5. Deleting a board cascades to all columns and tasks

### Column Rules
1. Column names must be unique within a board (can duplicate across boards)
2. Columns have integer positions for ordering
3. Default color is `#3498db` (blue)
4. Deleting a column cascades to all contained tasks
5. Moving a column reorders positions automatically

### Task Rules
1. Tasks belong to exactly one column
2. Tasks have integer positions for ordering within columns
3. Default priority is MEDIUM
4. Priority levels: LOW, MEDIUM, HIGH, URGENT
5. Due dates are optional (LocalDateTime)
6. Default color can be customized per task
7. Moving a task between columns updates both positions

### Position Management
- Positions are auto-managed on create/delete/move operations
- Services handle position recalculation to maintain contiguous ordering
- Drag-and-drop operations use the `move` endpoints

---

## Database Schema

### Collections

**boards:**
```javascript
{
  "_id": ObjectId("..."),
  "name": "Project Alpha",
  "description": "Main project board",
  "createdAt": ISODate("..."),
  "updatedAt": ISODate("..."),
  "columns": [DBRef("board_columns", "..."), ...]
}
```

**board_columns:**
```javascript
{
  "_id": ObjectId("..."),
  "name": "To Do",
  "position": 0,
  "color": "#e74c3c",
  "boardId": "...",
  "createdAt": ISODate("..."),
  "updatedAt": ISODate("..."),
  "tasks": [DBRef("tasks", "..."), ...]
}
```

**tasks:**
```javascript
{
  "_id": ObjectId("..."),
  "title": "Implement feature X",
  "description": "Detailed description...",
  "position": 0,
  "priority": "MEDIUM",
  "color": "#3498db",
  "dueDate": ISODate("..."),
  "columnId": "...",
  "createdAt": ISODate("..."),
  "updatedAt": ISODate("...")
}
```

---

## Frontend Architecture

### Templates (Thymeleaf)

**index.html** - Dashboard with statistics
- Shows total boards, columns, tasks
- Quick links to boards

**boards.html** - List all boards
- Grid/list view of boards
- Create new board button
- Edit/delete actions

**board-detail.html** - Kanban board view
- Drag-and-drop columns and tasks
- Add task functionality
- Task detail modals

**board-form.html** - Create/edit board
- Form validation
- Thymeleaf form binding

### JavaScript Architecture

**app.js** (9,643 bytes)
- API client utilities
- Common DOM helpers
- Event handling utilities
- Async/await patterns

**kanban.js** (14,736 bytes, 431 lines)
- Drag-and-drop implementation
- Task management
- Column management
- Position calculations
- API interactions for boards

### CSS Architecture

**style.css** (9,685 bytes)
- General application styling
- Layout and typography
- Form styles
- Button styles

**kanban.css** (7,229 bytes)
- Kanban-specific styles
- Column layouts
- Task card styles
- Drag-and-drop visual feedback

---

## Common Tasks & Guidance for AI Assistants

### When Adding New Features

1. **Follow the layered architecture:**
   - Start with Model (if new entity needed)
   - Add Repository (if new queries needed)
   - Implement Service (business logic)
   - Create Controller (API/web endpoints)
   - Update Frontend (templates/JS/CSS)

2. **Maintain consistency:**
   - Use existing patterns for validation
   - Follow naming conventions
   - Add appropriate error handling
   - Update timestamps (createdAt/updatedAt)

3. **Testing considerations:**
   - No test files currently exist (testing infrastructure is configured but not used)
   - When adding tests, use JUnit Platform
   - Test directory: `src/test/java`
   - Follow Spring Boot testing best practices

### When Modifying Entities

1. **Add validation annotations** where appropriate
2. **Update constructors** if new required fields added
3. **Maintain relationships** (@DBRef consistency)
4. **Handle timestamps** (preUpdate methods if needed)
5. **Update repository queries** if new fields need querying
6. **Cascade changes** to service layer logic

### When Adding API Endpoints

1. **REST endpoints:** Use `@RestController` and return ResponseEntity
2. **Web endpoints:** Use `@Controller` and return view names
3. **Enable CORS** with `@CrossOrigin(origins = "*")` on REST controllers
4. **Validate input** with `@Valid` on request bodies
5. **Handle errors** (GlobalExceptionHandler will catch IllegalArgumentException)
6. **Follow RESTful conventions:** GET (retrieve), POST (create), PUT (update), DELETE (delete)

### When Working with MongoDB

1. **Use String IDs:** MongoDB uses ObjectId strings
2. **Document annotations:** @Document(collection = "name")
3. **Reference relationships:** @DBRef for entity references
4. **Custom queries:** Define in repositories using method naming conventions
5. **Cascade deletes:** Implement in service layer, not via database
6. **Connection URI:** Check application.yml for current configuration

### When Updating the Frontend

1. **Thymeleaf syntax:** Use `th:*` attributes for dynamic content
2. **JavaScript:** Use vanilla ES6+ (no frameworks)
3. **API calls:** Use async/await patterns (see app.js for examples)
4. **Drag-and-drop:** Check kanban.js for existing implementation patterns
5. **Styling:** Add to existing CSS files, don't create new ones unless necessary
6. **Icons:** Font Awesome classes available

---

## Important Considerations

### Security Notes

⚠️ **Current security status:**
- CORS is fully open (`origins = "*"`) - appropriate for development, **not production**
- Default MongoDB credentials are hardcoded - **must change for production**
- No authentication/authorization implemented
- Input validation exists but should be reviewed for production use

**For production deployment:**
- Implement authentication (Spring Security)
- Restrict CORS to specific origins
- Use environment variables for credentials
- Enable HTTPS
- Add rate limiting
- Review and enhance input validation

### Performance Considerations

- **Eager loading:** @DBRef relationships load eagerly (be mindful with large datasets)
- **Indexing:** Consider adding MongoDB indexes for frequently queried fields (name, position, boardId, columnId)
- **Caching:** No caching currently implemented
- **Pagination:** Not implemented (all queries return full result sets)

### Migration History

**Recent Changes:**
- Migrated from H2 in-memory database to MongoDB
- Changed from JPA (@Entity) to MongoDB (@Document)
- Switched from Long IDs to String IDs (ObjectId)
- Updated repositories from JpaRepository to MongoRepository

**If referencing old code:**
- Look for MongoDB-specific patterns
- Don't use JPA annotations (@Entity, @Table, @Column)
- Don't use javax.persistence imports

### Known Limitations

1. **No tests:** Testing infrastructure is configured but no tests exist
2. **No pagination:** All lists return complete result sets
3. **No authentication:** Open access to all endpoints
4. **No caching:** Every request hits the database
5. **No WebSocket:** Real-time updates not implemented (page refresh required)
6. **Limited error messages:** Some validation errors could be more descriptive

---

## File Locations Reference

### Configuration Files
- Build: `/home/user/KanbanK/build.gradle`
- App config: `/home/user/KanbanK/src/main/resources/application.yml`
- Docker: `/home/user/KanbanK/docker-compose.yml`

### Main Application
- Entry point: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/KjkApplication.java`

### Models
- Board: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/model/Board.java`
- Column: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/model/BoardColumn.java`
- Task: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/model/Task.java`

### Services
- Board: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/service/BoardService.java`
- Column: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/service/BoardColumnService.java`
- Task: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/service/TaskService.java`

### Controllers
- Board API: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/controller/BoardController.java`
- Column API: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/controller/BoardColumnController.java`
- Task API: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/controller/TaskController.java`
- Web: `/home/user/KanbanK/src/main/java/dev/kreaker/kjk/controller/WebController.java`

### Frontend
- Templates: `/home/user/KanbanK/src/main/resources/templates/`
- CSS: `/home/user/KanbanK/src/main/resources/static/css/`
- JavaScript: `/home/user/KanbanK/src/main/resources/static/js/`

---

## Quick Start Checklist for AI Assistants

Before making changes:
- [ ] Understand which layer the change belongs to (Model/Repository/Service/Controller/View)
- [ ] Check existing patterns in similar files
- [ ] Identify any cascade effects (e.g., entity changes → repository → service → controller)
- [ ] Verify MongoDB is running (`podman ps`)
- [ ] Review validation requirements
- [ ] Consider position management if dealing with ordered entities

When implementing:
- [ ] Follow package structure: `dev.kreaker.kjk.*`
- [ ] Use String IDs for MongoDB entities
- [ ] Add validation annotations where appropriate
- [ ] Implement error handling with IllegalArgumentException
- [ ] Update timestamps (createdAt/updatedAt)
- [ ] Test with `./gradlew bootRun`
- [ ] Verify API with curl or browser

After implementation:
- [ ] Verify no compilation errors: `./gradlew build`
- [ ] Check application starts: `./gradlew bootRun`
- [ ] Test the feature works as expected
- [ ] Consider if documentation needs updating
- [ ] Consider if tests should be added

---

## Getting Help

### Documentation Files
- **README.md** - Project overview
- **MONGODB_SETUP.md** - Detailed MongoDB setup instructions
- **CLAUDE.md** - This file

### Useful Commands to Inspect the System

```bash
# Check Java version
java -version

# Check Gradle version
./gradlew --version

# View all Gradle tasks
./gradlew tasks

# Check MongoDB status
podman ps | grep mongodb

# View application logs (when running)
# Application logs appear in console when using ./gradlew bootRun

# Connect to MongoDB and explore
podman exec -it kanban-mongodb mongosh -u admin -p password --authenticationDatabase admin
> use kanban
> show collections
> db.boards.find()
> db.board_columns.find()
> db.tasks.find()
```

---

## Version History

- **v1.0.0** (Current)
  - MongoDB integration
  - Full CRUD for boards, columns, tasks
  - Drag-and-drop Kanban interface
  - Priority and due date support
  - Thymeleaf web interface
  - REST API

### Recent Commits
- `806897f` - refactor: rename package
- `a2e0890` - feat: using mongodb instead H2 on podman container
- `b37d55d` - feat: first fully functional app (H2)
- `0456487` - Initial commit

---

*Last Updated: 2025-11-16*
*Branch: claude/claude-md-mi29w0z1j4f2r353-01P19FYQY9AxFJEfVgo1ZuJc*
