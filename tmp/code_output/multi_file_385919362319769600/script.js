// Portfolio Website JavaScript

// DOM Elements
const navToggle = document.querySelector('.menu-toggle');
const navLinks = document.querySelector('.nav-links');
const contactForm = document.getElementById('contactForm');
const projectModal = document.getElementById('projectModal');
const modalClose = document.querySelector('.modal-close');
const viewProjectButtons = document.querySelectorAll('.view-project');
const currentYearSpan = document.getElementById('currentYear');

// Project data for modal
const projects = {
    1: {
        title: "E-commerce Platform",
        image: "https://picsum.photos/600/400?random=1",
        description: "A fully responsive e-commerce website built with vanilla JavaScript. Features include product catalog with filtering, shopping cart with real-time updates, user authentication, and a secure checkout process. The application uses local storage for cart persistence and implements modern UI patterns for optimal user experience.",
        tech: ["HTML5", "CSS3", "JavaScript", "LocalStorage", "Responsive Design"]
    },
    2: {
        title: "Task Management App",
        image: "https://picsum.photos/600/400?random=2",
        description: "A productivity application that helps users organize their daily tasks. Features include drag-and-drop functionality for task prioritization, due date tracking, task categorization, and local storage for data persistence. The app includes dark/light mode toggle and is fully accessible.",
        tech: ["JavaScript", "Drag & Drop API", "LocalStorage", "CSS Grid", "Accessibility"]
    },
    3: {
        title: "Weather Dashboard",
        image: "https://picsum.photos/600/400?random=3",
        description: "A weather application that fetches data from a public weather API. Displays current weather conditions, 5-day forecast, and location-based weather. Includes search functionality, temperature unit conversion, and weather alerts. The app features a clean, intuitive interface with weather icons and charts.",
        tech: ["Fetch API", "Async/Await", "Chart.js", "Geolocation API", "Responsive Design"]
    }
};

// Initialize the portfolio
function initPortfolio() {
    // Set current year in footer
    currentYearSpan.textContent = new Date().getFullYear();
    
    // Initialize event listeners
    setupEventListeners();
    
    // Add scroll effect to navbar
    setupNavbarScroll();
    
    // Initialize form validation
    setupFormValidation();
}

// Set up all event listeners
function setupEventListeners() {
    // Mobile menu toggle
    navToggle.addEventListener('click', toggleMobileMenu);
    
    // Close mobile menu when clicking on a link
    document.querySelectorAll('.nav-links a').forEach(link => {
        link.addEventListener('click', closeMobileMenu);
    });
    
    // Project modal functionality
    viewProjectButtons.forEach(button => {
        button.addEventListener('click', () => openProjectModal(button.dataset.project));
    });
    
    // Close modal when clicking close button or outside modal
    modalClose.addEventListener('click', closeModal);
    projectModal.addEventListener('click', (e) => {
        if (e.target === projectModal) {
            closeModal();
        }
    });
    
    // Close modal with Escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && projectModal.style.display === 'flex') {
            closeModal();
        }
    });
    
    // Form submission
    contactForm.addEventListener('submit', handleFormSubmit);
}

// Toggle mobile menu
function toggleMobileMenu() {
    navLinks.classList.toggle('active');
    navToggle.classList.toggle('active');
}

// Close mobile menu
function closeMobileMenu() {
    navLinks.classList.remove('active');
    navToggle.classList.remove('active');
}

// Navbar scroll effect
function setupNavbarScroll() {
    let lastScroll = 0;
    const navbar = document.querySelector('.navbar');
    
    window.addEventListener('scroll', () => {
        const currentScroll = window.pageYOffset;
        
        // Add shadow when scrolled
        if (currentScroll > 50) {
            navbar.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';
        } else {
            navbar.style.boxShadow = 'none';
        }
        
        // Hide/show navbar on scroll (mobile)
        if (window.innerWidth < 768) {
            if (currentScroll > lastScroll && currentScroll > 100) {
                navbar.style.transform = 'translateY(-100%)';
            } else {
                navbar.style.transform = 'translateY(0)';
            }
        }
        
        lastScroll = currentScroll;
    });
}

