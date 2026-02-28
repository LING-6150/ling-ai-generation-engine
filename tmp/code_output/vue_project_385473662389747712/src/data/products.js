export const products = [
  {
    id: 1,
    name: "Wireless Bluetooth Headphones",
    description: "Premium noise-cancelling headphones with 30-hour battery life and crystal clear audio quality.",
    price: 129.99,
    category: "Electronics",
    rating: 4.5,
    reviews: 128,
    image: "https://picsum.photos/400/300?random=1",
    stock: 42,
    featured: true
  },
  {
    id: 2,
    name: "Smart Fitness Watch",
    description: "Track your health metrics with this advanced smartwatch featuring heart rate monitoring and GPS.",
    price: 199.99,
    category: "Electronics",
    rating: 4.7,
    reviews: 89,
    image: "https://picsum.photos/400/300?random=2",
    stock: 25,
    featured: true
  },
  {
    id: 3,
    name: "Organic Cotton T-Shirt",
    description: "Comfortable and sustainable t-shirt made from 100% organic cotton. Available in multiple colors.",
    price: 29.99,
    category: "Fashion",
    rating: 4.3,
    reviews: 56,
    image: "https://picsum.photos/400/300?random=3",
    stock: 100,
    featured: false
  },
  {
    id: 4,
    name: "Stainless Steel Water Bottle",
    description: "Keep your drinks hot or cold for hours with this durable insulated water bottle.",
    price: 34.99,
    category: "Home",
    rating: 4.6,
    reviews: 203,
    image: "https://picsum.photos/400/300?random=4",
    stock: 75,
    featured: true
  },
  {
    id: 5,
    name: "Professional Chef's Knife",
    description: "High-quality Japanese steel knife perfect for professional chefs and cooking enthusiasts.",
    price: 89.99,
    category: "Home",
    rating: 4.8,
    reviews: 67,
    image: "https://picsum.photos/400/300?random=5",
    stock: 18,
    featured: false
  },
  {
    id: 6,
    name: "Yoga Mat Premium",
    description: "Non-slip, eco-friendly yoga mat with excellent cushioning for all types of yoga practice.",
    price: 49.99,
    category: "Fitness",
    rating: 4.4,
    reviews: 92,
    image: "https://picsum.photos/400/300?random=6",
    stock: 60,
    featured: false
  },
  {
    id: 7,
    name: "Portable Bluetooth Speaker",
    description: "Waterproof speaker with 360° sound and 12-hour battery life for outdoor adventures.",
    price: 79.99,
    category: "Electronics",
    rating: 4.5,
    reviews: 145,
    image: "https://picsum.photos/400/300?random=7",
    stock: 35,
    featured: true
  },
  {
    id: 8,
    name: "Leather Laptop Bag",
    description: "Professional leather bag with padded laptop compartment and multiple organizational pockets.",
    price: 149.99,
    category: "Fashion",
    rating: 4.6,
    reviews: 78,
    image: "https://picsum.photos/400/300?random=8",
    stock: 22,
    featured: false
  },
  {
    id: 9,
    name: "Smart LED Desk Lamp",
    description: "Adjustable desk lamp with smart features including color temperature control and scheduling.",
    price: 59.99,
    category: "Home",
    rating: 4.3,
    reviews: 41,
    image: "https://picsum.photos/400/300?random=9",
    stock: 50,
    featured: false
  },
  {
    id: 10,
    name: "Running Shoes Pro",
    description: "Lightweight running shoes with advanced cushioning technology for maximum comfort.",
    price: 119.99,
    category: "Fitness",
    rating: 4.7,
    reviews: 112,
    image: "https://picsum.photos/400/300?random=10",
    stock: 30,
    featured: true
  },
  {
    id: 11,
    name: "Wireless Earbuds",
    description: "True wireless earbuds with active noise cancellation and wireless charging case.",
    price: 159.99,
    category: "Electronics",
    rating: 4.4,
    reviews: 96,
    image: "https://picsum.photos/400/300?random=11",
    stock: 45,
    featured: false
  },
  {
    id: 12,
    name: "Ceramic Coffee Mug Set",
    description: "Set of 4 handmade ceramic mugs with unique designs and comfortable handles.",
    price: 39.99,
    category: "Home",
    rating: 4.5,
    reviews: 63,
    image: "https://picsum.photos/400/300?random=12",
    stock: 80,
    featured: false
  }
]

export const categories = [
  { id: 1, name: "All", count: 12 },
  { id: 2, name: "Electronics", count: 4 },
  { id: 3, name: "Fashion", count: 2 },
  { id: 4, name: "Home", count: 4 },
  { id: 5, name: "Fitness", count: 2 }
]

export const featuredProducts = products.filter(product => product.featured)