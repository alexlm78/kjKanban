-- Sample data for Kanban application

-- Insert sample boards
INSERT INTO board (id, name, description, created_at, updated_at) VALUES
(1, 'Project Alpha', 'Main development project for the new application', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Marketing Campaign', 'Q4 marketing campaign planning and execution', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Bug Fixes', 'Critical bug fixes and maintenance tasks', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample columns for Project Alpha board
INSERT INTO board_column (id, name, position, color, board_id, created_at, updated_at) VALUES
(1, 'Backlog', 0, '#6c757d', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'In Progress', 1, '#007bff', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Review', 2, '#ffc107', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Done', 3, '#28a745', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample columns for Marketing Campaign board
INSERT INTO board_column (id, name, position, color, board_id, created_at, updated_at) VALUES
(5, 'Ideas', 0, '#e83e8c', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Planning', 1, '#fd7e14', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Execution', 2, '#20c997', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Completed', 3, '#6f42c1', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample columns for Bug Fixes board
INSERT INTO board_column (id, name, position, color, board_id, created_at, updated_at) VALUES
(9, 'Reported', 0, '#dc3545', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Investigating', 1, '#17a2b8', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'Fixed', 2, '#28a745', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample tasks for Project Alpha board
INSERT INTO task (id, title, description, position, priority, color, due_date, column_id, created_at, updated_at) VALUES
(1, 'Setup project structure', 'Initialize the project with proper folder structure and dependencies', 0, 'HIGH', '#ffffff', '2024-01-15', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Design database schema', 'Create entity relationship diagram and database tables', 1, 'HIGH', '#ffffff', '2024-01-20', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Implement user authentication', 'Add login and registration functionality', 0, 'MEDIUM', '#ffffff', '2024-01-25', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Create REST API endpoints', 'Develop CRUD operations for all entities', 1, 'HIGH', '#ffffff', '2024-01-30', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Code review for authentication', 'Review the authentication implementation', 0, 'MEDIUM', '#ffffff', '2024-01-22', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Setup CI/CD pipeline', 'Configure automated testing and deployment', 0, 'LOW', '#ffffff', '2024-02-05', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample tasks for Marketing Campaign board
INSERT INTO task (id, title, description, position, priority, color, due_date, column_id, created_at, updated_at) VALUES
(7, 'Social media strategy', 'Develop comprehensive social media marketing strategy', 0, 'HIGH', '#ffffff', '2024-01-18', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Content calendar', 'Create monthly content calendar for all platforms', 1, 'MEDIUM', '#ffffff', '2024-01-25', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Email campaign design', 'Design email templates and campaign flow', 0, 'HIGH', '#ffffff', '2024-01-20', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Launch social media ads', 'Start Facebook and Instagram advertising campaigns', 0, 'URGENT', '#ffffff', '2024-01-15', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'Q3 campaign analysis', 'Analyze results from previous quarter campaign', 0, 'LOW', '#ffffff', NULL, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample tasks for Bug Fixes board
INSERT INTO task (id, title, description, position, priority, color, due_date, column_id, created_at, updated_at) VALUES
(12, 'Login page not responsive', 'Login form breaks on mobile devices', 0, 'HIGH', '#ffffff', '2024-01-16', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'Memory leak in dashboard', 'Dashboard page consuming excessive memory over time', 1, 'URGENT', '#ffffff', '2024-01-14', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'API timeout issues', 'Some API calls timing out under heavy load', 0, 'HIGH', '#ffffff', '2024-01-18', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'Email notifications not sent', 'Users not receiving email notifications for important events', 0, 'MEDIUM', '#ffffff', NULL, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Note: H2 auto-generates sequence names, so we don't need to reset them manually