// Kanban Board JavaScript

let currentBoardId = null;
let columns = [];
let tasks = [];
let currentEditingTask = null;
let currentEditingColumn = null;

// Initialize Kanban Board
async function initializeKanbanBoard(boardId) {
    currentBoardId = boardId;
    await loadBoard();
}

// Load board data
async function loadBoard() {
    try {
        app.loading.show(document.getElementById('kanbanColumns'), 'Loading board...');
        
        // Load columns with tasks
        columns = await app.api.get(`/boards/${currentBoardId}/columns`);
        
        renderBoard();
    } catch (error) {
        console.error('Error loading board:', error);
        app.showNotification('Error loading board', 'error');
    }
}

// Render the entire board
function renderBoard() {
    const columnsContainer = document.getElementById('kanbanColumns');
    app.dom.removeAllChildren(columnsContainer);
    
    columns.forEach(column => {
        const columnElement = createColumnElement(column);
        columnsContainer.appendChild(columnElement);
    });
    
    initializeDragAndDrop();
}

// Create column element
function createColumnElement(column) {
    const columnDiv = app.dom.createElement('div', 'kanban-column');
    columnDiv.setAttribute('data-column-id', column.id);
    
    // Column header
    const header = app.dom.createElement('div', 'column-header');
    header.style.backgroundColor = column.color || '#3498db';
    
    const title = app.dom.createElement('div', 'column-title');
    title.innerHTML = `
        <span>${column.name}</span>
        <span class="column-count">${column.tasks ? column.tasks.length : 0}</span>
    `;
    
    const actions = app.dom.createElement('div', 'column-actions');
    actions.innerHTML = `
        <button class="column-action" onclick="editColumn(${column.id})" title="Edit Column">
            <i class="fas fa-edit"></i>
        </button>
        <button class="column-action" onclick="deleteColumn(${column.id})" title="Delete Column">
            <i class="fas fa-trash"></i>
        </button>
    `;
    
    header.appendChild(title);
    header.appendChild(actions);
    
    // Column content
    const content = app.dom.createElement('div', 'column-content');
    content.setAttribute('data-column-id', column.id);
    
    if (column.tasks && column.tasks.length > 0) {
        column.tasks.forEach(task => {
            const taskElement = createTaskElement(task);
            content.appendChild(taskElement);
        });
    } else {
        const emptyState = app.dom.createElement('div', 'column-empty');
        emptyState.innerHTML = `
            <i class="fas fa-tasks"></i>
            <p>No tasks yet</p>
        `;
        content.appendChild(emptyState);
    }
    
    // Column footer
    const footer = app.dom.createElement('div', 'column-footer');
    footer.innerHTML = `
        <button class="add-task-btn" onclick="addTask(${column.id})">
            <i class="fas fa-plus"></i>
            Add Task
        </button>
    `;
    
    columnDiv.appendChild(header);
    columnDiv.appendChild(content);
    columnDiv.appendChild(footer);
    
    return columnDiv;
}

// Create task element
function createTaskElement(task) {
    const taskDiv = app.dom.createElement('div', `task-card priority-${task.priority.toLowerCase()}`);
    taskDiv.setAttribute('data-task-id', task.id);
    taskDiv.style.borderLeftColor = getPriorityColor(task.priority);
    
    const header = app.dom.createElement('div', 'task-header');
    
    const title = app.dom.createElement('div', 'task-title', task.title);
    
    const actions = app.dom.createElement('div', 'task-actions');
    actions.innerHTML = `
        <button class="task-action edit" onclick="editTask(${task.id})" title="Edit Task">
            <i class="fas fa-edit"></i>
        </button>
        <button class="task-action delete" onclick="deleteTask(${task.id})" title="Delete Task">
            <i class="fas fa-trash"></i>
        </button>
    `;
    
    header.appendChild(title);
    header.appendChild(actions);
    
    taskDiv.appendChild(header);
    
    // Description
    if (task.description) {
        const description = app.dom.createElement('div', 'task-description', task.description);
        taskDiv.appendChild(description);
    }
    
    // Meta information
    const meta = app.dom.createElement('div', 'task-meta');
    
    const priority = app.dom.createElement('span', `task-priority ${task.priority.toLowerCase()}`);
    priority.textContent = task.priority;
    
    const metaRight = app.dom.createElement('div');
    
    if (task.dueDate) {
        const dueDate = app.dom.createElement('span', 'task-due-date');
        const isOverdue = app.dateUtils.isOverdue(task.dueDate);
        const isDueSoon = app.dateUtils.isDueSoon(task.dueDate);
        
        if (isOverdue) {
            dueDate.classList.add('overdue');
        } else if (isDueSoon) {
            dueDate.classList.add('due-soon');
        }
        
        dueDate.innerHTML = `
            <i class="fas fa-clock"></i>
            ${app.dateUtils.formatDate(task.dueDate)}
        `;
        metaRight.appendChild(dueDate);
    }
    
    meta.appendChild(priority);
    meta.appendChild(metaRight);
    taskDiv.appendChild(meta);
    
    return taskDiv;
}

