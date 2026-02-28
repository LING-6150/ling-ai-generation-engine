// Calculator State
const calculator = {
    currentOperand: '0',
    previousOperand: '',
    operation: undefined,
    calculationHistory: []
};

// DOM Elements
const currentOperandElement = document.getElementById('current-operand');
const previousOperandElement = document.getElementById('previous-operand');
const historyListElement = document.getElementById('history-list');
const clearHistoryButton = document.getElementById('clear-history');
const currentYearElement = document.getElementById('current-year');

// Initialize the calculator
function initCalculator() {
    // Set current year in footer
    currentYearElement.textContent = new Date().getFullYear();
    
    // Add event listeners to all buttons
    document.querySelectorAll('.btn-number').forEach(button => {
        button.addEventListener('click', () => {
            appendNumber(button.getAttribute('data-number'));
            updateDisplay();
        });
    });
    
    document.querySelectorAll('.btn-operation').forEach(button => {
        button.addEventListener('click', () => {
            chooseOperation(button.getAttribute('data-action'));
            updateDisplay();
        });
    });
    
    document.querySelector('.btn-equals').addEventListener('click', () => {
        compute();
        updateDisplay();
    });
    
    // Clear history button
    clearHistoryButton.addEventListener('click', clearHistory);
    
    // Keyboard support
    document.addEventListener('keydown', handleKeyboardInput);
    
    // Initial display update
    updateDisplay();
}

// Append number to current operand
function appendNumber(number) {
    // Prevent multiple decimal points
    if (number === '.' && calculator.currentOperand.includes('.')) return;
    
    // Replace initial zero (unless it's a decimal)
    if (calculator.currentOperand === '0' && number !== '.') {
        calculator.currentOperand = number;
    } else {
        calculator.currentOperand += number;
    }
}

// Choose operation
function chooseOperation(action) {
    switch(action) {
        case 'clear':
            clearCalculator();
            break;
        case 'backspace':
            backspace();
            break;
        case 'percentage':
            calculatePercentage();
            break;
        case 'add':
        case 'subtract':
        case 'multiply':
        case 'divide':
            if (calculator.currentOperand === '') return;
            
            if (calculator.previousOperand !== '') {
                compute();
            }
            
            calculator.operation = action;
            calculator.previousOperand = calculator.currentOperand;
            calculator.currentOperand = '';
            break;
    }
}

// Perform calculation
function compute() {
    let computation;
    const prev = parseFloat(calculator.previousOperand);
    const current = parseFloat(calculator.currentOperand);
    
    if (isNaN(prev) || isNaN(current)) return;
    
    switch(calculator.operation) {
        case 'add':
            computation = prev + current;
            break;
        case 'subtract':
            computation = prev - current;
            break;
        case 'multiply':
            computation = prev * current;
            break;
        case 'divide':
            if (current === 0) {
                alert("Cannot divide by zero!");
                clearCalculator();
                return;
            }
            computation = prev / current;
            break;
        default:
            return;
    }
    
    // Create history entry
    const operationSymbols = {
        'add': '+',
        'subtract': '-',
        'multiply': '×',
        'divide': '÷'
    };
    
    const historyEntry = {
        calculation: `${prev} ${operationSymbols[calculator.operation]} ${current}`,
        result: computation
    };
    
    calculator.calculationHistory.unshift(historyEntry);
    updateHistoryDisplay();
    
    // Update calculator state
    calculator.currentOperand = computation.toString();
    calculator.operation = undefined;
    calculator.previousOperand = '';
}

// Clear the calculator
function clearCalculator() {
    calculator.currentOperand = '0';
    calculator.previousOperand = '';
    calculator.operation = undefined;
}

// Remove last digit
function backspace() {
    if (calculator.currentOperand.length > 1) {
        calculator.currentOperand = calculator.currentOperand.slice(0, -1);
    } else {
        calculator.currentOperand = '0';
    }
}

// Calculate percentage
function calculatePercentage() {
    const current = parseFloat(calculator.currentOperand);
    if (isNaN(current)) return;
    
    calculator.currentOperand = (current / 100).toString();
}

// Update the display
function updateDisplay() {
    currentOperandElement.textContent = calculator.currentOperand;
    
    if (calculator.operation != null) {
        const operationSymbols = {
            'add': '+',
            'subtract': '-',
            'multiply': '×',
            'divide': '÷'
        };
        
        previousOperandElement.textContent = 
            `${calculator.previousOperand} ${operationSymbols[calculator.operation]}`;
    } else {
        previousOperandElement.textContent = calculator.previousOperand;
    }
}

// Update history display
function updateHistoryDisplay() {
    // Clear current history display
    historyListElement.innerHTML = '';
    
    if (calculator.calculationHistory.length === 0) {
        historyListElement.innerHTML = '<div class="history-empty">No calculations yet</div>';
        return;
    }
    
    // Add each history item
    calculator.calculationHistory.forEach(entry => {
        const historyItem = document.createElement('div');
        historyItem.className = 'history-item';
        historyItem.innerHTML = `
            <div><strong>${entry.calculation}</strong></div>
            <div>= ${entry.result}</div>
        `;
        historyListElement.appendChild(historyItem);
    });
}

// Clear history
function clearHistory() {
    calculator.calculationHistory = [];
    updateHistoryDisplay();
}

// Handle keyboard input
function handleKeyboardInput(e) {
    if (e.key >= '0' && e.key <= '9') {
        appendNumber(e.key);
        updateDisplay();
    } else if (e.key === '.') {
        appendNumber('.');
        updateDisplay();
    } else if (e.key === '+' || e.key === '-') {
        chooseOperation(e.key === '+' ? 'add' : 'subtract');
        updateDisplay();
    } else if (e.key === '*' || e.key === 'x') {
        chooseOperation('multiply');
        updateDisplay();
    } else if (e.key === '/') {
        chooseOperation('divide');
        updateDisplay();
    } else if (e.key === 'Enter' || e.key === '=') {
        e.preventDefault();
        compute();
        updateDisplay();
    } else if (e.key === 'Escape' || e.key === 'Delete') {
        clearCalculator();
        updateDisplay();
    } else if (e.key === 'Backspace') {
        backspace();
        updateDisplay();
    } else if (e.key === '%') {
        calculatePercentage();
        updateDisplay();
    }
}

// Initialize the calculator when the page loads
document.addEventListener('DOMContentLoaded', initCalculator);