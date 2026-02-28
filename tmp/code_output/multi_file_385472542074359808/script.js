// DOM Elements
const navLinks = document.querySelectorAll('.nav-link');
const pageSections = document.querySelectorAll('.page-section');
const menuToggle = document.querySelector('.menu-toggle');
const navMenu = document.querySelector('.nav-menu');
const messageForm = document.getElementById('messageForm');

// Initialize the page
document.addEventListener('DOMContentLoaded', function() {
    // Set active section based on URL hash or default to home
    const hash = window.location.hash || '#home';
    showSection(hash.substring(1));
    updateActiveNav(hash.substring(1));
    
    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href.startsWith('#') && href.length > 1) {
                e.preventDefault();
                const targetId = href.substring(1);
                showSection(targetId);
                updateActiveNav(targetId);
                updateURLHash(targetId);
                
                // Close mobile menu if open
                if (navMenu.classList.contains('active')) {
                    toggleMobileMenu();
                }
            }
        });
    });
    
    // Mobile menu toggle
    menuToggle.addEventListener('click', toggleMobileMenu);
    
    // Form submission
    messageForm.addEventListener('submit', handleFormSubmit);
    
    // Close mobile menu when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.navbar') && navMenu.classList.contains('active')) {
            toggleMobileMenu();
        }
    });
});

// Show specific section and hide others
function showSection(sectionId) {
    pageSections.forEach(section => {
        if (section.id === sectionId) {
            section.classList.add('active-section');
            // Scroll to top of section
            setTimeout(() => {
                section.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }, 100);
        } else {
            section.classList.remove('active-section');
        }
    });
}

// Update active navigation link
function updateActiveNav(sectionId) {
    navLinks.forEach(link => {
        if (link.getAttribute('data-page') === sectionId) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

// Update URL hash without scrolling
function updateURLHash(sectionId) {
    history.pushState(null, null, `#${sectionId}`);
}

// Toggle mobile menu
function toggleMobileMenu() {
    menuToggle.classList.toggle('active');
    navMenu.classList.toggle('active');
    
    // Update aria-expanded attribute for accessibility
    const isExpanded = navMenu.classList.contains('active');
    menuToggle.setAttribute('aria-expanded', isExpanded);
}

// Handle contact form submission
function handleFormSubmit(e) {
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
    
    // In a real application, you would send this data to a server
    // For this demo, we'll simulate a successful submission
    console.log('Form submitted:', formData);
    
    // Show success message
    showNotification('Thank you for your message! We will get back to you soon.', 'success');
    
    // Reset form
    messageForm.reset();
}

// Show notification message
function showNotification(message, type) {
    // Remove existing notification if present
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        border-radius: 5px;
        color: white;
        font-weight: 500;
        z-index: 1001;
        animation: slideIn 0.3s ease;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
    `;
    
    // Set background color based on type
    if (type === 'success') {
        notification.style.backgroundColor = '#10b981';
    } else {
        notification.style.backgroundColor = '#ef4444';
    }
    
    // Add animation
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
    `;
    document.head.appendChild(style);
    
    // Add to page
    document.body.appendChild(notification);
    
    // Remove after 5 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        notification.style.transform = 'translateX(100%)';
        notification.style.opacity = '0';
        
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
            if (style.parentNode) {
                style.remove();
            }
        }, 300);
    }, 5000);
}

// Handle browser back/forward buttons
window.addEventListener('popstate', function() {
    const hash = window.location.hash || '#home';
    const sectionId = hash.substring(1);
    showSection(sectionId);
    updateActiveNav(sectionId);
});