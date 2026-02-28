import { reactive, computed } from 'vue'

const cartStore = reactive({
  items: [],
  
  addToCart(product) {
    const existingItem = this.items.find(item => item.id === product.id)
    
    if (existingItem) {
      existingItem.quantity += 1
    } else {
      this.items.push({
        ...product,
        quantity: 1
      })
    }
  },
  
  removeFromCart(productId) {
    const index = this.items.findIndex(item => item.id === productId)
    if (index !== -1) {
      this.items.splice(index, 1)
    }
  },
  
  updateQuantity(productId, quantity) {
    const item = this.items.find(item => item.id === productId)
    if (item) {
      if (quantity <= 0) {
        this.removeFromCart(productId)
      } else {
        item.quantity = quantity
      }
    }
  },
  
  clearCart() {
    this.items = []
  },
  
  get totalItems() {
    return this.items.reduce((total, item) => total + item.quantity, 0)
  },
  
  get subtotal() {
    return this.items.reduce((total, item) => total + (item.price * item.quantity), 0)
  },
  
  get shipping() {
    return this.subtotal >= 50 ? 0 : 5.99
  },
  
  get tax() {
    return this.subtotal * 0.08 // 8% tax
  },
  
  get total() {
    return this.subtotal + this.shipping + this.tax
  }
})

export const useCartStore = () => {
  return cartStore
}