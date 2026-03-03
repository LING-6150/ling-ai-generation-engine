// Todo List Application
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const taskInput = document.getElementById('taskInput');
    const addTaskBtn = document.getElementById('addTaskBtn');
    const taskList = document.getElementById('taskList');
    const emptyState = document.getElementById('emptyState');
    const filterButtons = document.querySelectorAll('.filter-btn');
    const clearCompletedBtn = document.getElementById('clearCompletedBtn');
    const totalTasksEl = document.getElementById('totalTasks');
    const completedTasksEl = document.getElementById('completedTasks');
    const pendingTasksEl = document.getElementById('pendingTasks');
    
    // State
    let tasks = [];
    let currentFilter = 'all';
    
    // Initialize the app
    function init() {
        loadTasksFromStorage();
        renderTasks();
        updateStats();
        setupEventListeners();
    }
    
    // Load tasks from localStorage
    function loadTasksFromStorage() {
        const storedTasks = localStorage.getItem('todoTasks');
        if (storedTasks) {
            tasks = JSON.parse(storedTasks);
        }
    }
    
    // Save tasks to localStorage
    function saveTasksToStorage() {
        localStorage.setItem('todoTasks', JSON.stringify(tasks));
    }
    
    // Set up event listeners
    function setupEventListeners() {
        // Add task button click
        addTaskBtn.addEventListener('click', addTask);
        
        // Add task on Enter key press
        taskInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                addTask();
            }
        });
        
        // Filter buttons
        filterButtons.forEach(button => {
            button.addEventListener('click', function() {
                // Remove active class from all buttons
                filterButtons.forEach(btn => btn.classList.remove('active'));
                // Add active class to clicked button
                this.classList.add('active');
                // Update filter and re-render
                currentFilter = this.dataset.filter;
                renderTasks();
            });
        });
        
        // Clear completed tasks button
        clearCompletedBtn.addEventListener('click', clearCompletedTasks);
    }
    
    // Add a new task
    function addTask() {
        const taskText = taskInput.value.trim();
        
        if (taskText === '') {
            showNotification('Please enter a task', 'error');
            return;
        }
        
        // Create new task object
        const newTask = {
            id: Date.now(), // Simple unique ID
            text: taskText,
            completed: false,
            createdAt: new Date().toISOString()
        };
        
        // Add to tasks array
        tasks.unshift(newTask);
        
        // Clear input
        taskInput.value = '';
        
        // Save, render, and update stats
        saveTasksToStorage();
        renderTasks();
        updateStats();
        
        // Show success notification
        showNotification('Task added successfully', 'success');
        
        // Focus back on input
        taskInput.focus();
    }
    
    // Toggle task completion status
    function toggleTaskCompletion(taskId) {
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        
        if (taskIndex !== -1) {
            tasks[taskIndex].completed = !tasks[taskIndex].completed;
            saveTasksToStorage();
            renderTasks();
            updateStats();
            
            const status = tasks[taskIndex].completed ? 'completed' : 'pending';
            showNotification(`Task marked as ${status}`, 'info');
        }
    }
    
    // Delete a task
    function deleteTask(taskId) {
        // Find task index
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        
        if (taskIndex !== -1) {
            // Remove task with animation
            const taskElement = document.querySelector(`[data-task-id="${taskId}"]`);
            if (taskElement) {
                taskElement.style.animation = 'fadeOut 0.3s ease-out forwards';
                
                setTimeout(() => {
                    tasks.splice(taskIndex, 1);
                    saveTasksToStorage();
                    renderTasks();
                    updateStats();
                    showNotification('Task deleted', 'info');
                }, 300);
            }
        }
    }
    
    // Edit a task
    function editTask(taskId) {
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        
        if (taskIndex !== -1) {
            const currentText = tasks[taskIndex].text;
            const newText = prompt('Edit your task:', currentText);
            
            if (newText !== null && newText.trim() !== '') {
                tasks[taskIndex].text = newText.trim();
                saveTasksToStorage();
                renderTasks();
                showNotification('Task updated', 'success');
            }
        }
    }
    
    // Clear all completed tasks
    function clearCompletedTasks() {
        const completedCount = tasks.filter(task => task.completed).length;
        
        if (completedCount === 0) {
            showNotification('No completed tasks to clear', 'info');
            return;
        }
        
        if (confirm(`Are you sure you want to clear ${completedCount} completed task(s)?`)) {
            tasks = tasks.filter(task => !task.completed);
            saveTasksToStorage();
            renderTasks();
            updateStats();
            showNotification('Completed tasks cleared', 'success');
        }
    }
    
    // Render tasks based on current filter
    function renderTasks() {
        // Clear current task list
        taskList.innerHTML = '';
        
        // Filter tasks based on current selection
        let filteredTasks = tasks;
        
        if (currentFilter === 'pending') {
            filteredTasks = tasks.filter(task => !task.completed);
        } else if (currentFilter === 'completed') {
            filteredTasks = tasks.filter(task => task.completed);
        }
        
        // Show/hide empty state
        if (filteredTasks.length === 0) {
            emptyState.style.display = 'block';
        } else {
            emptyState.style.display = 'none';
            
            // Render each task
            filteredTasks.forEach(task => {
                const taskItem = createTaskElement(task);
                taskList.appendChild(taskItem);
            });
        }
    }
    
    // Create a task element
    function createTaskElement(task) {
        const li = document.createElement('li');
        li.className = `task-item ${task.completed ? 'completed' : ''}`;
        li.dataset.taskId = task.id;
        
        li.innerHTML = `
            <input 
                type="checkbox" 
                class="task-checkbox" 
                ${task.completed ? 'checked' : ''}
            >
            <div class="task-text">${escapeHtml(task.text)}</div>
            <div class="task-actions">
                <button class="task-action-btn edit-btn" title="Edit task">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="task-action-btn delete-btn" title="Delete task">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        `;
        
        // Add event listeners to the task
        const checkbox = li.querySelector('.task-checkbox');
        const editBtn = li.querySelector('.edit-btn');
        const deleteBtn = li.querySelector('.delete-btn');
        const taskText = li.querySelector('.task-text');
        
        // Toggle completion on checkbox click
        checkbox.addEventListener('click', () => toggleTaskCompletion(task.id));
        
        // Edit on button click
        editBtn.addEventListener('click', () => editTask(task.id));
        
        // Edit on double-click task text
        taskText.addEventListener('dblclick', () => editTask(task.id));
        
        // Delete on button click
        deleteBtn.addEventListener('click', () => deleteTask(task.id));
        
        return li;
    }
    
    // Update task statistics
    function updateStats() {
        const total = tasks.length;
        const completed = tasks.filter(task => task.completed).length;
        const pending = total - completed;
        
        totalTasksEl.textContent = `Total: ${total}`;
        completedTasksEl.textContent = `Completed: ${completed}`;
        pendingTasksEl.textContent = `Pending: ${pending}`;
    }
    
    // Show notification
    function showNotification(message, type) {
        // Remove existing notification if any
        const existingNotification = document.querySelector('.notification');
        if (existingNotification) {
            existingNotification.remove();
        }
        
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;
        
        // Style the notification
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            border-radius: 10px;
            color: white;
            font-weight: 600;
            z-index: 1000;
            animation: fadeIn 0.3s ease-out;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        `;
        
        // Set background color based on type
        const bgColors = {
            success: '#2ecc71',
            error: '#e74c3c',
            info: '#3498db'
        };
        
        notification.style.backgroundColor = bgColors[type] || '#3498db';
        
        // Add to DOM
        document.body.appendChild(notification);
        
        // Remove after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'fadeOut 0.3s ease-out forwards';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    }
    
    // Helper function to escape HTML (prevent XSS)
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
    
    // Initialize the app
    init();
});