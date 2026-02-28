<template>
  <div class="calculator">
    <div class="display">
      <div class="history">
        <div v-for="(item, index) in history" :key="index" class="history-item">
          {{ item }}
        </div>
      </div>
      <div class="current-display">{{ displayValue }}</div>
    </div>
    
    <div class="buttons">
      <div class="row">
        <button class="btn operator" @click="clearAll">C</button>
        <button class="btn operator" @click="clearEntry">CE</button>
        <button class="btn operator" @click="deleteLast">⌫</button>
        <button class="btn operator" @click="setOperation('÷')">÷</button>
      </div>
      
      <div class="row">
        <button class="btn number" @click="appendNumber('7')">7</button>
        <button class="btn number" @click="appendNumber('8')">8</button>
        <button class="btn number" @click="appendNumber('9')">9</button>
        <button class="btn operator" @click="setOperation('×')">×</button>
      </div>
      
      <div class="row">
        <button class="btn number" @click="appendNumber('4')">4</button>
        <button class="btn number" @click="appendNumber('5')">5</button>
        <button class="btn number" @click="appendNumber('6')">6</button>
        <button class="btn operator" @click="setOperation('-')">-</button>
      </div>
      
      <div class="row">
        <button class="btn number" @click="appendNumber('1')">1</button>
        <button class="btn number" @click="appendNumber('2')">2</button>
        <button class="btn number" @click="appendNumber('3')">3</button>
        <button class="btn operator" @click="setOperation('+')">+</button>
      </div>
      
      <div class="row">
        <button class="btn number zero" @click="appendNumber('0')">0</button>
        <button class="btn number" @click="appendDecimal()">.</button>
        <button class="btn equals" @click="calculate">=</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const displayValue = ref('0')
const firstOperand = ref(null)
const operator = ref(null)
const waitingForSecondOperand = ref(false)
const history = ref([])

const appendNumber = (number) => {
  if (waitingForSecondOperand.value) {
    displayValue.value = number
    waitingForSecondOperand.value = false
  } else {
    displayValue.value = displayValue.value === '0' ? number : displayValue.value + number
  }
}

const appendDecimal = () => {
  if (waitingForSecondOperand.value) {
    displayValue.value = '0.'
    waitingForSecondOperand.value = false
    return
  }
  
  if (!displayValue.value.includes('.')) {
    displayValue.value += '.'
  }
}

const setOperation = (nextOperator) => {
  const inputValue = parseFloat(displayValue.value)
  
  if (firstOperand.value === null) {
    firstOperand.value = inputValue
  } else if (operator.value) {
    const result = calculateResult()
    displayValue.value = String(result)
    firstOperand.value = result
  }
  
  waitingForSecondOperand.value = true
  operator.value = nextOperator
}

const calculateResult = () => {
  const secondOperand = parseFloat(displayValue.value)
  
  if (firstOperand.value === null || operator.value === null) {
    return secondOperand
  }
  
  let result = 0
  
  switch (operator.value) {
    case '+':
      result = firstOperand.value + secondOperand
      break
    case '-':
      result = firstOperand.value - secondOperand
      break
    case '×':
      result = firstOperand.value * secondOperand
      break
    case '÷':
      result = firstOperand.value / secondOperand
      break
  }
  
  return result
}

const calculate = () => {
  if (firstOperand.value === null || operator.value === null) {
    return
  }
  
  const result = calculateResult()
  
  // Add to history
  const historyEntry = `${firstOperand.value} ${operator.value} ${displayValue.value} = ${result}`
  history.value.unshift(historyEntry)
  
  // Keep only last 5 calculations
  if (history.value.length > 5) {
    history.value.pop()
  }
  
  displayValue.value = String(result)
  firstOperand.value = null
  operator.value = null
  waitingForSecondOperand.value = false
}

const clearAll = () => {
  displayValue.value = '0'
  firstOperand.value = null
  operator.value = null
  waitingForSecondOperand.value = false
}

const clearEntry = () => {
  displayValue.value = '0'
}

const deleteLast = () => {
  if (displayValue.value.length > 1) {
    displayValue.value = displayValue.value.slice(0, -1)
  } else {
    displayValue.value = '0'
  }
}
</script>

<style scoped>
.calculator {
  background: #1a1a1a;
  color: white;
  border-radius: 20px;
  overflow: hidden;
}

.display {
  padding: 2rem;
  background: #2d2d2d;
  min-height: 180px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: flex-end;
}

.history {
  width: 100%;
  min-height: 60px;
  margin-bottom: 1rem;
  overflow-y: auto;
}

.history-item {
  color: #888;
  font-size: 0.9rem;
  text-align: right;
  margin-bottom: 0.25rem;
  padding: 0.25rem 0;
  border-bottom: 1px solid #333;
}

.current-display {
  font-size: 3rem;
  font-weight: 300;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
  text-align: right;
}

.buttons {
  display: grid;
  grid-template-rows: repeat(5, 1fr);
  gap: 1px;
  background: #333;
}

.row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1px;
}

.btn {
  border: none;
  font-size: 1.5rem;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
  outline: none;
  font-family: inherit;
}

.btn:active {
  opacity: 0.8;
  transform: scale(0.95);
}

.number {
  background: #3a3a3a;
  color: white;
}

.number:hover {
  background: #4a4a4a;
}

.zero {
  grid-column: span 2;
}

.operator {
  background: #ff9500;
  color: white;
}

.operator:hover {
  background: #ffaa33;
}

.equals {
  background: #ff9500;
  color: white;
  grid-column: span 1;
}

.equals:hover {
  background: #ffaa33;
}

@media (max-width: 768px) {
  .display {
    padding: 1.5rem;
    min-height: 150px;
  }
  
  .current-display {
    font-size: 2.5rem;
  }
  
  .btn {
    padding: 1.25rem;
    font-size: 1.3rem;
  }
}

@media (max-width: 480px) {
  .display {
    padding: 1rem;
    min-height: 120px;
  }
  
  .current-display {
    font-size: 2rem;
  }
  
  .btn {
    padding: 1rem;
    font-size: 1.2rem;
  }
  
  .history-item {
    font-size: 0.8rem;
  }
}
</style>