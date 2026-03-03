// Todo List Application
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const todoInput = document.getElementById('todoInput');
    const addBtn = document.getElementById('addBtn');
    const todoList = document.getElementById('todoList');
    const emptyState = document.getElementById('emptyState');
    const filterBtns = document.querySelectorAll('.filter-btn');
    const clearCompletedBtn = document.getElementById('clearCompletedBtn');
    const clearAllBtn = document.getElementById('clearAllBtn');
    const totalTasksEl = document.getElementById('totalTasks');
    const activeTasksEl = document.getElementById('activeTasks');
    const completedTasksEl = document.getElementById('completedTasks');
    
    // State variables
    let todos = JSON.parse(localStorage.getItem('todos')) || [];
    let currentFilter = 'all';
    let dragSrcEl = null;
    
    // Initialize the app
    function init() {
        renderTodos();
        updateStats();
        setupEventListeners();
    }
    
    // Set up event listeners
    function setupEventListeners() {
        // Add todo
        addBtn.addEventListener('click', addTodo);
        todoInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                addTodo();
            }
        });
        
        // Filter buttons
        filterBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                // Update active state
                filterBtns.forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                
                // Update filter and render
                currentFilter = this.dataset.filter;
                renderTodos();
            });
        });
        
        // Clear buttons
        clearCompletedBtn.addEventListener('click', clearCompletedTodos);
        clearAllBtn.addEventListener('click', clearAllTodos);
    }
    
    // Add a new todo
    function addTodo() {
        const text = todoInput.value.trim();
        
        if (text === '') {
            showNotification('Please enter a task', 'error');
            return;
        }
        
        const newTodo = {
            id: Date.now(),
            text: text,
            completed: false,
            createdAt: new Date().toISOString()
        };
        
        todos.push(newTodo);
        saveTodos();
        renderTodos();
        updateStats();
        
        // Clear input and focus
        todoInput.value = '';
        todoInput.focus();
        
        showNotification('Task added successfully', 'success');
    }
    
    // Toggle todo completion status
    function toggleTodo(id) {
        todos = todos.map(todo => {
            if (todo.id === id) {
                return { ...todo, completed: !todo.completed };
            }
            return todo;
        });
        
        saveTodos();
        renderTodos();
        updateStats();
    }
    
    // Edit todo text
    function editTodo(id, newText) {
        if (newText.trim() === '') {
            deleteTodo(id);
            return;
        }
        
        todos = todos.map(todo => {
            if (todo.id === id) {
                return { ...todo, text: newText.trim() };
            }
            return todo;
        });
        
        saveTodos();
        renderTodos();
        showNotification('Task updated', 'success');
    }
    
    // Delete a todo
    function deleteTodo(id) {
        todos = todos.filter(todo => todo.id !== id);
        saveTodos();
        renderTodos();
        updateStats();
        showNotification('Task deleted', 'info');
    }
    
    // Clear all completed todos
    function clearCompletedTodos() {
        if (!todos.some(todo => todo.completed)) {
            showNotification('No completed tasks to clear', 'info');
            return;
        }
        
        todos = todos.filter(todo => !todo.completed);
        saveTodos();
        renderTodos();
        updateStats();
        showNotification('Completed tasks cleared', 'success');
    }
    
    // Clear all todos
    function clearAllTodos() {
        if (todos.length === 0) {
            showNotification('No tasks to clear', 'info');
            return;
        }
        
        if (confirm('Are you sure you want to delete all tasks?')) {
            todos = [];
            saveTodos();
            renderTodos();
            updateStats();
            showNotification('All tasks cleared', 'success');
        }
    }
    
    // Filter todos based on current filter
    function getFilteredTodos() {
        switch (currentFilter) {
            case 'active':
                return todos.filter(todo => !todo.completed);
            case 'completed':
                return todos.filter(todo => todo.completed);
            default:
                return todos;
        }
    }
    
    // Render todos to the DOM
    function renderTodos() {
        const filteredTodos = getFilteredTodos();
        
        // Show/hide empty state
        if (filteredTodos.length === 0) {
            emptyState.style.display = 'block';
            todoList.style.display = 'none';
        } else {
            emptyState.style.display = 'none';
            todoList.style.display = 'block';
        }
        
        // Clear current list
        todoList.innerHTML = '';
        
        // Add todos to the list
        filteredTodos.forEach(todo => {
            const todoItem = createTodoElement(todo);
            todoList.appendChild(todoItem);
        });
        
        // Setup drag and drop if there are multiple items
        if (filteredTodos.length > 1) {
            setupDragAndDrop();
        }
    }
    
    // Create a todo element
    function createTodoElement(todo) {
        const li = document.createElement('li');
        li.className = `todo-item ${todo.completed ? 'completed' : ''}`;
        li.dataset.id = todo.id;
        li.draggable = true;
        
        // Checkbox
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.className = 'todo-checkbox';
        checkbox.checked = todo.completed;
        checkbox.addEventListener('change', () => toggleTodo(todo.id));
        
        // Todo text (or edit input)
        const textSpan = document.createElement('span');
        textSpan.className = 'todo-text';
        textSpan.textContent = todo.text;
        
        // Make text editable on double click
        textSpan.addEventListener('dblclick', function() {
            const editInput = document.createElement('input');
            editInput.type = 'text';
            editInput.className = 'todo-edit-input';
            editInput.value = todo.text;
            
            // Replace text with input
            li.replaceChild(editInput, textSpan);
            editInput.focus();
            editInput.select();
            
            // Handle edit completion
            function finishEdit() {
                const newText = editInput.value.trim();
                editTodo(todo.id, newText);
            }
            
            editInput.addEventListener('blur', finishEdit);
            editInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    finishEdit();
                }
            });
        });
        
        // Actions container
        const actionsDiv = document.createElement('div');
        actionsDiv.className = 'todo-actions';
        
        // Edit button
        const editBtn = document.createElement('button');
        editBtn.innerHTML = '<i class="fas fa-edit"></i>';
        editBtn.title = 'Edit task';
        editBtn.addEventListener('click', () => {
            textSpan.dispatchEvent(new Event('dblclick'));
        });
        
        // Delete button
        const deleteBtn = document.createElement('button');
        deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
        deleteBtn.title = 'Delete task';
        deleteBtn.addEventListener('click', () => deleteTodo(todo.id));
        
        // Assemble the element
        actionsDiv.appendChild(editBtn);
        actionsDiv.appendChild(deleteBtn);
        
        li.appendChild(checkbox);
        li.appendChild(textSpan);
        li.appendChild(actionsDiv);
        
        return li;
    }
    
    // Update statistics
    function updateStats() {
        const total = todos.length;
        const completed = todos.filter(todo => todo.completed).length;
        const active = total - completed;
        
        totalTasksEl.textContent = total;
        activeTasksEl.textContent = active;
        completedTasksEl.textContent = completed;
    }
    
    // Save todos to localStorage
    function saveTodos() {
        localStorage.setItem('todos', JSON.stringify(todos));
    }
    
    // Drag and drop functionality
    function setupDragAndDrop() {
        const items = document.querySelectorAll('.todo-item');
        
        items.forEach(item => {
            item.addEventListener('dragstart', handleDragStart);
            item.addEventListener('dragover', handleDragOver);
            item.addEventListener('drop', handleDrop);
            item.addEventListener('dragend', handleDragEnd);
        });
    }
    
    function handleDragStart(e) {
        dragSrcEl = this;
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('text/html', this.innerHTML);
        this.style.opacity = '0.4';
    }
    
    function handleDragOver(e) {
        if (e.preventDefault) {
            e.preventDefault();
        }
        e.dataTransfer.dropEffect = 'move';
        return false;
    }
    
    function handleDrop(e) {
        if (e.stopPropagation) {
            e.stopPropagation();
        }
        
        if (dragSrcEl !== this) {
            // Get the IDs of the dragged and target items
            const draggedId = parseInt(dragSrcEl.dataset.id);
            const targetId = parseInt(this.dataset.id);
            
            // Find indices in the todos array
            const draggedIndex = todos.findIndex(todo => todo.id === draggedId);
            const targetIndex = todos.findIndex(todo => todo.id === targetId);
            
            // Reorder the array
            if (draggedIndex !== -1 && targetIndex !== -1) {
                const [draggedTodo] = todos.splice(draggedIndex, 1);
                todos.splice(targetIndex, 0, draggedTodo);
                
                saveTodos();
                renderTodos();
                showNotification('Task reordered', 'success');
            }
        }
        
        return false;
    }
    
    function handleDragEnd() {
        this.style.opacity = '1';
        const items = document.querySelectorAll('.todo-item');
        items.forEach(item => {
            item.classList.remove('over');
        });
    }
    
    // Show notification
    function showNotification(message, type) {
        // Remove existing notification
        const existingNotification = document.querySelector('.notification');
        if (existingNotification) {
            existingNotification.remove();
        }
        
        // Create notification
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        
        // Add styles
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            border-radius: 8px;
            color: white;
            font-weight: 600;
            z-index: 1000;
            animation: slideIn 0.3s ease-out;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        `;
        
        // Set background color based on type
        const colors = {
            success: '#28a745',
            error: '#dc3545',
            info: '#17a2b8',
            warning: '#ffc107'
        };
        
        notification.style.backgroundColor = colors[type] || colors.info;
        
        // Add to DOM
        document.body.appendChild(notification);
        
        // Remove after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 300);
        }, 3000);
        
        // Add keyframe animations
        if (!document.querySelector('#notification-styles')) {
            const style = document.createElement('style');
            style.id = 'notification-styles';
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