// Get priority color
function getPriorityColor(priority) {
    const colors = {
        'LOW': '#95a5a6',
        'MEDIUM': '#f39c12',
        'HIGH': '#e67e22',
        'URGENT': '#e74c3c'
    };
    return colors[priority] || '#95a5a6';
}

// Initialize drag and drop
function initializeDragAndDrop() {
    const columnContents = document.querySelectorAll('.column-content');
    
    columnContents.forEach(content => {
        new Sortable(content, {
            group: 'tasks',
            animation: 150,
            ghostClass: 'sortable-ghost',
            chosenClass: 'sortable-chosen',
            dragClass: 'dragging',
            onStart: function(evt) {
                evt.item.classList.add('dragging');
            },
            onEnd: function(evt) {
                evt.item.classList.remove('dragging');
                
                const taskId = evt.item.getAttribute('data-task-id');
                const newColumnId = evt.to.getAttribute('data-column-id');
                const newPosition = evt.newIndex;
                
                moveTaskToColumn(taskId, newColumnId, newPosition);
            }
        });
    });
}

// Move task to different column
async function moveTaskToColumn(taskId, newColumnId, newPosition) {
    try {
        await app.api.put(`/tasks/${taskId}/move`, {
            newColumnId: parseInt(newColumnId),
            newPosition: newPosition
        });
        
        // Reload board to reflect changes
        await loadBoard();
    } catch (error) {
        console.error('Error moving task:', error);
        app.showNotification('Error moving task', 'error');
        // Reload board to revert changes
        await loadBoard();
    }
}

// Add Column
function addColumn() {
    currentEditingColumn = null;
    document.getElementById('columnModalTitle').textContent = 'Add Column';
    document.getElementById('columnForm').reset();
    document.getElementById('columnModal').style.display = 'flex';
    document.getElementById('columnName').focus();
}

// Edit Column
async function editColumn(columnId) {
    try {
        const column = await app.api.get(`/columns/${columnId}`);
        currentEditingColumn = column;
        
        document.getElementById('columnModalTitle').textContent = 'Edit Column';
        document.getElementById('columnName').value = column.name;
        document.getElementById('columnColor').value = column.color || '#3498db';
        document.getElementById('columnModal').style.display = 'flex';
        document.getElementById('columnName').focus();
    } catch (error) {
        console.error('Error loading column:', error);
        app.showNotification('Error loading column', 'error');
    }
}

// Delete Column
async function deleteColumn(columnId) {
    if (!confirm('Are you sure you want to delete this column? All tasks in this column will be deleted.')) {
        return;
    }
    
    try {
        await app.api.delete(`/columns/${columnId}`);
        app.showNotification('Column deleted successfully', 'success');
        await loadBoard();
    } catch (error) {
        console.error('Error deleting column:', error);
        app.showNotification('Error deleting column', 'error');
    }
}

// Close Column Modal
function closeColumnModal() {
    document.getElementById('columnModal').style.display = 'none';
    currentEditingColumn = null;
}

// Add Task
function addTask(columnId = null) {
    currentEditingTask = null;
    document.getElementById('taskModalTitle').textContent = 'Add Task';
    document.getElementById('taskForm').reset();
    
    // Populate column options
    populateColumnOptions();
    
    // Set default column if provided
    if (columnId) {
        document.getElementById('taskColumn').value = columnId;
    }
    
    document.getElementById('taskModal').style.display = 'flex';
    document.getElementById('taskTitle').focus();
}

