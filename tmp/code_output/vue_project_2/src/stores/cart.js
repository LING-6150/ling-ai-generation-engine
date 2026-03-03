import { ref, computed } from 'vue'

// Mock product data
const products = [
  {
    id: 1,
    name: 'Wireless Bluetooth Headphones',
    description: 'Premium noise-cancelling headphones with 30-hour battery life',
    price: 129.99,
    image: 'https://images.pexels.com/photos/3394660/pexels-photo-3394660.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'electronics',
    stock: 15
  },
  {
    id: 2,
    name: 'Smart Watch Pro',
    description: 'Advanced fitness tracker with heart rate monitoring and GPS',
    price: 249.99,
    image: 'https://images.pexels.com/photos/437037/pexels-photo-437037.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'wearables',
    stock: 8
  },
  {
    id: 3,
    name: 'Ultra HD 4K Monitor',
    description: '32-inch 4K UHD monitor with HDR support and 144Hz refresh rate',
    price: 499.99,
    image: 'https://images.pexels.com/photos/1029757/pexels-photo-1029757.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'computers',
    stock: 5
  },
  {
    id: 4,
    name: 'Professional DSLR Camera',
    description: '24MP DSLR camera with 4K video recording and lens kit',
    price: 899.99,
    image: 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'cameras',
    stock: 3
  },
  {
    id: 5,
    name: 'Wireless Mechanical Keyboard',
    description: 'RGB mechanical keyboard with customizable switches',
    price: 89.99,
    image: 'https://images.pexels.com/photos/2115256/pexels-photo-2115256.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'accessories',
    stock: 25
  },
  {
    id: 6,
    name: 'Smart Home Hub',
    description: 'Central control for all your smart home devices',
    price: 149.99,
    image: 'https://images.pexels.com/photos/1571460/pexels-photo-1571460.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'smart-home',
    stock: 12
  },
  {
    id: 7,
    name: 'Gaming Laptop',
    description: 'High-performance gaming laptop with RTX 4070 graphics',
    price: 1499.99,
    image: 'https://images.pexels.com/photos/18105/pexels-photo.jpg?auto=compress&cs=tinysrgb&h=400',
    category: 'computers',
    stock: 4
  },
  {
    id: 8,
    name: 'Portable Power Bank',
    description: '20000mAh power bank with fast charging support',
    price: 39.99,
    image: 'https://images.pexels.com/photos/1279107/pexels-photo-1279107.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'accessories',
    stock: 30
  },
  {
    id: 9,
    name: 'Wireless Earbuds',
    description: 'True wireless earbuds with active noise cancellation',
    price: 79.99,
    image: 'https://images.pexels.com/photos/3780681/pexels-photo-3780681.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'electronics',
    stock: 20
  },
  {
    id: 10,
    name: 'Tablet Pro',
    description: '10.5-inch tablet with stylus support and 256GB storage',
    price: 399.99,
    image: 'https://images.pexels.com/photos/1334597/pexels-photo-1334597.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'electronics',
    stock: 7
  },
  {
    id: 11,
    name: 'External SSD Drive',
    description: '1TB external SSD with USB-C 3.2 Gen 2 interface',
    price: 129.99,
    image: 'https://images.pexels.com/photos/2588757/pexels-photo-2588757.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'accessories',
    stock: 18
  },
  {
    id: 12,
    name: 'Smart Light Bulbs',
    description: 'Set of 4 smart LED bulbs with color changing capabilities',
    price: 49.99,
    image: 'https://images.pexels.com/photos/1493226/pexels-photo-1493226.jpeg?auto=compress&cs=tinysrgb&h=400',
    category: 'smart-home',
    stock: 22
  }
]

export function useCartStore() {
  const cartItems = ref([])

  // Add product to cart
  const addToCart = (product) => {
    const existingItem = cartItems.value.find(item => item.id === product.id)
    
    if (existingItem) {
      if (existingItem.quantity < product.stock) {
        existingItem.quantity++
      }
    } else {
      cartItems.value.push({
        ...product,
        quantity: 1
      })
    }
  }

  // Remove product from cart
  const removeFromCart = (productId) => {
    const index = cartItems.value.findIndex(item => item.id === productId)
    if (index !== -1) {
      cartItems.value.splice(index, 1)
    }
  }

  // Update product quantity
  const updateQuantity = (productId, quantity) => {
    const item = cartItems.value.find(item => item.id === productId)
    if (item) {
      const product = products.find(p => p.id === productId)
      if (product) {
        item.quantity = Math.min(Math.max(1, quantity), product.stock)
      }
    }
  }

  // Clear cart
  const clearCart = () => {
    cartItems.value = []
  }

  // Computed properties
  const totalItems = computed(() => {
    return cartItems.value.reduce((total, item) => total + item.quantity, 0)
  })

  const subtotal = computed(() => {
    return cartItems.value.reduce((total, item) => total + (item.price * item.quantity), 0)
  })

  const shipping = computed(() => {
    return subtotal.value >= 50 ? 0 : 9.99
  })

  const tax = computed(() => {
    return subtotal.value * 0.08
  })

  const total = computed(() => {
    return subtotal.value + shipping.value + tax.value
  })

  return {
    products,
    cartItems,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    totalItems,
    subtotal,
    shipping,
    tax,
    total
  }
}