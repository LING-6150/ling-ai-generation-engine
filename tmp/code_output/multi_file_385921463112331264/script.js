// Task Manager Application
class TaskManager {
    constructor() {
        this.tasks = JSON.parse(localStorage.getItem('tasks')) || [];
        this.currentFilter = 'all';
        this.editingTaskId = null;
        this.initializeElements();
        this.setupEventListeners();
        this.updateDisplay();
        this.updateStats();
        this.setCurrentYear();
    }

    initializeElements() {
        // Input and buttons
        this.taskInput = document.getElementById('taskInput');
        this.addTaskBtn = document.getElementById('addTaskBtn');
        this.clearCompletedBtn = document.getElementById('clearCompletedBtn');
        this.tasksContainer = document.getElementById('tasksContainer');
        this.emptyState = document.getElementById('emptyState');
        this.taskTemplate = document.getElementById('taskTemplate');

        // Stats elements
        this.totalTasksEl = document.getElementById('totalTasks');
        this.completedTasksEl = document.getElementById('completedTasks');
        this.pendingTasksEl = document.getElementById('pendingTasks');

        // Filter buttons
        this.filterButtons = document.querySelectorAll('.filter-btn');
    }

    setupEventListeners() {
        // Add task on button click
        this.addTaskBtn.addEventListener('click', () => this.addTask());

        // Add task on Enter key
        this.taskInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.addTask();
            }
        });

        // Clear completed tasks
        this.clearCompletedBtn.addEventListener('click', () => this.clearCompletedTasks());

        // Filter buttons
        this.filterButtons.forEach(btn => {
            btn.addEventListener('click', (e) => this.setFilter(e.target.dataset.filter));
        });

        // Initialize with focus on input
        this.taskInput.focus();
    }

    addTask() {
        const text = this.taskInput.value.trim();
        
        if (!text) {
            this.showNotification('Please enter a task', 'error');
            this.taskInput.focus();
            return;
        }

        if (this.editingTaskId) {
            // Update existing task
            this.updateTask(this.editingTaskId, text);
            this.editingTaskId = null;
            this.addTaskBtn.innerHTML = '<span class="btn-icon">+</span> Add Task';
        } else {
            // Create new task
            const task = {
                id: Date.now().toString(),
                text: text,
                completed: false,
                createdAt: new Date().toISOString(),
                updatedAt: new Date().toISOString()
            };

            this.tasks.unshift(task);
            this.showNotification('Task added successfully!', 'success');
        }

        // Clear input and save
        this.taskInput.value = '';
        this.saveTasks();
        this.updateDisplay();
        this.updateStats();
        this.taskInput.focus();
    }

    updateTask(id, newText) {
        const taskIndex = this.tasks.findIndex(task => task.id === id);
        if (taskIndex !== -1) {
            this.tasks[taskIndex].text = newText;
            this.tasks[taskIndex].updatedAt = new Date().toISOString();
            this.showNotification('Task updated successfully!', 'success');
        }
    }

    toggleTaskStatus(id) {
        const task = this.tasks.find(task => task.id === id);
        if (task) {
            task.completed = !task.completed;
            task.updatedAt = new Date().toISOString();
            this.saveTasks();
            this.updateDisplay();
            this.updateStats();
            
            const status = task.completed ? 'completed' : 'pending';
            this.showNotification(`Task marked as ${status}`, 'info');
        }
    }

    deleteTask(id) {
        if (confirm('Are you sure you want to delete this task?')) {
            this.tasks = this.tasks.filter(task => task.id !== id);
            this.saveTasks();
            this.updateDisplay();
            this.updateStats();
            this.showNotification('Task deleted', 'info');
        }
    }

    editTask(id) {
        const task = this.tasks.find(task => task.id === id);
        if (task) {
            this.taskInput.value = task.text;
            this.taskInput.focus();
            this.editingTaskId = id;
            this.addTaskBtn.innerHTML = '<span class="btn-icon">✓</span> Update Task';
            this.showNotification('Editing task...', 'info');
        }
    }

    clearCompletedTasks() {
        const completedCount = this.tasks.filter(task => task.completed).length;
        
        if (completedCount === 0) {
            this.showNotification('No completed tasks to clear', 'info');
            return;
        }

        if (confirm(`Clear ${completedCount} completed task${completedCount > 1 ? 's' : ''}?`)) {
            this.tasks = this.tasks.filter(task => !task.completed);
            this.saveTasks();
            this.updateDisplay();
            this.updateStats();
            this.showNotification('Completed tasks cleared', 'success');
        }
    }

    setFilter(filter) {
        this.currentFilter = filter;
        
        // Update active filter button
        this.filterButtons.forEach(btn => {
            btn.classList.toggle('active', btn.dataset.filter === filter);
        });

        this.updateDisplay();
    }

    getFilteredTasks() {
        switch (this.currentFilter) {
            case 'pending':
                return this.tasks.filter(task => !task.completed);
            case 'completed':
                return this.tasks.filter(task => task.completed);
            default:
                return this.tasks;
        }
    }

    updateDisplay() {
        const filteredTasks = this.getFilteredTasks();
        
        // Clear container
        this.tasksContainer.innerHTML = '';

        if (filteredTasks.length === 0) {
            // Show empty state
            this.emptyState.style.display = 'block';
            this.tasksContainer.appendChild(this.emptyState);
        } else {
            // Hide empty state
            this.emptyState.style.display = 'none';
            
            // Add tasks
            filteredTasks.forEach(task => {
                const taskElement = this.createTaskElement(task);
                this.tasksContainer.appendChild(taskElement);
            });
        }
    }

    createTaskElement(task) {
        const template = this.taskTemplate.content.cloneNode(true);
        const taskElement = template.querySelector('.task-item');
        
        // Set task data
        taskElement.dataset.id = task.id;
        if (task.completed) {
            taskElement.classList.add('completed');
        }

        // Set checkbox state
        const checkbox = taskElement.querySelector('.task-status');
        checkbox.checked = task.completed;
        checkbox.addEventListener('change', () => this.toggleTaskStatus(task.id));

        // Set task text
        const taskText = taskElement.querySelector('.task-text');
        taskText.textContent = task.text;
        
        // Double-click to edit
        taskText.addEventListener('dblclick', () => this.editTask(task.id));

        // Set date
        const taskDate = taskElement.querySelector('.task-date');
        const date = new Date(task.createdAt);
        taskDate.textContent = `Added: ${date.toLocaleDateString()}`;

        // Set up edit button
        const editBtn = taskElement.querySelector('.edit-btn');
        editBtn.addEventListener('click', () => this.editTask(task.id));

        // Set up delete button
        const deleteBtn = taskElement.querySelector('.delete-btn');
        deleteBtn.addEventListener('click', () => this.deleteTask(task.id));

        return taskElement;
    }

    updateStats() {
        const total = this.tasks.length;
        const completed = this.tasks.filter(task => task.completed).length;
        const pending = total - completed;

        this.totalTasksEl.textContent = total;
        this.completedTasksEl.textContent = completed;
        this.pendingTasksEl.textContent = pending;
    }

    saveTasks() {
        localStorage.setItem('tasks', JSON.stringify(this.tasks));
    }

    showNotification(message, type) {
        // Remove existing notification
        const existingNotification = document.querySelector('.notification');
        if (existingNotification) {
            existingNotification.remove();
        }

        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 1rem 1.5rem;
            background: ${type === 'error' ? '#f72585' : type === 'success' ? '#4cc9f0' : '#4361ee'};
            color: white;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 1000;
            animation: slideInRight 0.3s ease;
        `;

        // Add animation
        const style = document.createElement('style');
        style.textContent = `
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
        `;
        document.head.appendChild(style);

        document.body.appendChild(notification);

        // Auto-remove after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    }

    setCurrentYear() {
        document.getElementById('currentYear').textContent = new Date().getFullYear();
    }
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new TaskManager();
});