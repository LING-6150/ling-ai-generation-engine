// To-Do List Application
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const taskInput = document.getElementById('taskInput');
    const addTaskBtn = document.getElementById('addTaskBtn');
    const taskList = document.getElementById('taskList');
    const emptyState = document.getElementById('emptyState');
    const filterBtns = document.querySelectorAll('.filter-btn');
    const clearCompletedBtn = document.getElementById('clearCompletedBtn');
    const clearAllBtn = document.getElementById('clearAllBtn');
    
    // Stats elements
    const totalTasksEl = document.getElementById('totalTasks');
    const pendingTasksEl = document.getElementById('pendingTasks');
    const completedTasksEl = document.getElementById('completedTasks');
    
    // State variables
    let tasks = [];
    let currentFilter = 'all';
    
    // Initialize the app
    function init() {
        loadTasks();
        renderTasks();
        updateStats();
        setupEventListeners();
    }
    
    // Load tasks from localStorage
    function loadTasks() {
        const savedTasks = localStorage.getItem('todoTasks');
        if (savedTasks) {
            tasks = JSON.parse(savedTasks);
        }
    }
    
    // Save tasks to localStorage
    function saveTasks() {
        localStorage.setItem('todoTasks', JSON.stringify(tasks));
    }
    
    // Update statistics
    function updateStats() {
        const total = tasks.length;
        const completed = tasks.filter(task => task.completed).length;
        const pending = total - completed;
        
        totalTasksEl.textContent = total;
        completedTasksEl.textContent = completed;
        pendingTasksEl.textContent = pending;
    }
    
    // Render tasks based on current filter
    function renderTasks() {
        // Clear the task list
        taskList.innerHTML = '';
        
        // Filter tasks based on current filter
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
            
            // Create task items
            filteredTasks.forEach((task, index) => {
                const taskItem = createTaskElement(task, index);
                taskList.appendChild(taskItem);
            });
        }
    }
    
    // Create a task element
    function createTaskElement(task, originalIndex) {
        const li = document.createElement('li');
        li.className = `task-item ${task.completed ? 'completed' : ''}`;
        li.dataset.index = originalIndex;
        
        // Checkbox
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.className = 'task-checkbox';
        checkbox.checked = task.completed;
        
        // Task text
        const taskText = document.createElement('span');
        taskText.className = 'task-text';
        taskText.textContent = task.text;
        
        // Delete button
        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'task-delete';
        deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
        
        // Assemble the task item
        li.appendChild(checkbox);
        li.appendChild(taskText);
        li.appendChild(deleteBtn);
        
        return li;
    }
    
    // Add a new task
    function addTask() {
        const text = taskInput.value.trim();
        
        if (text === '') {
            alert('Please enter a task');
            return;
        }
        
        // Create new task object
        const newTask = {
            id: Date.now(),
            text: text,
            completed: false,
            createdAt: new Date().toISOString()
        };
        
        // Add to tasks array
        tasks.push(newTask);
        
        // Save, update UI, and clear input
        saveTasks();
        renderTasks();
        updateStats();
        taskInput.value = '';
        taskInput.focus();
    }
    
    // Toggle task completion
    function toggleTaskCompletion(index) {
        tasks[index].completed = !tasks[index].completed;
        saveTasks();
        renderTasks();
        updateStats();
    }
    
    // Delete a task
    function deleteTask(index, event) {
        // Prevent event bubbling to avoid toggling completion
        event.stopPropagation();
        
        if (confirm('Are you sure you want to delete this task?')) {
            tasks.splice(index, 1);
            saveTasks();
            renderTasks();
            updateStats();
        }
    }
    
    // Clear all completed tasks
    function clearCompletedTasks() {
        const completedCount = tasks.filter(task => task.completed).length;
        
        if (completedCount === 0) {
            alert('No completed tasks to clear');
            return;
        }
        
        if (confirm(`Clear ${completedCount} completed task(s)?`)) {
            tasks = tasks.filter(task => !task.completed);
            saveTasks();
            renderTasks();
            updateStats();
        }
    }
    
    // Clear all tasks
    function clearAllTasks() {
        if (tasks.length === 0) {
            alert('No tasks to clear');
            return;
        }
        
        if (confirm('Clear all tasks? This action cannot be undone.')) {
            tasks = [];
            saveTasks();
            renderTasks();
            updateStats();
        }
    }
    
    // Change filter
    function changeFilter(filter) {
        currentFilter = filter;
        
        // Update active filter button
        filterBtns.forEach(btn => {
            if (btn.dataset.filter === filter) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
        
        renderTasks();
    }
    
    // Set up event listeners
    function setupEventListeners() {
        // Add task button click
        addTaskBtn.addEventListener('click', addTask);
        
        // Add task on Enter key
        taskInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                addTask();
            }
        });
        
        // Task list events (delegation)
        taskList.addEventListener('click', function(e) {
            const taskItem = e.target.closest('.task-item');
            if (!taskItem) return;
            
            const index = parseInt(taskItem.dataset.index);
            
            // Check if delete button was clicked
            if (e.target.closest('.task-delete')) {
                deleteTask(index, e);
                return;
            }
            
            // Otherwise toggle completion
            toggleTaskCompletion(index);
        });
        
        // Filter buttons
        filterBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                changeFilter(this.dataset.filter);
            });
        });
        
        // Clear buttons
        clearCompletedBtn.addEventListener('click', clearCompletedTasks);
        clearAllBtn.addEventListener('click', clearAllTasks);
    }
    
    // Initialize the application
    init();
});