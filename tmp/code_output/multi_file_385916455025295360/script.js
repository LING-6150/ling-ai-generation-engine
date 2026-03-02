// DOM Elements
const navToggle = document.querySelector('.nav-toggle');
const navMenu = document.querySelector('.nav-menu');
const navLinks = document.querySelectorAll('.nav-link');
const meowBtn = document.getElementById('meowBtn');
const meowSound = document.getElementById('meowSound');
const loadMoreBtn = document.getElementById('loadMoreBtn');
const subscribeForm = document.getElementById('subscribeForm');
const formMessage = document.getElementById('formMessage');

// Mobile Navigation Toggle
navToggle.addEventListener('click', () => {
    navMenu.classList.toggle('active');
    navToggle.classList.toggle('active');
});

// Close mobile menu when clicking on a link
navLinks.forEach(link => {
    link.addEventListener('click', () => {
        navMenu.classList.remove('active');
        navToggle.classList.remove('active');
        
        // Update active link
        navLinks.forEach(l => l.classList.remove('active'));
        link.classList.add('active');
    });
});

// Meow Button Functionality
meowBtn.addEventListener('click', () => {
    meowSound.currentTime = 0;
    meowSound.play().catch(e => console.log('Audio play failed:', e));
    
    // Visual feedback
    meowBtn.textContent = 'Meow! 🐱';
    meowBtn.style.backgroundColor = '#ff6b6b';
    
    setTimeout(() => {
        meowBtn.textContent = 'Hear a Meow! 🐾';
        meowBtn.style.backgroundColor = '';
    }, 500);
});

// Load More Cats Functionality
let catCount = 4;
loadMoreBtn.addEventListener('click', () => {
    const gallery = document.querySelector('.gallery');
    
    // Create new gallery items
    for (let i = 0; i < 2; i++) {
        catCount++;
        const newItem = document.createElement('div');
        newItem.className = 'gallery-item';
        newItem.innerHTML = `
            <img src="https://picsum.photos/id/${430 + catCount}/400/300" 
                 alt="Cat ${catCount}" 
                 class="gallery-img">
        `;
        gallery.appendChild(newItem);
    }
    
    // Add animation to new items
    const newItems = gallery.querySelectorAll('.gallery-item');
    newItems.forEach((item, index) => {
        if (index >= gallery.children.length - 2) {
            item.style.opacity = '0';
            item.style.transform = 'translateY(20px)';
            
            setTimeout(() => {
                item.style.transition = 'all 0.5s ease';
                item.style.opacity = '1';
                item.style.transform = 'translateY(0)';
            }, 100);
        }
    });
    
    // Disable button after loading 8 more images
    if (catCount >= 12) {
        loadMoreBtn.disabled = true;
        loadMoreBtn.textContent = 'No More Cats Available';
        loadMoreBtn.style.opacity = '0.6';
        loadMoreBtn.style.cursor = 'not-allowed';
    }
});

// Form Submission
subscribeForm.addEventListener('submit', (e) => {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const catType = document.getElementById('catType').value;
    
    // Simple validation
    if (!name || !email || !catType) {
        showFormMessage('Please fill in all fields.', 'error');
        return;
    }
    
    if (!isValidEmail(email)) {
        showFormMessage('Please enter a valid email address.', 'error');
        return;
    }
    
    // Simulate form submission
    showFormMessage(`Thank you, ${name}! You've successfully subscribed to our cat newsletter. 🐾`, 'success');
    
    // Reset form
    subscribeForm.reset();
    
    // Store in localStorage (simulating backend)
    const subscriber = {
        name,
        email,
        catType,
        date: new Date().toISOString()
    };
    
    let subscribers = JSON.parse(localStorage.getItem('catSubscribers') || '[]');
    subscribers.push(subscriber);
    localStorage.setItem('catSubscribers', JSON.stringify(subscribers));
});

// Email validation helper
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Show form message
function showFormMessage(message, type) {
    formMessage.textContent = message;
    formMessage.className = `form-message ${type}`;
    
    // Hide message after 5 seconds
    setTimeout(() => {
        formMessage.style.opacity = '0';
        setTimeout(() => {
            formMessage.className = 'form-message';
            formMessage.style.opacity = '1';
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
            window.scrollTo({
                top: targetElement.offsetTop - 80,
                behavior: 'smooth'
            });
        }
    });
});

// Update active nav link on scroll
window.addEventListener('scroll', () => {
    let current = '';
    const sections = document.querySelectorAll('section');
    
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

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    console.log('Purrfect Cats website loaded successfully! 🐱');
    
    // Check for existing subscribers
    const subscribers = JSON.parse(localStorage.getItem('catSubscribers') || '[]');
    console.log(`Total subscribers: ${subscribers.length}`);
});