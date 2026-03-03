// Blog Data - In a real app, this would come from an API
const blogPosts = [
    {
        id: 1,
        title: "Getting Started with Vanilla JavaScript",
        excerpt: "Learn how to build modern web applications without relying on frameworks. Discover the power of native JavaScript.",
        content: `<p>In today's web development landscape, it's easy to get caught up in the latest frameworks and libraries. While tools like React, Vue, and Angular are incredibly powerful, there's something to be said for understanding and using vanilla JavaScript.</p>
        
        <h2>Why Vanilla JavaScript?</h2>
        <p>Vanilla JavaScript refers to using plain JavaScript without any additional libraries or frameworks. Here are some benefits:</p>
        
        <ul>
            <li><strong>Performance:</strong> No framework overhead means faster load times and better performance</li>
            <li><strong>Understanding:</strong> You'll develop a deeper understanding of how JavaScript actually works</li>
            <li><strong>Flexibility:</strong> You're not constrained by framework conventions</li>
            <li><strong>Maintenance:</strong> Your code won't become obsolete when frameworks change</li>
        </ul>
        
        <h2>Getting Started</h2>
        <p>To begin with vanilla JavaScript, you don't need any special setup. Just create an HTML file and start writing JavaScript. Modern browsers have excellent support for ES6+ features, making vanilla JavaScript more powerful than ever.</p>
        
        <p>Start by mastering the fundamentals: variables, functions, arrays, objects, and DOM manipulation. Once you're comfortable with these, explore more advanced topics like promises, async/await, and modules.</p>`,
        date: "June 15, 2023",
        readTime: "5 min read",
        tag: "JavaScript",
        image: "https://picsum.photos/800/400?random=10"
    },
    {
        id: 2,
        title: "The Art of Responsive Web Design",
        excerpt: "Creating websites that look great on all devices is no longer optional. Here's how to master responsive design.",
        content: `<p>Responsive web design has evolved from a nice-to-have feature to an absolute necessity. With mobile devices accounting for over half of all web traffic, your website must perform beautifully across all screen sizes.</p>
        
        <h2>Core Principles</h2>
        <p>Responsive design is built on three foundational principles:</p>
        
        <ol>
            <li><strong>Fluid Grids:</strong> Use percentage-based widths instead of fixed pixels</li>
            <li><strong>Flexible Images:</strong> Ensure images scale appropriately within their containers</li>
            <li><strong>Media Queries:</strong> Apply different styles based on device characteristics</li>
        </ol>
        
        <h2>Mobile-First Approach</h2>
        <p>Start designing for the smallest screen first, then progressively enhance for larger screens. This approach forces you to prioritize content and creates a better experience for all users.</p>
        
        <p>Remember to test on actual devices whenever possible. Emulators are helpful, but nothing beats real-world testing.</p>`,
        date: "June 10, 2023",
        readTime: "4 min read",
        tag: "Design",
        image: "https://picsum.photos/800/400?random=11"
    },
    {
        id: 3,
        title: "Building a Personal Blog from Scratch",
        excerpt: "A step-by-step guide to creating your own blog without using any frameworks or content management systems.",
        content: `<p>Creating a personal blog is one of the best ways to share your knowledge, document your journey, and establish your online presence. While platforms like WordPress and Medium are popular, building your own blog from scratch gives you complete control and valuable learning experience.</p>
        
        <h2>Planning Your Blog</h2>
        <p>Before writing any code, consider what you want your blog to achieve. Who is your audience? What topics will you cover? How often will you post?</p>
        
        <p>For a simple personal blog, you'll need:</p>
        
        <ul>
            <li>A homepage with article listings</li>
            <li>Individual article pages</li>
            <li>Navigation</li>
            <li>About and contact pages</li>
        </ul>
        
        <h2>Implementation</h2>
        <p>Start with clean HTML structure, add CSS for styling, and use JavaScript for interactivity. Store your articles in a JavaScript array or, for a more advanced approach, create a simple backend API.</p>
        
        <p>Focus on creating a good reading experience with proper typography, adequate spacing, and a clean design.</p>`,
        date: "June 5, 2023",
        readTime: "6 min read",
        tag: "Web Development",
        image: "https://picsum.photos/800/400?random=12"
    },
    {
        id: 4,
        title: "CSS Grid vs Flexbox: When to Use Each",
        excerpt: "Understanding the strengths of CSS Grid and Flexbox will help you create better layouts faster.",
        content: `<p>CSS Grid and Flexbox are two powerful layout systems in CSS, each with its own strengths and use cases. Knowing when to use each can significantly improve your workflow and the quality of your layouts.</p>
        
        <h2>CSS Grid</h2>
        <p>CSS Grid is designed for two-dimensional layouts - both rows and columns. It's perfect for:</p>
        
        <ul>
            <li>Complex page layouts</li>
            <li>Grid-based designs</li>
            <li>Aligning items in both directions</li>
            <li>Creating responsive layouts with minimal media queries</li>
        </ul>
        
        <h2>Flexbox</h2>
        <p>Flexbox is designed for one-dimensional layouts - either a row or a column. It excels at:</p>
        
        <ul>
            <li>Distributing space along a single axis</li>
            <li>Aligning items within a container</li>
            <li>Creating flexible components like navigation bars</li>
            <li>Handling dynamic or unknown sizes of elements</li>
        </ul>
        
        <h2>The Best of Both Worlds</h2>
        <p>In many cases, you can use both together. Use Grid for the overall page layout and Flexbox for the components within that layout.</p>`,
        date: "May 28, 2023",
        readTime: "5 min read",
        tag: "CSS",
        image: "https://picsum.photos/800/400?random=13"
    },
    {
        id: 5,
        title: "The Importance of Web Accessibility",
        excerpt: "Creating websites that everyone can use isn't just good practice - it's essential for an inclusive web.",
        content: `<p>Web accessibility ensures that people with disabilities can perceive, understand, navigate, and interact with the web. It's not just a nice-to-have feature; it's a fundamental aspect of good web development.</p>
        
        <h2>Why Accessibility Matters</h2>
        <p>Approximately 15% of the world's population experiences some form of disability. By ignoring accessibility, you're excluding a significant portion of potential users. Beyond the ethical considerations, many countries have legal requirements for web accessibility.</p>
        
        <h2>Key Principles</h2>
        <p>The Web Content Accessibility Guidelines (WCAG) are organized around four principles:</p>
        
        <ol>
            <li><strong>Perceivable:</strong> Information must be presented in ways users can perceive</li>
            <li><strong>Operable:</strong> Interface components must be operable</li>
            <li><strong>Understandable:</strong> Information and operation must be understandable</li>
            <li><strong>Robust:</strong> Content must be robust enough for various user agents</li>
        </ol>
        
        <h2>Simple Steps to Improve Accessibility</h2>
        <p>Start with semantic HTML, provide text alternatives for images, ensure sufficient color contrast, and make all functionality available from a keyboard. Test your site with screen readers and other assistive technologies.</p>`,
        date: "May 20, 2023",
        readTime: "7 min read",
        tag: "Accessibility",
        image: "https://picsum.photos/800/400?random=14"
    }
];

