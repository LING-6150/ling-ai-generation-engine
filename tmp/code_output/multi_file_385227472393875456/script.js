// Todo List Application
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const taskInput = document.getElementById('taskInput');
    const addTaskBtn = document.getElementById('addTaskBtn');
    const tasksList = document.getElementById('tasksList');
    const clearCompletedBtn = document.getElementById('clearCompletedBtn');
    const taskTemplate = document.getElementById('taskTemplate');
    
    // Stats elements
    const totalTasksEl = document.getElementById('totalTasks');
    const completedTasksEl = document.getElementById('completedTasks');
    const pendingTasksEl = document.getElementById('pendingTasks');
    
    // Tasks array
    let tasks = [];
    
    // Initialize the app
    function init() {
        loadTasksFromStorage();
        renderTasks();
        updateStats();
        
        // Set up event listeners
        addTaskBtn.addEventListener('click', addTask);
        taskInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                addTask();
            }
        });
        
        clearCompletedBtn.addEventListener('click', clearCompletedTasks);
        
        // Focus on input field
        taskInput.focus();
    }
    
    // Load tasks from localStorage
    function loadTasksFromStorage() {
        const storedTasks = localStorage.getItem('todoTasks');
        if (storedTasks) {
            try {
                tasks = JSON.parse(storedTasks);
            } catch (e) {
                console.error('Error parsing tasks from localStorage:', e);
                tasks = [];
            }
        }
    }
    
    // Save tasks to localStorage
    function saveTasksToStorage() {
        localStorage.setItem('todoTasks', JSON.stringify(tasks));
    }
    
    // Add a new task
    function addTask() {
        const taskText = taskInput.value.trim();
        
        if (taskText === '') {
            showNotification('Please enter a task', 'error');
            taskInput.focus();
            return;
        }
        
        // Create new task object
        const newTask = {
            id: Date.now(),
            text: taskText,
            completed: false,
            createdAt: new Date().toISOString()
        };
        
        // Add to tasks array
        tasks.unshift(newTask);
        
        // Save and update UI
        saveTasksToStorage();
        renderTasks();
        updateStats();
        
        // Clear input and focus
        taskInput.value = '';
        taskInput.focus();
        
        // Show success notification
        showNotification('Task added successfully', 'success');
    }
    
    // Toggle task completion
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
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        
        if (taskIndex !== -1) {
            const taskText = tasks[taskIndex].text;
            tasks.splice(taskIndex, 1);
            saveTasksToStorage();
            renderTasks();
            updateStats();
            
            showNotification(`Task "${taskText.substring(0, 20)}..." deleted`, 'info');
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
            
            showNotification(`Cleared ${completedCount} completed task(s)`, 'success');
        }
    }
    
    // Render all tasks
    function renderTasks() {
        // Clear the tasks list
        tasksList.innerHTML = '';
        
        // If no tasks, show empty state
        if (tasks.length === 0) {
            const emptyState = document.createElement('div');
            emptyState.className = 'empty-state';
            emptyState.innerHTML = `
                <i class="fas fa-clipboard-list"></i>
                <h3>No tasks yet</h3>
                <p>Add your first task using the input above</p>
            `;
            tasksList.appendChild(emptyState);
            return;
        }
        
        // Render each task
        tasks.forEach(task => {
            const taskElement = createTaskElement(task);
            tasksList.appendChild(taskElement);
        });
    }
    
    // Create a task element from template
    function createTaskElement(task) {
        const taskClone = taskTemplate.content.cloneNode(true);
        const taskItem = taskClone.querySelector('.task-item');
        const taskText = taskClone.querySelector('.task-text');
        const taskDate = taskClone.querySelector('.task-date');
        const taskCheckbox = taskClone.querySelector('.task-complete');
        const deleteBtn = taskClone.querySelector('.delete-task');
        
        // Set task content
        taskText.textContent = task.text;
        
        // Format date
        const date = new Date(task.createdAt);
        const formattedDate = date.toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        taskDate.textContent = `Added: ${formattedDate}`;
        
        // Set completion state
        taskCheckbox.checked = task.completed;
        if (task.completed) {
            taskItem.classList.add('completed');
        }
        
        // Add event listeners
        taskCheckbox.addEventListener('change', () => toggleTaskCompletion(task.id));
        
        // Double-click to toggle completion
        taskItem.addEventListener('dblclick', () => toggleTaskCompletion(task.id));
        
        deleteBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            deleteTask(task.id);
        });
        
        return taskItem;
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
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        
        // Add to DOM
        document.body.appendChild(notification);
        
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
            animation: slideIn 0.3s ease-out;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        `;
        
        // Set background color based on type
        const bgColors = {
            success: '#4CAF50',
            error: '#ff4757',
            info: '#3498db'
        };
        
        notification.style.backgroundColor = bgColors[type] || '#3498db';
        
        // Remove notification after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 300);
        }, 3000);
        
        // Add CSS animations if not already present
        if (!document.querySelector('#notificationStyles')) {
            const style = document.createElement('style');
            style.id = 'notificationStyles';
            style.textContent = `
                @keyframes slideIn {
                    from {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
                
                @keyframes slideOut {
                    from {
                        transform: translateX(0);
                        opacity: 1;
                    }
                    to {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                }
            `;
            document.head.appendChild(style);
        }
    }
    
    // Initialize the application
    init();
});