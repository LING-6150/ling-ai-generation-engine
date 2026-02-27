// Task Tracker Application

// Task data structure
let tasks = [];

// DOM Elements
const taskForm = document.getElementById('taskForm');
const taskTitleInput = document.getElementById('taskTitle');
const taskDescriptionInput = document.getElementById('taskDescription');
const taskPrioritySelect = document.getElementById('taskPriority');
const tasksContainer = document.getElementById('tasksContainer');
const filterButtons = document.querySelectorAll('.filter-btn');

// Current filter state
let currentFilter = 'all';

// Initialize the application
function init() {
    // Load tasks from localStorage if available
    loadTasksFromStorage();
    
    // Render initial tasks
    renderTasks();
    
    // Set up event listeners
    setupEventListeners();
}

// Set up all event listeners
function setupEventListeners() {
    // Form submission for adding new task
    taskForm.addEventListener('submit', handleAddTask);
    
    // Filter button clicks
    filterButtons.forEach(button => {
        button.addEventListener('click', handleFilterClick);
    });
}

// Handle form submission to add new task
function handleAddTask(e) {
    e.preventDefault();
    
    // Get form values
    const title = taskTitleInput.value.trim();
    const description = taskDescriptionInput.value.trim();
    const priority = taskPrioritySelect.value;
    
    // Validate title
    if (!title) {
        alert('Please enter a task title');
        taskTitleInput.focus();
        return;
    }
    
    // Create new task object
    const newTask = {
        id: Date.now(), // Simple unique ID
        title: title,
        description: description,
        priority: priority,
        completed: false,
        createdAt: new Date().toISOString()
    };
    
    // Add to tasks array
    tasks.push(newTask);
    
    // Save to localStorage
    saveTasksToStorage();
    
    // Render updated tasks
    renderTasks();
    
    // Reset form
    taskForm.reset();
    taskPrioritySelect.value = 'medium';
    
    // Focus back to title input
    taskTitleInput.focus();
}

// Handle filter button clicks
function handleFilterClick(e) {
    // Update active button
    filterButtons.forEach(button => {
        button.classList.remove('active');
    });
    e.target.classList.add('active');
    
    // Update current filter
    currentFilter = e.target.dataset.filter;
    
    // Re-render tasks with new filter
    renderTasks();
}

// Render tasks based on current filter
function renderTasks() {
    // Clear container
    tasksContainer.innerHTML = '';
    
    // Filter tasks based on current filter
    let filteredTasks = tasks;
    
    if (currentFilter === 'pending') {
        filteredTasks = tasks.filter(task => !task.completed);
    } else if (currentFilter === 'completed') {
        filteredTasks = tasks.filter(task => task.completed);
    }
    
    // If no tasks, show message
    if (filteredTasks.length === 0) {
        const noTasksMsg = document.createElement('p');
        noTasksMsg.className = 'no-tasks';
        noTasksMsg.textContent = currentFilter === 'all' 
            ? 'No tasks added yet. Start by adding a task above!' 
            : `No ${currentFilter} tasks found.`;
        tasksContainer.appendChild(noTasksMsg);
        return;
    }
    
    // Render each task
    filteredTasks.forEach(task => {
        const taskElement = createTaskElement(task);
        tasksContainer.appendChild(taskElement);
    });
}

// Create DOM element for a task
function createTaskElement(task) {
    const taskDiv = document.createElement('div');
    taskDiv.className = `task-item ${task.priority} ${task.completed ? 'completed' : ''}`;
    taskDiv.dataset.id = task.id;
    
    // Priority badge text
    const priorityText = task.priority.charAt(0).toUpperCase() + task.priority.slice(1);
    
    // Task HTML structure
    taskDiv.innerHTML = `
        <div class="task-header">
            <h3 class="task-title">${escapeHtml(task.title)}</h3>
            <span class="task-priority priority-${task.priority}">${priorityText}</span>
        </div>
        <p class="task-description">${escapeHtml(task.description) || 'No description provided'}</p>
        <div class="task-actions">
            ${!task.completed ? `<button class="btn task-btn btn-complete" data-action="complete">Mark Complete</button>` : ''}
            <button class="btn task-btn btn-edit" data-action="edit">Edit</button>
            <button class="btn task-btn btn-delete" data-action="delete">Delete</button>
        </div>
    `;
    
    // Add event listeners to action buttons
    const actionButtons = taskDiv.querySelectorAll('.task-btn');
    actionButtons.forEach(button => {
        button.addEventListener('click', () => handleTaskAction(task.id, button.dataset.action));
    });
    
    return taskDiv;
}

// Handle task actions (complete, edit, delete)
function handleTaskAction(taskId, action) {
    const taskIndex = tasks.findIndex(task => task.id === taskId);
    
    if (taskIndex === -1) return;
    
    switch (action) {
        case 'complete':
            tasks[taskIndex].completed = true;
            break;
            
        case 'edit':
            // For simplicity, we'll just delete and re-add with current form values
            // In a more advanced version, you could implement a proper edit modal
            const taskToEdit = tasks[taskIndex];
            taskTitleInput.value = taskToEdit.title;
            taskDescriptionInput.value = taskToEdit.description;
            taskPrioritySelect.value = taskToEdit.priority;
            
            // Remove the task
            tasks.splice(taskIndex, 1);
            break;
            
        case 'delete':
            // Confirm deletion
            if (confirm('Are you sure you want to delete this task?')) {
                tasks.splice(taskIndex, 1);
            } else {
                return;
            }
            break;
    }
    
    // Save to storage and re-render
    saveTasksToStorage();
    renderTasks();
}

// Save tasks to localStorage
function saveTasksToStorage() {
    localStorage.setItem('taskTrackerTasks', JSON.stringify(tasks));
}

// Load tasks from localStorage
function loadTasksFromStorage() {
    const storedTasks = localStorage.getItem('taskTrackerTasks');
    if (storedTasks) {
        tasks = JSON.parse(storedTasks);
    }
}

// Simple HTML escaping to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', init);