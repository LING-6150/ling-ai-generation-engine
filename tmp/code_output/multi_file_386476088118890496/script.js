// DOM Elements
const menuToggle = document.querySelector('.menu-toggle');
const navMenu = document.querySelector('.nav-menu');
const navLinks = document.querySelectorAll('.nav-link');
const categoryBtns = document.querySelectorAll('.category-btn');
const menuCategories = document.querySelectorAll('.menu-category');
const reservationForm = document.getElementById('reservation-form');
const formMessage = document.getElementById('form-message');

// Mobile Navigation Toggle
menuToggle.addEventListener('click', () => {
    navMenu.classList.toggle('active');
    menuToggle.innerHTML = navMenu.classList.contains('active') 
        ? '<i class="fas fa-times"></i>' 
        : '<i class="fas fa-bars"></i>';
});

// Close mobile menu when clicking on a link
navLinks.forEach(link => {
    link.addEventListener('click', () => {
        navMenu.classList.remove('active');
        menuToggle.innerHTML = '<i class="fas fa-bars"></i>';
        
        // Update active nav link
        navLinks.forEach(item => item.classList.remove('active'));
        link.classList.add('active');
    });
});

// Menu Category Filtering
categoryBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // Remove active class from all buttons
        categoryBtns.forEach(button => button.classList.remove('active'));
        
        // Add active class to clicked button
        btn.classList.add('active');
        
        // Get category from data attribute
        const category = btn.getAttribute('data-category');
        
        // Hide all menu categories
        menuCategories.forEach(cat => {
            cat.classList.remove('active');
        });
        
        // Show selected category
        document.querySelector(`.menu-category.${category}`).classList.add('active');
    });
});

// Form Validation and Submission
reservationForm.addEventListener('submit', function(e) {
    e.preventDefault();
    
    // Reset previous error messages
    clearErrorMessages();
    
    // Get form values
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const date = document.getElementById('date').value;
    const time = document.getElementById('time').value;
    const guests = document.getElementById('guests').value;
    
    let isValid = true;
    
    // Validate name
    if (!name) {
        showError('name-error', 'Please enter your full name');
        isValid = false;
    }
    
    // Validate email
    if (!email) {
        showError('email-error', 'Please enter your email address');
        isValid = false;
    } else if (!isValidEmail(email)) {
        showError('email-error', 'Please enter a valid email address');
        isValid = false;
    }
    
    // Validate phone
    if (!phone) {
        showError('phone-error', 'Please enter your phone number');
        isValid = false;
    } else if (!isValidPhone(phone)) {
        showError('phone-error', 'Please enter a valid phone number');
        isValid = false;
    }
    
    // Validate date
    if (!date) {
        showError('date-error', 'Please select a reservation date');
        isValid = false;
    } else {
        const selectedDate = new Date(date);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (selectedDate < today) {
            showError('date-error', 'Please select a future date');
            isValid = false;
        }
    }
    
    // Validate time
    if (!time) {
        showError('time-error', 'Please select a reservation time');
        isValid = false;
    }
    
    // Validate guests
    if (!guests) {
        showError('guests-error', 'Please select number of guests');
        isValid = false;
    }
    
    // If form is valid, show success message
    if (isValid) {
        // In a real application, you would send the data to a server here
        showFormMessage('success', 'Thank you! Your reservation has been submitted. We will confirm via email shortly.');
        
        // Reset form
        reservationForm.reset();
        
        // Scroll to form message
        formMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
    } else {
        showFormMessage('error', 'Please fix the errors above before submitting.');
    }
});

// Helper Functions
function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    errorElement.textContent = message;
}

function clearErrorMessages() {
    const errorElements = document.querySelectorAll('.error-message');
    errorElements.forEach(element => {
        element.textContent = '';
    });
    formMessage.textContent = '';
    formMessage.className = 'form-message';
}

function showFormMessage(type, message) {
    formMessage.textContent = message;
    formMessage.className = `form-message ${type}`;
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function isValidPhone(phone) {
    // Simple phone validation - accepts various formats
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    const digitsOnly = phone.replace(/\D/g, '');
    return phoneRegex.test(digitsOnly);
}

// Set minimum date for reservation to today
const dateInput = document.getElementById('date');
const today = new Date().toISOString().split('T')[0];
dateInput.setAttribute('min', today);

// Smooth scrolling for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        e.preventDefault();
        
        const targetId = this.getAttribute('href');
        if (targetId === '#') return;
        
        const targetElement = document.querySelector(targetId);
        if (targetElement) {
            // Update active nav link
            navLinks.forEach(link => link.classList.remove('active'));
            this.classList.add('active');
            
            // Scroll to target
            window.scrollTo({
                top: targetElement.offsetTop - 80,
                behavior: 'smooth'
            });
        }
    });
});

// Highlight active section on scroll
window.addEventListener('scroll', () => {
    let current = '';
    const sections = document.querySelectorAll('section[id]');
    
    sections.forEach(section => {
        const sectionTop = section.offsetTop;
        const sectionHeight = section.clientHeight;
        
        if (scrollY >= (sectionTop - 100)) {
            current = section.getAttribute('id');
        }
    });
    
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === `#${current}`) {
            link.classList.add('active');
        }
    });
});