// DOM Elements
const navLinks = document.querySelectorAll('.nav-link');
const pages = document.querySelectorAll('.page');
const menuToggle = document.querySelector('.menu-toggle');
const navLinksContainer = document.querySelector('.nav-links');

// Initialize the page
document.addEventListener('DOMContentLoaded', () => {
    // Set initial active page based on hash or default to home
    const hash = window.location.hash.substring(1);
    const initialPage = hash || 'home';
    switchPage(initialPage);
    
    // Update active nav link
    updateActiveNavLink(initialPage);
});

// Navigation link click handler
navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const pageId = link.getAttribute('data-page');
        
        // Update URL hash without scrolling
        history.pushState(null, null, `#${pageId}`);
        
        // Switch page
        switchPage(pageId);
        
        // Update active nav link
        updateActiveNavLink(pageId);
        
        // Close mobile menu if open
        closeMobileMenu();
    });
});

// Mobile menu toggle
menuToggle.addEventListener('click', () => {
    menuToggle.classList.toggle('active');
    navLinksContainer.classList.toggle('active');
    
    // Prevent body scroll when menu is open
    if (menuToggle.classList.contains('active')) {
        document.body.style.overflow = 'hidden';
    } else {
        document.body.style.overflow = '';
    }
});

// Close mobile menu when clicking outside
document.addEventListener('click', (e) => {
    if (!menuToggle.contains(e.target) && !navLinksContainer.contains(e.target)) {
        closeMobileMenu();
    }
});

// Handle browser back/forward buttons
window.addEventListener('popstate', () => {
    const hash = window.location.hash.substring(1);
    const pageId = hash || 'home';
    switchPage(pageId);
    updateActiveNavLink(pageId);
});

// Function to switch between pages
function switchPage(pageId) {
    // Hide all pages
    pages.forEach(page => {
        page.classList.remove('active');
    });
    
    // Show selected page
    const targetPage = document.getElementById(`${pageId}-page`);
    if (targetPage) {
        targetPage.classList.add('active');
        
        // Scroll to top of the page
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    }
}

// Function to update active navigation link
function updateActiveNavLink(pageId) {
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('data-page') === pageId) {
            link.classList.add('active');
        }
    });
}

// Function to close mobile menu
function closeMobileMenu() {
    menuToggle.classList.remove('active');
    navLinksContainer.classList.remove('active');
    document.body.style.overflow = '';
}

// Smooth scroll for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        if (this.getAttribute('href') === '#') return;
        
        const targetId = this.getAttribute('href').substring(1);
        const targetElement = document.getElementById(targetId);
        
        if (targetElement && !targetElement.classList.contains('page')) {
            e.preventDefault();
            targetElement.scrollIntoView({
                behavior: 'smooth'
            });
        }
    });
});

// Add hover effect to skill cards
const skillCards = document.querySelectorAll('.skill-card');
skillCards.forEach(card => {
    card.addEventListener('mouseenter', () => {
        card.style.transform = 'translateY(-5px)';
    });
    
    card.addEventListener('mouseleave', () => {
        card.style.transform = 'translateY(0)';
    });
});

// Form validation for contact form (if added in the future)
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Debounce function for resize events
let resizeTimeout;
window.addEventListener('resize', () => {
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
        // Close mobile menu on resize to desktop
        if (window.innerWidth > 768) {
            closeMobileMenu();
        }
    }, 250);
});

// Add loading animation to images
const images = document.querySelectorAll('img');
images.forEach(img => {
    img.addEventListener('load', function() {
        this.style.opacity = '1';
    });
    
    // Set initial opacity for fade-in effect
    img.style.opacity = '0';
    img.style.transition = 'opacity 0.3s ease';
});

// Add scroll effect to navbar
let lastScrollTop = 0;
window.addEventListener('scroll', () => {
    const navbar = document.querySelector('.navbar');
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    
    if (scrollTop > lastScrollTop && scrollTop > 100) {
        // Scrolling down
        navbar.style.transform = 'translateY(-100%)';
    } else {
        // Scrolling up
        navbar.style.transform = 'translateY(0)';
    }
    
    lastScrollTop = scrollTop;
});