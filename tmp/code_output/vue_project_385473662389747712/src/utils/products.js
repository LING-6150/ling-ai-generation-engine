export const products = [
  {
    id: 1,
    name: "Wireless Bluetooth Headphones",
    description: "Premium noise-cancelling headphones with 30-hour battery life and crystal clear audio quality.",
    price: 129.99,
    category: "Electronics",
    rating: 4.5,
    reviewCount: 128,
    image: "https://picsum.photos/400/300?random=1",
    inStock: true,
    features: [
      "Active Noise Cancellation",
      "30-hour battery life",
      "Bluetooth 5.2",
      "Foldable design",
      "Built-in microphone"
    ]
  },
  {
    id: 2,
    name: "Smart Fitness Watch",
    description: "Track your fitness goals with heart rate monitoring, GPS, and sleep tracking features.",
    price: 199.99,
    category: "Wearables",
    rating: 4.7,
    reviewCount: 89,
    image: "https://picsum.photos/400/300?random=2",
    inStock: true,
    features: [
      "Heart rate monitoring",
      "GPS tracking",
      "Sleep analysis",
      "Water resistant",
      "7-day battery"
    ]
  },
  {
    id: 3,
    name: "Organic Cotton T-Shirt",
    description: "Comfortable and sustainable t-shirt made from 100% organic cotton.",
    price: 29.99,
    category: "Clothing",
    rating: 4.3,
    reviewCount: 56,
    image: "https://picsum.photos/400/300?random=3",
    inStock: true,
    features: [
      "100% organic cotton",
      "Machine washable",
      "Available in multiple colors",
      "Breathable fabric",
      "Sustainable production"
    ]
  },
  {
    id: 4,
    name: "Stainless Steel Water Bottle",
    description: "Keep your drinks hot or cold for hours with this durable insulated water bottle.",
    price: 34.99,
    category: "Home & Kitchen",
    rating: 4.6,
    reviewCount: 203,
    image: "https://picsum.photos/400/300?random=4",
    inStock: true,
    features: [
      "Double-wall insulation",
      "BPA-free",
      "Leak-proof lid",
      "24-hour temperature retention",
      "Dishwasher safe"
    ]
  },
  {
    id: 5,
    name: "Professional Camera Backpack",
    description: "Protect your camera gear with this weather-resistant backpack with customizable compartments.",
    price: 89.99,
    category: "Photography",
    rating: 4.8,
    reviewCount: 42,
    image: "https://picsum.photos/400/300?random=5",
    inStock: true,
    features: [
      "Weather-resistant material",
      "Customizable dividers",
      "Laptop compartment",
      "Tripod holder",
      "Padded shoulder straps"
    ]
  },
  {
    id: 6,
    name: "Ceramic Coffee Mug Set",
    description: "Set of 4 elegant ceramic mugs perfect for your morning coffee or tea.",
    price: 24.99,
    category: "Home & Kitchen",
    rating: 4.4,
    reviewCount: 78,
    image: "https://picsum.photos/400/300?random=6",
    inStock: true,
    features: [
      "Microwave safe",
      "Dishwasher safe",
      "Comfortable handle",
      "Set of 4",
      "Modern design"
    ]
  },
  {
    id: 7,
    name: "Gaming Keyboard",
    description: "Mechanical gaming keyboard with RGB lighting and programmable macro keys.",
    price: 79.99,
    category: "Gaming",
    rating: 4.5,
    reviewCount: 112,
    image: "https://picsum.photos/400/300?random=7",
    inStock: true,
    features: [
      "Mechanical switches",
      "RGB backlighting",
      "Programmable keys",
      "N-key rollover",
      "Detachable wrist rest"
    ]
  },
  {
    id: 8,
    name: "Yoga Mat",
    description: "Non-slip yoga mat with extra cushioning for comfortable workouts.",
    price: 39.99,
    category: "Fitness",
    rating: 4.2,
    reviewCount: 65,
    image: "https://picsum.photos/400/300?random=8",
    inStock: true,
    features: [
      "Non-slip surface",
      "Extra cushioning",
      "Eco-friendly material",
      "Easy to clean",
      "Includes carrying strap"
    ]
  }
]

export const categories = [
  "All Categories",
  "Electronics",
  "Clothing",
  "Home & Kitchen",
  "Wearables",
  "Photography",
  "Gaming",
  "Fitness"
]

export function getProductById(id) {
  return products.find(product => product.id === parseInt(id))
}

export function getProductsByCategory(category) {
  if (category === "All Categories") return products
  return products.filter(product => product.category === category)
}