// DOM Elements
const articleListView = document.getElementById('article-list-view');
const postDetailView = document.getElementById('post-detail-view');
const aboutView = document.getElementById('about-view');
const contactView = document.getElementById('contact-view');
const articlesContainer = document.getElementById('articles-container');
const postDetail = document.getElementById('post-detail');
const backBtn = document.getElementById('back-btn');
const backFromAboutBtn = document.getElementById('back-from-about-btn');
const backFromContactBtn = document.getElementById('back-from-contact-btn');
const homeLink = document.getElementById('home-link');
const allPostsLink = document.getElementById('all-posts-link');
const aboutLink = document.getElementById('about-link');
const contactLink = document.getElementById('contact-link');
const mobileMenuBtn = document.getElementById('mobile-menu-btn');
const contactForm = document.getElementById('contact-form');

// View Management Functions
function showArticleListView() {
    articleListView.classList.add('active');
    postDetailView.classList.remove('active');
    aboutView.classList.remove('active');
    contactView.classList.remove('active');
}

function showPostDetailView(postId) {
    articleListView.classList.remove('active');
    postDetailView.classList.add('active');
    aboutView.classList.remove('active');
    contactView.classList.remove('active');
    
    loadPostDetail(postId);
}

function showAboutView() {
    articleListView.classList.remove('active');
    postDetailView.classList.remove('active');
    aboutView.classList.add('active');
    contactView.classList.remove('active');
}

function showContactView() {
    articleListView.classList.remove('active');
    postDetailView.classList.remove('active');
    aboutView.classList.remove('active');
    contactView.classList.add('active');
}

