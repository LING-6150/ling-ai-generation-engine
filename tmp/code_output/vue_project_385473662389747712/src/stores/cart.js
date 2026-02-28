import { reactive, computed } from 'vue'

const cartState = reactive({
  items: [],
  shippingAddress: null,
  paymentMethod: null
})

export function useCartStore() {
  const addToCart = (product, quantity = 1) => {
    const existingItem = cartState.items.find(item => item.id === product.id)
    
    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      cartState.items.push({
        ...product,
        quantity
      })
    }
  }

  const removeFromCart = (productId) => {
    const index = cartState.items.findIndex(item => item.id === productId)
    if (index !== -1) {
      cartState.items.splice(index, 1)
    }
  }

  const updateQuantity = (productId, quantity) => {
    const item = cartState.items.find(item => item.id === productId)
    if (item) {
      if (quantity <= 0) {
        removeFromCart(productId)
      } else {
        item.quantity = quantity
      }
    }
  }

  const clearCart = () => {
    cartState.items = []
  }

  const setShippingAddress = (address) => {
    cartState.shippingAddress = address
  }

  const setPaymentMethod = (method) => {
    cartState.paymentMethod = method
  }

  const subtotal = computed(() => {
    return cartState.items.reduce((total, item) => {
      return total + (item.price * item.quantity)
    }, 0)
  })

  const shippingCost = computed(() => {
    return subtotal.value > 50 ? 0 : 5.99
  })

  const tax = computed(() => {
    return subtotal.value * 0.08 // 8% tax
  })

  const total = computed(() => {
    return subtotal.value + shippingCost.value + tax.value
  })

  return {
    items: computed(() => cartState.items),
    shippingAddress: computed(() => cartState.shippingAddress),
    paymentMethod: computed(() => cartState.paymentMethod),
    subtotal,
    shippingCost,
    tax,
    total,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    setShippingAddress,
    setPaymentMethod
  }
}