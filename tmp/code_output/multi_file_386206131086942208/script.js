// Main Application Script
document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const navLinks = document.querySelectorAll('.nav-link');
    const pages = document.querySelectorAll('.page');
    const menuToggle = document.querySelector('.menu-toggle');
    const navMenu = document.querySelector('.nav-menu');
    const contactForm = document.getElementById('contactForm');
    const formMessage = document.getElementById('formMessage');
    const footerLinks = document.querySelectorAll('.footer-links a');

    // Initialize the application
    initApp();

    // Initialize application
    function initApp() {
        // Set up event listeners
        setupNavigation();
        setupMobileMenu();
        setupContactForm();
        setupFooterNavigation();
        
        // Handle initial page load based on URL hash
        handleInitialPage();
    }

    // Handle initial page based on URL hash
    function handleInitialPage() {
        const hash = window.location.hash.substring(1);
        const validPages = ['home', 'about', 'contact'];
        
        if (validPages.includes(hash)) {
            switchPage(hash);
        } else {
            // Default to home page
            switchPage('home');
        }
    }

    // Set up page navigation
    function setupNavigation() {
        navLinks.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const pageId = this.getAttribute('data-page');
                switchPage(pageId);
                
                // Update URL hash without scrolling
                history.pushState(null, '', `#${pageId}`);
                
                // Close mobile menu if open
                if (navMenu.classList.contains('active')) {
                    navMenu.classList.remove('active');
                    menuToggle.classList.remove('active');
                }
            });
        });
    }

    // Set up footer navigation
    function setupFooterNavigation() {
        footerLinks.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const pageId = this.getAttribute('data-page');
                switchPage(pageId);
                
                // Update URL hash
                history.pushState(null, '', `#${pageId}`);
                
                // Scroll to top of the page
                window.scrollTo({ top: 0, behavior: 'smooth' });
            });
        });
    }

    // Switch between pages
    function switchPage(pageId) {
        // Hide all pages
        pages.forEach(page => {
            page.classList.remove('active');
        });
        
        // Show selected page
        const activePage = document.getElementById(pageId);
        if (activePage) {
            activePage.classList.add('active');
        }
        
        // Update active navigation link
        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('data-page') === pageId) {
                link.classList.add('active');
            }
        });
        
        // Scroll to top of the page
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // Set up mobile menu toggle
    function setupMobileMenu() {
        menuToggle.addEventListener('click', function() {
            navMenu.classList.toggle('active');
            this.classList.toggle('active');
            
            // Animate hamburger to X
            const hamburger = this.querySelector('.hamburger');
            if (this.classList.contains('active')) {
                hamburger.style.transform = 'rotate(45deg)';
                hamburger.style.backgroundColor = 'var(--primary-color)';
                hamburger.before.style.transform = 'rotate(-90deg) translate(-8px, 0)';
                hamburger.after.style.opacity = '0';
            } else {
                hamburger.style.transform = 'rotate(0)';
                hamburger.style.backgroundColor = 'var(--text-color)';
                hamburger.before.style.transform = 'rotate(0) translate(0, -8px)';
                hamburger.after.style.opacity = '1';
            }
        });
        
        // Close menu when clicking outside
        document.addEventListener('click', function(e) {
            if (!navMenu.contains(e.target) && !menuToggle.contains(e.target)) {
                navMenu.classList.remove('active');
                menuToggle.classList.remove('active');
            }
        });
    }

    // Set up contact form submission
    function setupContactForm() {
        if (!contactForm) return;
        
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Get form data
            const formData = new FormData(this);
            const formObject = Object.fromEntries(formData);
            
            // Simple validation
            if (!validateForm(formObject)) {
                return;
            }
            
            // Simulate form submission
            simulateFormSubmission(formObject);
        });
    }

    // Validate form data
    function validateForm(data) {
        // Reset previous messages
        formMessage.className = 'form-message';
        formMessage.style.display = 'none';
        
        // Check required fields
        const requiredFields = ['name', 'email', 'subject', 'message'];
        const missingFields = requiredFields.filter(field => !data[field].trim());
        
        if (missingFields.length > 0) {
            showFormMessage('Please fill in all required fields.', 'error');
            return false;
        }
        
        // Validate email format
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(data.email)) {
            showFormMessage('Please enter a valid email address.', 'error');
            return false;
        }
        
        return true;
    }

    // Simulate form submission (in a real app, this would be an API call)
    function simulateFormSubmission(data) {
        // Show loading state
        const submitButton = contactForm.querySelector('button[type="submit"]');
        const originalText = submitButton.textContent;
        submitButton.textContent = 'Sending...';
        submitButton.disabled = true;
        
        // Simulate API delay
        setTimeout(() => {
            // Reset button
            submitButton.textContent = originalText;
            submitButton.disabled = false;
            
            // Show success message
            showFormMessage('Thank you for your message! We\'ll get back to you soon.', 'success');
            
            // Reset form
            contactForm.reset();
            
            // Auto-hide success message after 5 seconds
            setTimeout(() => {
                formMessage.style.display = 'none';
            }, 5000);
        }, 1500);
    }

    // Show form message
    function showFormMessage(message, type) {
        formMessage.textContent = message;
        formMessage.className = `form-message ${type}`;
        formMessage.style.display = 'block';
        
        // Scroll to message
        formMessage.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    // Handle browser back/forward buttons
    window.addEventListener('popstate', function() {
        const hash = window.location.hash.substring(1);
        const validPages = ['home', 'about', 'contact'];
        
        if (validPages.includes(hash)) {
            switchPage(hash);
        }
    });

    // Add smooth scrolling for anchor links within the same page
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            
            // Only handle internal page links (not our SPA navigation)
            if (href === '#home' || href === '#about' || href === '#contact') {
                return; // Let our SPA navigation handle these
            }
            
            // Handle other anchor links
            const targetId = href.substring(1);
            const targetElement = document.getElementById(targetId);
            
            if (targetElement) {
                e.preventDefault();
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Add hover effect to service cards
    const serviceCards = document.querySelectorAll('.service-card');
    serviceCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
});