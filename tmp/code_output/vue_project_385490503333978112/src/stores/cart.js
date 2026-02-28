import { ref, computed } from 'vue'

// Mock product data
const mockProducts = [
  {
    id: 1,
    name: 'Wireless Bluetooth Headphones',
    description: 'Premium noise-cancelling headphones with 30-hour battery life',
    price: 129.99,
    category: 'Electronics',
    rating: 4.5,
    stock: 25,
    image: 'https://picsum.photos/400/300?random=1'
  },
  {
    id: 2,
    name: 'Smart Fitness Watch',
    description: 'Track your workouts, heart rate, and sleep patterns',
    price: 199.99,
    category: 'Fitness',
    rating: 4.7,
    stock: 15,
    image: 'https://picsum.photos/400/300?random=2'
  },
  {
    id: 3,
    name: 'Organic Cotton T-Shirt',
    description: 'Comfortable and sustainable cotton t-shirt',
    price: 29.99,
    category: 'Clothing',
    rating: 4.3,
    stock: 50,
    image: 'https://picsum.photos/400/300?random=3'
  },
  {
    id: 4,
    name: 'Ceramic Coffee Mug Set',
    description: 'Set of 4 elegant ceramic mugs with modern design',
    price: 39.99,
    category: 'Home',
    rating: 4.6,
    stock: 30,
    image: 'https://picsum.photos/400/300?random=4'
  },
  {
    id: 5,
    name: 'Portable Power Bank',
    description: '10000mAh fast charging power bank with dual USB ports',
    price: 49.99,
    category: 'Electronics',
    rating: 4.4,
    stock: 8,
    image: 'https://picsum.photos/400/300?random=5'
  },
  {
    id: 6,
    name: 'Yoga Mat Premium',
    description: 'Non-slip yoga mat with carrying strap',
    price: 34.99,
    category: 'Fitness',
    rating: 4.8,
    stock: 40,
    image: 'https://picsum.photos/400/300?random=6'
  },
  {
    id: 7,
    name: 'Leather Wallet',
    description: 'Genuine leather wallet with RFID protection',
    price: 59.99,
    category: 'Accessories',
    rating: 4.2,
    stock: 20,
    image: 'https://picsum.photos/400/300?random=7'
  },
  {
    id: 8,
    name: 'Desk Lamp with Wireless Charger',
    description: 'Modern LED desk lamp with built-in wireless charging pad',
    price: 79.99,
    category: 'Home',
    rating: 4.9,
    stock: 12,
    image: 'https://picsum.photos/400/300?random=8'
  }
]

// Cart store
const cartItems = ref([])

export function useCartStore() {
  // Computed properties
  const totalItems = computed(() => {
    return cartItems.value.reduce((total, item) => total + item.quantity, 0)
  })

  const subtotal = computed(() => {
    return cartItems.value.reduce((total, item) => total + (item.price * item.quantity), 0)
  })

  const shippingCost = computed(() => {
    return subtotal.value >= 100 ? 0 : 9.99
  })

  const tax = computed(() => {
    return subtotal.value * 0.08
  })

  const total = computed(() => {
    return subtotal.value + shippingCost.value + tax.value
  })

  // Methods
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
    const index = cartItems.value.findIndex(item => item.id === productId)
    if (index !== -1) {
      cartItems.value.splice(index, 1)
    }
  }

  const updateQuantity = (productId, quantity) => {
    const item = cartItems.value.find(item => item.id === productId)
    if (item) {
      item.quantity = Math.max(1, Math.min(quantity, 100))
    }
  }

  const clearCart = () => {
    cartItems.value = []
  }

  const getProductById = (productId) => {
    return mockProducts.find(product => product.id === productId)
  }

  const getProductsByCategory = (category) => {
    return mockProducts.filter(product => product.category === category)
  }

  const getAllProducts = () => {
    return [...mockProducts]
  }

  return {
    // State
    cartItems,
    
    // Computed
    totalItems,
    subtotal,
    shippingCost,
    tax,
    total,
    
    // Methods
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    getProductById,
    getProductsByCategory,
    getAllProducts
  }
}