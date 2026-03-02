// DOM Elements
const meowBtn = document.getElementById('meowBtn');
const cat = document.getElementById('cat');
const butterfly = document.getElementById('butterfly');
const updateWeatherBtn = document.getElementById('updateWeather');
const notification = document.getElementById('notification');
const navLinks = document.querySelectorAll('.nav-link');
const weatherTemp = document.querySelector('.weather-temp');
const weatherDesc = document.querySelector('.weather-desc');
const weatherIcon = document.querySelector('.weather-icon');

// Weather data
const weatherConditions = [
    { temp: '72°F', desc: 'Sunny and pleasant', icon: '☀️', note: 'Perfect for park visits!' },
    { temp: '68°F', desc: 'Partly cloudy', icon: '⛅', note: 'Great weather for exploring' },
    { temp: '75°F', desc: 'Warm and breezy', icon: '🌤️', note: 'Ideal for outdoor fun' },
    { temp: '65°F', desc: 'Light rain', icon: '🌦️', note: 'Bring an umbrella!' },
    { temp: '70°F', desc: 'Clear skies', icon: '🌞', note: 'Beautiful park day' }
];

// Cat meow sound simulation
function playMeow() {
    // Create audio context for meow sound
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);
    
    // Configure meow sound
    oscillator.type = 'sine';
    oscillator.frequency.setValueAtTime(200, audioContext.currentTime);
    oscillator.frequency.exponentialRampToValueAtTime(800, audioContext.currentTime + 0.1);
    oscillator.frequency.exponentialRampToValueAtTime(200, audioContext.currentTime + 0.3);
    
    gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.4);
    
    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.4);
    
    // Animate cat
    animateCat();
    
    // Show notification
    showNotification('Meow! The cat says hello! 🐱');
}

// Animate cat when meowing
function animateCat() {
    cat.style.transform = 'translateX(-50%) scale(1.1)';
    cat.style.transition = 'transform 0.2s ease';
    
    // Animate mouth
    const catMouth = document.querySelector('.cat-mouth');
    catMouth.style.height = '15px';
    catMouth.style.transition = 'height 0.2s ease';
    
    setTimeout(() => {
        cat.style.transform = 'translateX(-50%) scale(1)';
        catMouth.style.height = '10px';
    }, 200);
}

// Butterfly animation
function animateButterfly() {
    const container = document.querySelector('.cat-container');
    const containerWidth = container.clientWidth;
    const containerHeight = container.clientHeight;
    
    // Random position for butterfly
    const randomX = Math.random() * (containerWidth - 60);
    const randomY = Math.random() * (containerHeight / 2);
    
    butterfly.style.left = `${randomX}px`;
    butterfly.style.top = `${randomY}px`;
    
    // Random color for butterfly
    const colors = ['#FF6B6B', '#4ECDC4', '#FFD166', '#06D6A0', '#118AB2'];
    const randomColor = colors[Math.floor(Math.random() * colors.length)];
    butterfly.style.background = randomColor;
    
    // Make cat look at butterfly
    const catEyes = document.querySelectorAll('.cat-eye');
    const butterflyX = parseFloat(butterfly.style.left);
    const catX = containerWidth / 2;
    
    catEyes.forEach(eye => {
        if (butterflyX < catX) {
            eye.style.transform = 'translateX(-5px)';
        } else {
            eye.style.transform = 'translateX(5px)';
        }
    });
    
    // Reset eye position after delay
    setTimeout(() => {
        catEyes.forEach(eye => {
            eye.style.transform = 'translateX(0)';
        });
    }, 1000);
}

// Update weather display
function updateWeather() {
    const randomWeather = weatherConditions[Math.floor(Math.random() * weatherConditions.length)];
    
    weatherTemp.textContent = randomWeather.temp;
    weatherDesc.textContent = randomWeather.desc;
    weatherIcon.textContent = randomWeather.icon;
    
    // Update weather note
    const weatherNote = document.querySelector('.weather-note');
    weatherNote.textContent = randomWeather.note;
    
    showNotification(`Weather updated: ${randomWeather.desc} ${randomWeather.icon}`);
}

// Show notification
function showNotification(message) {
    notification.textContent = message;
    notification.classList.add('show');
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

// Smooth scrolling for navigation
function setupSmoothScrolling() {
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Update active nav link
            navLinks.forEach(navLink => navLink.classList.remove('active'));
            link.classList.add('active');
            
            // Scroll to section
            const targetId = link.getAttribute('href');
            const targetSection = document.querySelector(targetId);
            
            if (targetSection) {
                window.scrollTo({
                    top: targetSection.offsetTop - 80,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// Update active nav link on scroll
function updateActiveNavLink() {
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
}

// Initialize cat animations
function initCatAnimations() {
    // Random cat movements
    setInterval(() => {
        const movements = ['sleep', 'stretch', 'lookAround'];
        const randomMovement = movements[Math.floor(Math.random() * movements.length)];
        
        switch(randomMovement) {
            case 'sleep':
                // Animate eyes closing
                const catEyes = document.querySelectorAll('.cat-eye');
                catEyes.forEach(eye => {
                    eye.style.height = '5px';
                    setTimeout(() => {
                        eye.style.height = '25px';
                    }, 2000);
                });
                break;
                
            case 'stretch':
                cat.style.transform = 'translateX(-50%) scaleX(1.2)';
                setTimeout(() => {
                    cat.style.transform = 'translateX(-50%) scaleX(1)';
                }, 1000);
                break;
                
            case 'lookAround':
                const eyes = document.querySelectorAll('.cat-eye');
                const randomDirection = Math.random() > 0.5 ? -5 : 5;
                eyes.forEach(eye => {
                    eye.style.transform = `translateX(${randomDirection}px)`;
                });
                setTimeout(() => {
                    eyes.forEach(eye => {
                        eye.style.transform = 'translateX(0)';
                    });
                }, 500);
                break;
        }
    }, 5000);
}

// Event Listeners
meowBtn.addEventListener('click', playMeow);
updateWeatherBtn.addEventListener('click', updateWeather);

// Butterfly click event
butterfly.addEventListener('click', () => {
    animateButterfly();
    showNotification('The cat is chasing the butterfly! 🦋');
});

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    setupSmoothScrolling();
    initCatAnimations();
    
    // Initial butterfly animation
    setTimeout(animateButterfly, 1000);
    
    // Update butterfly position periodically
    setInterval(animateButterfly, 8000);
    
    // Update active nav link on scroll
    window.addEventListener('scroll', updateActiveNavLink);
    
    // Initial active nav link
    updateActiveNavLink();
    
    // Add hover effect to cat
    cat.addEventListener('mouseenter', () => {
        cat.style.transform = 'translateX(-50%) scale(1.05)';
        cat.style.transition = 'transform 0.3s ease';
    });
    
    cat.addEventListener('mouseleave', () => {
        cat.style.transform = 'translateX(-50%) scale(1)';
    });
});