// Todo List Application
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const todoInput = document.getElementById('todo-input');
    const addBtn = document.getElementById('add-btn');
    const todosContainer = document.getElementById('todos-container');
    const todoTemplate = document.getElementById('todo-template');
    const filterAllBtn = document.getElementById('filter-all');
    const filterPendingBtn = document.getElementById('filter-pending');
    const filterCompletedBtn = document.getElementById('filter-completed');
    const clearCompletedBtn = document.getElementById('clear-completed');
    const totalCountEl = document.getElementById('total-count');
    const pendingCountEl = document.getElementById('pending-count');
    const completedCountEl = document.getElementById('completed-count');
    const currentYearEl = document.getElementById('current-year');
    
    // State
    let todos = JSON.parse(localStorage.getItem('todos')) || [];
    let currentFilter = 'all';
    
    // Initialize the app
    function init() {
        // Set current year in footer
        currentYearEl.textContent = new Date().getFullYear();
        
        // Load todos from localStorage
        renderTodos();
        updateStats();
        
        // Set up event listeners
        setupEventListeners();
        
        // Focus on input field
        todoInput.focus();
    }
    
    // Set up all event listeners
    function setupEventListeners() {
        // Add todo
        addBtn.addEventListener('click', addTodo);
        todoInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                addTodo();
            }
        });
        
        // Filter buttons
        filterAllBtn.addEventListener('click', () => setFilter('all'));
        filterPendingBtn.addEventListener('click', () => setFilter('pending'));
        filterCompletedBtn.addEventListener('click', () => setFilter('completed'));
        
        // Clear completed todos
        clearCompletedBtn.addEventListener('click', clearCompletedTodos);
    }
    
    // Add a new todo
    function addTodo() {
        const text = todoInput.value.trim();
        
        if (text === '') {
            showMessage('Please enter a task', 'error');
            return;
        }
        
        const newTodo = {
            id: Date.now(),
            text: text,
            completed: false,
            createdAt: new Date().toISOString()
        };
        
        todos.unshift(newTodo);
        saveTodos();
        renderTodos();
        updateStats();
        
        // Clear input and focus
        todoInput.value = '';
        todoInput.focus();
        
        showMessage('Task added successfully!', 'success');
    }
    
    // Render todos based on current filter
    function renderTodos() {
        // Clear container
        todosContainer.innerHTML = '';
        
        // Filter todos
        let filteredTodos = todos;
        if (currentFilter === 'pending') {
            filteredTodos = todos.filter(todo => !todo.completed);
        } else if (currentFilter === 'completed') {
            filteredTodos = todos.filter(todo => todo.completed);
        }
        
        // Show empty state if no todos
        if (filteredTodos.length === 0) {
            const emptyState = document.createElement('div');
            emptyState.className = 'empty-state';
            
            let message = '';
            if (currentFilter === 'all') {
                message = 'Add your first task using the input above';
            } else if (currentFilter === 'pending') {
                message = 'No pending tasks';
            } else if (currentFilter === 'completed') {
                message = 'No completed tasks yet';
            }
            
            emptyState.innerHTML = `
                <i class="fas fa-clipboard-list"></i>
                <h3>No tasks yet</h3>
                <p>${message}</p>
            `;
            
            todosContainer.appendChild(emptyState);
            return;
        }
        
        // Render each todo
        filteredTodos.forEach(todo => {
            const todoElement = createTodoElement(todo);
            todosContainer.appendChild(todoElement);
        });
    }
    
    // Create a todo element from template
    function createTodoElement(todo) {
        const todoClone = todoTemplate.content.cloneNode(true);
        const todoItem = todoClone.querySelector('.todo-item');
        const checkbox = todoClone.querySelector('.todo-status');
        const todoText = todoClone.querySelector('.todo-text');
        const todoDate = todoClone.querySelector('.todo-date');
        const editBtn = todoClone.querySelector('.edit-btn');
        const deleteBtn = todoClone.querySelector('.delete-btn');
        
        // Set todo data
        todoItem.dataset.id = todo.id;
        checkbox.checked = todo.completed;
        todoText.value = todo.text;
        
        // Format date
        const date = new Date(todo.createdAt);
        todoDate.textContent = formatDate(date);
        
        // Apply completed styling
        if (todo.completed) {
            todoItem.classList.add('completed');
            todoText.classList.add('completed');
        }
        
        // Event listeners for this todo
        checkbox.addEventListener('change', () => toggleTodoStatus(todo.id));
        
        editBtn.addEventListener('click', () => {
            if (todoText.readOnly) {
                // Enable editing
                todoText.readOnly = false;
                todoText.focus();
                editBtn.innerHTML = '<i class="fas fa-save"></i>';
                editBtn.title = 'Save task';
            } else {
                // Save changes
                const newText = todoText.value.trim();
                if (newText === '') {
                    showMessage('Task cannot be empty', 'error');
                    todoText.value = todo.text;
                } else {
                    updateTodoText(todo.id, newText);
                    showMessage('Task updated successfully!', 'success');
                }
                todoText.readOnly = true;
                editBtn.innerHTML = '<i class="fas fa-edit"></i>';
                editBtn.title = 'Edit task';
            }
        });
        
        deleteBtn.addEventListener('click', () => deleteTodo(todo.id));
        
        // Save on Enter key, cancel on Escape
        todoText.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && !todoText.readOnly) {
                editBtn.click();
            } else if (e.key === 'Escape' && !todoText.readOnly) {
                todoText.value = todo.text;
                todoText.readOnly = true;
                editBtn.innerHTML = '<i class="fas fa-edit"></i>';
                editBtn.title = 'Edit task';
            }
        });
        
        return todoItem;
    }
    
    // Toggle todo completion status
    function toggleTodoStatus(id) {
        const todoIndex = todos.findIndex(todo => todo.id === id);
        if (todoIndex !== -1) {
            todos[todoIndex].completed = !todos[todoIndex].completed;
            saveTodos();
            renderTodos();
            updateStats();
            
            const status = todos[todoIndex].completed ? 'completed' : 'pending';
            showMessage(`Task marked as ${status}`, 'info');
        }
    }
    
    // Update todo text
    function updateTodoText(id, newText) {
        const todoIndex = todos.findIndex(todo => todo.id === id);
        if (todoIndex !== -1) {
            todos[todoIndex].text = newText;
            saveTodos();
            renderTodos();
        }
    }
    
    // Delete a todo
    function deleteTodo(id) {
        if (confirm('Are you sure you want to delete this task?')) {
            todos = todos.filter(todo => todo.id !== id);
            saveTodos();
            renderTodos();
            updateStats();
            showMessage('Task deleted', 'info');
        }
    }
    
    // Set current filter
    function setFilter(filter) {
        currentFilter = filter;
        
        // Update active filter button
        document.querySelectorAll('.filter-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        
        if (filter === 'all') {
            filterAllBtn.classList.add('active');
        } else if (filter === 'pending') {
            filterPendingBtn.classList.add('active');
        } else if (filter === 'completed') {
            filterCompletedBtn.classList.add('active');
        }
        
        renderTodos();
    }
    
    // Clear all completed todos
    function clearCompletedTodos() {
        const completedCount = todos.filter(todo => todo.completed).length;
        
        if (completedCount === 0) {
            showMessage('No completed tasks to clear', 'info');
            return;
        }
        
        if (confirm(`Are you sure you want to clear ${completedCount} completed task(s)?`)) {
            todos = todos.filter(todo => !todo.completed);
            saveTodos();
            renderTodos();
            updateStats();
            showMessage('Completed tasks cleared', 'success');
        }
    }
    
    // Update statistics
    function updateStats() {
        const total = todos.length;
        const completed = todos.filter(todo => todo.completed).length;
        const pending = total - completed;
        
        totalCountEl.textContent = total;
        pendingCountEl.textContent = pending;
        completedCountEl.textContent = completed;
    }
    
    // Save todos to localStorage
    function saveTodos() {
        localStorage.setItem('todos', JSON.stringify(todos));
    }
    
    // Format date for display
    function formatDate(date) {
        const now = new Date();
        const diffMs = now - date;
        const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
        
        if (diffDays === 0) {
            return 'Today';
        } else if (diffDays === 1) {
            return 'Yesterday';
        } else if (diffDays < 7) {
            return `${diffDays} days ago`;
        } else {
            return date.toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric'
            });
        }
    }
    
    // Show temporary message
    function showMessage(text, type) {
        // Remove existing message if any
        const existingMessage = document.querySelector('.message');
        if (existingMessage) {
            existingMessage.remove();
        }
        
        // Create message element
        const messageEl = document.createElement('div');
        messageEl.className = `message message-${type}`;
        messageEl.textContent = text;
        
        // Style the message
        messageEl.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            border-radius: 8px;
            color: white;
            font-weight: 600;
            z-index: 1000;
            animation: slideIn 0.3s ease;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        `;
        
        // Set background color based on type
        if (type === 'success') {
            messageEl.style.backgroundColor = '#28a745';
        } else if (type === 'error') {
            messageEl.style.backgroundColor = '#dc3545';
        } else {
            messageEl.style.backgroundColor = '#17a2b8';
        }
        
        // Add animation
        const style = document.createElement('style');
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
        `;
        document.head.appendChild(style);
        
        // Add to DOM
        document.body.appendChild(messageEl);
        
        // Remove after 3 seconds
        setTimeout(() => {
            messageEl.style.animation = 'slideOut 0.3s ease';
            
            // Add slideOut animation
            const slideOutStyle = document.createElement('style');
            slideOutStyle.textContent = `
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
            document.head.appendChild(slideOutStyle);
            
            setTimeout(() => {
                messageEl.remove();
                document.head.removeChild(slideOutStyle);
            }, 300);
        }, 3000);
    }
    
    // Initialize the app
    init();
});