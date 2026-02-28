// Main Application Script
document.addEventListener('DOMContentLoaded', function() {
    // Initialize the application
    initNavigation();
    initArticles();
    initNewsletter();
    initMobileMenu();
    
    // Set initial active section based on hash or default to home
    const hash = window.location.hash || '#home';
    navigateToSection(hash.substring(1));
});

// Navigation Management
function initNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    const sections = document.querySelectorAll('.page-section');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            
            // Update active navigation link
            navLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');
            
            // Navigate to section
            navigateToSection(targetId);
            
            // Update URL hash
            window.history.pushState(null, '', `#${targetId}`);
            
            // Close mobile menu if open
            closeMobileMenu();
        });
    });
    
    // Handle browser back/forward buttons
    window.addEventListener('popstate', function() {
        const hash = window.location.hash.substring(1) || 'home';
        navigateToSection(hash);
        
        // Update active navigation link
        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href').substring(1) === hash) {
                link.classList.add('active');
            }
        });
    });
}

function navigateToSection(sectionId) {
    const sections = document.querySelectorAll('.page-section');
    const targetSection = document.getElementById(sectionId);
    
    if (!targetSection) return;
    
    // Hide all sections
    sections.forEach(section => {
        section.classList.remove('active');
    });
    
    // Show target section
    targetSection.classList.add('active');
    
    // Scroll to top of the section
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// Articles Management
function initArticles() {
    const articlesData = [
        {
            id: 1,
            title: "The Future of Web Development",
            excerpt: "Exploring emerging trends and technologies that are shaping the future of web development, from AI-assisted coding to new frameworks.",
            date: "March 15, 2024",
            category: "technology",
            image: "https://picsum.photos/400/250?random=5"
        },
        {
            id: 2,
            title: "Minimalism in Digital Design",
            excerpt: "How minimalism principles can create more effective and user-friendly digital experiences across various platforms.",
            date: "March 10, 2024",
            category: "design",
            image: "https://picsum.photos/400/250?random=6"
        },
        {
            id: 3,
            title: "Building Personal Projects",
            excerpt: "A comprehensive guide to starting and completing personal projects that enhance your skills and portfolio.",
            date: "March 5, 2024",
            category: "productivity",
            image: "https://picsum.photos/400/250?random=7"
        },
        {
            id: 4,
            title: "The Psychology of Color in UI",
            excerpt: "Understanding how color choices affect user perception and behavior in digital interfaces.",
            date: "February 28, 2024",
            category: "design",
            image: "https://picsum.photos/400/250?random=8"
        },
        {
            id: 5,
            title: "JavaScript Performance Tips",
            excerpt: "Practical techniques for optimizing JavaScript code to improve application performance and user experience.",
            date: "February 20, 2024",
            category: "technology",
            image: "https://picsum.photos/400/250?random=9"
        },
        {
            id: 6,
            title: "Morning Routines for Productivity",
            excerpt: "How establishing effective morning routines can significantly boost your daily productivity and focus.",
            date: "February 15, 2024",
            category: "productivity",
            image: "https://picsum.photos/400/250?random=10"
        },
        {
            id: 7,
            title: "Sustainable Web Design",
            excerpt: "Exploring ways to create websites that are environmentally friendly and energy efficient.",
            date: "February 10, 2024",
            category: "design",
            image: "https://picsum.photos/400/250?random=11"
        },
        {
            id: 8,
            title: "Digital Detox Strategies",
            excerpt: "Practical approaches to reducing screen time and improving digital wellbeing in a connected world.",
            date: "February 5, 2024",
            category: "lifestyle",
            image: "https://picsum.photos/400/250?random=12"
        }
    ];
    
    const articlesContainer = document.getElementById('articles-container');
    const filterButtons = document.querySelectorAll('.filter-btn');
    
    // Render all articles initially
    renderArticles(articlesData, articlesContainer);
    
    // Setup filter functionality
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Update active filter button
            filterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            const filter = this.getAttribute('data-filter');
            const filteredArticles = filter === 'all' 
                ? articlesData 
                : articlesData.filter(article => article.category === filter);
            
            renderArticles(filteredArticles, articlesContainer);
        });
    });
}

function renderArticles(articles, container) {
    container.innerHTML = '';
    
    if (articles.length === 0) {
        container.innerHTML = '<p class="no-articles">No articles found in this category.</p>';
        return;
    }
    
    articles.forEach(article => {
        const articleElement = document.createElement('article');
        articleElement.className = 'article-card';
        articleElement.innerHTML = `
            <div class="article-image">
                <img src="${article.image}" alt="${article.title}">
            </div>
            <div class="article-content">
                <h3 class="article-title">${article.title}</h3>
                <p class="article-excerpt">${article.excerpt}</p>
                <div class="article-meta">
                    <span class="article-date">${article.date}</span>
                    <span class="article-category">${article.category.charAt(0).toUpperCase() + article.category.slice(1)}</span>
                </div>
            </div>
        `;
        container.appendChild(articleElement);
    });
}

// Newsletter Form
function initNewsletter() {
    const newsletterForm = document.getElementById('newsletter-form');
    
    if (!newsletterForm) return;
    
    newsletterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const emailInput = this.querySelector('input[type="email"]');
        const email = emailInput.value.trim();
        
        if (validateEmail(email)) {
            // In a real application, you would send this to a server
            alert(`Thank you for subscribing with email: ${email}`);
            emailInput.value = '';
        } else {
            alert('Please enter a valid email address.');
        }
    });
}

function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Mobile Menu Toggle
function initMobileMenu() {
    const menuToggle = document.querySelector('.menu-toggle');
    const navMenu = document.querySelector('.nav-menu');
    
    if (!menuToggle || !navMenu) return;
    
    menuToggle.addEventListener('click', function() {
        navMenu.classList.toggle('active');
        this.classList.toggle('active');
        
        // Animate hamburger to X
        const spans = this.querySelectorAll('span');
        if (navMenu.classList.contains('active')) {
            spans[0].style.transform = 'rotate(45deg) translate(5px, 5px)';
            spans[1].style.opacity = '0';
            spans[2].style.transform = 'rotate(-45deg) translate(7px, -6px)';
        } else {
            spans[0].style.transform = 'none';
            spans[1].style.opacity = '1';
            spans[2].style.transform = 'none';
        }
    });
    
    // Close menu when clicking outside
    document.addEventListener('click', function(e) {
        if (!navMenu.contains(e.target) && !menuToggle.contains(e.target)) {
            closeMobileMenu();
        }
    });
}

function closeMobileMenu() {
    const menuToggle = document.querySelector('.menu-toggle');
    const navMenu = document.querySelector('.nav-menu');
    const spans = menuToggle?.querySelectorAll('span');
    
    if (navMenu?.classList.contains('active')) {
        navMenu.classList.remove('active');
        menuToggle.classList.remove('active');
        
        if (spans) {
            spans[0].style.transform = 'none';
            spans[1].style.opacity = '1';
            spans[2].style.transform = 'none';
        }
    }
}

// Smooth scrolling for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        if (this.getAttribute('href') === '#') return;
        
        const targetId = this.getAttribute('href').substring(1);
        const targetElement = document.getElementById(targetId);
        
        if (targetElement) {
            e.preventDefault();
            window.scrollTo({
                top: targetElement.offsetTop - 70,
                behavior: 'smooth'
            });
        }
    });
});