// Edit Task
async function editTask(taskId) {
    try {
        const task = await app.api.get(`/tasks/${taskId}`);
        currentEditingTask = task;
        
        document.getElementById('taskModalTitle').textContent = 'Edit Task';
        document.getElementById('taskTitle').value = task.title;
        document.getElementById('taskDescription').value = task.description || '';
        document.getElementById('taskPriority').value = task.priority;
        document.getElementById('taskColor').value = task.color || '#ffffff';
        
        if (task.dueDate) {
            document.getElementById('taskDueDate').value = app.dateUtils.toInputFormat(task.dueDate);
        }
        
        // Populate column options and set current column
        populateColumnOptions();
        document.getElementById('taskColumn').value = task.column.id;
        
        document.getElementById('taskModal').style.display = 'flex';
        document.getElementById('taskTitle').focus();
    } catch (error) {
        console.error('Error loading task:', error);
        app.showNotification('Error loading task', 'error');
    }
}

// Delete Task
async function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) {
        return;
    }
    
    try {
        await app.api.delete(`/tasks/${taskId}`);
        app.showNotification('Task deleted successfully', 'success');
        await loadBoard();
    } catch (error) {
        console.error('Error deleting task:', error);
        app.showNotification('Error deleting task', 'error');
    }
}

// Close Task Modal
function closeTaskModal() {
    document.getElementById('taskModal').style.display = 'none';
    currentEditingTask = null;
}

// Populate column options in task form
function populateColumnOptions() {
    const select = document.getElementById('taskColumn');
    app.dom.removeAllChildren(select);
    
    columns.forEach(column => {
        const option = document.createElement('option');
        option.value = column.id;
        option.textContent = column.name;
        select.appendChild(option);
    });
}

// Form submission handlers
document.addEventListener('DOMContentLoaded', function() {
    // Column form submission
    const columnForm = document.getElementById('columnForm');
    if (columnForm) {
        columnForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(columnForm);
            const columnData = {
                name: formData.get('name'),
                color: formData.get('color')
            };
            
            try {
                app.validation.validateRequired(columnData.name, 'Column name');
                app.validation.validateMaxLength(columnData.name, 100, 'Column name');
                app.validation.validateColor(columnData.color);
                
                if (currentEditingColumn) {
                    // Update existing column
                    await app.api.put(`/columns/${currentEditingColumn.id}`, columnData);
                    app.showNotification('Column updated successfully', 'success');
                } else {
                    // Create new column
                    await app.api.post(`/boards/${currentBoardId}/columns`, columnData);
                    app.showNotification('Column created successfully', 'success');
                }
                
                closeColumnModal();
                await loadBoard();
            } catch (error) {
                console.error('Error saving column:', error);
                app.showNotification(error.message || 'Error saving column', 'error');
            }
        });
    }
    
    // Task form submission
    const taskForm = document.getElementById('taskForm');
    if (taskForm) {
        taskForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(taskForm);
            const taskData = {
                title: formData.get('title'),
                description: formData.get('description'),
                priority: formData.get('priority'),
                color: formData.get('color'),
                dueDate: formData.get('dueDate') || null
            };
            
            try {
                app.validation.validateRequired(taskData.title, 'Task title');
                app.validation.validateMaxLength(taskData.title, 200, 'Task title');
                app.validation.validateMaxLength(taskData.description, 1000, 'Description');
                app.validation.validateColor(taskData.color);
                
                if (currentEditingTask) {
                    // Update existing task
                    await app.api.put(`/tasks/${currentEditingTask.id}`, taskData);
                    app.showNotification('Task updated successfully', 'success');
                } else {
                    // Create new task
                    const columnId = formData.get('columnId');
                    await app.api.post(`/columns/${columnId}/tasks`, taskData);
                    app.showNotification('Task created successfully', 'success');
                }
                
                closeTaskModal();
                await loadBoard();
            } catch (error) {
                console.error('Error saving task:', error);
                app.showNotification(error.message || 'Error saving task', 'error');
            }
        });
    }
});