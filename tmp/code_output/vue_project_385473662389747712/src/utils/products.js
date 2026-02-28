export const categories = [
  'All Categories',
  'Electronics',
  'Clothing',
  'Home & Kitchen',
  'Wearables',
  'Accessories'
]

export const products = [
  {
    id: 1,
    name: "Wireless Noise-Canceling Headphones",
    category: "Electronics",
    price: 299.99,
    rating: 4.8,
    inStock: true,
    image: "https://picsum.photos/400/300?random=headphones",
    features: ["Bluetooth 5.2", "40-hour battery", "Active noise canceling", "Voice assistant"],
    description: "Premium wireless headphones with industry-leading noise cancellation technology."
  },
  {
    id: 2,
    name: "Smart Fitness Watch",
    category: "Wearables",
    price: 249.99,
    rating: 4.6,
    inStock: true,
    image: "https://picsum.photos/400/300?random=watch",
    features: ["Heart rate monitor", "GPS tracking", "Water resistant", "7-day battery"],
    description: "Advanced fitness tracker with comprehensive health monitoring features."
  },
  {
    id: 3,
    name: "Premium Cotton T-Shirt",
    category: "Clothing",
    price: 29.99,
    rating: 4.5,
    inStock: true,
    image: "https://picsum.photos/400/300?random=tshirt",
    features: ["100% organic cotton", "Breathable fabric", "Multiple colors", "Machine washable"],
    description: "Comfortable and durable cotton t-shirt made from premium materials."
  },
  {
    id: 4,
    name: "Professional Camera Kit",
    category: "Electronics",
    price: 1299.99,
    rating: 4.9,
    inStock: true,
    image: "https://picsum.photos/400/300?random=camera",
    features: ["24MP sensor", "4K video", "Wi-Fi connectivity", "Weather sealed"],
    description: "Professional-grade camera system for photographers and videographers."
  },
  {
    id: 5,
    name: "Air Fryer Oven",
    category: "Home & Kitchen",
    price: 149.99,
    rating: 4.7,
    inStock: true,
    image: "https://picsum.photos/400/300?random=airfryer",
    features: ["Digital controls", "8 cooking functions", "Non-stick basket", "Easy clean"],
    description: "Versatile air fryer oven that can bake, roast, and air fry with less oil."
  },
  {
    id: 6,
    name: "Leather Messenger Bag",
    category: "Accessories",
    price: 189.99,
    rating: 4.4,
    inStock: true,
    image: "https://picsum.photos/400/300?random=bag",
    features: ["Genuine leather", "Multiple compartments", "Laptop sleeve", "Adjustable strap"],
    description: "Stylish and functional leather bag perfect for work or travel."
  },
  {
    id: 7,
    name: "Gaming Keyboard",
    category: "Electronics",
    price: 89.99,
    rating: 4.3,
    inStock: true,
    image: "https://picsum.photos/400/300?random=keyboard",
    features: ["Mechanical switches", "RGB lighting", "Anti-ghosting", "Wrist rest"],
    description: "High-performance gaming keyboard with customizable RGB lighting."
  },
  {
    id: 8,
    name: "Yoga Mat",
    category: "Accessories",
    price: 39.99,
    rating: 4.2,
    inStock: true,
    image: "https://picsum.photos/400/300?random=yogamat",
    features: ["Non-slip surface", "Eco-friendly material", "Easy to clean", "Carry strap"],
    description: "Premium yoga mat with excellent grip and cushioning for all types of yoga."
  },
  {
    id: 9,
    name: "Smartphone Stand",
    category: "Accessories",
    price: 24.99,
    rating: 4.1,
    inStock: true,
    image: "https://picsum.photos/400/300?random=stand",
    features: ["Adjustable angle", "Universal compatibility", "Foldable design", "Non-slip base"],
    description: "Versatile smartphone stand for comfortable viewing at any angle."
  },
  {
    id: 10,
    name: "Coffee Maker",
    category: "Home & Kitchen",
    price: 79.99,
    rating: 4.6,
    inStock: true,
    image: "https://picsum.photos/400/300?random=coffee",
    features: ["Programmable timer", "Thermal carafe", "Brew strength control", "Auto shut-off"],
    description: "Automatic coffee maker with programmable features for perfect coffee every time."
  },
  {
    id: 11,
    name: "Running Shoes",
    category: "Clothing",
    price: 129.99,
    rating: 4.7,
    inStock: true,
    image: "https://picsum.photos/400/300?random=shoes",
    features: ["Breathable mesh", "Cushioned sole", "Lightweight design", "Multiple sizes"],
    description: "High-performance running shoes with superior cushioning and support."
  },
  {
    id: 12,
    name: "Bluetooth Speaker",
    category: "Electronics",
    price: 119.99,
    rating: 4.5,
    inStock: true,
    image: "https://picsum.photos/400/300?random=speaker",
    features: ["360° sound", "IPX7 waterproof", "20-hour battery", "Party mode"],
    description: "Portable Bluetooth speaker with immersive sound and waterproof design."
  }
]

export const searchProducts = (query) => {
  const searchTerm = query.toLowerCase().trim()
  if (!searchTerm) return products
  
  return products.filter(product => 
    product.name.toLowerCase().includes(searchTerm) ||
    product.category.toLowerCase().includes(searchTerm) ||
    product.description.toLowerCase().includes(searchTerm) ||
    product.features.some(feature => feature.toLowerCase().includes(searchTerm))
  )
}