// Load and Display Articles
function loadArticles() {
    articlesContainer.innerHTML = '';
    
    blogPosts.forEach(post => {
        const articleCard = document.createElement('div');
        articleCard.className = 'article-card';
        articleCard.setAttribute('data-id', post.id);
        
        articleCard.innerHTML = `
            <div class="article-image">
                <img src="${post.image}" alt="${post.title}">
            </div>
            <div class="article-content">
                <div class="article-meta">
                    <span class="article-date">${post.date}</span>
                    <span class="article-read-time">${post.readTime}</span>
                </div>
                <span class="article-tag">${post.tag}</span>
                <h3 class="article-title">${post.title}</h3>
                <p class="article-excerpt">${post.excerpt}</p>
                <button class="read-more-btn" data-id="${post.id}">Read More</button>
            </div>
        `;
        
        articlesContainer.appendChild(articleCard);
    });
    
    // Add event listeners to article cards and read more buttons
    document.querySelectorAll('.article-card').forEach(card => {
        card.addEventListener('click', function(e) {
            // Don't trigger if clicking on the read more button (handled separately)
            if (!e.target.classList.contains('read-more-btn')) {
                const postId = parseInt(this.getAttribute('data-id'));
                showPostDetailView(postId);
            }
        });
    });
    
    document.querySelectorAll('.read-more-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.stopPropagation(); // Prevent triggering the card click event
            const postId = parseInt(this.getAttribute('data-id'));
            showPostDetailView(postId);
        });
    });
}

// Load and Display Post Detail
function loadPostDetail(postId) {
    const post = blogPosts.find(p => p.id === postId);
    
    if (!post) {
        postDetail.innerHTML = '<p>Post not found.</p>';
        return;
    }
    
    postDetail.innerHTML = `
        <div class="post-header">
            <span class="post-tag">${post.tag}</span>
            <h1 class="post-title">${post.title}</h1>
            <div class="post-meta">
                <span class="post-date">${post.date}</span>
                <span class="post-read-time">${post.readTime}</span>
            </div>
        </div>
        <div class="post-image">
            <img src="${post.image}" alt="${post.title}">
        </div>
        <div class="post-body">
            ${post.content}
        </div>
    `;
}

// Mobile Menu Toggle
function toggleMobileMenu() {
    const navLinks = document.querySelector('.nav-links');
    navLinks.style.display = navLinks.style.display === 'flex' ? 'none' : 'flex';
    
    if (navLinks.style.display === 'flex') {
        navLinks.style.flexDirection = 'column';
        navLinks.style.position = 'absolute';
        navLinks.style.top = '100%';
        navLinks.style.left = '0';
        navLinks.style.right = '0';
        navLinks.style.backgroundColor = '#fff';
        navLinks.style.padding = '1rem';
        navLinks.style.boxShadow = '0 5px 10px rgba(0,0,0,0.1)';
        navLinks.style.gap = '1rem';
    }
}

// Handle Contact Form Submission
function handleContactFormSubmit(e) {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const message = document.getElementById('message').value;
    
    // In a real application, you would send this data to a server
    // For this demo, we'll just show an alert and reset the form
    alert(`Thank you for your message, ${name}! I'll get back to you soon.`);
    
    // Reset form
    contactForm.reset();
    
    // Return to article list
    showArticleListView();
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function() {
    // Load articles on page load
    loadArticles();
    
    // Set article list as active view
    showArticleListView();
    
    // Navigation event listeners
    backBtn.addEventListener('click', showArticleListView);
    backFromAboutBtn.addEventListener('click', showArticleListView);
    backFromContactBtn.addEventListener('click', showArticleListView);
    
    homeLink.addEventListener('click', function(e) {
        e.preventDefault();
        showArticleListView();
    });
    
    allPostsLink.addEventListener('click', function(e) {
        e.preventDefault();
        showArticleListView();
    });
    
    aboutLink.addEventListener('click', function(e) {
        e.preventDefault();
        showAboutView();
    });
    
    contactLink.addEventListener('click', function(e) {
        e.preventDefault();
        showContactView();
    });
    
    // Mobile menu
    mobileMenuBtn.addEventListener('click', toggleMobileMenu);
    
    // Contact form
    contactForm.addEventListener('submit', handleContactFormSubmit);
    
    // Close mobile menu when clicking outside
    document.addEventListener('click', function(e) {
        const navLinks = document.querySelector('.nav-links');
        if (navLinks.style.display === 'flex' && 
            !e.target.closest('.nav-links') && 
            !e.target.closest('.mobile-menu-btn')) {
            navLinks.style.display = 'none';
        }
    });
});

// Handle window resize - reset mobile menu on larger screens
window.addEventListener('resize', function() {
    const navLinks = document.querySelector('.nav-links');
    if (window.innerWidth > 768) {
        navLinks.style.display = 'flex';
        navLinks.style.flexDirection = 'row';
        navLinks.style.position = 'static';
        navLinks.style.backgroundColor = 'transparent';
        navLinks.style.padding = '0';
        navLinks.style.boxShadow = 'none';
    } else {
        navLinks.style.display = 'none';
    }
});