// Todo List Application
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const todoInput = document.getElementById('todo-input');
    const addBtn = document.getElementById('add-btn');
    const todoList = document.getElementById('todo-list');
    const totalTasksEl = document.getElementById('total-tasks');
    const completedTasksEl = document.getElementById('completed-tasks');
    const filterBtns = document.querySelectorAll('.filter-btn');
    const clearCompletedBtn = document.getElementById('clear-completed');
    const clearAllBtn = document.getElementById('clear-all');
    
    // State
    let todos = JSON.parse(localStorage.getItem('todos')) || [];
    let currentFilter = 'all';
    
    // Initialize the app
    initApp();
    
    // Initialize the application
    function initApp() {
        renderTodos();
        updateStats();
        setupEventListeners();
    }
    
    // Set up event listeners
    function setupEventListeners() {
        // Add todo on button click
        addBtn.addEventListener('click', addTodo);
        
        // Add todo on Enter key press
        todoInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                addTodo();
            }
        });
        
        // Filter buttons
        filterBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                // Update active filter button
                filterBtns.forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                
                // Set current filter and re-render
                currentFilter = this.dataset.filter;
                renderTodos();
            });
        });
        
        // Clear completed todos
        clearCompletedBtn.addEventListener('click', clearCompletedTodos);
        
        // Clear all todos
        clearAllBtn.addEventListener('click', clearAllTodos);
    }
    
    // Add a new todo
    function addTodo() {
        const text = todoInput.value.trim();
        
        if (text === '') {
            showNotification('Please enter a task', 'error');
            return;
        }
        
        // Create new todo object
        const newTodo = {
            id: Date.now(),
            text: text,
            completed: false,
            createdAt: new Date().toISOString()
        };
        
        // Add to todos array
        todos.unshift(newTodo);
        
        // Update UI and storage
        updateStorage();
        renderTodos();
        updateStats();
        
        // Clear input and focus
        todoInput.value = '';
        todoInput.focus();
        
        // Show success notification
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
        
        updateStorage();
        renderTodos();
        updateStats();
    }
    
    // Delete a todo
    function deleteTodo(id) {
        // Find todo to get text for notification
        const todoToDelete = todos.find(todo => todo.id === id);
        
        // Remove from array with animation
        const todoElement = document.querySelector(`[data-id="${id}"]`);
        if (todoElement) {
            todoElement.classList.add('fade-out');
            
            setTimeout(() => {
                todos = todos.filter(todo => todo.id !== id);
                updateStorage();
                renderTodos();
                updateStats();
                
                // Show notification
                showNotification(`Task "${todoToDelete.text}" deleted`, 'info');
            }, 300);
        }
    }
    
    // Edit a todo
    function editTodo(id, newText) {
        if (newText.trim() === '') {
            showNotification('Task cannot be empty', 'error');
            return false;
        }
        
        todos = todos.map(todo => {
            if (todo.id === id) {
                return { ...todo, text: newText.trim() };
            }
            return todo;
        });
        
        updateStorage();
        renderTodos();
        showNotification('Task updated successfully', 'success');
        return true;
    }
    
    // Clear all completed todos
    function clearCompletedTodos() {
        const completedCount = todos.filter(todo => todo.completed).length;
        
        if (completedCount === 0) {
            showNotification('No completed tasks to clear', 'info');
            return;
        }
        
        if (confirm(`Are you sure you want to clear ${completedCount} completed task(s)?`)) {
            todos = todos.filter(todo => !todo.completed);
            updateStorage();
            renderTodos();
            updateStats();
            showNotification('Completed tasks cleared', 'success');
        }
    }
    
    // Clear all todos
    function clearAllTodos() {
        if (todos.length === 0) {
            showNotification('Todo list is already empty', 'info');
            return;
        }
        
        if (confirm('Are you sure you want to clear ALL tasks?')) {
            todos = [];
            updateStorage();
            renderTodos();
            updateStats();
            showNotification('All tasks cleared', 'success');
        }
    }
    
    // Update localStorage
    function updateStorage() {
        localStorage.setItem('todos', JSON.stringify(todos));
    }
    
    // Update statistics
    function updateStats() {
        const total = todos.length;
        const completed = todos.filter(todo => todo.completed).length;
        
        totalTasksEl.textContent = `Total: ${total}`;
        completedTasksEl.textContent = `Completed: ${completed}`;
    }
    
    // Render todos based on current filter
    function renderTodos() {
        // Filter todos based on current filter
        let filteredTodos = [];
        
        switch (currentFilter) {
            case 'active':
                filteredTodos = todos.filter(todo => !todo.completed);
                break;
            case 'completed':
                filteredTodos = todos.filter(todo => todo.completed);
                break;
            default: // 'all'
                filteredTodos = [...todos];
        }
        
        // Clear the list
        todoList.innerHTML = '';
        
        // Show empty state if no todos
        if (filteredTodos.length === 0) {
            const emptyState = document.createElement('li');
            emptyState.className = 'empty-state';
            
            let message = '';
            switch (currentFilter) {
                case 'active':
                    message = 'No active tasks. Great job!';
                    break;
                case 'completed':
                    message = 'No completed tasks yet. Finish some tasks!';
                    break;
                default:
                    message = 'Your todo list is empty. Add a task to get started!';
            }
            
            emptyState.innerHTML = `
                <i class="fas fa-clipboard-list"></i>
                <p>${message}</p>
            `;
            
            todoList.appendChild(emptyState);
            return;
        }
        
        // Render each todo item
        filteredTodos.forEach(todo => {
            const todoItem = document.createElement('li');
            todoItem.className = `todo-item ${todo.completed ? 'completed' : ''}`;
            todoItem.dataset.id = todo.id;
            
            todoItem.innerHTML = `
                <input 
                    type="checkbox" 
                    class="todo-checkbox" 
                    ${todo.completed ? 'checked' : ''}
                >
                <span class="todo-text">${escapeHtml(todo.text)}</span>
                <div class="todo-actions">
                    <button class="edit-btn" title="Edit task">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="delete-btn" title="Delete task">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            `;
            
            // Add event listeners for this todo item
            const checkbox = todoItem.querySelector('.todo-checkbox');
            const todoText = todoItem.querySelector('.todo-text');
            const editBtn = todoItem.querySelector('.edit-btn');
            const deleteBtn = todoItem.querySelector('.delete-btn');
            
            // Toggle completion
            checkbox.addEventListener('change', () => toggleTodo(todo.id));
            
            // Edit functionality
            editBtn.addEventListener('click', () => {
                const currentText = todoText.textContent;
                const input = document.createElement('input');
                input.type = 'text';
                input.value = currentText;
                input.className = 'edit-input';
                
                // Replace text with input
                todoText.replaceWith(input);
                input.focus();
                input.select();
                
                // Save on Enter or blur
                const saveEdit = () => {
                    const newText = input.value.trim();
                    if (editTodo(todo.id, newText)) {
                        // Edit successful, will be re-rendered
                    } else {
                        // Edit failed, revert to original text
                        input.replaceWith(todoText);
                    }
                };
                
                input.addEventListener('keypress', (e) => {
                    if (e.key === 'Enter') {
                        saveEdit();
                    }
                });
                
                input.addEventListener('blur', saveEdit);
            });
            
            // Delete functionality
            deleteBtn.addEventListener('click', () => deleteTodo(todo.id));
            
            // Add to list
            todoList.appendChild(todoItem);
        });
    }
    
    // Show notification
    function showNotification(message, type) {
        // Remove existing notification
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
            animation: slideIn 0.3s ease-out;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        `;
        
        // Set background color based on type
        const colors = {
            success: '#2ecc71',
            error: '#e74c3c',
            info: '#3498db'
        };
        
        notification.style.backgroundColor = colors[type] || '#3498db';
        
        // Add to DOM
        document.body.appendChild(notification);
        
        // Remove after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-out forwards';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
        
        // Add CSS animations for notifications
        if (!document.querySelector('#notification-styles')) {
            const style = document.createElement('style');
            style.id = 'notification-styles';
            style.textContent = `
                @keyframes slideIn {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @keyframes slideOut {
                    from { transform: translateX(0); opacity: 1; }
                    to { transform: translateX(100%); opacity: 0; }
                }
            `;
            document.head.appendChild(style);
        }
    }
    
    // Helper function to escape HTML
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});