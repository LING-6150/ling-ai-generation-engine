export const categories = [
  { id: 'technology', name: 'Technology', color: '#667eea' },
  { id: 'design', name: 'Design', color: '#764ba2' },
  { id: 'business', name: 'Business', color: '#f093fb' },
  { id: 'lifestyle', name: 'Lifestyle', color: '#4facfe' },
  { id: 'education', name: 'Education', color: '#00f2fe' }
]

export const articles = [
  {
    id: 1,
    title: 'The Future of Web Development with Vue 3',
    excerpt: 'Exploring the latest features and improvements in Vue 3 and how they revolutionize modern web development.',
    content: `Vue 3 represents a significant leap forward in the JavaScript framework landscape. With the introduction of the Composition API, developers now have more flexibility in organizing their code logic. The new reactivity system, built on ES6 Proxies, provides better performance and more intuitive behavior.

One of the most exciting features is the improved TypeScript support, making Vue 3 a first-class citizen in the TypeScript ecosystem. The new teleport feature allows components to render their content in different parts of the DOM tree, solving many modal and tooltip positioning issues.

Performance improvements are substantial, with smaller bundle sizes and faster rendering. The new compiler generates more optimized code, and tree-shaking works better than ever before. These improvements make Vue 3 an excellent choice for both small projects and large-scale applications.

The ecosystem has also matured significantly, with libraries like Vite providing lightning-fast development experience and Pinia offering a simpler state management solution. The future looks bright for Vue developers as the framework continues to evolve and improve.`,
    category: 'technology',
    author: 'Alex Johnson',
    date: '2023-10-15',
    readTime: '5 min',
    image: 'https://picsum.photos/800/400?random=1'
  },
  {
    id: 2,
    title: 'Modern UI Design Principles for 2023',
    excerpt: 'Key design principles that every developer should know to create beautiful and functional user interfaces.',
    content: `Modern UI design has evolved significantly in recent years, focusing on user experience, accessibility, and performance. One of the most important principles is consistency - maintaining visual harmony across all components and pages.

Accessibility is no longer optional. Designing with accessibility in mind ensures that your applications can be used by everyone, regardless of their abilities. This includes proper color contrast, keyboard navigation, and screen reader support.

Performance has become a design consideration. Users expect fast-loading interfaces, and design choices can significantly impact performance. Optimizing images, reducing JavaScript bundle sizes, and implementing lazy loading are now essential practices.

Dark mode support has become standard, requiring careful consideration of color palettes and contrast ratios. Micro-interactions and animations should be purposeful and enhance the user experience rather than distract from it.

The trend towards minimalism continues, with clean interfaces that focus on content and functionality. However, this doesn't mean boring - thoughtful use of color, typography, and spacing can create visually appealing interfaces that are both beautiful and functional.`,
    category: 'design',
    author: 'Sarah Miller',
    date: '2023-10-10',
    readTime: '4 min',
    image: 'https://picsum.photos/800/400?random=2'
  },
  {
    id: 3,
    title: 'Building Scalable Web Applications',
    excerpt: 'Architectural patterns and best practices for building web applications that can scale with your business.',
    content: `Scalability is a critical consideration for any web application that expects to grow. The first step in building scalable applications is choosing the right architecture. Microservices architecture has gained popularity for its ability to scale individual components independently.

Database design plays a crucial role in scalability. Proper indexing, query optimization, and considering when to use SQL vs NoSQL databases can make a significant difference. Caching strategies, using tools like Redis, can dramatically improve performance for read-heavy applications.

Load balancing and horizontal scaling are essential for handling increased traffic. Using containerization with Docker and orchestration with Kubernetes provides flexibility and reliability in deployment.

Monitoring and observability are often overlooked but are critical for maintaining scalable applications. Implementing proper logging, metrics collection, and alerting systems helps identify and resolve issues before they affect users.

Finally, adopting a DevOps culture with continuous integration and deployment pipelines ensures that your application can evolve quickly while maintaining stability. Automated testing and infrastructure as code are essential components of a scalable development workflow.`,
    category: 'technology',
    author: 'Michael Chen',
    date: '2023-10-05',
    readTime: '6 min',
    image: 'https://picsum.photos/800/400?random=3'
  },
  {
    id: 4,
    title: 'The Rise of Remote Work Culture',
    excerpt: 'How remote work is changing business dynamics and what companies need to adapt to this new reality.',
    content: `The shift to remote work has fundamentally changed how businesses operate. Companies that successfully adapt to this new reality are seeing benefits in productivity, employee satisfaction, and access to global talent.

Communication is the cornerstone of successful remote work. Tools like Slack, Zoom, and Microsoft Teams have become essential, but it's the practices around these tools that matter most. Regular check-ins, clear documentation, and asynchronous communication are key.

Company culture needs intentional cultivation in a remote environment. Virtual team-building activities, regular all-hands meetings, and creating spaces for informal interaction help maintain team cohesion and morale.

Productivity measurement shifts from hours worked to outcomes delivered. This requires clear goal-setting, trust in employees, and focus on results rather than presence.

Security considerations become more complex with distributed teams. Implementing proper VPNs, multi-factor authentication, and security training are essential for protecting company data.

The future of work is likely hybrid, with flexibility being the key differentiator for attracting and retaining top talent. Companies that embrace this change will have a competitive advantage in the years to come.`,
    category: 'business',
    author: 'Emma Wilson',
    date: '2023-09-28',
    readTime: '5 min',
    image: 'https://picsum.photos/800/400?random=4'
  },
  {
    id: 5,
    title: 'Sustainable Tech: Building Green Applications',
    excerpt: 'How developers can contribute to environmental sustainability through conscious coding practices.',
    content: `Sustainability in technology is becoming increasingly important as we recognize the environmental impact of digital products. Developers have a significant role to play in creating more sustainable applications.

Energy efficiency starts with code optimization. Writing efficient algorithms, minimizing database queries, and reducing unnecessary computations can significantly reduce server energy consumption.

Frontend optimization is equally important. Minimizing JavaScript bundle sizes, optimizing images, and implementing lazy loading reduce data transfer and energy usage on user devices.

Server infrastructure choices matter. Using green hosting providers that run on renewable energy, optimizing server configurations, and implementing proper caching can dramatically reduce carbon footprint.

User behavior can be influenced through design. Features like dark mode, offline functionality, and data-saving modes not only improve user experience but also reduce energy consumption.

Measuring and monitoring environmental impact should become part of the development lifecycle. Tools are emerging to help developers understand the carbon footprint of their applications, allowing for continuous improvement.

As developers, we have the power to make choices that positively impact the environment while still delivering excellent user experiences.`,
    category: 'technology',
    author: 'David Park',
    date: '2023-09-20',
    readTime: '4 min',
    image: 'https://picsum.photos/800/400?random=5'
  },
  {
    id: 6,
    title: 'The Psychology of User Experience',
    excerpt: 'Understanding human psychology to create more engaging and effective user interfaces.',
    content: `User experience design is deeply rooted in psychology. Understanding how users think, feel, and behave allows designers to create more effective interfaces.

Cognitive load theory suggests that users have limited mental resources. Good design minimizes cognitive load by presenting information clearly, using familiar patterns, and reducing unnecessary complexity.

Hick's Law states that the time it takes to make a decision increases with the number of choices. This is why effective interfaces often limit options and guide users toward desired actions.

The Von Restorff effect (isolation effect) explains why distinctive items are more memorable. This principle can be used to highlight important actions or information.

Fitts's Law describes the relationship between target size, distance, and selection time. This has practical implications for button sizes, spacing, and placement in interfaces.

Color psychology influences user perception and behavior. Different colors evoke different emotions and associations, which should be considered in design choices.

By applying psychological principles, designers can create interfaces that are not only beautiful but also intuitive, efficient, and satisfying to use.`,
    category: 'design',
    author: 'Lisa Thompson',
    date: '2023-09-15',
    readTime: '5 min',
    image: 'https://picsum.photos/800/400?random=6'
  }
]

export function getArticleById(id) {
  return articles.find(article => article.id === parseInt(id))
}

export function getArticlesByCategory(category) {
  if (!category) return articles
  return articles.filter(article => article.category === category)
}

export function getCategoryById(id) {
  return categories.find(cat => cat.id === id)
}