// Todo List Application
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const taskInput = document.getElementById('taskInput');
    const addTaskBtn = document.getElementById('addTaskBtn');
    const prioritySelect = document.getElementById('prioritySelect');
    const dueDateInput = document.getElementById('dueDate');
    const taskList = document.getElementById('taskList');
    const taskCount = document.getElementById('taskCount');
    const clearCompletedBtn = document.getElementById('clearCompletedBtn');
    const filterButtons = document.querySelectorAll('.filter-btn');
    const emptyState = document.getElementById('emptyState');
    
    // Set minimum date to today for due date input
    const today = new Date().toISOString().split('T')[0];
    dueDateInput.min = today;
    
    // State
    let currentFilter = 'all';
    let tasks = JSON.parse(localStorage.getItem('todoTasks')) || [];
    
    // Initialize the app
    initApp();
    
    // Event Listeners
    addTaskBtn.addEventListener('click', addTask);
    taskInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            addTask();
        }
    });
    
    clearCompletedBtn.addEventListener('click', clearCompletedTasks);
    
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Update active filter button
            filterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            // Update filter and render tasks
            currentFilter = this.getAttribute('data-filter');
            renderTasks();
        });
    });
    
    // Functions
    function initApp() {
        renderTasks();
        updateTaskCount();
    }
    
    function addTask() {
        const text = taskInput.value.trim();
        if (!text) {
            alert('Please enter a task!');
            taskInput.focus();
            return;
        }
        
        const newTask = {
            id: Date.now(),
            text: text,
            completed: false,
            priority: prioritySelect.value,
            dueDate: dueDateInput.value || null,
            createdAt: new Date().toISOString()
        };
        
        tasks.unshift(newTask);
        saveTasks();
        renderTasks();
        updateTaskCount();
        
        // Reset input fields
        taskInput.value = '';
        taskInput.focus();
        prioritySelect.value = 'medium';
        dueDateInput.value = '';
    }
    
    function renderTasks() {
        // Clear the task list
        taskList.innerHTML = '';
        
        // Filter tasks based on current filter
        let filteredTasks = tasks;
        if (currentFilter === 'active') {
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
    
    function createTaskElement(task) {
        const li = document.createElement('li');
        li.className = `task-item ${task.completed ? 'completed' : ''}`;
        li.setAttribute('data-id', task.id);
        
        // Format due date for display
        let dueDateDisplay = '';
        if (task.dueDate) {
            const dueDate = new Date(task.dueDate);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            
            const timeDiff = dueDate.getTime() - today.getTime();
            const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));
            
            if (daysDiff === 0) {
                dueDateDisplay = 'Today';
            } else if (daysDiff === 1) {
                dueDateDisplay = 'Tomorrow';
            } else if (daysDiff === -1) {
                dueDateDisplay = 'Yesterday';
            } else if (daysDiff < 0) {
                dueDateDisplay = `${Math.abs(daysDiff)} days ago`;
            } else {
                dueDateDisplay = `In ${daysDiff} days`;
            }
        }
        
        li.innerHTML = `
            <input type="checkbox" class="task-checkbox" ${task.completed ? 'checked' : ''}>
            <div class="task-content">
                <div class="task-text ${task.completed ? 'completed' : ''}">${escapeHtml(task.text)}</div>
                <input type="text" class="task-edit-input" value="${escapeHtml(task.text)}" style="display: none;">
                <div class="task-meta">
                    <span class="priority-badge priority-${task.priority}">${task.priority}</span>
                    ${task.dueDate ? `
                        <span class="task-due-date">
                            <i class="far fa-calendar-alt"></i>
                            ${dueDateDisplay}
                        </span>
                    ` : ''}
                </div>
            </div>
            <div class="task-actions">
                <button class="task-action-btn edit-btn" title="Edit task">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="task-action-btn delete-btn" title="Delete task">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </div>
        `;
        
        // Add event listeners to the task elements
        const checkbox = li.querySelector('.task-checkbox');
        const editBtn = li.querySelector('.edit-btn');
        const deleteBtn = li.querySelector('.delete-btn');
        const taskText = li.querySelector('.task-text');
        const editInput = li.querySelector('.task-edit-input');
        
        checkbox.addEventListener('change', function() {
            toggleTaskCompletion(task.id, this.checked);
        });
        
        deleteBtn.addEventListener('click', function() {
            deleteTask(task.id);
        });
        
        // Double-click to edit
        taskText.addEventListener('dblclick', function() {
            enterEditMode(li, task.id);
        });
        
        editBtn.addEventListener('click', function() {
            enterEditMode(li, task.id);
        });
        
        // Handle edit input
        editInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                saveTaskEdit(task.id, this.value);
                exitEditMode(li);
            }
        });
        
        editInput.addEventListener('blur', function() {
            saveTaskEdit(task.id, this.value);
            exitEditMode(li);
        });
        
        return li;
    }
    
    function enterEditMode(taskElement, taskId) {
        const taskText = taskElement.querySelector('.task-text');
        const editInput = taskElement.querySelector('.task-edit-input');
        
        taskText.classList.add('edit-mode');
        editInput.style.display = 'block';
        editInput.focus();
        editInput.select();
    }
    
    function exitEditMode(taskElement) {
        const taskText = taskElement.querySelector('.task-text');
        const editInput = taskElement.querySelector('.task-edit-input');
        
        taskText.classList.remove('edit-mode');
        editInput.style.display = 'none';
    }
    
    function saveTaskEdit(taskId, newText) {
        const trimmedText = newText.trim();
        if (!trimmedText) {
            alert('Task text cannot be empty!');
            return;
        }
        
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        if (taskIndex !== -1) {
            tasks[taskIndex].text = trimmedText;
            saveTasks();
            renderTasks();
        }
    }
    
    function toggleTaskCompletion(taskId, completed) {
        const taskIndex = tasks.findIndex(task => task.id === taskId);
        if (taskIndex !== -1) {
            tasks[taskIndex].completed = completed;
            saveTasks();
            updateTaskCount();
            
            // Re-render only if we're filtering
            if (currentFilter !== 'all') {
                renderTasks();
            } else {
                // Just update the class of the specific task
                const taskElement = document.querySelector(`[data-id="${taskId}"]`);
                if (taskElement) {
                    if (completed) {
                        taskElement.classList.add('completed');
                        taskElement.querySelector('.task-text').classList.add('completed');
                    } else {
                        taskElement.classList.remove('completed');
                        taskElement.querySelector('.task-text').classList.remove('completed');
                    }
                }
            }
        }
    }
    
    function deleteTask(taskId) {
        // Add fade-out animation
        const taskElement = document.querySelector(`[data-id="${taskId}"]`);
        if (taskElement) {
            taskElement.style.animation = 'fadeOut 0.3s ease-out forwards';
            
            setTimeout(() => {
                tasks = tasks.filter(task => task.id !== taskId);
                saveTasks();
                renderTasks();
                updateTaskCount();
            }, 300);
        }
    }
    
    function clearCompletedTasks() {
        if (!confirm('Are you sure you want to clear all completed tasks?')) {
            return;
        }
        
        tasks = tasks.filter(task => !task.completed);
        saveTasks();
        renderTasks();
        updateTaskCount();
    }
    
    function updateTaskCount() {
        const activeTasks = tasks.filter(task => !task.completed).length;
        const totalTasks = tasks.length;
        
        if (totalTasks === 0) {
            taskCount.textContent = 'No tasks';
        } else if (activeTasks === 0) {
            taskCount.textContent = 'All tasks completed!';
        } else {
            taskCount.textContent = `${activeTasks} of ${totalTasks} tasks remaining`;
        }
    }
    
    function saveTasks() {
        localStorage.setItem('todoTasks', JSON.stringify(tasks));
    }
    
    // Helper function to prevent XSS
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});