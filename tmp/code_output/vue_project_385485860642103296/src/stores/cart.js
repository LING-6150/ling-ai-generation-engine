import { ref, computed } from 'vue'

export function useCartStore() {
  const cartItems = ref([])
  
  const addToCart = (product, quantity = 1) => {
    const existingItem = cartItems.value.find(item => item.id === product.id)
    
    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      cartItems.value.push({
        ...product,
        quantity
      })
    }
  }
  
  const removeFromCart = (productId) => {
    cartItems.value = cartItems.value.filter(item => item.id !== productId)
  }
  
  const updateQuantity = (productId, quantity) => {
    const item = cartItems.value.find(item => item.id === productId)
    if (item) {
      if (quantity <= 0) {
        removeFromCart(productId)
      } else {
        item.quantity = quantity
      }
    }
  }
  
  const clearCart = () => {
    cartItems.value = []
  }
  
  const totalItems = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
  })
  
  const subtotal = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
  })
  
  const shippingCost = computed(() => {
    return subtotal.value > 100 ? 0 : 9.99
  })
  
  const tax = computed(() => {
    return subtotal.value * 0.08 // 8% tax
  })
  
  const total = computed(() => {
    return subtotal.value + shippingCost.value + tax.value
  })
  
  return {
    cartItems,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    totalItems,
    subtotal,
    shippingCost,
    tax,
    total
  }
}