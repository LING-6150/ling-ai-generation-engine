// DOM Elements
const menuToggle = document.querySelector('.menu-toggle');
const navLinks = document.querySelector('.nav-links');
const navLinksItems = document.querySelectorAll('.nav-link');
const categoryBtns = document.querySelectorAll('.category-btn');
const menuCategories = document.querySelectorAll('.menu-category');
const reservationForm = document.getElementById('reservation-form');
const formMessage = document.getElementById('form-message');
const dateInput = document.getElementById('date');

// Set minimum date to today for reservation
const today = new Date().toISOString().split('T')[0];
dateInput.min = today;

// Mobile Navigation Toggle
menuToggle.addEventListener('click', () => {
    navLinks.classList.toggle('active');
    menuToggle.classList.toggle('active');
});

// Close mobile menu when clicking on a link
navLinksItems.forEach(link => {
    link.addEventListener('click', () => {
        navLinks.classList.remove('active');
        menuToggle.classList.remove('active');
        
        // Update active nav link
        navLinksItems.forEach(item => item.classList.remove('active'));
        link.classList.add('active');
    });
});

// Smooth scrolling for navigation links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        e.preventDefault();
        
        const targetId = this.getAttribute('href');
        if (targetId === '#') return;
        
        const targetElement = document.querySelector(targetId);
        if (targetElement) {
            // Update active nav link
            navLinksItems.forEach(item => item.classList.remove('active'));
            this.classList.add('active');
            
            // Scroll to target
            window.scrollTo({
                top: targetElement.offsetTop - 70,
                behavior: 'smooth'
            });
        }
    });
});

// Menu Category Switching
categoryBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // Remove active class from all buttons and categories
        categoryBtns.forEach(b => b.classList.remove('active'));
        menuCategories.forEach(category => category.classList.remove('active'));
        
        // Add active class to clicked button
        btn.classList.add('active');
        
        // Show corresponding category
        const categoryId = btn.getAttribute('data-category');
        const targetCategory = document.getElementById(categoryId);
        if (targetCategory) {
            targetCategory.classList.add('active');
        }
    });
});

// Form Validation and Submission
reservationForm.addEventListener('submit', function(e) {
    e.preventDefault();
    
    // Reset previous error messages
    clearErrorMessages();
    formMessage.style.display = 'none';
    
    // Get form values
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const guests = document.getElementById('guests').value;
    const date = document.getElementById('date').value;
    const time = document.getElementById('time').value;
    
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
    
    // Validate guests
    if (!guests) {
        showError('guests-error', 'Please select number of guests');
        isValid = false;
    }
    
    // Validate date
    if (!date) {
        showError('date-error', 'Please select a date');
        isValid = false;
    }
    
    // Validate time
    if (!time) {
        showError('time-error', 'Please select a time');
        isValid = false;
    }
    
    // If form is valid, show success message
    if (isValid) {
        // In a real application, you would send the data to a server here
        // For this demo, we'll just show a success message
        
        formMessage.textContent = `Thank you, ${name}! Your reservation for ${guests} people on ${formatDate(date)} at ${formatTime(time)} has been received. We will contact you shortly to confirm.`;
        formMessage.className = 'form-message success';
        formMessage.style.display = 'block';
        
        // Reset form
        reservationForm.reset();
        
        // Scroll to show success message
        formMessage.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    } else {
        formMessage.textContent = 'Please fix the errors above before submitting.';
        formMessage.className = 'form-message error';
        formMessage.style.display = 'block';
    }
});

// Helper Functions
function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = message;
    }
}

function clearErrorMessages() {
    const errorElements = document.querySelectorAll('.error-message');
    errorElements.forEach(element => {
        element.textContent = '';
    });
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function isValidPhone(phone) {
    // Simple phone validation - accepts various formats
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    const cleanedPhone = phone.replace(/[\s\-\(\)\.]/g, '');
    return phoneRegex.test(cleanedPhone);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function formatTime(timeString) {
    const [hours, minutes] = timeString.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12;
    return `${displayHour}:${minutes} ${ampm}`;
}

// Update active nav link based on scroll position
window.addEventListener('scroll', () => {
    const sections = document.querySelectorAll('section[id]');
    const scrollPosition = window.scrollY + 100;
    
    sections.forEach(section => {
        const sectionTop = section.offsetTop;
        const sectionHeight = section.offsetHeight;
        const sectionId = section.getAttribute('id');
        
        if (scrollPosition >= sectionTop && scrollPosition < sectionTop + sectionHeight) {
            navLinksItems.forEach(link => {
                link.classList.remove('active');
                if (link.getAttribute('href') === `#${sectionId}`) {
                    link.classList.add('active');
                }
            });
        }
    });
});

// Initialize the page
document.addEventListener('DOMContentLoaded', () => {
    // Set today's date as default for date input
    dateInput.value = today;
    
    // Add some sample menu items if needed (for demonstration)
    console.log('Restaurant website loaded successfully!');
});