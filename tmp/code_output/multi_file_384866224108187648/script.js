// DOM Elements
const menuToggle = document.querySelector('.menu-toggle');
const navLinks = document.querySelector('.nav-links');
const meowButton = document.getElementById('meowButton');
const catMouth = document.getElementById('catMouth');
const expressionButtons = document.querySelectorAll('.expr-btn');
const contactForm = document.getElementById('contactForm');
const newsletterForm = document.getElementById('newsletterForm');
const currentYearElement = document.getElementById('currentYear');

// Set current year in footer
currentYearElement.textContent = new Date().getFullYear();

// Mobile menu toggle
menuToggle.addEventListener('click', () => {
    navLinks.classList.toggle('active');
    
    // Animate hamburger to X
    const spans = menuToggle.querySelectorAll('span');
    if (navLinks.classList.contains('active')) {
        spans[0].style.transform = 'rotate(45deg) translate(6px, 6px)';
        spans[1].style.opacity = '0';
        spans[2].style.transform = 'rotate(-45deg) translate(6px, -6px)';
    } else {
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
    }
});

// Close mobile menu when clicking on a link
document.querySelectorAll('.nav-links a').forEach(link => {
    link.addEventListener('click', () => {
        navLinks.classList.remove('active');
        const spans = menuToggle.querySelectorAll('span');
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
    });
});

// Meow button functionality
meowButton.addEventListener('click', () => {
    // Create a meow sound using the Web Audio API
    playMeowSound();
    
    // Animate the cat's mouth
    catMouth.style.transform = 'scaleY(1.5)';
    catMouth.style.transition = 'transform 0.1s';
    
    // Change button text temporarily
    const originalText = meowButton.textContent;
    meowButton.textContent = 'Meow!';
    meowButton.disabled = true;
    
    // Reset after animation
    setTimeout(() => {
        catMouth.style.transform = 'scaleY(1)';
    }, 100);
    
    setTimeout(() => {
        meowButton.textContent = originalText;
        meowButton.disabled = false;
    }, 1000);
});

// Function to play meow sound using Web Audio API
function playMeowSound() {
    try {
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();
        
        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);
        
        // Configure the meow sound
        oscillator.frequency.setValueAtTime(200, audioContext.currentTime);
        oscillator.frequency.exponentialRampToValueAtTime(100, audioContext.currentTime + 0.3);
        
        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);
        
        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.3);
    } catch (error) {
        console.log("Audio context not supported, but the visual animation still works!");
    }
}

// Expression buttons functionality
expressionButtons.forEach(button => {
    button.addEventListener('click', () => {
        const expression = button.getAttribute('data-expression');
        changeCatExpression(expression);
        
        // Highlight active button
        expressionButtons.forEach(btn => btn.style.backgroundColor = '');
        button.style.backgroundColor = 'var(--primary-yellow)';
    });
});

// Function to change cat expression
function changeCatExpression(expression) {
    const leftEye = document.querySelector('.left-eye');
    const rightEye = document.querySelector('.right-eye');
    const mouth = document.getElementById('catMouth');
    
    // Reset to default
    leftEye.style.height = '60px';
    leftEye.style.borderRadius = '50%';
    rightEye.style.height = '60px';
    rightEye.style.borderRadius = '50%';
    mouth.style.borderBottom = '3px solid #333';
    mouth.style.borderRadius = '0 0 50% 50%';
    mouth.style.width = '40px';
    mouth.style.height = '20px';
    
    // Apply expression changes
    switch(expression) {
        case 'happy':
            // Smiling eyes
            leftEye.style.height = '40px';
            leftEye.style.borderRadius = '50% 50% 0 0';
            rightEye.style.height = '40px';
            rightEye.style.borderRadius = '50% 50% 0 0';
            
            // Smiling mouth
            mouth.style.borderBottom = '3px solid #333';
            mouth.style.borderRadius = '0 0 50% 50%';
            mouth.style.width = '60px';
            break;
            
        case 'sleepy':
            // Half-closed eyes
            leftEye.style.height = '20px';
            leftEye.style.borderRadius = '50% 50% 0 0';
            rightEye.style.height = '20px';
            rightEye.style.borderRadius = '50% 50% 0 0';
            
            // Small mouth
            mouth.style.borderBottom = '2px solid #333';
            mouth.style.width = '30px';
            break;
            
        case 'playful':
            // Wide eyes
            leftEye.style.height = '70px';
            rightEye.style.height = '70px';
            
            // Open mouth (surprised/playful)
            mouth.style.borderBottom = 'none';
            mouth.style.border = '3px solid #333';
            mouth.style.borderRadius = '50%';
            mouth.style.height = '30px';
            break;
    }
}

// Contact form submission
contactForm.addEventListener('submit', (e) => {
    e.preventDefault();
    
    // Get form values
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const message = document.getElementById('message').value;
    
    // In a real application, you would send this data to a server
    // For this demo, we'll just show a confirmation message
    alert(`Thank you, ${name}! Your yellow cat story has been received. We'll be in touch at ${email} if needed.`);
    
    // Reset form
    contactForm.reset();
    
    // Change cat expression to happy
    changeCatExpression('happy');
    expressionButtons.forEach(btn => {
        btn.style.backgroundColor = btn.getAttribute('data-expression') === 'happy' ? 'var(--primary-yellow)' : '';
    });
});

// Newsletter form submission
newsletterForm.addEventListener('submit', (e) => {
    e.preventDefault();
    
    const emailInput = newsletterForm.querySelector('input[type="email"]');
    const email = emailInput.value;
    
    // In a real application, you would send this to a newsletter service
    // For this demo, we'll just show a confirmation
    alert(`Thank you for subscribing with ${email}! You'll receive yellow cat updates soon.`);
    
    // Reset form
    newsletterForm.reset();
});

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

// Add some interactive hover effects to gallery items
document.querySelectorAll('.gallery-item').forEach(item => {
    item.addEventListener('mouseenter', () => {
        // Add a subtle scale effect
        item.style.transform = 'translateY(-10px) scale(1.02)';
    });
    
    item.addEventListener('mouseleave', () => {
        item.style.transform = 'translateY(0) scale(1)';
    });
});

// Initialize with happy expression
changeCatExpression('happy');
expressionButtons[0].style.backgroundColor = 'var(--primary-yellow)';