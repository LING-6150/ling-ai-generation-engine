// DOM Elements
const menuToggle = document.getElementById('menuToggle');
const navLinks = document.getElementById('navLinks');
const contactForm = document.getElementById('contactForm');
const currentYearSpan = document.getElementById('currentYear');
const themeToggle = document.getElementById('themeToggle');
const themeIcon = themeToggle.querySelector('i');

// Theme Management
const STORAGE_KEY = 'portfolio-theme';
const THEMES = {
    DARK: 'dark',
    LIGHT: 'light'
};

// Initialize theme from localStorage or default to dark
function initTheme() {
    const savedTheme = localStorage.getItem(STORAGE_KEY);
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    
    // Set initial theme: saved preference > system preference > dark
    const initialTheme = savedTheme || (prefersDark ? THEMES.DARK : THEMES.DARK);
    setTheme(initialTheme);
}

// Set theme and update UI
function setTheme(theme) {
    const html = document.documentElement;
    
    if (theme === THEMES.LIGHT) {
        html.setAttribute('data-theme', THEMES.LIGHT);
        themeIcon.classList.remove('fa-moon');
        themeIcon.classList.add('fa-sun');
        themeToggle.setAttribute('aria-label', 'Switch to dark mode');
    } else {
        html.removeAttribute('data-theme');
        themeIcon.classList.remove('fa-sun');
        themeIcon.classList.add('fa-moon');
        themeToggle.setAttribute('aria-label', 'Switch to light mode');
    }
    
    // Save to localStorage
    localStorage.setItem(STORAGE_KEY, theme);
}

// Toggle theme between dark and light
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === THEMES.LIGHT ? THEMES.DARK : THEMES.LIGHT;
    setTheme(newTheme);
    
    // Add animation effect
    themeToggle.style.transform = 'scale(1.2)';
    setTimeout(() => {
        themeToggle.style.transform = 'scale(1)';
    }, 200);
}

// Mobile Menu Toggle
menuToggle.addEventListener('click', () => {
    navLinks.classList.toggle('active');
    
    // Change menu icon based on state
    const icon = menuToggle.querySelector('i');
    if (navLinks.classList.contains('active')) {
        icon.classList.remove('fa-bars');
        icon.classList.add('fa-times');
    } else {
        icon.classList.remove('fa-times');
        icon.classList.add('fa-bars');
    }
});

// Close mobile menu when clicking a link
document.querySelectorAll('.nav-links a').forEach(link => {
    link.addEventListener('click', () => {
        navLinks.classList.remove('active');
        const icon = menuToggle.querySelector('i');
        icon.classList.remove('fa-times');
        icon.classList.add('fa-bars');
    });
});

// Set current year in footer
currentYearSpan.textContent = new Date().getFullYear();

// Contact Form Submission
contactForm.addEventListener('submit', function(e) {
    e.preventDefault();
    
    // Get form data
    const formData = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        subject: document.getElementById('subject').value,
        message: document.getElementById('message').value
    };
    
    // Basic validation
    if (!formData.name || !formData.email || !formData.subject || !formData.message) {
        showNotification('Please fill in all fields.', 'error');
        return;
    }
    
    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
        showNotification('Please enter a valid email address.', 'error');
        return;
    }
    
    // In a real application, you would send the data to a server here
    // For this demo, we'll simulate a successful submission
    
    // Show loading state
    const submitBtn = contactForm.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Sending...';
    submitBtn.disabled = true;
    
    // Simulate API call
    setTimeout(() => {
        // Reset form
        contactForm.reset();
        
        // Reset button
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        
        // Show success message
        showNotification('Message sent successfully! I\'ll get back to you soon.', 'success');
        
        // Log form data (in a real app, this would be sent to a server)
        console.log('Form submitted with data:', formData);
    }, 1500);
});

// Show notification function
function showNotification(message, type) {
    // Remove any existing notifications
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    
    // Style the notification
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.padding = '15px 20px';
    notification.style.borderRadius = '4px';
    notification.style.fontWeight = '500';
    notification.style.zIndex = '9999';
    notification.style.boxShadow = '0 5px 15px rgba(0, 0, 0, 0.3)';
    notification.style.transition = 'transform 0.3s ease, opacity 0.3s ease';
    
    // Set color based on type and theme
    const isLightTheme = document.documentElement.getAttribute('data-theme') === 'light';
    
    if (type === 'success') {
        notification.style.backgroundColor = isLightTheme ? '#10b981' : '#10b981';
        notification.style.color = 'white';
    } else {
        notification.style.backgroundColor = isLightTheme ? '#ef4444' : '#ef4444';
        notification.style.color = 'white';
    }
    
    // Add to DOM
    document.body.appendChild(notification);
    
    // Animate in
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
        notification.style.opacity = '1';
    }, 10);
    
    // Initially position off-screen
    notification.style.transform = 'translateX(100%)';
    notification.style.opacity = '0';
    
    // Remove after 5 seconds
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        notification.style.opacity = '0';
        
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 5000);
}

// Smooth scrolling for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        e.preventDefault();
        
        const targetId = this.getAttribute('href');
        if (targetId === '#') return;
        
        const targetElement = document.querySelector(targetId);
        if (targetElement) {
            // Calculate offset for fixed navbar
            const navbarHeight = document.querySelector('.navbar').offsetHeight;
            const targetPosition = targetElement.offsetTop - navbarHeight;
            
            window.scrollTo({
                top: targetPosition,
                behavior: 'smooth'
            });
        }
    });
});

// Add scroll effect to navbar
window.addEventListener('scroll', () => {
    const navbar = document.querySelector('.navbar');
    const isLightTheme = document.documentElement.getAttribute('data-theme') === 'light';
    
    if (window.scrollY > 50) {
        if (isLightTheme) {
            navbar.style.backgroundColor = 'rgba(248, 250, 252, 0.98)';
        } else {
            navbar.style.backgroundColor = 'rgba(15, 23, 42, 0.98)';
        }
        navbar.style.boxShadow = '0 5px 20px rgba(0, 0, 0, 0.1)';
    } else {
        if (isLightTheme) {
            navbar.style.backgroundColor = 'rgba(248, 250, 252, 0.95)';
        } else {
            navbar.style.backgroundColor = 'rgba(15, 23, 42, 0.95)';
        }
        navbar.style.boxShadow = 'none';
    }
});

// Event Listeners
themeToggle.addEventListener('click', toggleTheme);

// Listen for system theme changes
window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    // Only update if user hasn't set a preference
    if (!localStorage.getItem(STORAGE_KEY)) {
        setTheme(e.matches ? THEMES.DARK : THEMES.DARK);
    }
});

// Initialize theme on page load
document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    // Initialize scroll effect
    window.dispatchEvent(new Event('scroll'));
});