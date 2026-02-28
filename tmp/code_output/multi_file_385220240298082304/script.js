// DOM Elements
const navLinks = document.querySelectorAll('.nav-link');
const menuToggle = document.querySelector('.menu-toggle');
const navMenu = document.querySelector('.nav-menu');
const pages = document.querySelectorAll('.page');
const filterButtons = document.querySelectorAll('.filter-btn');
const articlesContainer = document.getElementById('articles-container');

// Sample articles data
const articles = [
    {
        id: 1,
        title: "Understanding JavaScript Closures",
        excerpt: "A deep dive into one of JavaScript's most powerful features and how to use closures effectively in your code.",
        category: "technology",
        date: "March 20, 2024",
        readTime: "7 min read",
        image: "https://picsum.photos/400/250?random=6"
    },
    {
        id: 2,
        title: "The Psychology of Color in Design",
        excerpt: "How color choices impact user perception and behavior in digital interfaces.",
        category: "design",
        date: "March 18, 2024",
        readTime: "5 min read",
        image: "https://picsum.photos/400/250?random=7"
    },
    {
        id: 3,
        title: "Morning Routines of Successful Creators",
        excerpt: "Insights into how successful digital creators structure their mornings for maximum productivity.",
        category: "lifestyle",
        date: "March 12, 2024",
        readTime: "6 min read",
        image: "https://picsum.photos/400/250?random=8"
    },
    {
        id: 4,
        title: "CSS Grid vs Flexbox: When to Use Which",
        excerpt: "A practical guide to choosing between CSS Grid and Flexbox for different layout scenarios.",
        category: "technology",
        date: "March 8, 2024",
        readTime: "8 min read",
        image: "https://picsum.photos/400/250?random=9"
    },
    {
        id: 5,
        title: "Accessibility in Modern Web Design",
        excerpt: "Essential accessibility practices every web designer should implement in their projects.",
        category: "design",
        date: "March 3, 2024",
        readTime: "9 min read",
        image: "https://picsum.photos/400/250?random=10"
    },
    {
        id: 6,
        title: "Digital Detox: Finding Balance",
        excerpt: "Strategies for maintaining a healthy relationship with technology in an always-connected world.",
        category: "lifestyle",
        date: "February 28, 2024",
        readTime: "5 min read",
        image: "https://picsum.photos/400/250?random=11"
    },
    {
        id: 7,
        title: "Introduction to WebAssembly",
        excerpt: "What WebAssembly is and how it's changing the landscape of web development.",
        category: "technology",
        date: "February 25, 2024",
        readTime: "10 min read",
        image: "https://picsum.photos/400/250?random=12"
    },
    {
        id: 8,
        title: "Typography Principles for the Web",
        excerpt: "Fundamental typography rules that will improve the readability and aesthetics of your websites.",
        category: "design",
        date: "February 20, 2024",
        readTime: "6 min read",
        image: "https://picsum.photos/400/250?random=13"
    }
];

// Initialize the application
function init() {
    // Load articles on page load
    loadArticles('all');
    
    // Set up event listeners
    setupEventListeners();
}

// Set up all event listeners
function setupEventListeners() {
    // Navigation links
    navLinks.forEach(link => {
        link.addEventListener('click', handleNavigation);
    });
    
    // Mobile menu toggle
    menuToggle.addEventListener('click', toggleMobileMenu);
    
    // Filter buttons
    filterButtons.forEach(button => {
        button.addEventListener('click', handleFilterClick);
    });
    
    // Close mobile menu when clicking outside
    document.addEventListener('click', (e) => {
        if (!navMenu.contains(e.target) && !menuToggle.contains(e.target)) {
            navMenu.classList.remove('active');
            menuToggle.classList.remove('active');
        }
    });
}

// Handle navigation between pages
function handleNavigation(e) {
    e.preventDefault();
    
    const targetPage = this.getAttribute('data-page');
    
    // Update active navigation link
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('data-page') === targetPage) {
            link.classList.add('active');
        }
    });
    
    // Show target page
    pages.forEach(page => {
        page.classList.remove('active');
        if (page.id === targetPage) {
            // Add a small delay to ensure smooth transition
            setTimeout(() => {
                page.classList.add('active');
            }, 10);
        }
    });
    
    // Close mobile menu if open
    navMenu.classList.remove('active');
    menuToggle.classList.remove('active');
    
    // Scroll to top of page
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    // If navigating to articles page, ensure articles are loaded
    if (targetPage === 'articles') {
        loadArticles('all');
    }
}

// Toggle mobile menu
function toggleMobileMenu() {
    navMenu.classList.toggle('active');
    this.classList.toggle('active');
}

// Handle filter button clicks
function handleFilterClick() {
    // Update active filter button
    filterButtons.forEach(btn => btn.classList.remove('active'));
    this.classList.add('active');
    
    // Load filtered articles
    const filter = this.getAttribute('data-filter');
    loadArticles(filter);
}

// Load and display articles
function loadArticles(filter) {
    // Clear current articles
    articlesContainer.innerHTML = '';
    
    // Filter articles if needed
    let filteredArticles = articles;
    if (filter !== 'all') {
        filteredArticles = articles.filter(article => article.category === filter);
    }
    
    // Create article cards
    filteredArticles.forEach(article => {
        const articleCard = createArticleCard(article);
        articlesContainer.appendChild(articleCard);
    });
    
    // If no articles found, show message
    if (filteredArticles.length === 0) {
        const noResults = document.createElement('div');
        noResults.className = 'no-results';
        noResults.innerHTML = `
            <h3>No articles found in this category</h3>
            <p>Check back soon for new content!</p>
        `;
        articlesContainer.appendChild(noResults);
    }
}

// Create an article card element
function createArticleCard(article) {
    const card = document.createElement('article');
    card.className = 'article-card';
    card.setAttribute('data-category', article.category);
    
    card.innerHTML = `
        <div class="post-image">
            <img src="${article.image}" alt="${article.title}">
            <span class="post-category">${article.category.charAt(0).toUpperCase() + article.category.slice(1)}</span>
        </div>
        <div class="post-content">
            <h3 class="post-title">${article.title}</h3>
            <p class="post-excerpt">${article.excerpt}</p>
            <div class="post-meta">
                <span class="post-date">${article.date}</span>
                <span class="read-time">${article.readTime}</span>
            </div>
            <a href="#" class="read-more">Read Article →</a>
        </div>
    `;
    
    // Add click event to read more button
    const readMoreBtn = card.querySelector('.read-more');
    readMoreBtn.addEventListener('click', (e) => {
        e.preventDefault();
        alert(`Opening article: ${article.title}\n\nThis is a demo. In a real application, this would open the full article.`);
    });
    
    return card;
}

// Smooth scroll for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        
        const targetId = this.getAttribute('href');
        if (targetId === '#') return;
        
        const targetElement = document.querySelector(targetId);
        if (targetElement) {
            window.scrollTo({
                top: targetElement.offsetTop - 80,
                behavior: 'smooth'
            });
        }
    });
});

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', init);