// Open project modal
function openProjectModal(projectId) {
    const project = projects[projectId];
    
    if (!project) return;
    
    // Update modal content
    document.getElementById('modalTitle').textContent = project.title;
    document.getElementById('modalImage').src = project.image;
    document.getElementById('modalImage').alt = project.title;
    document.getElementById('modalDescription').textContent = project.description;
    
    // Update tech tags
    const modalTech = document.getElementById('modalTech');
    modalTech.innerHTML = '';
    project.tech.forEach(tech => {
        const tag = document.createElement('span');
        tag.className = 'tech-tag';
        tag.textContent = tech;
        modalTech.appendChild(tag);
    });
    
    // Show modal
    projectModal.style.display = 'flex';
    document.body.style.overflow = 'hidden'; // Prevent background scrolling
}

// Close modal
function closeModal() {
    projectModal.style.display = 'none';
    document.body.style.overflow = 'auto'; // Re-enable scrolling
}

// Form validation setup
function setupFormValidation() {
    const inputs = contactForm.querySelectorAll('input, textarea');
    
    inputs.forEach(input => {
        // Add validation on blur
        input.addEventListener('blur', () => {
            validateField(input);
        });
        
        // Remove error on input
        input.addEventListener('input', () => {
            clearError(input);
        });
    });
}

// Validate individual form field
function validateField(field) {
    const value = field.value.trim();
    let isValid = true;
    let errorMessage = '';
    
    // Clear previous error
    clearError(field);
    
    // Required validation
    if (field.hasAttribute('required') && !value) {
        isValid = false;
        errorMessage = 'This field is required';
    }
    
    // Email validation
    if (field.type === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            isValid = false;
            errorMessage = 'Please enter a valid email address';
        }
    }
    
    // Show error if invalid
    if (!isValid) {
        showError(field, errorMessage);
    }
    
    return isValid;
}

// Show error message for field
function showError(field, message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    errorDiv.style.color = '#ef4444';
    errorDiv.style.fontSize = '0.875rem';
    errorDiv.style.marginTop = '0.25rem';
    
    field.parentNode.appendChild(errorDiv);
    field.style.borderColor = '#ef4444';
}

// Clear error from field
function clearError(field) {
    const errorDiv = field.parentNode.querySelector('.error-message');
    if (errorDiv) {
        errorDiv.remove();
    }
    field.style.borderColor = '#cbd5e1';
}

// Handle form submission
function handleFormSubmit(e) {
    e.preventDefault();
    
    // Validate all fields
    const inputs = contactForm.querySelectorAll('input, textarea');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!validateField(input)) {
            isValid = false;
        }
    });
    
    // If form is valid, simulate submission
    if (isValid) {
        // Get form data
        const formData = new FormData(contactForm);
        const data = Object.fromEntries(formData);
        
        // In a real application, you would send this data to a server
        console.log('Form submitted with data:', data);
        
        // Show success message
        showFormSuccess();
        
        // Reset form
        contactForm.reset();
    }
}

// Show form submission success message
function showFormSuccess() {
    // Create success message
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message';
    successDiv.textContent = 'Thank you for your message! I\'ll get back to you soon.';
    successDiv.style.backgroundColor = '#10b981';
    successDiv.style.color = 'white';
    successDiv.style.padding = '1rem';
    successDiv.style.borderRadius = 'var(--border-radius)';
    successDiv.style.marginTop = '1rem';
    successDiv.style.textAlign = 'center';
    
    // Insert after form
    contactForm.parentNode.insertBefore(successDiv, contactForm.nextSibling);
    
    // Remove message after 5 seconds
    setTimeout(() => {
        successDiv.remove();
    }, 5000);
}

// Smooth scroll for anchor links (fallback for browsers that don't support CSS smooth scroll)
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        const targetId = this.getAttribute('href');
        if (targetId === '#') return;
        
        const targetElement = document.querySelector(targetId);
        if (targetElement) {
            e.preventDefault();
            window.scrollTo({
                top: targetElement.offsetTop - 80, // Account for fixed navbar
                behavior: 'smooth'
            });
        }
    });
});

// Initialize portfolio when DOM is loaded
document.addEventListener('DOMContentLoaded', initPortfolio);