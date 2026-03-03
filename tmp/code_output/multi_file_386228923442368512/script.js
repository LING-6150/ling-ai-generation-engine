// Event Website - Main JavaScript File

// DOM Content Loaded Event
document.addEventListener('DOMContentLoaded', function() {
    // Initialize all functionality
    initNavigation();
    initScheduleTabs();
    initRegistrationForm();
    initSmoothScrolling();
});

// Navigation Menu Toggle
function initNavigation() {
    const navToggle = document.querySelector('.nav-toggle');
    const navMenu = document.querySelector('.nav-menu');
    const navLinks = document.querySelectorAll('.nav-link');

    // Toggle mobile menu
    navToggle.addEventListener('click', function() {
        navMenu.classList.toggle('active');
        this.classList.toggle('active');
    });

    // Close mobile menu when clicking a link
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            navMenu.classList.remove('active');
            navToggle.classList.remove('active');
            
            // Update active link
            navLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // Update active link on scroll
    window.addEventListener('scroll', function() {
        const sections = document.querySelectorAll('section');
        const scrollPos = window.scrollY + 100;

        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionHeight = section.clientHeight;
            const sectionId = section.getAttribute('id');

            if (scrollPos >= sectionTop && scrollPos < sectionTop + sectionHeight) {
                navLinks.forEach(link => {
                    link.classList.remove('active');
                    if (link.getAttribute('href') === `#${sectionId}`) {
                        link.classList.add('active');
                    }
                });
            }
        });
    });
}

// Schedule Tab Switching
function initScheduleTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const daySchedules = document.querySelectorAll('.day-schedule');

    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const day = this.getAttribute('data-day');
            
            // Update active tab button
            tabButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            // Show selected day's schedule
            daySchedules.forEach(schedule => {
                schedule.classList.remove('active');
                if (schedule.id === day) {
                    schedule.classList.add('active');
                }
            });
        });
    });
}

// Registration Form Handling
function initRegistrationForm() {
    const registrationForm = document.getElementById('registration-form');
    const successMessage = document.getElementById('success-message');

    // Form validation
    registrationForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (validateForm()) {
            // Simulate form submission
            simulateSubmission();
        }
    });

    // Real-time validation
    const formInputs = registrationForm.querySelectorAll('input, select');
    formInputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        
        input.addEventListener('input', function() {
            clearError(this);
        });
    });
}

// Form Validation Functions
function validateForm() {
    const form = document.getElementById('registration-form');
    let isValid = true;

    // Validate required fields
    const requiredFields = form.querySelectorAll('[required]');
    requiredFields.forEach(field => {
        if (!validateField(field)) {
            isValid = false;
        }
    });

    // Validate at least one day is selected
    const dayCheckboxes = form.querySelectorAll('input[name="days"]:checked');
    const daysError = document.getElementById('days-error');
    
    if (dayCheckboxes.length === 0) {
        daysError.textContent = 'Please select at least one day';
        isValid = false;
    } else {
        daysError.textContent = '';
    }

    return isValid;
}

function validateField(field) {
    const errorElement = document.getElementById(`${field.name || field.id}-error`);
    
    if (!errorElement) return true;

    // Clear previous error
    errorElement.textContent = '';

    // Check required fields
    if (field.hasAttribute('required') && !field.value.trim()) {
        errorElement.textContent = 'This field is required';
        return false;
    }

    // Email validation
    if (field.type === 'email' && field.value.trim()) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(field.value)) {
            errorElement.textContent = 'Please enter a valid email address';
            return false;
        }
    }

    return true;
}

function clearError(field) {
    const errorElement = document.getElementById(`${field.name || field.id}-error`);
    if (errorElement) {
        errorElement.textContent = '';
    }
}

// Simulate form submission
function simulateSubmission() {
    const form = document.getElementById('registration-form');
    const successMessage = document.getElementById('success-message');
    
    // Show loading state
    const submitBtn = form.querySelector('.btn-submit');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Processing...';
    submitBtn.disabled = true;

    // Simulate API call delay
    setTimeout(() => {
        // Hide form and show success message
        form.classList.add('hidden');
        successMessage.classList.remove('hidden');
        
        // Reset form (in a real app, this would happen after successful server response)
        form.reset();
        
        // Reset button state
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        
        // Scroll to success message
        successMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
        
        // Log form data (in a real app, this would be sent to a server)
        const formData = new FormData(form);
        const formObject = {};
        formData.forEach((value, key) => {
            formObject[key] = value;
        });
        console.log('Form submitted with data:', formObject);
    }, 1500);
}

// Smooth Scrolling for Anchor Links
function initSmoothScrolling() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                const headerHeight = document.querySelector('.header').offsetHeight;
                const targetPosition = targetElement.offsetTop - headerHeight;
                
                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// Add some interactive effects to schedule items
document.addEventListener('DOMContentLoaded', function() {
    const scheduleItems = document.querySelectorAll('.schedule-item');
    
    scheduleItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-4px)';
            this.style.boxShadow = '0 12px 20px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)';
        });
        
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)';
        });
    });
});