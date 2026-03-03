// Gallery Data
const galleryData = [
    { id: 1, src: 'https://picsum.photos/400/300?random=11', category: 'portrait', title: 'Urban Portrait', description: 'Street style portrait in downtown' },
    { id: 2, src: 'https://picsum.photos/400/300?random=12', category: 'landscape', title: 'Mountain Sunrise', description: 'Early morning in the mountains' },
    { id: 3, src: 'https://picsum.photos/400/300?random=13', category: 'urban', title: 'City Lights', description: 'Night cityscape' },
    { id: 4, src: 'https://picsum.photos/400/300?random=14', category: 'nature', title: 'Forest Path', description: 'Sunlight through the trees' },
    { id: 5, src: 'https://picsum.photos/400/300?random=15', category: 'portrait', title: 'Studio Session', description: 'Professional studio portrait' },
    { id: 6, src: 'https://picsum.photos/400/300?random=16', category: 'landscape', title: 'Ocean Cliffs', description: 'Dramatic coastal landscape' },
    { id: 7, src: 'https://picsum.photos/400/300?random=17', category: 'urban', title: 'Architecture', description: 'Modern building details' },
    { id: 8, src: 'https://picsum.photos/400/300?random=18', category: 'nature', title: 'Wildflowers', description: 'Close-up of field flowers' },
    { id: 9, src: 'https://picsum.photos/400/300?random=19', category: 'portrait', title: 'Candid Moment', description: 'Natural outdoor portrait' },
    { id: 10, src: 'https://picsum.photos/400/300?random=20', category: 'landscape', title: 'Desert Dunes', description: 'Golden hour in the desert' },
    { id: 11, src: 'https://picsum.photos/400/300?random=21', category: 'urban', title: 'Street Art', description: 'Colorful urban graffiti' },
    { id: 12, src: 'https://picsum.photos/400/300?random=22', category: 'nature', title: 'Waterfall', description: 'Long exposure waterfall' }
];

// DOM Elements
const navLinks = document.querySelectorAll('.nav-link');
const menuToggle = document.querySelector('.menu-toggle');
const navMenu = document.querySelector('.nav-menu');
const pageSections = document.querySelectorAll('.page-section');
const filterButtons = document.querySelectorAll('.filter-btn');
const galleryGrid = document.querySelector('.gallery-grid');
const lightbox = document.getElementById('lightbox');
const lightboxImage = document.querySelector('.lightbox-image');
const lightboxTitle = document.querySelector('.lightbox-title');
const lightboxCategory = document.querySelector('.lightbox-category');
const lightboxClose = document.querySelector('.lightbox-close');
const lightboxPrev = document.querySelector('.lightbox-nav.prev');
const lightboxNext = document.querySelector('.lightbox-nav.next');
const contactForm = document.getElementById('contactForm');

// Current state
let currentFilter = 'all';
let currentLightboxIndex = 0;
let filteredGalleryItems = [];

// Initialize the application
function init() {
    renderGallery();
    setupEventListeners();
}

// Render gallery items
function renderGallery() {
    galleryGrid.innerHTML = '';
    
    filteredGalleryItems = currentFilter === 'all' 
        ? galleryData 
        : galleryData.filter(item => item.category === currentFilter);
    
    filteredGalleryItems.forEach((item, index) => {
        const galleryItem = document.createElement('div');
        galleryItem.className = 'gallery-item';
        galleryItem.dataset.index = index;
        galleryItem.dataset.category = item.category;
        
        galleryItem.innerHTML = `
            <img src="${item.src}" alt="${item.title}">
            <div class="gallery-item-overlay">
                <h3>${item.title}</h3>
                <p>${item.description}</p>
            </div>
        `;
        
        galleryGrid.appendChild(galleryItem);
    });
    
    // Add click event to gallery items
    document.querySelectorAll('.gallery-item').forEach(item => {
        item.addEventListener('click', () => openLightbox(parseInt(item.dataset.index)));
    });
}

// Setup all event listeners
function setupEventListeners() {
    // Navigation
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = link.getAttribute('href').substring(1);
            navigateToSection(targetId);
        });
    });
    
    // Mobile menu toggle
    menuToggle.addEventListener('click', () => {
        menuToggle.classList.toggle('active');
        navMenu.classList.toggle('active');
    });
    
    // Close mobile menu when clicking a link
    navMenu.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', () => {
            menuToggle.classList.remove('active');
            navMenu.classList.remove('active');
        });
    });
    
    // Gallery filter buttons
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Update active button
            filterButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            
            // Update filter and re-render
            currentFilter = button.dataset.filter;
            renderGallery();
        });
    });
    
    // Lightbox controls
    lightboxClose.addEventListener('click', closeLightbox);
    lightboxPrev.addEventListener('click', showPrevImage);
    lightboxNext.addEventListener('click', showNextImage);
    
    // Close lightbox on background click
    lightbox.addEventListener('click', (e) => {
        if (e.target === lightbox) {
            closeLightbox();
        }
    });
    
    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
        if (lightbox.classList.contains('active')) {
            switch(e.key) {
                case 'Escape':
                    closeLightbox();
                    break;
                case 'ArrowLeft':
                    showPrevImage();
                    break;
                case 'ArrowRight':
                    showNextImage();
                    break;
            }
        }
    });
    
    // Contact form submission
    if (contactForm) {
        contactForm.addEventListener('submit', handleContactSubmit);
    }
    
    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                window.scrollTo({
                    top: targetElement.offsetTop - 70,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// Navigate to specific section
function navigateToSection(sectionId) {
    // Update active navigation link
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === `#${sectionId}`) {
            link.classList.add('active');
        }
    });
    
    // Show target section
    pageSections.forEach(section => {
        section.classList.remove('active');
        if (section.id === sectionId) {
            section.classList.add('active');
        }
    });
    
    // Scroll to top of section
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        window.scrollTo({
            top: targetSection.offsetTop - 70,
            behavior: 'smooth'
        });
    }
}

// Lightbox functions
function openLightbox(index) {
    currentLightboxIndex = index;
    updateLightbox();
    lightbox.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeLightbox() {
    lightbox.classList.remove('active');
    document.body.style.overflow = 'auto';
}

function showPrevImage() {
    currentLightboxIndex = (currentLightboxIndex - 1 + filteredGalleryItems.length) % filteredGalleryItems.length;
    updateLightbox();
}

function showNextImage() {
    currentLightboxIndex = (currentLightboxIndex + 1) % filteredGalleryItems.length;
    updateLightbox();
}

function updateLightbox() {
    const item = filteredGalleryItems[currentLightboxIndex];
    if (item) {
        lightboxImage.src = item.src;
        lightboxImage.alt = item.title;
        lightboxTitle.textContent = item.title;
        lightboxCategory.textContent = item.category.charAt(0).toUpperCase() + item.category.slice(1);
    }
}

// Contact form handler
function handleContactSubmit(e) {
    e.preventDefault();
    
    const formData = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        service: document.getElementById('service').value,
        message: document.getElementById('message').value
    };
    
    // In a real application, you would send this data to a server
    console.log('Form submitted:', formData);
    
    // Show success message
    alert('Thank you for your message! I will get back to you soon.');
    
    // Reset form
    contactForm.reset();
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', init);