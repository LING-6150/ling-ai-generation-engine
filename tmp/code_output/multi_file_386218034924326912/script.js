// Todo List Application
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const todoInput = document.getElementById('todo-input');
    const addBtn = document.getElementById('add-btn');
    const tasksList = document.getElementById('tasks-list');
    const filterBtns = document.querySelectorAll('.filter-btn');
    const clearCompletedBtn = document.getElementById('clear-completed');
    const totalTasksEl = document.getElementById('total-tasks');
    const completedTasksEl = document.getElementById('completed-tasks');
    const pendingTasksEl = document.getElementById('pending-tasks');
    
    // State
    let tasks = JSON.parse(localStorage.getItem('todoTasks')) || [];
    let currentFilter = 'all';
    
    // Initialize the app
    initApp();
    
    // Initialize the application
    function initApp() {
        renderTasks();
        updateStats();
        setupEventListeners();
    }
    
    // Set up event listeners
    function setupEventListeners() {
        // Add task button click
        addBtn.addEventListener('click', addTask);
        
        // Add task on Enter key press
        todoInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                addTask();
            }
        });
        
        // Filter buttons
        filterBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                // Remove active class from all filter buttons
                filterBtns.forEach(b => b.classList.remove('active'));
                // Add active class to clicked button
                this.classList.add('active');
                // Set current filter
                currentFilter = this.getAttribute('data-filter');
                // Render tasks with new filter
                renderTasks();
            });
        });
        
        // Clear completed tasks button
        clearCompletedBtn.addEventListener('click', clearCompletedTasks);
    }
    
    // Add a new task
    function addTask() {
        const taskText = todoInput.value.trim();
        
        if (taskText === '') {
            showNotification('Please enter a task', 'error');
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
        tasks.push(newTask);
        
        // Save to localStorage
        saveTasks();
        
        // Clear input
        todoInput.value = '';
        
        // Render tasks and update stats
        renderTasks();
        updateStats();
        
        // Show success notification
        showNotification('Task added successfully', 'success');
        
        // Focus back on input
        todoInput.focus();
    }
    
    // Toggle task completion status
    function toggleTaskCompletion(taskId) {
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        
        if (taskIndex !== -1) {
            tasks[taskIndex].completed = !tasks[taskIndex].completed;
            saveTasks();
            renderTasks();
            updateStats();
            
            const status = tasks[taskIndex].completed ? 'completed' : 'marked as pending';
            showNotification(`Task ${status}`, 'info');
        }
    }
    
    // Delete a task
    function deleteTask(taskId) {
        const taskText = tasks.find(task => task.id === taskId)?.text || '';
        
        if (confirm(`Are you sure you want to delete "${taskText}"?`)) {
            tasks = tasks.filter(task => task.id !== taskId);
            saveTasks();
            renderTasks();
            updateStats();
            showNotification('Task deleted', 'info');
        }
    }
    
    // Edit a task
    function editTask(taskId) {
        const taskItem = document.querySelector(`[data-id="${taskId}"]`);
        const taskTextEl = taskItem.querySelector('.task-text');
        const currentText = tasks.find(task => task.id === taskId).text;
        
        // Create input for editing
        const editInput = document.createElement('input');
        editInput.type = 'text';
        editInput.value = currentText;
        editInput.className = 'edit-input';
        editInput.style.cssText = `
            flex: 1;
            padding: 8px;
            border: 2px solid #2575fc;
            border-radius: 4px;
            font-size: 1.1rem;
            margin-right: 10px;
        `;
        
        // Replace text with input
        taskTextEl.replaceWith(editInput);
        editInput.focus();
        editInput.select();
        
        // Save on Enter or blur
        const saveEdit = () => {
            const newText = editInput.value.trim();
            
            if (newText === '') {
                showNotification('Task cannot be empty', 'error');
                editInput.focus();
                return;
            }
            
            // Update task in array
            const taskIndex = tasks.findIndex(task => task.id === taskId);
            if (taskIndex !== -1) {
                tasks[taskIndex].text = newText;
                saveTasks();
                renderTasks();
                showNotification('Task updated', 'success');
            }
        };
        
        editInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                saveEdit();
            }
        });
        
        editInput.addEventListener('blur', saveEdit);
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
            saveTasks();
            renderTasks();
            updateStats();
            showNotification('Completed tasks cleared', 'success');
        }
    }
    
    // Render tasks based on current filter
    function renderTasks() {
        // Clear the tasks list
        tasksList.innerHTML = '';
        
        // Filter tasks based on current filter
        let filteredTasks = tasks;
        
        if (currentFilter === 'pending') {
            filteredTasks = tasks.filter(task => !task.completed);
        } else if (currentFilter === 'completed') {
            filteredTasks = tasks.filter(task => task.completed);
        }
        
        // If no tasks, show empty state
        if (filteredTasks.length === 0) {
            const emptyState = document.createElement('li');
            emptyState.className = 'empty-state';
            emptyState.innerHTML = `
                <i class="fas fa-clipboard-list"></i>
                <p>No ${currentFilter !== 'all' ? currentFilter : ''} tasks found.</p>
            `;
            tasksList.appendChild(emptyState);
            return;
        }
        
        // Render each task
        filteredTasks.forEach(task => {
            const taskItem = document.createElement('li');
            taskItem.className = 'task-item';
            taskItem.setAttribute('data-id', task.id);
            
            taskItem.innerHTML = `
                <input 
                    type="checkbox" 
                    class="task-checkbox" 
                    ${task.completed ? 'checked' : ''}
                >
                <span class="task-text ${task.completed ? 'completed' : ''}">
                    ${escapeHtml(task.text)}
                </span>
                <div class="task-actions">
                    <button class="task-action-btn edit-btn" title="Edit task">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="task-action-btn delete-btn" title="Delete task">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            `;
            
            // Add event listeners for this task
            const checkbox = taskItem.querySelector('.task-checkbox');
            const editBtn = taskItem.querySelector('.edit-btn');
            const deleteBtn = taskItem.querySelector('.delete-btn');
            const taskText = taskItem.querySelector('.task-text');
            
            // Toggle completion on checkbox click
            checkbox.addEventListener('click', () => toggleTaskCompletion(task.id));
            
            // Edit on button click
            editBtn.addEventListener('click', () => editTask(task.id));
            
            // Edit on double-click task text
            taskText.addEventListener('dblclick', () => editTask(task.id));
            
            // Delete on button click
            deleteBtn.addEventListener('click', () => deleteTask(task.id));
            
            tasksList.appendChild(taskItem);
        });
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
    
    // Save tasks to localStorage
    function saveTasks() {
        localStorage.setItem('todoTasks', JSON.stringify(tasks));
    }
    
    // Show notification (simple alert alternative)
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
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            border-radius: 8px;
            color: white;
            font-weight: 600;
            z-index: 1000;
            animation: fadeIn 0.3s ease;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        `;
        
        // Set background color based on type
        if (type === 'success') {
            notification.style.backgroundColor = '#40c057';
        } else if (type === 'error') {
            notification.style.backgroundColor = '#fa5252';
        } else {
            notification.style.backgroundColor = '#228be6';
        }
        
        // Add to DOM
        document.body.appendChild(notification);
        
        // Remove after 3 seconds
        setTimeout(() => {
            notification.style.opacity = '0';
            notification.style.transition = 'opacity 0.5s ease';
            setTimeout(() => notification.remove(), 500);
        }, 3000);
    }
    
    // Helper function to escape HTML (prevent XSS)
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});